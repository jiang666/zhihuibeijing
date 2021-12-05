package com.itheima.mobliesafe61;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;

import com.itheima.mobliesafe61.service.AddressService;
import com.itheima.mobliesafe61.service.CallSmsService;
import com.itheima.mobliesafe61.ui.SettingClickView;
import com.itheima.mobliesafe61.ui.SettingView;
import com.itheima.mobliesafe61.utils.ServiceUtils;

public class HomeSetttingActivity extends Activity {

	private SettingView sv_homesetting_update;
	private SharedPreferences sp;
	private SettingView sv_homesetting_address;
	private SettingClickView sv_homesetting_changbg;
	private SettingClickView sv_homesetting_chang_location;
	// -----黑名单拦截开关
	private SettingView call_sms_service;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_homesetting);
		sp = getSharedPreferences("config", MODE_PRIVATE);
		sv_homesetting_update = (SettingView) findViewById(R.id.sv_homesetting_update);
		sv_homesetting_address = (SettingView) findViewById(R.id.sv_homesetting_address);
		sv_homesetting_changbg = (SettingClickView) findViewById(R.id.sv_homesetting_changbg);
		sv_homesetting_chang_location = (SettingClickView) findViewById(R.id.sv_homesetting_chang_location);
		call_sms_service = (SettingView) findViewById(R.id.call_sms_service);

	

		update();
		changbg();
		changLocation();
	}

	/**
	 * 归属地提示框位置
	 */
	private void changLocation() {
		sv_homesetting_chang_location.setTitle("归属地提示框位置");
		sv_homesetting_chang_location.setDes("设置归属地提示框的显示位置");
		sv_homesetting_chang_location.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// 跳转到归属地提示显示位置的界面
				Intent intent = new Intent(HomeSetttingActivity.this, DragViewActivity.class);
				startActivity(intent);
			}
		});
	}

	/**
	 * 归属地提示框风格
	 */
	private void changbg() {
		// 单选框条目的数组
		final String[] items = { "半透明", "活力橙", "卫士蓝", "金属灰", "苹果绿" };
		sv_homesetting_changbg.setTitle("归属地提示框风格");
		sv_homesetting_changbg.setDes(items[sp.getInt("which", 0)]);
		sv_homesetting_changbg.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				AlertDialog.Builder builder = new Builder(HomeSetttingActivity.this);
				builder.setIcon(R.drawable.ic_launcher);
				builder.setTitle("归属地提示框风格");
				// items : 条目的文本组合
				// checkedItem : 选中的选项
				// listener : 点击事件
				builder.setSingleChoiceItems(items, sp.getInt("which", 0), new DialogInterface.OnClickListener() {
					// which : 选中条目
					// 点击单选框条目会触发的事件
					@Override
					public void onClick(DialogInterface dialog, int which) {
						Editor edit = sp.edit();
						edit.putInt("which", which);
						edit.commit();
						// 根据选中的条目设置自定义组合控件的描述信息
						sv_homesetting_changbg.setDes(items[which]);
						dialog.dismiss();
					}
				});
				builder.setNegativeButton("取消", null);
				builder.show();
			}
		});

	}

	// 界面可见调用
	@Override
	protected void onStart() {
		super.onStart();
		address();
		showCallSmsServiceState();
	}

	private void showCallSmsServiceState() {
		//回显状态 
		if (ServiceUtils.isRunningService(this, CallSmsService.class.getCanonicalName())) {
			call_sms_service.setChecked(true);
		} else {
			call_sms_service.setChecked(false);
		}
		call_sms_service.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				Intent service = new Intent(getBaseContext(), CallSmsService.class);
				if (call_sms_service.isChecked()) {
					stopService(service);
					call_sms_service.setChecked(false);
				} else {
					startService(service);
					call_sms_service.setChecked(true);
				}
			}
		});
	}

	
	
	
	
	
	
	/**
	 * 显示号码归属地
	 */
	private void address() {
		// 动态服务开启状态
		if (ServiceUtils.isRunningService(this, "com.itheima.mobliesafe61.service.AddressService")) {
			sv_homesetting_address.setChecked(true);
		} else {
			sv_homesetting_address.setChecked(false);
		}
		sv_homesetting_address.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				Intent intent = new Intent(HomeSetttingActivity.this, AddressService.class);
				if (sv_homesetting_address.isChecked()) {
					// 关闭服务
					stopService(intent);
					sv_homesetting_address.setChecked(false);
				} else {
					// 开启服务
					startService(intent);
					sv_homesetting_address.setChecked(true);
				}
			}
		});
	}

	/**
	 * 提示更新
	 */
	private void update() {
		// sv_homesetting_update.setTitle("提示更新");
		if (sp.getBoolean("update", true)) {
			// sv_homesetting_update.setDes("打开提示更新");
			sv_homesetting_update.setChecked(true);
		} else {
			// sv_homesetting_update.setDes("关闭提示更新");
			sv_homesetting_update.setChecked(false);
		}
		// 提示更新条目的点击事件，自定义组合控件的点击事件
		sv_homesetting_update.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				Editor edit = sp.edit();
				// 判断checkbox的状态，如果是选中，就变成不选中，如果是不选中，就变成选中，同时改变描述信息的值
				if (sv_homesetting_update.isChecked()) {
					// 不选中，关闭提示更新
					sv_homesetting_update.setChecked(false);
					// sv_homesetting_update.setDes("关闭提示更新");
					edit.putBoolean("update", false);
				} else {
					// 选中，打开提示更新
					sv_homesetting_update.setChecked(true);
					// sv_homesetting_update.setDes("打开提示更新");
					edit.putBoolean("update", true);
					// edit.apply();//保存到文件中，但是9版本以上有效，9版本以下，保存在内存当中的
				}
				edit.commit();
			}
		});
	}

}
