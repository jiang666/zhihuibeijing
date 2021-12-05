package com.itheima.mobliesafe61.db.dao;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

public class VirusDao {

	/**
	 * 查找是否是病毒
	 * 
	 * @param context
	 * @param md5
	 * @return
	 */
	public String getDesc(Context context, String md5) {
		String path = "/data/data/" + context.getPackageName() + "/antivirus.db";
		// //openDatabase打开已经存在数据 返回数据库实例
		// SQLiteDatabase db=SQLiteDatabase.openDatabase(path, null, 方式)
		SQLiteDatabase db = SQLiteDatabase.openDatabase(path, null, SQLiteDatabase.OPEN_READONLY);
		String sql = "select desc from datable where md5=?;";
		// 查杀
		Cursor c = db.rawQuery(sql, new String[] { md5 });
		String desc = null;
		if (c.moveToNext()) {
			desc = c.getString(c.getColumnIndex("desc"));
		}
		c.close();
		db.close();
		return desc;
		// select desc from datable where
		// md5='000bbd1eedb946c57a1732f12abadbc7';
	}

	public void add(Context context, String md5, String desc, String type, String packagename) {
		String path = "/data/data/" + context.getPackageName() + "/antivirus.db";
		// //openDatabase打开已经存在数据 返回数据库实例
		// SQLiteDatabase db=SQLiteDatabase.openDatabase(path, null, 方式)
		SQLiteDatabase db = SQLiteDatabase.openDatabase(path, null, SQLiteDatabase.OPEN_READWRITE);
		// //添加增量
		// insert into datable (md5,type,name,desc)
		// values('024b2f447af53cf90ac02dc88de53201',6,'com.itheima.kuaibo','av病毒')
		ContentValues values = new ContentValues();
		values.put("md5", md5);
		values.put("name", packagename);
		values.put("type", type);
		values.put("desc", desc);
		db.insert("datable", "", values);
		db.close();

	}
	public void updateVersion(Context context, String versionNum) {
		String path = "/data/data/" + context.getPackageName() + "/antivirus.db";
		// //openDatabase打开已经存在数据 返回数据库实例
		// SQLiteDatabase db=SQLiteDatabase.openDatabase(path, null, 方式)
		SQLiteDatabase db = SQLiteDatabase.openDatabase(path, null, SQLiteDatabase.OPEN_READWRITE);
		ContentValues values = new ContentValues();
		values.put("subcnt", versionNum);
		// //版本号
		// update version set subcnt=1722;
		db.update("version", values, null, null);
		db.close();

	}

}
