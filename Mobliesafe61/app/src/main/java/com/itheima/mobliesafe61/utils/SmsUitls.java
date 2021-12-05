package com.itheima.mobliesafe61.utils;

import java.io.File;
import java.io.FileOutputStream;

import org.xmlpull.v1.XmlSerializer;

import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.os.Environment;
import android.util.Xml;

public class SmsUitls {

	public static void backup(Context context, OnProgressListener listener) throws Exception {
		// try {
		if (Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {
			// mnt/sdcard0
			File xmlFile = new File(Environment.getExternalStorageDirectory().getAbsolutePath() + "/sms.xml");
			// if (xmlFile.exists()) {
			xmlFile.createNewFile();
			// 创建 序列化器
			XmlSerializer writer = Xml.newSerializer();
			// 设置文件
			writer.setOutput(new FileOutputStream(xmlFile), "UTF-8");
			// }
			// 文件编码
			int progressValue = 0;
			writer.startDocument("utf-8", true);
			writer.startTag(null, "sms-list");
			Uri uri = Uri.parse("content://sms");
			// //全查
			// }

			// select body,date,address,type from sms ;
			Cursor cursor = context.getContentResolver().query(uri, new String[] { "body", "address", "date", "type" }, null, null, null);
			int total = cursor.getCount();// 总数
			writer.attribute(null, "count", total + "");// count="100";
			while (cursor.moveToNext()) {
				writer.startTag(null, "sms");
				// 每读取一条短信 。生成xml标签 同步UI
				String body = cursor.getString(cursor.getColumnIndex("body"));
				String address = cursor.getString(cursor.getColumnIndex("address"));
				String date = cursor.getString(cursor.getColumnIndex("date"));
				String type = cursor.getString(cursor.getColumnIndex("type"));
				//

				writer.startTag(null, "body");
				writer.text(body);
				writer.endTag(null, "body");
				writer.startTag(null, "address");
				writer.text(address);
				writer.endTag(null, "address");
				writer.startTag(null, "date");
				writer.text(date);
				writer.endTag(null, "date");
				writer.startTag(null, "type");
				writer.text(type);
				writer.endTag(null, "type");
				writer.endTag(null, "sms");
				try {
					Thread.sleep(100);
					++progressValue;
					if (listener != null) {
						listener.onProgressChanged(progressValue, total);
					}

				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}
			cursor.close();
			writer.endTag(null, "sms-list");
			writer.endDocument();// 操作结束
//			
//			if (listener != null) {
//				listener.onFinish(progressValue, total);
//			}
			// } catch (Exception e) {
			// e.printStackTrace();
			// }
		}
	}

	public static interface OnProgressListener {

		public void onProgressChanged(int progressValue, int total);
//		public void onFinish(int progressValue, int total);
	}
	// {
	// publishProgress(progressValue);
	// }
}