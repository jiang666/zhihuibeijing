package com.itcast.googleplayteach.http.protocol;

import java.util.ArrayList;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.itcast.googleplayteach.domain.AppInfo;
import com.itcast.googleplayteach.domain.AppInfo.SafeInfo;

/**
 * 首页详情页网络请求和数据解析
 * 
 * @author Kevin
 */
public class HomeDetailProtocol extends BaseProtocol<AppInfo> {

	private String packageName;

	public HomeDetailProtocol(String packageName) {
		this.packageName = packageName;
	}

	@Override
	public String getKey() {
		return "detail";
	}

	@Override
	public String getParams() {
		return "&packageName=" + packageName;
	}

	@Override
	public AppInfo parseJson(String result) {
		try {
			JSONObject jo = new JSONObject(result);

			AppInfo info = new AppInfo();
			info.des = jo.getString("des");
			info.downloadUrl = jo.getString("downloadUrl");
			info.iconUrl = jo.getString("iconUrl");
			info.id = jo.getString("id");
			info.name = jo.getString("name");
			info.packageName = jo.getString("packageName");
			info.size = jo.getLong("size");
			info.stars = jo.getDouble("stars");

			info.author = jo.getString("author");
			info.date = jo.getString("date");
			info.downloadNum = jo.getString("downloadNum");
			info.version = jo.getString("version");

			// 解析安全相关信息
			JSONArray ja = jo.getJSONArray("safe");
			ArrayList<SafeInfo> safe = new ArrayList<AppInfo.SafeInfo>();
			for (int i = 0; i < ja.length(); i++) {
				SafeInfo safeInfo = new SafeInfo();
				JSONObject jo1 = ja.getJSONObject(i);
				safeInfo.safeDes = jo1.getString("safeDes");
				safeInfo.safeDesUrl = jo1.getString("safeDesUrl");
				safeInfo.safeUrl = jo1.getString("safeUrl");
				safeInfo.safeDesColor = jo1.getInt("safeDesColor");

				safe.add(safeInfo);
			}
			info.safe = safe;

			// 解析图片信息
			JSONArray ja1 = jo.getJSONArray("screen");
			ArrayList<String> screen = new ArrayList<String>();
			for (int i = 0; i < ja1.length(); i++) {
				String picUrl = ja1.getString(i);
				screen.add(picUrl);
			}
			info.screen = screen;

			return info;
		} catch (JSONException e) {
			e.printStackTrace();
		}

		return null;
	}

}
