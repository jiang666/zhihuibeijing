package com.itheima.mobliesafe61.receiver;

import com.itheima.mobliesafe61.R;
import com.itheima.mobliesafe61.service.GPSService;

import android.app.admin.DevicePolicyManager;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.sax.StartElementListener;
import android.telephony.SmsManager;
import android.telephony.SmsMessage;
import android.text.TextUtils;

public class SmsReceiver extends BroadcastReceiver {
	private SharedPreferences sp;
	@Override
	public void onReceive(Context context, Intent intent) {
		sp = context.getSharedPreferences("config", Context.MODE_PRIVATE);
		//获取设备管理者
		DevicePolicyManager devicePolicyManager = (DevicePolicyManager) context.getSystemService(Context.DEVICE_POLICY_SERVICE);
		//获取组件的标示
		ComponentName componentName = new ComponentName(context, AdminReceiver.class);
		//监听短信到来，解析短信的内容
		//pdus : 短信的协议，代表的短信
		//70汉字代表一条短信，75汉字，两条短信
		//接收多条短信
		Object[] objs = (Object[]) intent.getExtras().get("pdus");
		for(Object obj:objs){
			//将接收的短信转换成短信的对象
			SmsMessage smsMessage = SmsMessage.createFromPdu((byte[]) obj);
			//获取短信的内容
			String body = smsMessage.getMessageBody();
			//获取短信的发件人，真机测试的一定要进行发件人的判断
			String sender = smsMessage.getOriginatingAddress();
			System.out.println("发件人："+sender+"   短信的内容："+body);
			//注意，判断发件人是否为设置的安全号码，避免随随便便接受一条短信接执行相应的操作
			if ("#*location*#".equals(body)) {
				//gps追踪
				//定位操作  
				//android规定：在onReceive方法中执行的操作，不能超过10秒钟，超过10秒钟，android系统就不会执行了，会接着执行下面的操作
				//开启定位服务
				Intent intent_gps = new Intent(context,GPSService.class);
				context.startService(intent_gps);
				//获取经纬度坐标
				String longitude = sp.getString("longitude", "");
				String latitude = sp.getString("latitude", "");
				//发送短信,因为用户第一次发送指令的时候会发送空的坐标，所以做为空判断
				if (!TextUtils.isEmpty(longitude) && !TextUtils.isEmpty(latitude)) {
					SmsManager manager = SmsManager.getDefault();//获取短息管理者
					manager.sendTextMessage(sp.getString("safenum", "5556"), null, "longitude:"+longitude+";latitude:"+latitude, null, null);
				}
				System.out.println("gps追踪");
			}else if("#*alarm*#".equals(body)){
				//播放报警音乐
				System.out.println("播放报警音乐");
				//先把手机的音量调到最大
				AudioManager audioManager = (AudioManager) context.getSystemService(Context.AUDIO_SERVICE);//音量的管理者
				//设置系统音量
				//streamType : 音乐的类型
				//index ： 音量大小   0最小		15最大  	audioManager.getStreamMaxVolume(streamType):获取系统最大的音量,streamType音乐的类型
				//flags ： 指定权限
				audioManager.setStreamVolume(AudioManager.STREAM_MUSIC, audioManager.getStreamMaxVolume(AudioManager.STREAM_MUSIC),AudioManager.ADJUST_LOWER);
				//准备音乐资源
				MediaPlayer mediaPlayer = MediaPlayer.create(context, R.raw.ylzs);
				//播放音乐
				mediaPlayer.start();
			}else if("#*wipedata*#".equals(body)){
				//远程删除数据
				System.out.println("远程删除数据");
				//判断超级管理员是否激活
				if (devicePolicyManager.isAdminActive(componentName)) {
					//擦除数据
					devicePolicyManager.wipeData(0);//回复出厂设置一样
				}
			}else if("#*lockscreen*#".equals(body)){
				//远程锁屏
				System.out.println("远程锁屏");
				//判断超级管理员是否激活
				if (devicePolicyManager.isAdminActive(componentName)) {
					//锁屏
					devicePolicyManager.lockNow();
				}
			}
		}
//		abortBroadcast();//拦截短信,原生的android系统可以实现拦截操作，但是因为国产的某写定制android系统将此操作屏蔽了，所以无法实现相应的操作，比如小米，三星
	}
}
