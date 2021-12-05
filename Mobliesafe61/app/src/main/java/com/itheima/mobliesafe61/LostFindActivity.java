package com.itheima.mobliesafe61;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

public class LostFindActivity extends Activity {
	private SharedPreferences sp;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		sp = getSharedPreferences("config", MODE_PRIVATE);
		if (sp.getBoolean("first", true)) {
			//true : 表示是第一次进入，还没有进行任何设置，需要跳转设置向导界面进行功能设置
			Intent intent = new Intent(this,SetUp1Activity.class);
			startActivity(intent);
			finish();
		}else{
			//false  已经进行了设置，会进入手机防盗界面，显示设置功能
			setContentView(R.layout.activity_lostfind);
			TextView tv_lostfind_safenum = (TextView) findViewById(R.id.tv_lostfind_safenum);
			ImageView iv_lostfind_protectedimageview = (ImageView) findViewById(R.id.iv_lostfind_protectedimageview);
			tv_lostfind_safenum.setText(sp.getString("safenum", ""));//设置安全号码
			//获取防盗保护是否开启的状态
			boolean protected_sp = sp.getBoolean("protected", false);
			if (protected_sp) {
				iv_lostfind_protectedimageview.setImageResource(R.drawable.btn_circle_pressed);
			}else{
				iv_lostfind_protectedimageview.setImageResource(R.drawable.btn_circle_pressed);
			}
		}
	}
	public void tosetup(View v){
		Intent intent = new Intent(this,SetUp1Activity.class);
		startActivity(intent);
		finish();
	}
}
