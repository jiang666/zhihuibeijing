package com.itcast.googleplayteach.utils;

import android.graphics.drawable.Drawable;
import android.graphics.drawable.GradientDrawable;
import android.graphics.drawable.StateListDrawable;

/**
 * 生成图像的工具类
 * 
 * @author Kevin
 */
public class DrawableUtils {

	/**
	 * 创建圆角矩形
	 * 
	 * @param rgb
	 *            颜色值
	 * @param radius
	 *            圆角半径
	 * @return
	 */
	public static Drawable getGradientDrawable(int rgb, int radius) {
		// 初始化对象
		GradientDrawable drawable = new GradientDrawable();
		// 矩形类型
		drawable.setGradientType(GradientDrawable.RECTANGLE);
		// 设置颜色
		drawable.setColor(rgb);
		// 设置圆角半径
		drawable.setCornerRadius(radius);
		return drawable;
	}

	/**
	 * 返回状态选择器对象(selector)
	 * 
	 * @param normal
	 *            默认图像
	 * @param pressed
	 *            按下图像
	 */
	public static Drawable getStateListDrawable(Drawable normal,
			Drawable pressed) {
		StateListDrawable drawable = new StateListDrawable();
		drawable.addState(new int[] { android.R.attr.state_pressed }, pressed);
		drawable.addState(new int[] {}, normal);
		return drawable;
	}

	/**
	 * 返回状态选择器对象(selector)
	 * 
	 * @param normalColor
	 *            默认颜色
	 * @param pressedColor
	 *            按下颜色
	 * @param radius
	 *            圆角半径
	 * @return
	 */
	public static Drawable getStateListDrawable(int normalColor,
			int pressedColor, int radius) {
		Drawable normal = getGradientDrawable(normalColor, radius);
		Drawable pressed = getGradientDrawable(pressedColor, radius);
		return getStateListDrawable(normal, pressed);
	}
}
