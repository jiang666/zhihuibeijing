package com.itheima.mobliesafe61.service;

import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import android.app.ActivityManager;
import android.app.ActivityManager.RunningAppProcessInfo;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.CountDownTimer;
import android.os.IBinder;

public class AutoKillProcess extends Service {
	@Override
	public void onCreate() {
		// TODO Auto-generated method stub
		super.onCreate();
		System.out.println("----自动清理服务----");
		receiver = new BroadcastReceiver() {
			@Override
			public void onReceive(Context context, Intent intent) {
				autoKillProcess(context);
			}

		};
		IntentFilter filter = new IntentFilter();
		filter.addAction(Intent.ACTION_SCREEN_OFF);
		filter.addAction("android.intent.action.killall");
		registerReceiver(receiver, filter);
		// startKillTimer();

//		countDowntimer();
	}

	/**
	 * 倒计时清理
	 */
	private void countDowntimer() {
		// downTimer=new CountDownTimer(执行时间，时间间隔 ) {
		downTimer = new CountDownTimer(1000 * 30, 1000) {
			private int count = 30;

			@Override
			public void onTick(long millisUntilFinished) {// 按时间间隔执行
				System.out.println("onTick----滴答" + count);
				--count;
				autoKillProcess(getBaseContext());
			}

			@Override
			public void onFinish() {// 最后一次
				System.out.println("onFinish----" + count);
				autoKillProcess(getBaseContext());

			}
		};
		downTimer.start();
	}

	private CountDownTimer downTimer = null;

	/**
	 * Timer创建的定时器
	 */
	private void startKillTimer() {
		// 定时清理
		timer = new Timer();
		// timer.schedule(执行任务, 延时时间, 间隔时间);
		timer.schedule(new TimerTask() {

			@Override
			public void run() {
				// TODO Auto-generated method stub
				autoKillProcess(getBaseContext());
			}
		}, 1000, 5000);
	}

	Timer timer;

	private void autoKillProcess(Context context) {
		// 获取ActivityManager
		ActivityManager am = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
		// 获取所有进程
		List<RunningAppProcessInfo> plist = am.getRunningAppProcesses();
		// killBackgroundProcesses对 优先级大于BackGround
		for (RunningAppProcessInfo info : plist) {
			am.killBackgroundProcesses(info.processName);
		}
		System.out.println("----清理成功-----");
	}

	BroadcastReceiver receiver;

	@Override
	public void onDestroy() {
		// TODO Auto-generated method stub
		super.onDestroy();
		unregisterReceiver(receiver);
		if (timer != null) {
			timer.cancel();
			timer = null;
		}
		if (downTimer != null) {
			downTimer.cancel();
			downTimer = null;
		}
	}

	@Override
	public IBinder onBind(Intent intent) {
		// TODO Auto-generated method stub
		return null;
	}

}
