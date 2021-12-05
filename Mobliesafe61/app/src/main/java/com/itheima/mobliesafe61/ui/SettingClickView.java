package com.itheima.mobliesafe61.ui;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.itheima.mobliesafe61.R;

public class SettingClickView extends RelativeLayout {
	
	private TextView tv_homesetting_title;
	private TextView tv_homesetting_des;
	//在代码中使用的时候调用
	public SettingClickView(Context context) {
		super(context);
		init();
	}
	//在布局文件中使用的调用，两个参数的一样，多了样式
	public SettingClickView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		init();
	}
	//在布局文件中使用的调用
	public SettingClickView(Context context, AttributeSet attrs) {
		super(context, attrs);
		init();
	}
	/*
	 * 初始化控件，添加布局文件操作
	 */
	private void init() {
//		TextView textView = new TextView(getContext());
//		textView.setText("这是一个自定义组合控件");
//		//第一种方式
//		View view = View.inflate(getContext(), R.layout.settingview, null);//爹有了，直接找孩子，正常
//		//参数1：宽	参数2：高
//		this.addView(view,new LayoutParams(LayoutParams.MATCH_PARENT, 60));//在相对布局中添加一个textview
		//第二方式
		//root:在相应控件中添加view对象
		View view = View.inflate(getContext(), R.layout.settingclickview, this);//孩子有了，直接找爹，喜当爹，不定义属性，就会使用原布局控件中的属性
		//初始化控件
		tv_homesetting_title = (TextView) view.findViewById(R.id.tv_homesetting_title);
		tv_homesetting_des = (TextView) view.findViewById(R.id.tv_homesetting_des);
	}
	/**
	 * 设置标题
	 * @param title
	 */
	public void setTitle(String title){
		tv_homesetting_title.setText(title);
	}
	/**
	 * 设置描述信息
	 * @param des
	 */
	public void setDes(String des){
		tv_homesetting_des.setText(des);
	}
}
