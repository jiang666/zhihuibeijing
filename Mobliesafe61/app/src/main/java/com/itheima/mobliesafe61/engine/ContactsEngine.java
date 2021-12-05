package com.itheima.mobliesafe61.engine;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import android.content.ContentResolver;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.os.SystemClock;

public class ContactsEngine {
	/**
	 * 获取联系人
	 * 
	 * @return
	 */
	public static List<HashMap<String, String>> getAllContacts(Context context) {
		SystemClock.sleep(3000);
		ArrayList<HashMap<String, String>> list = new ArrayList<HashMap<String, String>>();
		// 获取内容解析者
		ContentResolver contentResolver = context.getContentResolver();
		// 获取内容提供者地址 ：com.android.contacts www.baidu.com/jdk
		// raw_contact地址：raw_contacts
		// view_data地址：data
		// 生成查询地址
		Uri raw_uri = Uri.parse("content://com.android.contacts/raw_contacts");// http://；content://代表内容提供者的协议
		Uri data_uri = Uri.parse("content://com.android.contacts/data");
		// 根据地址查询相应的数据
		Cursor cursor = contentResolver.query(raw_uri,
				new String[] { "contact_id" }, null, null, null);
		// 解析cursor获取数据
		while (cursor.moveToNext()) {
			// 获取数据
			String contact_id = cursor.getString(0);
			// cursor.getString(cursor.getColumnIndex("contact_id"));
			// 根据contact_id查询view_data表中的数据
			if (contact_id != null) {
				Cursor c = contentResolver.query(data_uri, new String[] {
						"data1", "mimetype" }, "contact_id=?",
						new String[] { contact_id }, null);
				HashMap<String, String> map = new HashMap<String, String>();
				// 解析c获取数据
				while (c.moveToNext()) {
					// 获取数据
					String data1 = c.getString(0);
					String mimetype = c.getString(1);
					// 因为不知道data1是什么类型，所以要根据mimetype进行判断data1的数据类型是否为姓名或者电话号码
					if (mimetype.equals("vnd.android.cursor.item/name")) {
						// 姓名
						map.put("name", data1);
					} else if (mimetype
							.equals("vnd.android.cursor.item/phone_v2")) {
						// 电话
						map.put("phone", data1);
					}
					System.out.println("data1:" + data1 + " mimetype:"
							+ mimetype);
				}
				list.add(map);
				c.close();
			}
		}
		cursor.close();
		return list;
	}
}
