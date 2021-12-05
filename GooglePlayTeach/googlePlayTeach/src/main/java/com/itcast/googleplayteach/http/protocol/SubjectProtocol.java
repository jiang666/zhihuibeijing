package com.itcast.googleplayteach.http.protocol;

import java.util.ArrayList;

import org.json.JSONArray;
import org.json.JSONObject;

import com.itcast.googleplayteach.domain.SubjectInfo;

/**
 * 主题页访问网络
 * 
 * @author Kevin
 * 
 */
public class SubjectProtocol extends BaseProtocol<ArrayList<SubjectInfo>> {

	private ArrayList<SubjectInfo> mSubjectList;// 主题列表集合

	@Override
	public String getKey() {
		return "subject";
	}

	@Override
	public String getParams() {
		return "";
	}

	@Override
	public ArrayList<SubjectInfo> parseJson(String result) {
		try {
			JSONArray ja = new JSONArray(result);
			mSubjectList = new ArrayList<SubjectInfo>();
			for (int i = 0; i < ja.length(); i++) {
				SubjectInfo info = new SubjectInfo();

				JSONObject jo1 = (JSONObject) ja.get(i);
				info.des = jo1.getString("des");
				info.url = jo1.getString("url");

				mSubjectList.add(info);
			}

			return mSubjectList;

		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}
}
