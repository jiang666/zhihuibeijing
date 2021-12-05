package com.itheima.mobliesafe61.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.telephony.SmsManager;
import android.telephony.TelephonyManager;
/**
 * 监听手机重启的广播接受者
 * @author Administrator
 *
 */
public class BootCompleteReceiver extends BroadcastReceiver {

	@Override
	public void onReceive(Context context, Intent intent) {
			System.out.println("手机重启了");
			//判断SIM卡是否一致
			//获取保存的sim卡号
			SharedPreferences sp = context.getSharedPreferences("config", Context.MODE_PRIVATE);
			boolean protected_sp = sp.getBoolean("protected", false);
			if (protected_sp) {
				String sim_sp = sp.getString("sim", "");//获取保存的SIM卡号
				
				//重新获取本手机的sim卡号
				TelephonyManager telephonyManager = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
//				telephonyManager.getLine1Number();//获取sim卡绑定的手机号，Line1：双卡双待的问题，在中国，不太好用，中国的运营商一般不会让sim卡和手机号绑定，不太适用
				String sim = telephonyManager.getSimSerialNumber();//获取sim卡的串号，sim卡唯一标示
				if (!"".equals(sim_sp)) {
					if (!sim_sp.equals(sim)) {
						//发送报警短息了
						SmsManager manager = SmsManager.getDefault();//获取短息管理者
						//参数1：收件人
						//参数2：服务中心地址
						//参数3：发送内容，模拟器汉字
						//参数4：短信是否发送成功的状态
						//参数5：协议
						manager.sendTextMessage(sp.getString("safenum", "5556"), null, "da ge wo bei tou le,help me!!!!", null, null);
					}
				}
				
			}
	}

}
