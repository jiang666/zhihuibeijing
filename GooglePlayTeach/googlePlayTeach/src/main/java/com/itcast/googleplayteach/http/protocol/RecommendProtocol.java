package com.itcast.googleplayteach.http.protocol;

import java.util.ArrayList;

import org.json.JSONArray;

/**
 * 推荐页访问网络
 * 
 * @author Kevin
 * 
 */
public class RecommendProtocol extends BaseProtocol<ArrayList<String>> {

	private ArrayList<String> mRecommendList;// 推荐列表集合

	@Override
	public String getKey() {
		return "recommend";
	}

	@Override
	public String getParams() {
		return "";
	}

	@Override
	public ArrayList<String> parseJson(String result) {
		try {
			JSONArray ja = new JSONArray(result);
			mRecommendList = new ArrayList<String>();
			for (int i = 0; i < ja.length(); i++) {
				String str = ja.getString(i);
				mRecommendList.add(str);
			}

			return mRecommendList;

		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}
}
