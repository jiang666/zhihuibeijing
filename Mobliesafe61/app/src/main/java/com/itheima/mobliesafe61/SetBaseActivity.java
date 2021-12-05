package com.itheima.mobliesafe61;

import android.app.Activity;
import android.content.SharedPreferences;
import android.gesture.GestureOverlayView.OnGestureListener;
import android.os.Bundle;
import android.view.GestureDetector;
import android.view.GestureDetector.SimpleOnGestureListener;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Toast;

public abstract class SetBaseActivity extends Activity {
	protected SharedPreferences sp;
	/**
	 * 手势识别器
	 */
	private GestureDetector detector;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		sp = getSharedPreferences("config", MODE_PRIVATE);
		detector = new GestureDetector(this, new MyGestureListener());
	}
	private class MyGestureListener extends SimpleOnGestureListener{
		
		//滑动的事件
		//e1 ：代表按下的事件     保存有按下的坐标
		//e2 :代表抬起的事件    保存有抬起的坐标
		//velocity : 滑动的速率      速度
		@Override
		public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX,
				float velocityY) {
			float startX = e1.getRawX();//获取按下的x坐标
			float endX = e2.getRawX();//获取抬起的x坐标
			float startY = e1.getRawY();//获取按下的Y坐标
			float endY = e2.getRawY();//获取抬起的Y坐标
			//屏蔽斜滑的操作
			if (Math.abs(startY-endY) > 50) {
				Toast.makeText(getApplicationContext(), "你小子又乱滑了！！", 0).show();
				return false;
			}
			//下一步  50 最小实现滑动的距离
			if ((startX-endX) > 50) {
				next_activity();
			}
			//上一步
			if ((endX-startX) > 50) {
				pre_activity();
			}
//			true if the event is consumed, else false
			return true;
		}
	}
	//触摸事件
	@Override
	public boolean onTouchEvent(MotionEvent event) {
		detector.onTouchEvent(event);//触摸事件传递给手势识别器
		return super.onTouchEvent(event);
	}
	public void next(View v){
		next_activity();
	}
	public void pre(View v){
		pre_activity();
	}
	//下一步
	public abstract void next_activity();
	//上一步
	public abstract void pre_activity();
//	@Override
//	public void onBackPressed() {
//		// TODO Auto-generated method stub
//		super.onBackPressed();
//	}
	//物理按键点击的时候调用的方法
	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		//KEYCODE_BACK 返回按键
		if (keyCode == KeyEvent.KEYCODE_BACK) {
			//true:拦截
//			return true;
			pre_activity();
		}
		return super.onKeyDown(keyCode, event);
	}
}
