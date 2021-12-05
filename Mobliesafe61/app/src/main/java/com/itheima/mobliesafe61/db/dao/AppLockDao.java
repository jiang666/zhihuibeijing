package com.itheima.mobliesafe61.db.dao;

import java.util.ArrayList;
import java.util.List;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.net.Uri;

public class AppLockDao {

	// //创建表
	private static final String sql = "create table lockapps (_id integer primary key autoincrement ,packagename text);";
	private static final String TABLE = "lockapps";
	private static final String DB = "lockapps.db";

	/**
	 * 帮助类实例
	 */
	private MyHelper mMyHelper = null;
	private Context context = null;

	public AppLockDao(Context context) {
		super();
		this.context = context;
		mMyHelper = new MyHelper(context);
	}

	private class MyHelper extends SQLiteOpenHelper {

		public MyHelper(Context context) {
			super(context, DB, null, 1);
		}

		@Override
		public void onCreate(SQLiteDatabase db) {
			db.execSQL(sql);

		}

		@Override
		public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
			// TODO Auto-generated method stub

		}

	}

	/**
	 * 加锁
	 * 
	 * @param packagename
	 */

	public void add(String packagename) {
		SQLiteDatabase db = mMyHelper.getWritableDatabase();
		// 创建数据Map
		ContentValues values = new ContentValues();
		values.put("packagename", packagename);
		//
		// //加锁
		// insert into lockapps (packagename)values('com.itheiam.safe61');
		db.insert(TABLE, "", values);
		db.close();
		
		Uri uri=Uri.parse("content://"+AppLockDao.class.getName());
		context.getContentResolver().notifyChange(uri, null);

	}

	/**
	 * 解锁
	 * @param packagename
	 */
	public void delete(String packagename) {
		SQLiteDatabase db = mMyHelper.getWritableDatabase();
		// //解锁
		// delete from lockapps where packagename='com.itheiam.safe61';
		//
		db.delete(TABLE, "packagename=?", new String[] { packagename });
		db.close();
		
		Uri uri=Uri.parse("content://"+AppLockDao.class.getName());
		context.getContentResolver().notifyChange(uri, null);


	}

	/**
	 * 判断是否加锁
	 * 
	 * @param packagename
	 * @return
	 */
	public boolean hasExist(String packagename) {
		SQLiteDatabase db = mMyHelper.getReadableDatabase();
		// //是否被加锁
		// select packagename from lockapps where
		// packagename='com.itheiam.safe62';
		String sql = "select packagename from lockapps where packagename=?;";
		Cursor c = db.rawQuery(sql, new String[] { packagename });

		boolean flag = false;
		if (c.getCount() > 0) {
			flag = true;
		} else {
			flag = false;
		}
		c.close();
		db.close();
		return flag;
	}

	/**
	 * 查询所有数据
	 * @return
	 */
	public List<String> findAll() {
		SQLiteDatabase db = mMyHelper.getReadableDatabase();
		List<String> list = new ArrayList<String>();
		// //是否被加锁
		// select packagename from lockapps ;
		String sql = "select packagename from lockapps ;";
		Cursor c = db.rawQuery(sql, new String[] {});

		while (c.moveToNext()) {
			String packagename = c.getString(c.getColumnIndex("packagename"));
			list.add(packagename);
		}

		c.close();
		db.close();
		return list;
	}
}
