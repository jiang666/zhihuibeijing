package com.itheima.mobliesafe61.db.dao;

import java.util.ArrayList;
import java.util.List;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.itheima.mobliesafe61.bean.NumberInfo;

public class BlackNumberDao {

	private Context context;

	public BlackNumberDao(Context context) {
		super();
		this.context = context;
		mMyHelper = new MyHelper(context);
	}

	// 查询
	public List<NumberInfo> findAll() {
		// 获取数据库实例
		SQLiteDatabase db = mMyHelper.getReadableDatabase();
		// 全查
		// select * from blacknumber order by _id asc ;
		Cursor cursor = db.query(TABLE, null, null, null, null, null, "_id  DESC");
		List<NumberInfo> list = new ArrayList<NumberInfo>();
		while (cursor.moveToNext()) {
			// 获取每个字段
			NumberInfo info = new NumberInfo();
			info.number = cursor.getString(cursor.getColumnIndex("number"));
			info.id = cursor.getString(cursor.getColumnIndex("_id"));
			info.mode = cursor.getString(cursor.getColumnIndex("mode"));
			list.add(info);
		}

		// 关闭资源 IO
		cursor.close();
		db.close();
		return list;
	}
	// 查找号码状态
	public String  findModeByNumber(String number) {
		// 获取数据库实例
		SQLiteDatabase db = mMyHelper.getReadableDatabase();
		// 全查
		// select mode from blacknumber where number='10010111';
		String sql="select mode from blacknumber where number=?;";
		Cursor cursor = db.rawQuery(sql, new String[]{number});
		String mode="-1";
		if (cursor.moveToNext()) {
			mode=cursor.getString(cursor.getColumnIndex("mode"));
		}
		
		// 关闭资源 IO
		cursor.close();
		db.close();
		return mode;
	}
	// 查询分页
	
//	使用 limit关键字的sql语句
//
//	pageMax=5;
//
//	currPage=1;
//
//	limit 每页最大数据  offset 指定开始行
//
//	1    0~4  (currpage-1)*pagemanx    currpage*pagemanx-1
//	2    5~9    5
//	3    10~14
//
//	select * from blacknumber order by _id DESC;
//	select * from blacknumber   order by _id DESC limit 5 offset 0;
//	select * from blacknumber   order by _id DESC limit 5 offset 5;
	public List<NumberInfo> findPageByCurrentIndex(int pagemax,int currpage) {
//		(currpage-1)*pagemanx    currpage*pagemanx-1
		int start=(currpage-1)*pagemax;
		// 获取数据库实例
		SQLiteDatabase db = mMyHelper.getReadableDatabase();
		// 全查
		//select * from blacknumber   order by _id DESC limit 5 offset 5;
		String sql="select * from blacknumber   order by _id DESC limit ? offset ?;";
		Cursor cursor = db.rawQuery(sql, new String[]{pagemax+"",start+""});
		List<NumberInfo> list = new ArrayList<NumberInfo>();
		while (cursor.moveToNext()) {
			// 获取每个字段
			NumberInfo info = new NumberInfo();
			info.number = cursor.getString(cursor.getColumnIndex("number"));
			info.id = cursor.getString(cursor.getColumnIndex("_id"));
			info.mode = cursor.getString(cursor.getColumnIndex("mode"));
			list.add(info);
		}
		// 关闭资源 IO
		cursor.close();
		db.close();
		return list;
	}

	//删除 号码
	public int delete(String id) {
		// 获取数据库实例
		SQLiteDatabase db = mMyHelper.getWritableDatabase();
		// delete from blacknumber where _id=1;
		int count = db.delete(TABLE, "_id=?", new String[] { id });
		db.close();
		return count;
	}

	// 添加
	public boolean add(NumberInfo info) {
		// 获取数据库实例
		SQLiteDatabase db = mMyHelper.getWritableDatabase();
		// 添加
		// insert into blacknumber(number,mode)values('10011','0');
		ContentValues values = new ContentValues();
		values.put("number", info.number);
		values.put("mode", info.mode);
		long row = db.insert(TABLE, "", values);
		db.close();
		if (row == -1) {
			return false;
		} else {
			info.id = row + "";
			return true;
		}

	}

	private MyHelper mMyHelper;
	private static final String DB = "number.db";
	private static final String TABLE = "blacknumber";

	// 帮助类
	class MyHelper extends SQLiteOpenHelper {

		public MyHelper(Context context) {
			super(context, DB, null, 1);
		}

		@Override
		public void onCreate(SQLiteDatabase db) {
			// 发送给数据库要求建表
			db.execSQL("create table blacknumber (_id integer primary key  autoincrement ,number text ,mode text);");
		}

		@Override
		public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
			// TODO Auto-generated method stub

		}

	}

}
