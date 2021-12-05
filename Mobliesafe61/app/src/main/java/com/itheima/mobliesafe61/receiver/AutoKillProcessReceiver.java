package com.itheima.mobliesafe61.receiver;

import java.util.List;

import android.app.ActivityManager;
import android.app.ActivityManager.RunningAppProcessInfo;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.widget.Toast;

public class AutoKillProcessReceiver extends BroadcastReceiver {

	// 接收到指定广播的时候
	@Override
	public void onReceive(Context context, Intent intent) {
		// 获取ActivityManager
		ActivityManager am = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
		// 获取所有进程
		List<RunningAppProcessInfo> plist = am.getRunningAppProcesses();
		//killBackgroundProcesses对 优先级大于BackGround
		for (RunningAppProcessInfo info : plist) {
			am.killBackgroundProcesses(info.processName);
		}
		System.out.println("----清理成功-----");
	}

}
