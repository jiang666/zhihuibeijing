package com.itheima.mobliesafe61;

import android.content.ComponentName;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentTransaction;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.TextView;

import com.itheima.mobliesafe61.fragments.AppLockFragment;
import com.itheima.mobliesafe61.fragments.AppUnLockFragment;
import com.itheima.mobliesafe61.service.WatchDogService;
import com.lidroid.xutils.ViewUtils;
import com.lidroid.xutils.view.annotation.ViewInject;

//FragmentActivity如果页面包含Fragment必须是FragmentActivity
public class AppLockActivity extends FragmentActivity {

	@ViewInject(R.id.tab_lock)
	TextView tab_lock;
	@ViewInject(R.id.tab_unlock)
	TextView tab_unlock;

	private AppLockFragment applockFragment = new AppLockFragment();
	private AppUnLockFragment appunlockFragment = new AppUnLockFragment();

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_applock);
		ViewUtils.inject(this);
		
		
		ComponentName name=new ComponentName(this,WatchDogService.class.getName());
		Intent intent=new Intent();
		intent.setComponent(name);
		startService(intent);

		initUi();
	}

	//初始化界面
	private void initUi() {
		OnClickListener listener = new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				switch (v.getId()) {
				case R.id.tab_unlock:
					tab_unlock.setEnabled(false);
					tab_lock.setEnabled(true);
					System.out.println("--tab_unlock-");
					
					// 未加锁界面Support-->Default
					FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
					// ft.replace(显示Fragment的布局, 片段)
					ft.replace(R.id.fragment_layout, appunlockFragment);
					ft.commit();// 操作生效

					break;
				case R.id.tab_lock:
					tab_unlock.setEnabled(true);
					tab_lock.setEnabled(false);
					System.out.println("--tab_lock-");
					
					// 未加锁界面Support-->Default
					FragmentTransaction ft2 = getSupportFragmentManager().beginTransaction();
					// ft.replace(显示Fragment的布局, 片段)
					ft2.replace(R.id.fragment_layout, applockFragment);
					ft2.commit();// 操作生效
					break;
				}

			}
		};
		tab_unlock.setEnabled(false);
		tab_lock.setOnClickListener(listener);
		tab_unlock.setOnClickListener(listener);

		// 未加锁界面Support-->Default
		FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
		// ft.add(显示Fragment的布局, 片段)
		ft.add(R.id.fragment_layout, appunlockFragment);
		ft.commit();// 操作生效
	}
}
