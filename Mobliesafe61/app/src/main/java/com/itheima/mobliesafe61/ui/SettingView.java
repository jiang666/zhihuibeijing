package com.itheima.mobliesafe61.ui;

import com.itheima.mobliesafe61.R;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.widget.CheckBox;
import android.widget.RelativeLayout;
import android.widget.TextView;

public class SettingView extends RelativeLayout {
	
	private TextView tv_homesetting_title;
	private TextView tv_homesetting_des;
	private CheckBox cb_homesetting_checkbox;
	private String des_on;
	private String des_off;
	//在代码中使用的时候调用
	public SettingView(Context context) {
		super(context);
		init();
	}
	//在布局文件中使用的调用，两个参数的一样，多了样式
	public SettingView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		init();
	}
	//在布局文件中使用的调用
	public SettingView(Context context, AttributeSet attrs) {
		super(context, attrs);
		init();
		//通过AttributeSet去获取所有的属性
		//获取属性的个数
//		int count = attrs.getAttributeCount();
//		System.out.println(count+"");
//		//获取所有属性的值
//		for (int i = 0; i < count; i++) {
//			System.out.println(attrs.getAttributeValue(i));
//		}
		//namespace : 命名空间		name ： 控件的名称
		//通过命名空间和属性的名称获取相应的属性的值
		String title = attrs.getAttributeValue("http://schemas.android.com/apk/res/com.itheima.mobliesafe61", "title");
		des_on = attrs.getAttributeValue("http://schemas.android.com/apk/res/com.itheima.mobliesafe61", "des_on");
		des_off = attrs.getAttributeValue("http://schemas.android.com/apk/res/com.itheima.mobliesafe61", "des_off");
		tv_homesetting_title.setText(title);//设置标题
		if (isChecked()) {
			tv_homesetting_des.setText(des_on);
		}else{
			tv_homesetting_des.setText(des_off);
		}
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
		View view = View.inflate(getContext(), R.layout.settingview, this);//孩子有了，直接找爹，喜当爹，不定义属性，就会使用原布局控件中的属性
		//初始化控件
		tv_homesetting_title = (TextView) view.findViewById(R.id.tv_homesetting_title);
		tv_homesetting_des = (TextView) view.findViewById(R.id.tv_homesetting_des);
		cb_homesetting_checkbox = (CheckBox) view.findViewById(R.id.cb_homesetting_checkbox);
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
	/**
	 * 设置checkbox的状态
	 * @param isChecked
	 */
	public void setChecked(boolean isChecked){
		cb_homesetting_checkbox.setChecked(isChecked);
		//相当于在setChecked里面封装了setDes
		if (isChecked()) {
			tv_homesetting_des.setText(des_on);
		}else{
			tv_homesetting_des.setText(des_off);
		}
	}
	/**
	 * 获取checkBox的状态
	 * @return
	 */
	public boolean isChecked(){
		return cb_homesetting_checkbox.isChecked();
	}
}
