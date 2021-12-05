package com.itcast.googleplayteach.domain;

import java.util.ArrayList;

/**
 * 首页应用信息封装
 * 
 * @author Kevin
 * 
 */
public class AppInfo {

	public String des;
	public String downloadUrl;
	public String iconUrl;
	public String id;
	public String name;
	public String packageName;
	public long size;
	public double stars;

	// 以下字段共应用详情页使用
	public String author;
	public String date;
	public String downloadNum;
	public String version;
	public ArrayList<SafeInfo> safe;
	public ArrayList<String> screen;

	public static class SafeInfo {
		public String safeDes;
		public int safeDesColor;
		public String safeDesUrl;
		public String safeUrl;
	}

}
