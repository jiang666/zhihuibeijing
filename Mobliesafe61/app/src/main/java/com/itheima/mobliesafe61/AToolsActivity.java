package com.itheima.mobliesafe61;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

import org.xmlpull.v1.XmlSerializer;

import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.util.Xml;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.itheima.mobliesafe61.utils.SmsUitls;
import com.itheima.mobliesafe61.utils.SmsUitls.OnProgressListener;
import com.lidroid.xutils.ViewUtils;
import com.lidroid.xutils.view.annotation.ViewInject;

public class AToolsActivity extends Activity {

	@ViewInject(R.id.progresstext)
	TextView progresstext;
	@ViewInject(R.id.progress)
	ProgressBar progress;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_atools);
		ViewUtils.inject(this);

	}

	public void query(View v) {
		Intent intent = new Intent(this, AddressActivity.class);
		startActivity(intent);
	}

	public void applock(View v) {
		Intent intent = new Intent(this, AppLockActivity.class);
		startActivity(intent);
	}

	private boolean isRunning = false;

	public void backup(View v) {
		if (isRunning) {
			return;
		}
		isRunning = true;
		// 任务开始
		// 第二个参数 表示进度值
		new AsyncTask<Void, Integer, Void>() {

			private String smsXml = "";
			private XmlSerializer writer = null;

			protected void onPreExecute() {
				progresstext.setVisibility(View.VISIBLE);
				progress.setVisibility(View.VISIBLE);
				progress.setMax(100);
				progress.setProgress(0);
				progresstext.setText("0%");

			};

			// 任务进行中

			private int smsprogressValue = 0;
			int smstotal = 0;

			@Override
			protected Void doInBackground(Void... params) {

				OnProgressListener listener = new OnProgressListener() {
					@Override
					public void onProgressChanged(int progressValue, int total) {
						smstotal = total;
						smsprogressValue = progressValue;
						publishProgress(progressValue);// handler .....
					}
					// @Override
					// public void onFinish(int progressValue, int total) {
					//
					// }
				};
				try {
					SmsUitls.backup(getBaseContext(), listener);
				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				return null;
			}

			// 更新进度
			protected void onProgressUpdate(Integer[] values) {
				progress.setMax(smstotal);
				progress.setProgress(values[0]);
				progresstext.setText((values[0] * 100 / smstotal) + "%");
			};

			// 任务完成
			protected void onPostExecute(Void result) {
				progress.setVisibility(View.GONE);
				progresstext.setVisibility(View.GONE);
				Toast.makeText(getBaseContext(), "备份完成" + smsprogressValue + " 条短信", 0).show();
				isRunning = false;
			};

			// 任务结束
		}.execute();// start
	}
}
