package com.itheima.mobliesafe61;

import com.lidroid.xutils.ViewUtils;
import com.lidroid.xutils.view.annotation.ViewInject;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences.Editor;
import android.os.Bundle;
import android.view.View;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;

public class SetUp4Activity extends SetBaseActivity {
	@ViewInject(R.id.eb_setup4_protected)
	private CheckBox eb_setup4_protected;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_setup4);
		ViewUtils.inject(this);//findviewbyid放在那里，viewUtils.inject()就放在那里
		if (sp.getBoolean("protected", false)) {
			eb_setup4_protected.setChecked(true);
			eb_setup4_protected.setText("你已经开启防盗保护");
		}else{
			eb_setup4_protected.setChecked(false);
			eb_setup4_protected.setText("你还没有开启防盗保护");
		}
		//监听checkbox状态改变的监听
		eb_setup4_protected.setOnCheckedChangeListener(new OnCheckedChangeListener() {
			//CompoundButton ： checkBox
			//isChecked ： checkBox改变过的状态
			@Override
			public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
				Editor edit = sp.edit();
				if (isChecked) {
					//开启防盗保护
					eb_setup4_protected.setText("你已经开启防盗保护");
					edit.putBoolean("protected", true);
				}else{
					//关闭防盗保护
					eb_setup4_protected.setText("你还没有开启防盗保护");
					edit.putBoolean("protected", false);
				}
				edit.commit();
			}
		});
	}
	/**
	 * 下一步
	 */
	@Override
	public void next_activity() {
		
		Editor editor = sp.edit();
		editor.putBoolean("first", false);
		editor.commit();
		
		Intent intent = new Intent(this,LostFindActivity.class);
		startActivity(intent);
		finish();
		overridePendingTransition(R.anim.resetup_next_enter, R.anim.resetup_next_exit);
	}
	/**
	 * 上一步
	 */
	@Override
	public void pre_activity() {
		Intent intent = new Intent(this,SetUp3Activity.class);
		startActivity(intent);
		finish();
		overridePendingTransition(R.anim.resetup_pre_enter, R.anim.resetup_pre_exit);
	}
	
}
