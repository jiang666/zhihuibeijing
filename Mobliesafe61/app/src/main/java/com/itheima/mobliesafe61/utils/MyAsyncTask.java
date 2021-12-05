package com.itheima.mobliesafe61.utils;

import android.os.Handler;
/**
 * 异步加载框架
 * @author Administrator
 *
 */
public abstract class MyAsyncTask {
	//模板设计模式：父类创建一些方法，但是不知道具体怎么去执行这些方法，所有会交给子类，让子类根据自己的特性去执行响应的方法
	private Handler handler = new Handler(){
		public void handleMessage(android.os.Message msg) {
			postTask();
		};
	};
	/**
	 * 在子线程之前执行的方法
	 */
	public abstract void preTask();
	/**
	 * 在子线程之中执行的方法
	 */
	public abstract void doInback();
	/**
	 * 在子线程之后执行的方法
	 */
	public abstract void postTask();
	/**
	 * 执行操作
	 */
	public void exeuted(){
		preTask();
		new Thread(){
			public void run() {
				doInback();
				handler.sendEmptyMessage(0);
			};
		}.start();
	}
	
}
