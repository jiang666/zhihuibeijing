package com.itheima.mobliesafe61.service;

import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.graphics.Color;
import android.graphics.PixelFormat;
import android.os.IBinder;
import android.telephony.PhoneStateListener;
import android.telephony.TelephonyManager;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.view.View.OnTouchListener;
import android.widget.TextView;

import com.itheima.mobliesafe61.R;
import com.itheima.mobliesafe61.db.dao.AddressDao;

public class AddressService extends Service {
	/**
	 * 电话的管理者
	 */
	private TelephonyManager telephonyManager;
	private MyTelePhoneStateListener telePhoneStateListener;
	private WindowManager windowManager;
	private TextView textView;
	private View view;
	private MyOutGoingCallReceiver myOutGoingCallReceiver;
	private SharedPreferences sp;
	private WindowManager.LayoutParams params;

	@Override
	public IBinder onBind(Intent intent) {
		// TODO Auto-generated method stub
		return null;
	}

	/**
	 * 监听外拨电话的广播接受者
	 * 
	 * @author Administrator
	 * 
	 */
	private class MyOutGoingCallReceiver extends BroadcastReceiver {

		@Override
		public void onReceive(Context context, Intent intent) {
			// 获取电话
			String resultData = getResultData();// 获取外拨的电话号码
			System.out.println(resultData);
			String queryAddress = AddressDao.queryAddress(context, resultData);
			if (!TextUtils.isEmpty(queryAddress)) {
				showToast(queryAddress);
			}
		}
	}

	@Override
	public void onCreate() {
		super.onCreate();
		sp = getSharedPreferences("config", MODE_PRIVATE);
		// 注册广播接受者
		myOutGoingCallReceiver = new MyOutGoingCallReceiver();// 1.创建广播接受者
		IntentFilter intentFilter = new IntentFilter();// 创建过滤意图
		intentFilter.addAction("android.intent.action.NEW_OUTGOING_CALL");// 添加监听的广播事件
		registerReceiver(myOutGoingCallReceiver, intentFilter);

		telephonyManager = (TelephonyManager) getSystemService(TELEPHONY_SERVICE);
		telePhoneStateListener = new MyTelePhoneStateListener();
		// events : 监听什么事件
		telephonyManager.listen(new MyTelePhoneStateListener(),
				PhoneStateListener.LISTEN_CALL_STATE);// 监听电话的状态
	}

	private class MyTelePhoneStateListener extends PhoneStateListener {
		// state : 电话的状态
		// incomingNumber ： 来电的电话
		@Override
		public void onCallStateChanged(int state, String incomingNumber) {
			super.onCallStateChanged(state, incomingNumber);
			switch (state) {
			case TelephonyManager.CALL_STATE_IDLE:// 电话空闲的状态，挂断电话的状态也是空闲状态
				hideToast();
				break;
			case TelephonyManager.CALL_STATE_RINGING:// 响铃状态
				String queryAddress = AddressDao.queryAddress(
						getApplicationContext(), incomingNumber);
				if (!TextUtils.isEmpty(queryAddress)) {
					// Toast.makeText(getApplicationContext(), queryAddress,
					// 0).show();
					showToast(queryAddress);
				}
				break;
			case TelephonyManager.CALL_STATE_OFFHOOK:// 通话状态
				break;
			default:
				break;
			}
		}
	}

	@Override
	public void onDestroy() {
		super.onDestroy();
		// 注销监听电话的状态
		telephonyManager.listen(telePhoneStateListener,
				PhoneStateListener.LISTEN_NONE);
		// 注销监听外拨电话的广播接受者
		unregisterReceiver(myOutGoingCallReceiver);
	}

	/**
	 * 设置toast的样式
	 */
	public void showToast(String queryAddress) {
		int[] bgcolor = new int[] { R.drawable.btn_circle_pressed,
				R.drawable.btn_circle_pressed, R.drawable.btn_circle_pressed,
				R.drawable.btn_circle_pressed, R.drawable.btn_circle_pressed };
		// 获取屏幕的管理者
		windowManager = (WindowManager) getSystemService(WINDOW_SERVICE);
		view = View.inflate(getApplicationContext(), R.layout.toast_custom,
				null);
		// 根据归属地提示框风格条目保存的数据，设置归属地提示框显示的背景图片
		view.setBackgroundResource(bgcolor[sp.getInt("which", 0)]);
		TextView tv_toastcustom_address = (TextView) view
				.findViewById(R.id.tv_toastcustom_address);
		// textView = new TextView(getApplicationContext());
		tv_toastcustom_address.setText(queryAddress);
		// textView.setTextColor(Color.RED);
		// textView.setTextSize(20);
		setOnTouch();
		//设置toast的属性
		params = new WindowManager.LayoutParams();
		params.height = WindowManager.LayoutParams.WRAP_CONTENT;// 高度包裹内容，-2
		params.width = WindowManager.LayoutParams.WRAP_CONTENT;// 宽度包裹内容
		params.flags = 
				WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE// 没有焦点
//				| WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE// 没有触摸事件
				|
				WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON;// 保持屏幕常亮
		params.format = PixelFormat.TRANSLUCENT;// 半透明效果
		params.type = WindowManager.LayoutParams.TYPE_PRIORITY_PHONE;// 设置控件的类型，TYPE_TOAST：toast类型，toast是天生没有触摸事件的
		params.setTitle("Toast");

		// 到左边
		params.gravity = Gravity.LEFT | Gravity.TOP; // +,同时设置两个属性，但是不能设置两个相对的属性
		params.x = sp.getInt("x", 100);// 不是坐标，相对于params.gravity，如果Gravity.LEFT，x就代表控件距离左边的距离，如果Gravity.RIGHT，x就代表距离右边的位置
		params.y = sp.getInt("y", 100);// 不是坐标,Gravity.TOP,代表距离顶部的距离，Gravity.BOTTOM,代表距离底部的距离

		windowManager.addView(view, params);// params ：控件在布局文件中的参数
	}

	private void setOnTouch() {
		// 给控件添加触摸事件
		view.setOnTouchListener(new OnTouchListener() {
			private int startX;
			private int startY;

			// v ： 当前的控件
			// event ： 触摸事件
			@Override
			public boolean onTouch(View v, MotionEvent event) {
				switch (event.getAction()) {
				case MotionEvent.ACTION_DOWN:// 按下的事件
					// System.out.println("按下了....");
					// 1.记录开始x和y的坐标
					startX = (int) event.getRawX();
					startY = (int) event.getRawY();
					break;
				case MotionEvent.ACTION_MOVE:// 移动的事件
					// System.out.println("移动了....");
					// 2.移动到新的位置，记录新的位置x和y的坐标
					int newX = (int) event.getRawX();
					int newY = (int) event.getRawY();
					// 3.计算偏移量
					int dX = newX - startX;
					int dY = newY - startY;
					// 4.控件移动相应的偏移量，重新分配位置
					params.x+=dX;
					params.y+=dY;
					windowManager.updateViewLayout(view, params);//更新位置
					// 5.更新开始的坐标
					startX = newX;
					startY = newY;
					break;
				case MotionEvent.ACTION_UP:// 抬起的事件
					// System.out.println("抬起了....");
					// 保存控件的坐标
					int x = params.x;
					int y = params.y;
					// 保存坐标
					Editor edit = sp.edit();
					edit.putInt("x", x);
					edit.putInt("y", y);
					edit.commit();
					break;
				}
				// True if the listener has consumed the event, false otherwise.
				// true 表示事件被消费掉了，执行了，false:表示事件被拦截了
				return true;
			}
		});
	}

	/**
	 * 隐藏toast
	 */
	public void hideToast() {
		// 防止不调用showtaost直接调用hideToast,出现windowmanager为null的异常
		if (windowManager != null && view != null) {
			windowManager.removeView(view);
			// 调用showToast之后调用hideToast,又再次调用hideToast，造成提示windowManager没有textview的异常
			windowManager = null;
			view = null;
		}
	}
}
