package com.itheima.mobliesafe61.service;

import java.util.ArrayList;
import java.util.List;

import android.app.ActivityManager;
import android.app.ActivityManager.RunningTaskInfo;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.ContentObserver;
import android.net.Uri;
import android.os.Handler;
import android.os.IBinder;

import com.itheima.mobliesafe61.PwdEnterActivity;
import com.itheima.mobliesafe61.db.dao.AppLockDao;

public class WatchDogService extends Service {

	private boolean isWatching = false;

	private AppLockDao dao = null;

	private List<String> lockApps = new ArrayList<String>();

	@Override
	public void onDestroy() {
		// TODO Auto-generated method stub
		super.onDestroy();
		unregisterReceiver(receiver);
		getContentResolver().unregisterContentObserver(observer);
	}

	ContentObserver observer;

	@Override
	public void onCreate() {
		super.onCreate();
		System.out.println("######看门狗是单身狗吗？############");
		dao = new AppLockDao(getBaseContext());
		lockApps = dao.findAll();
		// 运行看门狗线程
		startWatchDogThread();

		registerLetGo();
		keepListSync();
	}

	/**
	 * 使用集合保持同步
	 */
	private void keepListSync() {
		// ContentObserver:接收修改信号
		observer = new ContentObserver(new Handler()) {
			// 低版本
			@Override
			public void onChange(boolean selfChange) {
				lockApps = dao.findAll();// 同步
				System.out.println("########### 同步");
			}

			// 高版
			@Override
			public void onChange(boolean selfChange, Uri uri) {
				lockApps = dao.findAll();// 同步
				System.out.println("########### 同步");
			}
		};

		// 信号
		Uri uri = Uri.parse("content://" + AppLockDao.class.getName());
		// 注意第二个参数:子路径是否有效 content://add content://add/1
		getContentResolver().registerContentObserver(uri, true, observer);
	}

	/**
	 * 放行程序
	 */
	private void registerLetGo() {
		// 接收密码输入界面的广播
		receiver = new BroadcastReceiver() {
			@Override
			public void onReceive(Context context, Intent intent) {
				if (intent.getAction().equals("com.itheima.dog.letgo")) {
					letgoPackageName = intent.getStringExtra("packagename");
				} else if (intent.getAction().equals(Intent.ACTION_SCREEN_OFF)) {
					isWatching = false;

				} else if (intent.getAction().equals(Intent.ACTION_SCREEN_ON)) {
					isWatching = true;
					startWatchDogThread();

				}
			}
		};
		// 绑定参数
		IntentFilter filter = new IntentFilter();
		filter.addAction("com.itheima.dog.letgo");
		filter.addAction(Intent.ACTION_SCREEN_OFF);// 暗
		filter.addAction(Intent.ACTION_SCREEN_ON);// 亮
		registerReceiver(receiver, filter);
	}

	BroadcastReceiver receiver;
	private String letgoPackageName = "";

	/**
	 * 启动 看门狗线程
	 */
	private void startWatchDogThread() {
		isWatching = true;
		// 判断 当前哪个程序 正在使用 -->进程
		new Thread() {
			public void run() {
				while (isWatching) {
					try {
						Thread.sleep(1000);
					} catch (InterruptedException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
					// 获取进程管理者
					ActivityManager am = (ActivityManager) getSystemService(Context.ACTIVITY_SERVICE);
					// 判断当前显示屏幕上的软件的包名 任务栈
					List<RunningTaskInfo> list = am.getRunningTasks(1);
					String packagename = list.get(0).topActivity.getPackageName();// 正在操作的页面
					// --》包名
					System.out.println("监测到程序包名  " + packagename);
					// if (dao.hasExist(packagename)) { //IO -->效率低
					if (lockApps.contains(packagename))// Mem-->效率高
					{
						if (packagename.equals(letgoPackageName)) {
							System.out.println("放行...Letgo " + letgoPackageName);
						} else {
							Intent intent = new Intent(getBaseContext(), PwdEnterActivity.class);
							intent.putExtra("packagename", packagename);
							intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
							startActivity(intent);
						}

					}
				}
			};
		}.start();
	}

	@Override
	public IBinder onBind(Intent intent) {
		// TODO Auto-generated method stub
		return null;
	}
}
