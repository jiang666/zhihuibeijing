package com.itheima.mobliesafe61.utils;

import java.util.List;

import android.app.ActivityManager;
import android.app.ActivityManager.RunningAppProcessInfo;
import android.content.Context;
import android.text.format.Formatter;

public class ProcessUtils {

	/**
	 * 进程个数
	 * 
	 * @param am
	 */
	public static int  getProcessSize(Context context) {
		ActivityManager am=(ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
		// 查询所有进程
		// RunningAppProcessInfo 进程信息的封装对象 1.进程名 2.pid 进程编号
		List<RunningAppProcessInfo> list = am.getRunningAppProcesses();
		int processCount = list.size();
		return processCount;
	}
	
	public static  String formate(Context context,long size) {
		return Formatter.formatFileSize(context, size);
	}
	/**
	 * 获取内存信息
	 * 
	 * @param am
	 */
	

	public static  long getMemInfo(Context context ) {
		ActivityManager am=(ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
		long availmemSize=0;
		// MemoryInfo:内存信息的封装对象
		ActivityManager.MemoryInfo mInfo = new ActivityManager.MemoryInfo();
		am.getMemoryInfo(mInfo);// 获取内存信息
		availmemSize = mInfo.availMem;
		return availmemSize;
	}
}
