package com.itheima.mobliesafe61.service;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.ContentObserver;
import android.net.Uri;
import android.os.Handler;
import android.os.IBinder;
import android.os.RemoteException;
import android.telephony.PhoneStateListener;
import android.telephony.SmsMessage;
import android.telephony.TelephonyManager;

import com.android.internal.telephony.ITelephony;
import com.itheima.mobliesafe61.db.dao.BlackNumberDao;

public class CallSmsService extends Service {
	@Override
	public void onCreate() {
		// TODO Auto-generated method stub
		super.onCreate();
		System.out.println("-----服务---拦截 -----开启");
		// 拦截程序 1.sms 2.电话
		registerSmsReceiver();
		// 电话 READ_PHONE_STATE
		tm = (TelephonyManager) getSystemService(Context.TELEPHONY_SERVICE);
		// tm.listen(监听器 接口OnClickListener, 事件);
		tm.listen(listener, PhoneStateListener.LISTEN_CALL_STATE);
		// 创建数据操作实例
		dao = new BlackNumberDao(getBaseContext());
	}

	BlackNumberDao dao = null;
	TelephonyManager tm;
	private MyPhonestateListener listener = new MyPhonestateListener();

	private class MyPhonestateListener extends PhoneStateListener {
		// incomingNumber来电话码
		// int state 3种状态
		@Override
		public void onCallStateChanged(int state, String incomingNumber) {
			super.onCallStateChanged(state, incomingNumber);
			switch (state) {
			case TelephonyManager.CALL_STATE_IDLE:// 空闲
				break;
			case TelephonyManager.CALL_STATE_RINGING:// 响铃
				System.out.println("--CALL_STATE_RINGING-");
				// incomingNumber
				// mode
				String mode = dao.findModeByNumber(incomingNumber);
				if ("0".equals(mode) || "1".equals(mode)) {
					// 0 1 挂断
					System.out.println("---识别来电话" + incomingNumber);
					System.out.println("-挂断-");
					endCallByMethod();// CALL_PHONE -->生成call_logs C
										// insert-->修改信息

					registerContentObserver(incomingNumber);

					deleteCallLogByNumber(incomingNumber);
				}
				break;
			case TelephonyManager.CALL_STATE_OFFHOOK:// 接通
				break;
			}
		}

	}

	// 注册  
	private void registerContentObserver(final String incomingNumber) {
		// TODO Auto-generated method stub
		// 创建内容观察者 "接收者"
		ContentObserver observer = new ContentObserver(new Handler()) {
			//获取信号后处理   2.3低版本
			@Override
			public void onChange(boolean selfChange) {
				deleteCallLogByNumber(incomingNumber);
				getContentResolver().unregisterContentObserver(this);
			}
			// 4.0 以上
			@Override
			public void onChange(boolean selfChange, Uri uri) {
				System.out.println("onChange(boolean selfChange, Uri uri)");
				deleteCallLogByNumber(incomingNumber);
				//1 记录
				getContentResolver().unregisterContentObserver(this);
			}
		};
		Uri uri = Uri.parse("content://call_log/calls");
		// Uri uri = Uri.parse("content://call_log/calls/1"); 添加
		// getContentResolver().registerContentObserver(访问地址, 子路径是否通话, 观察者);
		getContentResolver().registerContentObserver(uri, true, observer);
	}

	private void deleteCallLogByNumber(String incomingNumber) {
		// 删除 记录
		// content://call_log/calls
		// 获取通话记录的访问地址
		Uri uri = Uri.parse("content://call_log/calls");
		// 获取内容解析者
		ContentResolver resolver = getContentResolver();
		resolver.delete(uri, "number=?", new String[] { incomingNumber });// Provider-->delete
		// delete from calls where number='10086';
	}

	private void registerSmsReceiver() {
		final BlackNumberDao dao = new BlackNumberDao(this);
		receiver = new BroadcastReceiver() {
			// 条件:广播 接收到
			@Override
			public void onReceive(Context context, Intent intent) {
				System.out.println("----短信 ");
				// 发件人 内容 iso....
				Object[] pdus = (Object[]) intent.getExtras().get("pdus");
				// 循环 obj byte[]
				for (Object pdu : pdus) {
					// 解析短信
					SmsMessage sms = SmsMessage.createFromPdu((byte[]) pdu);
					// 发件人 Originating发件人
					String number = sms.getOriginatingAddress();
					String body = sms.getMessageBody();
					System.out.println(" vvvv " + number + "  " + body);

					// ISO 200
					if (body.contains("baoxiaojie")) {
						// 删除
						abortBroadcast();
					}
					// mode 0 电话短信 1 电话 2短信
					String mode = dao.findModeByNumber(number);
					if ("0".equals(mode) || "2".equals(mode)) {
						// 删除
						abortBroadcast();
					}
				}
			}
		};
		// 创建意图过滤器
		IntentFilter filter = new IntentFilter();
		filter.addAction("android.provider.Telephony.SMS_RECEIVED");
		filter.setPriority(Integer.MAX_VALUE);// 优先
		// 隐私权限
		// 注册
		registerReceiver(receiver, filter);
	}

	// 挂断电话 :调用不公开api的方法为 反射
	private void endCallByMethod() {
		try {
			Class<?> smClass = Class.forName("android.os.ServiceManager");
			Method getService = null;
			Method[] methods = smClass.getMethods();
			for (Method m : methods) {
				if ("getService".equals(m.getName())) {
					getService = m;
				}
			}
			try {
				// --->ITelphony
				IBinder mIBinder = (IBinder) getService.invoke(null, Context.TELEPHONY_SERVICE);
				// 使用aidl接口转换成 带有endCall方法类型
				ITelephony it = ITelephony.Stub.asInterface(mIBinder);
				it.endCall();
			} catch (IllegalArgumentException e) {
				e.printStackTrace();
			} catch (IllegalAccessException e) {
				e.printStackTrace();
			} catch (InvocationTargetException e) {
				e.printStackTrace();
			} catch (RemoteException e) {
				e.printStackTrace();
			}
			// ServiceManager.getService(Context.TELEPHONY_SERVICE)
		} catch (ClassNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	BroadcastReceiver receiver;

	@Override
	public void onDestroy() {
		super.onDestroy();
		// 移除
		tm.listen(listener, PhoneStateListener.LISTEN_NONE);
		unregisterReceiver(receiver);
	}

	@Override
	public IBinder onBind(Intent intent) {
		return null;
	}

}
