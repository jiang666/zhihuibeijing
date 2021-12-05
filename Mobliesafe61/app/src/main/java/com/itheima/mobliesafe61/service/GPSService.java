package com.itheima.mobliesafe61.service;

import java.util.List;

import android.app.Service;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.IBinder;

public class GPSService extends Service {

	/**
	 * 位置的管理者
	 */
	private LocationManager locationManager;
	private MyLocationListener myLocationListener;
	private SharedPreferences sp;
	@Override
	public IBinder onBind(Intent intent) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void onCreate() {
		super.onCreate();
		sp = getSharedPreferences("config", MODE_PRIVATE);
		locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);
		//获取所有的定位方式，true:返回所有可用的定位方式
		List<String> providers = locationManager.getProviders(true);
		for (int i = 0; i < providers.size(); i++) {
			System.out.println(providers.get(i));
		}
		
		Criteria criteria = new Criteria();
		criteria.setAltitudeRequired(true);//可以定位海拔，一般如果可以定位海拔，返回gps定位
		//获取最佳的定位方式,true：如果可用就返回
		String bestProvider = locationManager.getBestProvider(criteria, true);
		
		myLocationListener = new MyLocationListener();
		//使用一种定位方式进行定位操作
		//provider ： 定位的方式
		//minTime ： 最小定位的时间，间隔多少时间定位一次
		//minDistance ： 最小距离，间隔多少距离定位一次
		//locationListener
		locationManager.requestLocationUpdates(bestProvider, 0, 0, new MyLocationListener());
	}
	private class MyLocationListener implements LocationListener{
		
		//在定位位置变化的时候调用，Location:当前的位置
		@Override
		public void onLocationChanged(Location location) {
			//获取坐标
			location.getAccuracy();//获取精确的位置
			location.getAltitude();//获取海拔，gps定位是可以定位海拔
			double latitude = location.getLatitude();//获取纬度，平行赤道
			double longitude = location.getLongitude();//获取经度
			//保存经纬度坐标信息
			Editor edit = sp.edit();
			edit.putString("longitude", longitude+"");
			edit.putString("latitude", latitude+"");
			edit.commit();
		}
		//在定位状态发生变化的时候调用的
		@Override
		public void onStatusChanged(String provider, int status, Bundle extras) {
			// TODO Auto-generated method stub
			
		}
		//当定位可用的时候调用
		@Override
		public void onProviderEnabled(String provider) {
			// TODO Auto-generated method stub
			
		}
		//当定位不可用的时候调用
		@Override
		public void onProviderDisabled(String provider) {
			// TODO Auto-generated method stub
		}
	}
	
	@Override
	public void onDestroy() {
		super.onDestroy();
		locationManager.removeUpdates(myLocationListener);
	}
	
}
