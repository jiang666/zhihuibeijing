package com.itheima.mobliesafe61;

import android.app.Activity;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.Bundle;
import android.os.SystemClock;
import android.util.DisplayMetrics;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.view.WindowManager;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.RelativeLayout.LayoutParams;

public class DragViewActivity extends Activity {

	private LinearLayout ll_dragview_address;
	private SharedPreferences sp;
	private int width;
	private int height;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_dragview);
		sp = getSharedPreferences("config", MODE_PRIVATE);
		ll_dragview_address = (LinearLayout) findViewById(R.id.ll_dragview_address);
		tv_dragview_bottom = (TextView) findViewById(R.id.tv_dragview_bottom);
		tv_dragview_top = (TextView) findViewById(R.id.tv_dragview_top);

		int l = sp.getInt("x", 0);
		int t = sp.getInt("y", 0);
		System.out.println("x:"+l+"  y:"+t);
		
//		int width = ll_dragview_address.getWidth();
//		int height = ll_dragview_address.getHeight();
//		System.out.println("width："+width+"   height:"+height);
//		
//		//绘制控件
//		ll_dragview_address.layout(l, t, l+width, t+height);//重新绘制控件
		
		//重新设置控件的位置
		//获取params
		RelativeLayout.LayoutParams params= (LayoutParams) ll_dragview_address.getLayoutParams();//获取控件在布局文件的参数
		params.leftMargin=l;//设置控件距离界面左边框的距离
		params.topMargin=t;//设置控件距离界面顶部的距离
		ll_dragview_address.setLayoutParams(params);//给控件设置相应的属性
		
		//获取屏幕的宽度
		WindowManager windowManager = (WindowManager) getSystemService(WINDOW_SERVICE);
//		windowManager.getDefaultDisplay().getWidth();//获取屏幕宽度
		DisplayMetrics outMetrics = new DisplayMetrics();//白纸
		windowManager.getDefaultDisplay().getMetrics(outMetrics);//给白纸添加宽高
		width = outMetrics.widthPixels;
		height = outMetrics.heightPixels;
		
		if (t > height/2) {
			//顶部的显示，底部隐藏
			tv_dragview_bottom.setVisibility(View.INVISIBLE);//不能使用GONE属性，会使用控件的位置无法确定
			tv_dragview_top.setVisibility(View.VISIBLE);
		}else{
			//顶部隐藏，底部显示
			tv_dragview_top.setVisibility(View.INVISIBLE);
			tv_dragview_bottom.setVisibility(View.VISIBLE);
		}
		
		changeLocation();
		doubleClick();
	}
	//第一次单击的时间
//	long firstTime = 0;
	long[] mHits = new long[2];
	private TextView tv_dragview_bottom;
	private TextView tv_dragview_top;
	/**
	 * 双击居中
	 * 实际上就是两次单击，只不过需要判断两次单击的间隔时间
	 */
	private void doubleClick() {
		ll_dragview_address.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
//				if (firstTime == 0) {
//					firstTime = System.currentTimeMillis();//获取第一次单击的时间  		500
//					new Thread(){
//						public void run() {
//							SystemClock.sleep(500);//控制第一次点击和第二次点击之间的时间，如果大于500，就表示你放弃执行双击操作，会重新初始化双击事件
//							firstTime = 0;
//						};
//					}.start();
//				}else{
//					long secondTime = System.currentTimeMillis();//获取第二次单击的时间	800
//					if (secondTime - firstTime < 500) {
//						System.out.println("我被双击了");
//						firstTime = 0;
//					}
//				}
				System.arraycopy(mHits, 1, mHits, 0, mHits.length-1);
		         mHits[mHits.length-1] = SystemClock.uptimeMillis();//获取距离开机时间  500   
		         if (mHits[0] >= (SystemClock.uptimeMillis()-500)) {
//		        	 System.out.println("我被双击了....");
		        	 int l = (width-ll_dragview_address.getWidth())/2;//得到屏幕宽度中间的位置
		        	 int t = (height-25-ll_dragview_address.getHeight())/2;//得到屏幕高度中间的位置
		        	 //重新绘制控件
		        	 ll_dragview_address.layout(l, t, l+ll_dragview_address.getWidth(), t+ll_dragview_address.getHeight());
		        	 
		        	 Editor edit = sp.edit();
		        	 edit.putInt("x", l);
		        	 edit.putInt("y", t);
		        	 edit.commit();
		         }
			}
		});
	}

	private void changeLocation() {
		//给控件添加触摸事件
		ll_dragview_address.setOnTouchListener(new OnTouchListener() {
			private int startX;
			private int startY;

			//v ： 当前的控件
			//event ： 触摸事件
			@Override
			public boolean onTouch(View v, MotionEvent event) {
				switch (event.getAction()) {
				case MotionEvent.ACTION_DOWN://按下的事件
//					System.out.println("按下了....");
					//1.记录开始x和y的坐标
					startX = (int) event.getRawX();
					startY = (int) event.getRawY();
					break;
				case MotionEvent.ACTION_MOVE://移动的事件
//					System.out.println("移动了....");
					//2.移动到新的位置，记录新的位置x和y的坐标
					int newX = (int) event.getRawX();
					int newY = (int) event.getRawY();
					//3.计算偏移量
					int dX = newX-startX;
					int dY = newY-startY;
					//4.控件移动相应的偏移量，重新分配位置
					//获取控件移动前的距离左边和顶部距离
					int l = ll_dragview_address.getLeft();//获取控件距离左边的距离
					int t = ll_dragview_address.getTop();//获取控件距离顶部的距离
					l+=dX;
					t+=dY;
					int r = l+ll_dragview_address.getWidth();
					int b = t+ll_dragview_address.getHeight();
					//防止控件移出屏幕
					if (l < 0 || r > width || t < 0 || b > height-25) {
						break;
					}
					//l:距离左边的位置
					//t:距离听不的位置
					//r：控件的右边框距离界面的左边框的距离
					//b:控件的底边框距离界面的顶部的距离
					ll_dragview_address.layout(l, t, r, b);//重新绘制控件
					int top = ll_dragview_address.getTop();
					if (top > height/2) {
						//顶部的显示，底部隐藏
						tv_dragview_bottom.setVisibility(View.INVISIBLE);//不能使用GONE属性，会使用控件的位置无法确定
						tv_dragview_top.setVisibility(View.VISIBLE);
					}else{
						//顶部隐藏，底部显示
						tv_dragview_top.setVisibility(View.INVISIBLE);
						tv_dragview_bottom.setVisibility(View.VISIBLE);
					}
					//5.更新开始的坐标
					startX = newX;
					startY = newY;
					break;
				case MotionEvent.ACTION_UP://抬起的事件
//					System.out.println("抬起了....");
					//保存控件的坐标
					int x = ll_dragview_address.getLeft();
					int y = ll_dragview_address.getTop();
					//保存坐标
					Editor edit = sp.edit();
					edit.putInt("x", x);
					edit.putInt("y", y);
					edit.commit();
					break;
				}
				//True if the listener has consumed the event, false otherwise.
				//true 表示事件被消费掉了，执行了，false:表示事件被拦截了
				return false;
			}
		});
	}

}
