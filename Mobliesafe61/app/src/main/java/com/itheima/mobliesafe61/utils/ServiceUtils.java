package com.itheima.mobliesafe61.utils;

import java.util.List;

import android.app.ActivityManager;
import android.app.ActivityManager.RunningServiceInfo;
import android.content.ComponentName;
import android.content.Context;

/**
 * 动态获取服务是否开启状态
 * @author Administrator
 *
 */
public class ServiceUtils {
	/**
	 * 获取服务器的是否开始状态
	 * @param serviceClassName
	 * @return
	 */
	public static boolean isRunningService(Context context,String serviceClassName){
		//活动的管理者，进程的管理者
		ActivityManager activityManager = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
		//获取系统当中运行的service
		List<RunningServiceInfo> runningServices = activityManager.getRunningServices(1000);//maxNum ： 最多返回多少个，上限值 ， 
		for (RunningServiceInfo runningServiceInfo : runningServices) {
			ComponentName service = runningServiceInfo.service;//获取组件的标示
			String className = service.getClassName();
			if (serviceClassName.equals(className)) {
				return true;
			}
		}
		return false;
	}
	//  10M  10G   手机的最大的运行内存4G  内存溢出
	//RAM:运行内存，关机保存的信息就会消除
	//ROM ： 机身内存，关机保存的信息不会消除
	
	
	
	
}
