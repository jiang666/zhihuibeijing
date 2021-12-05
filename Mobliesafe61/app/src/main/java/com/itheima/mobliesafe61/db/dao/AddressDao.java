package com.itheima.mobliesafe61.db.dao;

import java.io.File;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

public class AddressDao {
	/**
	 * 查询号码归属地
	 * @param num
	 * @return
	 */
	public static String queryAddress(Context context,String num){
		File file = new File(context.getFilesDir(),"address.db");
		String location="";
		//读取数据库
		//file:///android_asset/address.db  只能打开html文件，不能打开数据库
		//file.getAbsolutePath() ： 打开文件的路径
		//正则表达式
		//^1[34578]\d{9}$
		//身份证    18位     前六位：出生地     往后八位：出生日期	 最后四位：前两位：出生编号   后两位：第一位：性别    奇数男  偶数女     最后一位  ：校验位  x  前17位进行计算得出
		SQLiteDatabase sqLiteDatabase = SQLiteDatabase.openDatabase(file.getAbsolutePath(), null, SQLiteDatabase.OPEN_READONLY);
		if (num.matches("^1[34578]\\d{9}$")) {
			//sql ： sql语句
			//selectionArgs : 条件的参数
			//substring ： 包含头，不包含尾
			Cursor cursor = sqLiteDatabase.rawQuery(
					"select location from data2 where id=(select outkey from data1 where id=?)", 
					new String[]{num.substring(0, 7)});
			if (cursor.moveToNext()) {
				location = cursor.getString(0);
			}	
			//千万别忘记  OOM异常
			cursor.close();
		}else{
			switch (num.length()) {
			case 3://110   120  119
				location = "特殊号码";
				break;
			case 4://5556  5554
				location = "虚拟号码";
				break;
			case 5://10086   10010   100000   95588
				location = "客服电话";
				break;
			case 7://本地，座机电话
			case 8:
				location = "本地电话";
				break;
			default://长途电话        010 1234567  10位     010 12345678  11位        0372 1234567   11位		0372 12345678  12位
				if (num.length() >= 10  && num.startsWith("0")) {
					//区号是三位的情况
					String result = num.substring(1, 3);
					Cursor cursor = sqLiteDatabase.rawQuery(
							"select location from data2 where area=?", 
							new String[]{result});
					if (cursor.moveToNext()) {
						location = cursor.getString(0);
						location = location.substring(0, location.length()-2);
						cursor.close();
					}else{
						//区号是四位的情况
						result = num.substring(1, 4);
						cursor = sqLiteDatabase.rawQuery(
								"select location from data2 where area=?", 
								new String[]{result});
						if (cursor.moveToNext()) {
							location = cursor.getString(0);
							location = location.substring(0, location.length()-2);
							cursor.close();
						}
					}
				}
				break;
			}
		}
		sqLiteDatabase.close();
		return location;
	}
}
