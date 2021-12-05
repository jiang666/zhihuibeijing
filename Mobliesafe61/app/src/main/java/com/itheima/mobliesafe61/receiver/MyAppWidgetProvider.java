package com.itheima.mobliesafe61.receiver;

import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.Context;
import android.content.Intent;

import com.itheima.mobliesafe61.service.WidgetUpdateService;


// 标准类:AppWidgetProvider:广播接收者
public class MyAppWidgetProvider extends AppWidgetProvider {
	@Override
	public void onEnabled(Context context) {
		// TODO Auto-generated method stub
		super.onEnabled(context);
		System.out.println("------onEnabled----------");
		context.startService(new Intent(context, WidgetUpdateService.class));
	}
	@Override
	public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {
		// TODO Auto-generated method stub
		super.onUpdate(context, appWidgetManager, appWidgetIds);
		System.out.println("------onUpdate----------");
	}
	@Override
	public void onDeleted(Context context, int[] appWidgetIds) {
		// TODO Auto-generated method stub
		super.onDeleted(context, appWidgetIds);
		System.out.println("------onDeleted----------");
	}
	@Override
	public void onDisabled(Context context) {
		// TODO Auto-generated method stub
		super.onDisabled(context);
		System.out.println("------onDisabled----------");
		context.stopService(new Intent(context, WidgetUpdateService.class));
	}
}
