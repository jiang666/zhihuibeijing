package com.itheima.mobliesafe61.ui;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.TextView;

public class Home_TextView extends TextView {
	
	//代码当中调用的时候使用
	public Home_TextView(Context context) {
		super(context);
	}
	//在布局文件中使用，跟两个参数是一样的，只不过多了个样式
	public Home_TextView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
	}
	//在布局文件中调用，布局文件中控件都会通过一些编译转换成相应的代码，控件的所有属性都会保存到AttributeSet
	public Home_TextView(Context context, AttributeSet attrs) {
		super(context, attrs);
	}
	//是否获取焦点
	@Override
	public boolean isFocused() {
		//true 获取焦点  	false 不去获取焦点
		return true;
	}
	//设置滚动次数
	@Override
	public void setMarqueeRepeatLimit(int marqueeLimit) {
		// TODO Auto-generated method stub
		super.setMarqueeRepeatLimit(marqueeLimit);
	}
}
