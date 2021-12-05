package com.itheima.mobliesafe61;

import android.app.Activity;
import android.content.ContentResolver;
import android.content.Intent;
import android.content.SharedPreferences.Editor;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

public class SetUp3Activity extends SetBaseActivity {
	
	private EditText et_setup3_safenum;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_setup3);
		et_setup3_safenum = (EditText) findViewById(R.id.et_setup3_safenum);
		et_setup3_safenum.setText(sp.getString("safenum", ""));
	}
	@Override
	public void next_activity() {
		
		//获取输入的安全号码
		String phone = et_setup3_safenum.getText().toString().trim();
		if (TextUtils.isEmpty(phone)) {
			Toast.makeText(getApplicationContext(), "请输入安全号码", 0).show();
			return;
		}
		//不为空，保存安全号码
		Editor edit = sp.edit();
		edit.putString("safenum", phone);
		edit.commit();
		
		Intent intent = new Intent(this,SetUp4Activity.class);
		startActivity(intent);
		finish();
		overridePendingTransition(R.anim.resetup_next_enter, R.anim.resetup_next_exit);
	}
	@Override
	public void pre_activity() {
		Intent intent = new Intent(this,SetUp2Activity.class);
		startActivity(intent);
		finish();
		overridePendingTransition(R.anim.resetup_pre_enter, R.anim.resetup_pre_exit);
	}
	public void selectContacts(View v){
//		Intent intent = new Intent(this,ContactsActivity.class);
//		//当当前的activity退出的时候，回去调用以前activity的onActivityResult方法
//		startActivityForResult(intent, 0);
		Intent intent = new Intent();
		intent.setAction("android.intent.action.PICK");
		intent.addCategory("android.intent.category.DEFAULT");
		intent.setType("vnd.android.cursor.dir/phone_v2");
		startActivityForResult(intent, 1);
	}
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
//		if (data!=null) {
//			String num = data.getStringExtra("num");//数据为空了，null.方法
//			et_setup3_safenum.setText(num);
//		}
		if(data !=null){
			//	String num = data.getStringExtra("num");
			Uri uri = data.getData();
			String num = null;
			// 创建内容解析者
					ContentResolver contentResolver = getContentResolver();
					Cursor cursor = contentResolver.query(uri,
							null, null, null, null);
					while(cursor.moveToNext()){
						num = cursor.getString(cursor.getColumnIndex("data1"));
						
					}
					cursor.close();
					num = num.replaceAll("-", "");
					et_setup3_safenum.setText(num);	
		}
	}
}
