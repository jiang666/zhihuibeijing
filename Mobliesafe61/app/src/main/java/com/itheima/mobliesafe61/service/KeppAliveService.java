package com.itheima.mobliesafe61.service;

import com.itheima.mobliesafe61.HomeActivity;
import com.itheima.mobliesafe61.R;

import android.app.Notification;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.os.IBinder;

public class KeppAliveService extends Service {
	@Override
	public void onCreate() {
		super.onCreate();
		System.out.println("###复活    服务#############");
		// Notification notification=new Notification(小图标, 文本, 创建时间);
		Notification notification = new Notification(R.drawable.ic_launcher, "快播服务打开..", System.currentTimeMillis());
		// PendingIntent: 本质上是一个Intent 被点击才会执行的Intent
		// notification.setLatestEventInfo(上下文, 大字标题, 描述, 点击生效的Intent)
		Intent intent = new Intent(getBaseContext(), HomeActivity.class);
		PendingIntent pendingIntent = PendingIntent.getActivity(getBaseContext(), 0, intent, 0);
		notification.setLatestEventInfo(getBaseContext(), "快播:卫士", "有大把福利....", pendingIntent);
		// 提高优先级 Service BackGorund-->ForeGround
		// notification id 不能为0
		// notification 标题通知
		startForeground(1, notification);
	}

	@Override
	public void onDestroy() {
		super.onDestroy();
		// // Timer:指定时间执行任务
		// Timer timer = new Timer();
		// // timer.schedule(任务, 延时间, 间隔时间);
		// timer.schedule(new TimerTask() {
		// @Override
		// public void run() {
		// startService(new Intent(getBaseContext(), KeppAliveService.class));
		// }
		// }, 5000, 0);
	}

	@Override
	public IBinder onBind(Intent intent) {
		// TODO Auto-generated method stub
		return null;
	}
}
