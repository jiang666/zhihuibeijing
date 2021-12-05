package com.itheima.mobliesafe61;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;

public class SetUp1Activity extends SetBaseActivity {
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_setup1);
	}
	/**
	 * 下一步
	 */
	@Override
	public void next_activity() {
		//跳转到第二界面
		Intent intent = new Intent(this,SetUp2Activity.class);
		startActivity(intent);
		finish();
		//enterAnim : 新的界面进入的动画
		//exitAnim : 旧的界面移出的动画
		overridePendingTransition(R.anim.resetup_next_enter, R.anim.resetup_next_exit);
	}
	/**
	 * 上一步
	 */
	@Override
	public void pre_activity() {
		
	}
}
