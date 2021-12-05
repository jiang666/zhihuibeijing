package com.itheima.mobliesafe61;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.content.res.AssetManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.SystemClock;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.itheima.mobliesafe61.db.dao.VirusDao;
import com.itheima.mobliesafe61.utils.StreamUtils;
import com.lidroid.xutils.HttpUtils;
import com.lidroid.xutils.exception.HttpException;
import com.lidroid.xutils.http.ResponseInfo;
import com.lidroid.xutils.http.callback.RequestCallBack;
import com.lidroid.xutils.http.client.HttpRequest.HttpMethod;
import com.lidroid.xutils.util.IOUtils;

public class SplashActivity extends Activity {
	protected static final int MSG_UPDATE_DIALOG = 1;
	protected static final int MSG_UPDATE_ENTERHOME = 2;
	protected static final int MSG_SERVER_ERROR = 3;
	protected static final int MSG_URL_ERROR = 4;
	protected static final int MSG_IO_ERROR = 5;
	protected static final int MSG_JSON_ERROR = 6;
	/**
	 * 版本号
	 */
	private TextView tv_splash_version;
	private String code;
	private String apkurl;
	private String des;
	private SharedPreferences sp;
	private Handler handler = new Handler() {
		public void handleMessage(Message msg) {
			switch (msg.what) {
			case MSG_UPDATE_DIALOG:
				// 弹出对话框
				showdialog();
				break;
			case MSG_UPDATE_ENTERHOME:
				// 跳转到主界面
				enterHome();
				break;
			case MSG_SERVER_ERROR:
				Toast.makeText(getApplicationContext(), "服务器异常", 0).show();
				enterHome();
				break;
			case MSG_IO_ERROR:
				Toast.makeText(getApplicationContext(), "您的网络不给力", 0).show();
				enterHome();
				break;
			case MSG_URL_ERROR:
				Toast.makeText(getApplicationContext(), "错误号：" + MSG_URL_ERROR, 0).show();
				enterHome();
				break;
			case MSG_JSON_ERROR:
				Toast.makeText(getApplicationContext(), "错误号：" + MSG_JSON_ERROR, 0).show();
				enterHome();
				break;
			default:
				break;
			}
		};
	};
	private TextView tv_splash_plan;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_splash);
		sp = getSharedPreferences("config", MODE_PRIVATE);
		tv_splash_version = (TextView) findViewById(R.id.tv_splash_version);
		tv_splash_plan = (TextView) findViewById(R.id.tv_splash_plan);
		tv_splash_version.setText("版本号:" + getVersionCode());
		if (sp.getBoolean("update", true)) {
			update();
		} else {
			new Thread() {
				public void run() {
					SystemClock.sleep(2000);// 不能再主线程中睡两秒钟，主线程会去渲染splash界面
					// ui线程就是主线程，运行主线程
					runOnUiThread(new Runnable() {// 内部封装了handler

						@Override
						public void run() {
							// 跳转到主界面去
							enterHome();
						}
					});
				};
			}.start();
		}

		// Intent intent = new Intent(this,AddressService.class);
		// startService(intent);
//		new Thread() {
//			public void run() {
				copyDb(getFilesDir().getAbsolutePath(), "address.db");

				String antivirus = "data/data/" + getPackageName() + "/antivirus.db";
				File file = new File(antivirus);
				if (!file.exists()) {
					copyDb("data/data/" + getPackageName() + "/", "antivirus.db");
					keepVirusDbSynchrozied();
				} else {
					keepVirusDbSynchrozied();
				}

//			};
//		}.start();
	}

	public void keepVirusDbSynchrozied() {
		final VirusDao dao=new VirusDao();
		HttpUtils http = new HttpUtils();
		String url = "http://192.168.22.65:8080/web/virus_1722";
		http.send(HttpMethod.GET, url, new RequestCallBack<String>() {
			@Override
			public void onFailure(HttpException arg0, String arg1) {

			}
			@Override
			public void onSuccess(ResponseInfo<String> result) {
			
				try {
					String json = result.result;
					JSONObject jsonObj=new JSONObject(json);
					String versioncode=jsonObj.getString("versioncode");
					System.out.println("versioncode ######"+versioncode);
					dao.updateVersion(getBaseContext(), versioncode);
					JSONArray viruslist=jsonObj.getJSONArray("viruslist");
					for(int i=0;i<viruslist.length();i++)
					{
						JSONObject virus=viruslist.getJSONObject(i);
						String desc=virus.getString("desc");
						String type=virus.getString("type");
						String name=virus.getString("name");
						String md5=virus.getString("md5");
						System.out.println(" md5 "+md5);
						dao.add(getBaseContext(), md5, desc, type, name);
					}
				} catch (Exception e) {
					e.printStackTrace();
				}
				
				// {"versioncode":"1723",viruslist:[{"desc":"av病毒",type:"6",name:"com.itheima.kuaibo",md5:"024b2f447af53cf90ac02dc88de53201"}]}
			}
		});
	}

	/**
	 * 拷贝数据库
	 */
	private void copyDb(String dir, String soureFile) {
		File file = new File(dir, soureFile);
		// 如果数据库不存在，就拷贝数据库
		if (!file.exists()) {
			// 从assets目录下将数据库读取出来
			AssetManager assetManager = getAssets();// 获取asset管理者
			InputStream in = null;
			FileOutputStream out = null;
			try {
				// fileName : 数据库的名称
				in = assetManager.open(soureFile);
				// getCacheDir() 存放到缓存路径 消除掉的，不建议存储
				// getFilesDir() 存放到文件目录下
				out = new FileOutputStream(file);
				byte[] b = new byte[1024];
				int len = -1;
				while ((len = in.read(b)) != -1) {
					out.write(b, 0, len);
				}
			} catch (IOException e) {
				e.printStackTrace();
			} finally {
				// out.close();
				// in.close();
				// 使用xutils进行关流操作
				IOUtils.closeQuietly(out);
				IOUtils.closeQuietly(in);
			}
		}
	}

	/**
	 * 更新版本对话框
	 */
	protected void showdialog() {
		// 创建对话框,必须告诉dialog要挂载到那个activity，才能正常显示dialog
		AlertDialog.Builder builder = new Builder(this);
		// 设置title
		builder.setTitle("新版本：" + code);
		// 设置对话框不会消失
		builder.setCancelable(false);
		// 设置图标
		builder.setIcon(R.drawable.ic_launcher);
		// 设置描述信息
		builder.setMessage(des);
		// 设置升级按钮
		builder.setPositiveButton("升级", new DialogInterface.OnClickListener() {

			@Override
			public void onClick(DialogInterface dialog, int which) {
				downloadAPk();
			}
		});
		// 设置取消按钮
		builder.setNegativeButton("取消", new DialogInterface.OnClickListener() {

			@Override
			public void onClick(DialogInterface dialog, int which) {
				// 对话框消失
				dialog.dismiss();
				enterHome();
			}
		});
		// builder.create().show();
		builder.show();// 内部也是得到一个dialog，在调用show()方法
	}

	/**
	 * 下载最新的版本
	 */
	protected void downloadAPk() {
		HttpUtils httpUtils = new HttpUtils();
		// 参数1：下载地址
		// 参数2：保存路径
		// 参数3：回调函数
		httpUtils.download(apkurl, "/mnt/sdcard/mobliesafe612.0.apk", new RequestCallBack<File>() {

			// 下载成功的调用
			@Override
			public void onSuccess(ResponseInfo<File> arg0) {
				Toast.makeText(getApplicationContext(), "下载成功", 0).show();
				intallAPK();
			}

			// 下载失败的调用
			@Override
			public void onFailure(HttpException arg0, String arg1) {
				// TODO Auto-generated method stub

			}

			// 下载中可以调用的方法
			// total : 下载的总进度
			// current : 当前的进度
			// isUploading : 是否装载
			@Override
			public void onLoading(long total, long current, boolean isUploading) {
				super.onLoading(total, current, isUploading);
				tv_splash_plan.setVisibility(View.VISIBLE);// 显示textview
				tv_splash_plan.setText(current + "/" + total);
			}

		});
	}

	/**
	 * 安装应用
	 */
	protected void intallAPK() {
		/**
		 * <intent-filter> <action android:name="android.intent.action.VIEW" />
		 * <category android:name="android.intent.category.DEFAULT" /> <data
		 * android:scheme="content" /> <data android:scheme="file" /> <data
		 * android:mimeType="application/vnd.android.package-archive" />
		 * </intent-filter>
		 */
		Intent intent = new Intent();
		intent.setAction("android.intent.action.VIEW");
		intent.addCategory("android.intent.category.DEFAULT");

		// 因为setType和setData是会相互覆盖，不适用
		// intent.setType("application/vnd.android.package-archive");
		// intent.setData(Uri.fromFile(new
		// File("/mnt/sdcard/mobliesafe612.0.apk")));

		intent.setDataAndType(Uri.fromFile(new File("/mnt/sdcard/mobliesafe612.0.apk")), "application/vnd.android.package-archive");
		// 当前当前 activity消失的时候，会调用以前的activity的onActivityResult方法
		startActivityForResult(intent, 0);
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		enterHome();
	}

	/**
	 * 跳转主界面的操作
	 */
	protected void enterHome() {
		Intent intent = new Intent(this, HomeActivity.class);
		startActivity(intent);
		// activity消失
		finish();
	}

	/**
	 * 更新版本
	 */
	private void update() {
		// 连接服务器，耗时操作，4.0不允许在主线程执行耗时操作
		new Thread() {

			public void run() {
				Message message = Message.obtain();// 从消息池中那消息，比较节约资源
				long startTime = System.currentTimeMillis();
				try {
					// 连接地址
					URL url = new URL("http://192.168.22.83:8080/updateinfo.html");
					// 连接操作
					HttpURLConnection conn = (HttpURLConnection) url.openConnection();// http协议
					// 设置连接超时时间
					conn.setConnectTimeout(5000);// 如果连接5秒还没有连接成功，就不去连接了
					// 设置读取超时时间
					// conn.setReadTimeout(5000);//读取数据，读取5秒还没读完，就不去读取了
					// 设置请求方式
					conn.setRequestMethod("GET");// post
					// 获取服务器返回的状态码
					int responseCode = conn.getResponseCode();// 200 404
					if (responseCode == 200) {
						// 连接成功 ， 获取服务器返回的数据 code : 新版本的版本号 apkurl :　新版本的下载地址
						// des : 新版本的描述信息
						// xml json
						// System.out.println("连接成功");
						InputStream in = conn.getInputStream();// 获取服务器返回的流信息
						// 把流信息转换成字符串信息
						String json = StreamUtils.parserStream(in);
						// 解析json数据
						JSONObject jsonObject = new JSONObject(json);
						code = jsonObject.getString("code");
						apkurl = jsonObject.getString("apkurl");
						des = jsonObject.getString("des");
						if (code.equals(getVersionCode())) {
							// 一致，不升级
							message.what = MSG_UPDATE_ENTERHOME;
						} else {
							// 不一致，弹出对话框提醒用户升级
							message.what = MSG_UPDATE_DIALOG;
						}
					} else {
						// 连接失败
						// System.out.println("连接失败");
						message.what = MSG_SERVER_ERROR;
					}
				} catch (MalformedURLException e) {
					e.printStackTrace();
					message.what = MSG_URL_ERROR;
				} catch (IOException e) {
					e.printStackTrace();
					message.what = MSG_IO_ERROR;
				} catch (JSONException e) {
					e.printStackTrace();
					message.what = MSG_JSON_ERROR;
				} finally {
					long endTime = System.currentTimeMillis();
					long dTime = endTime - startTime;
					if (dTime < 2000) {
						SystemClock.sleep(2000 - dTime);// 保证能睡两秒
					}
					handler.sendMessage(message);
				}
			};
		}.start();
	}

	/**
	 * 获取当前应用的版本号
	 */
	private String getVersionCode() {
		// 包的管理者，获取应用程序中清单文件里的所有信息了
		PackageManager packageManager = getPackageManager();
		// packageName : 当前应用程序的包名
		// flags:指定的信息
		try {
			// getPackageName() : 获取应用程序的包名
			PackageInfo packageInfo = packageManager.getPackageInfo(getPackageName(), 0);
			// 获取当前应用程序的版本号
			String versionName = packageInfo.versionName;
			return versionName;
		} catch (NameNotFoundException e) {
			// 找不到包名的异常
			e.printStackTrace();
		}
		return null;
	}

}
