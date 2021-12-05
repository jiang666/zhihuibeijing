package com.itheima.mobliesafe61;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.Bundle;
import android.widget.CheckBox;
import android.widget.CompoundButton;

import com.itheima.mobliesafe61.service.AutoKillProcess;
import com.itheima.mobliesafe61.utils.ServiceUtils;
import com.lidroid.xutils.ViewUtils;
import com.lidroid.xutils.view.annotation.ViewInject;

public class ProcessManagerSetActivity extends Activity {

	@ViewInject(R.id.show_sys_process)
	CheckBox show_sys_process;
	@ViewInject(R.id.time_kill_process)
	CheckBox time_kill_process;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_process_manager_set);
		ViewUtils.inject(this);

		show_sys_process.setOnCheckedChangeListener(new CheckBox.OnCheckedChangeListener() {

			@Override
			public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
				// SharedPreferences sp=getSharedPreferences(文件名, 模式);
				SharedPreferences sp = getSharedPreferences("config", Context.MODE_PRIVATE);
				// 获取编辑器
				Editor editor = sp.edit();
				editor.putBoolean("show_sys_process", isChecked);// 生成保存标签
				editor.commit();
			}
		});
		time_kill_process.setOnCheckedChangeListener(new CheckBox.OnCheckedChangeListener() {

			@Override
			public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
				if (isChecked) {
					startService(new Intent(getBaseContext(), AutoKillProcess.class));
				} else {
					stopService(new Intent(getBaseContext(), AutoKillProcess.class));
				}
			}
		});
	}

	@Override
	protected void onStart() {
		// TODO Auto-generated method stub
		super.onStart();
		// SharedPreferences sp=getSharedPreferences(文件名, 模式);
		SharedPreferences sp = getSharedPreferences("config", Context.MODE_PRIVATE);
		boolean falg = sp.getBoolean("show_sys_process", true);
		show_sys_process.setChecked(falg);

		// Canonical全部
		boolean isRunning = ServiceUtils.isRunningService(this, AutoKillProcess.class.getCanonicalName());
		time_kill_process.setChecked(isRunning);

	}
}
