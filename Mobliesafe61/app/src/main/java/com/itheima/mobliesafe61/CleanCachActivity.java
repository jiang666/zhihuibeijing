package com.itheima.mobliesafe61;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.content.pm.IPackageDataObserver;
import android.content.pm.IPackageStatsObserver;
import android.content.pm.PackageManager;
import android.content.pm.PackageStats;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.RemoteException;
import android.text.format.Formatter;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.itheima.mobliesafe61.bean.AppInfo;
import com.itheima.mobliesafe61.utils.InstalledSoftUtils;
import com.lidroid.xutils.ViewUtils;
import com.lidroid.xutils.view.annotation.ViewInject;
import com.lidroid.xutils.view.annotation.event.OnClick;

public class CleanCachActivity extends Activity {
	// 1/3布局 1/3列表 1/3处理数据
	@ViewInject(R.id.listview)
	ListView listview;
	@ViewInject(R.id.loading_view)
	LinearLayout loading_view;
	@ViewInject(R.id.progress)
	ProgressBar progress;
	@ViewInject(R.id.progress_text)
	TextView progress_text;

	// @ViewInject(R.id.cleanall)
	// TextView cleanall;

	@OnClick(R.id.cleanall)
	public void cleanall(View view) {
		// * @hide
		// */
		// // @SystemApi
		// public abstract void freeStorageAndNotify(long freeStorageSize, 10M
		// 100M
		// IPackageDataObserver observer 获取清理完毕的信号);
		try {
			Method[] methods = PackageManager.class.getMethods();// public
			Method freeStorageAndNotify = null;
			for (Method m : methods) {
				if ("freeStorageAndNotify".equals(m.getName())) {
					freeStorageAndNotify = m;
					break;
				}
			}
			// 获取结束时间
			IPackageDataObserver observer = new IPackageDataObserver.Stub() {

				// 清理成功。。。
				@Override
				public void onRemoveCompleted(String packageName, boolean succeeded) throws RemoteException {
					System.out.println(packageName + "  " + succeeded);
					scanResult.clear();// 清理所有查找结果
					CleanCachActivity.this.runOnUiThread(new Runnable() {
						@Override
						public void run() {
							if (adapter != null) {
								adapter.notifyDataSetChanged();
							}
							Toast.makeText(getBaseContext(), "你的手机无比干净...", 0).show();

						}
					});
				}
			};

			freeStorageAndNotify.invoke(getPackageManager(), Integer.MAX_VALUE, observer);
		} catch (IllegalAccessException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IllegalArgumentException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (InvocationTargetException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	private List<AppInfo> scanResult = new ArrayList<AppInfo>();

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);

		// android.R.style.Theme_Black_NoTitleBar
		setContentView(R.layout.activity_clean_cache);
		ViewUtils.inject(this);

		new AsyncTask<Void, AppInfo, Void>() {
			// 任务前
			protected void onPreExecute() {
				// ..
				loading_view.setVisibility(View.VISIBLE);
			};

			// 任务中

			@Override
			protected Void doInBackground(Void... params) {
				List<AppInfo> list = InstalledSoftUtils.getAllSofts(getBaseContext());
				for (AppInfo bean : list) {
					try {
						// info.name = "快播" + i;
						// info.packageName = "com.itheima.com.kuaibo" + i;
						// info.cachesize = 1000;
						// 缓存数据
						// scanResult.add(bean);
						getCache(bean);
						publishProgress(bean);
						Thread.sleep(50);
					} catch (InterruptedException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
				return null;
			}

			// 任务中的更新界面 publishProgress
			protected void onProgressUpdate(AppInfo[] values) {
				AppInfo info = values[0];
				progress_text.setText("扫描:" + info.name);

			};

			protected void onPostExecute(Void result) {
				// 任务后
				loading_view.setVisibility(View.GONE);

				int cacheSizeInPhone = 0;
				for (AppInfo info : scanResult) {
					cacheSizeInPhone += info.cachesize;
				}
				if (cacheSizeInPhone > 0) {
					Toast.makeText(getBaseContext(), "你的手机都成垃圾堆...", 0).show();
				}
				// 显示列表
				adapter = new CacheInfoAdapter();
				listview.setAdapter(adapter);
			};

		}.execute();

	}

	private void getCache(final AppInfo bean) {
		PackageManager pm = getPackageManager();
		// PackageManager.class.getMethod(方法名, 参数的类型 class);
		try {
			Method getPackageSizeInfo = PackageManager.class.getMethod("getPackageSizeInfo", String.class, IPackageStatsObserver.class);
			// getPackageSizeInfo:要求添加权限
			System.out.println(getPackageSizeInfo);
			// 实现接口
			IPackageStatsObserver observer = new IPackageStatsObserver.Stub() {
				// data/data/包名
				@Override
				public void onGetStatsCompleted(PackageStats pStats, boolean succeeded) throws RemoteException {
					// PackageStats:cache 文件大小
					if (pStats.cacheSize > 12 * 1024) {
						bean.cachesize = pStats.cacheSize;
						scanResult.add(bean);
					}
					System.out.println(Formatter.formatFileSize(getBaseContext(), pStats.cacheSize));
				}
			};
			getPackageSizeInfo.invoke(pm, bean.packageName, observer);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		// IPackageStatsObserver:aidl接口
		// 非公开api
		// public void getPackageSizeInfo(String packageName,
		// IPackageStatsObserver observer) {
		// getPackageSizeInfo(packageName, UserHandle.myUserId(), observer);
		// }

	}

	private CacheInfoAdapter adapter = null;

	private class CacheInfoAdapter extends BaseAdapter {

		// 返回列表行数
		@Override
		public int getCount() {
			return scanResult.size();
		}

		class ViewHolder {
			ImageView icon;
			TextView appname;
			TextView size;
		}

		// 返回行视图 显示指定下标的数据
		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			// 数据
			AppInfo info = scanResult.get(position);

			ViewHolder holder = null;
			if (convertView == null) {
				holder = new ViewHolder();
				convertView = View.inflate(getBaseContext(), R.layout.item_app_cache, null);
				holder.icon = (ImageView) convertView.findViewById(R.id.icon);
				holder.appname = (TextView) convertView.findViewById(R.id.appname);
				holder.size = (TextView) convertView.findViewById(R.id.size);
				convertView.setTag(holder);
			} else {
				holder = (ViewHolder) convertView.getTag();
			}
			holder.icon.setImageDrawable(info.icon);
			holder.appname.setText(info.name);
			holder.size.setText(Formatter.formatFileSize(getBaseContext(), info.cachesize));
			return convertView;
		}

		@Override
		public Object getItem(int position) {
			return null;
		}

		@Override
		public long getItemId(int position) {
			// TODO Auto-generated method stub
			return 0;
		}

	}
}
