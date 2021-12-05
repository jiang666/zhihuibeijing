package com.itheima.mobliesafe61.service;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Timer;
import java.util.TimerTask;

import android.app.PendingIntent;
import android.app.Service;
import android.appwidget.AppWidgetManager;
import android.content.ComponentName;
import android.content.Intent;
import android.os.IBinder;
import android.widget.RemoteViews;

import com.itheima.mobliesafe61.R;
import com.itheima.mobliesafe61.receiver.MyAppWidgetProvider;
import com.itheima.mobliesafe61.utils.ProcessUtils;

public class WidgetUpdateService extends Service {

	@Override
	public void onCreate() {
		// TODO Auto-generated method stub
		super.onCreate();
		System.out.println("---Widget刷新服务-");

		timer = new Timer();
		timer.schedule(new TimerTask() {
			@Override
			public void run() {
				// Date date=new Date();
				// SimpleDateFormat format=new SimpleDateFormat("HH:mm:ss");
				// String timeString=format.format(date);
				// AppwdigetManager替换视图
				// RemoteViews
				// RemoteViews widget=new RemoteViews(包名，视图)
				RemoteViews widget = new RemoteViews(getPackageName(), R.layout.mywidget_view);
				Intent intent = new Intent();
				intent.setAction("android.intent.action.killall");
				// 18：33：33
				// ##RemoteViews给控件添加点击事件不能使用OnClickListner 使用点击生效的PendingIntent
				PendingIntent pendingIntent = PendingIntent.getBroadcast(getBaseContext(), 0, intent, 0);
				// 操作
				widget.setOnClickPendingIntent(R.id.kill_all, pendingIntent);
				widget.setTextViewText(R.id.processcount, "进程个数" + ProcessUtils.getProcessSize(getBaseContext()));
				long availMem = ProcessUtils.getMemInfo(getBaseContext());
				String sizeString = ProcessUtils.formate(getBaseContext(), availMem);
				widget.setTextViewText(R.id.availmem, "可用内存" + sizeString);
				// updateAppWidge发送视图给桌面程序 要求替换原有的视图
				// 标签
				ComponentName name = new ComponentName(getPackageName(), MyAppWidgetProvider.class.getCanonicalName());
				AppWidgetManager.getInstance(getBaseContext()).updateAppWidget(name, widget);
			}
		}, 1000, 1000);

	}

	Timer timer;

	@Override
	public void onDestroy() {
		// TODO Auto-generated method stub
		super.onDestroy();
		timer.cancel();
		timer = null;
	}

	@Override
	public IBinder onBind(Intent intent) {
		// TODO Auto-generated method stub
		return null;
	}
}
