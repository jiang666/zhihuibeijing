package com.itheima.mobliesafe61;

import com.itheima.mobliesafe61.ui.SettingView;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences.Editor;
import android.os.Bundle;
import android.telephony.TelephonyManager;
import android.text.TextUtils;
import android.view.View;
import android.view.View.OnClickListener;

public class SetUp2Activity extends SetBaseActivity {
	
	private SettingView sv_setup2_sim;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_setup2);
		sv_setup2_sim = (SettingView) findViewById(R.id.sv_setup2_sim);
		
		if (TextUtils.isEmpty(sp.getString("sim", ""))) {
			sv_setup2_sim.setChecked(false);
		}else{
			sv_setup2_sim.setChecked(true);
		}
		
		sv_setup2_sim.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				Editor edit = sp.edit();
				//获取checkebox的状态
				if (sv_setup2_sim.isChecked()) {
					//解绑
					edit.putString("sim", "");
					sv_setup2_sim.setChecked(false);
				}else{
					//绑定sim卡
					//获取sim卡号
					//电话的管理者
					TelephonyManager telephonyManager = (TelephonyManager) getSystemService(TELEPHONY_SERVICE);
//					telephonyManager.getLine1Number();//获取sim卡绑定的手机号，Line1：双卡双待的问题，在中国，不太好用，中国的运营商一般不会让sim卡和手机号绑定，不太适用
					String sim = telephonyManager.getSimSerialNumber();//获取sim卡的串号，sim卡唯一标示
					//保存sim卡号
					edit.putString("sim", sim);
					//改变自定义组合控件的状态
					sv_setup2_sim.setChecked(true);
				}
				edit.commit();
			}
		});
	}
	@Override
	public void next_activity() {
		Intent intent = new Intent(this,SetUp3Activity.class);
		startActivity(intent);
		finish();	
		overridePendingTransition(R.anim.resetup_next_enter, R.anim.resetup_next_exit);
	}
	@Override
	public void pre_activity() {
		Intent intent = new Intent(this,SetUp1Activity.class);
		startActivity(intent);
		finish();
		overridePendingTransition(R.anim.resetup_pre_enter, R.anim.resetup_pre_exit);
	}
}
