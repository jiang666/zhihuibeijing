package com.itheima.mobliesafe61;

import android.app.Activity;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.lidroid.xutils.ViewUtils;
import com.lidroid.xutils.view.annotation.ViewInject;
import com.lidroid.xutils.view.annotation.event.OnClick;

public class PwdEnterActivity extends Activity {

	@ViewInject(R.id.iv_lock_appicon)
	ImageView icon;
	@ViewInject(R.id.tv_lock_appname)
	TextView appname;
	@ViewInject(R.id.et_password)
	EditText pwd;

	// @ViewInject(R.id.unlock)
	// Button unlock;
	@OnClick(R.id.unlock)
	public void unlock(View view) {

		if ("123".equals(pwd.getText().toString().trim())) {

			Intent intent = new Intent();
			intent.putExtra("packagename", packagename);
			intent.setAction("com.itheima.dog.letgo");
			sendBroadcast(intent);
			finish();
		}
	}

	String packagename;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_enter_pwd);
		ViewUtils.inject(this);
		Intent intent = getIntent();
		// 参数
		packagename = intent.getStringExtra("packagename");
		PackageManager pm = getPackageManager();
		try {
			ApplicationInfo info = pm.getApplicationInfo(packagename, 0);
			Drawable appicon = info.loadIcon(pm);
			String name = info.loadLabel(pm).toString();
			appname.setText(name);
			icon.setImageDrawable(appicon);
		} catch (NameNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

	// 条件：物理按键

	// @Override
	// public void onBackPressed() {
	// // TODO Auto-generated method stub
	// super.onBackPressed();
	// }
	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_BACK) {
			// 隐式意图
			Intent intent = new Intent();
			// <intent-filter>
			// <action android:name="android.intent.action.MAIN" />
			// <category android:name="android.intent.category.HOME" />
			// <category android:name="android.intent.category.DEFAULT" />
			// <category android:name="android.intent.category.MONKEY"/>
			// </intent-filter>
			intent.setAction("android.intent.action.MAIN");
			intent.addCategory("android.intent.category.HOME");
			intent.addCategory("android.intent.category.DEFAULT");
			intent.addCategory("android.intent.category.MONKEY");
			startActivity(intent);
			// 打开桌面
			return true;// 处理事件
		}
		return super.onKeyDown(keyCode, event);
	}
}
