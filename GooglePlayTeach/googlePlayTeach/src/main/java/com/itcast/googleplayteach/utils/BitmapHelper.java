package com.itcast.googleplayteach.utils;

import com.itcast.googleplayteach.R;
import com.lidroid.xutils.BitmapUtils;

/**
 * 获取BitmapUtils对象, 保证多个模块共用一个BitmapUtils对象,避免内存溢出
 * 
 * @author Kevin
 * 
 */
public class BitmapHelper {

	private static BitmapUtils mBitmapUtils = null;

	public static BitmapUtils getBitmapUtils() {
		if (mBitmapUtils == null) {
			mBitmapUtils = new BitmapUtils(UIUtils.getContext());
		}

		//默认加载图片是一张空图
		mBitmapUtils.configDefaultLoadingImage(R.drawable.nothing);
		return mBitmapUtils;
	}
}
