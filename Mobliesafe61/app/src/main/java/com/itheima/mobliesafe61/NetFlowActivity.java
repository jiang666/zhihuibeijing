package com.itheima.mobliesafe61;

import android.app.Activity;
import android.net.TrafficStats;
import android.os.Bundle;
import android.text.format.Formatter;

public class NetFlowActivity extends Activity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_net_flow);
		// TrafficStats:读取系统流量 工具类 proc Sqlite:
		long mobileRx = TrafficStats.getMobileRxBytes();// 手机流量 接收
		long mobilex = TrafficStats.getMobileTxBytes();// 手机流量 发送
		long Rx = TrafficStats.getTotalRxBytes();// 流量 接收 wifi+手机流量
		long Tx = TrafficStats.getTotalTxBytes();// 流量 发送 wifi+手机流量
		System.out.println(" 手机流量 接收" + formate(mobileRx));
		System.out.println("手机流量 发送 " + formate(mobilex));
		System.out.println("流量 发送 " + formate(Rx));
		System.out.println("流量 发送 " + formate(Tx));
		// TrafficStats.getUidRxBytes(uid)//一个应用消耗的流量
		// TrafficStats.getUidTxBytes(uid);
	}

	public String formate(long size) {
		return Formatter.formatFileSize(this, size);
	}
}
