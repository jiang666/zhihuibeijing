package com.itcast.googleplayteach.http.protocol;

import java.util.ArrayList;

import org.json.JSONArray;
import org.json.JSONObject;

import com.itcast.googleplayteach.domain.AppInfo;

/**
 * 应用页访问网络
 * 
 * @author Kevin
 * 
 */
public class AppProtocol extends BaseProtocol<ArrayList<AppInfo>> {

	private ArrayList<AppInfo> mAppList;// 应用列表集合

	@Override
	public String getKey() {
		return "app";
	}

	@Override
	public String getParams() {
		return "";
	}

	@Override
	public ArrayList<AppInfo> parseJson(String result) {
		try {
			JSONArray ja = new JSONArray(result);
			mAppList = new ArrayList<AppInfo>();
			for (int i = 0; i < ja.length(); i++) {
				AppInfo info = new AppInfo();

				JSONObject jo1 = (JSONObject) ja.get(i);
				info.des = jo1.getString("des");
				info.downloadUrl = jo1.getString("downloadUrl");
				info.iconUrl = jo1.getString("iconUrl");
				info.id = jo1.getString("id");
				info.name = jo1.getString("name");
				info.packageName = jo1.getString("packageName");
				info.size = jo1.getLong("size");
				info.stars = jo1.getDouble("stars");

				mAppList.add(info);
			}

			return mAppList;

		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}
}
