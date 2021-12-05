package com.itheima.mobliesafe61.bean;

import android.graphics.drawable.Drawable;

public class AppInfo {
	public String packageName;// ID
	public String name;//
	public long size;//
	public long cachesize;//
	public boolean isUser;//
	public boolean isInSd;//
	public Drawable icon;//
	public String path;//
	@Override
	public String toString() {
		return "AppInfo [packageName=" + packageName + ", name=" + name + ", size=" + size + ", isUser=" + isUser + ", isInSd=" + isInSd + ", icon=" + icon + ", path=" + path + "]";
	}
	
	
}
