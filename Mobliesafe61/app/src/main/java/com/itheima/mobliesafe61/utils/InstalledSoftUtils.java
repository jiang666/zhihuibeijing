package com.itheima.mobliesafe61.utils;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;

import com.itheima.mobliesafe61.bean.AppInfo;

public class InstalledSoftUtils {

	
	/**
	 * 查询所有已经安装软件信息
	 * @param context
	 * @return
	 */
	public static List<AppInfo>  getAllSofts(Context context)
	{
		
		List<AppInfo> apps=new ArrayList<AppInfo>();
		// 获取包管理者
		PackageManager pm = context.getPackageManager();
		//
		// pm.getInstalledPackages(标签信息)
		List<PackageInfo> apkInfos = pm.getInstalledPackages(0);
		AppInfo bean = null;
		for (PackageInfo apk : apkInfos) {
			// 获取application标签信息

				bean = new AppInfo();
				ApplicationInfo ai = apk.applicationInfo;
				// 包名
				bean.packageName = ai.packageName;
				//apk 文件路径
				bean.path = ai.sourceDir;
				// 名称
				bean.name = ai.loadLabel(pm).toString();
				// 图标
				bean.icon = ai.loadIcon(pm);
				apps.add(bean);
			
		}
		return apps;
	}
}
