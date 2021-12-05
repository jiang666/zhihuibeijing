package com.itheima.mobliesafe61;

import java.util.HashMap;
import java.util.List;

import com.itheima.mobliesafe61.engine.ContactsEngine;
import com.itheima.mobliesafe61.utils.MyAsyncTask;
import com.lidroid.xutils.ViewUtils;
import com.lidroid.xutils.view.annotation.ViewInject;

import android.app.Activity;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.AdapterView.OnItemClickListener;

public class ContactsActivity extends Activity implements OnItemClickListener {
	@ViewInject(R.id.lv_contact_contactlist)
	private ListView lv_contact_contactlist;
	/**
	 * 获取的所有联系人集合
	 */
	private List<HashMap<String, String>> allContacts;
	//注解，通过注解的方法初始化数据，类似spring注解，反射原理实现findiviewbyId
	@ViewInject(R.id.loading)
	private ProgressBar loading;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_contacts);
		ViewUtils.inject(this);
		//异步加载框
		new MyAsyncTask() {
			
			@Override
			public void preTask() {
				//控制控件的显示和隐藏   VISIBLE：显示
				loading.setVisibility(View.VISIBLE);
			}
			
			@Override
			public void postTask() {
				lv_contact_contactlist.setAdapter(new MyAdapter());
				loading.setVisibility(View.INVISIBLE);//数据显示完成，隐藏进度条,GONE:隐藏进度条，但是进度条的位置没有了，INVISIBLE:进度条的位置还是在
			}
			
			@Override
			public void doInback() {
				allContacts = ContactsEngine.getAllContacts(ContactsActivity.this);
			}
		}.exeuted();
		/*
		//三个参数为了提高方法扩展性
		//参数1：子线程执行所需要的参数
		//参数2：执行的进度
		//参数3：子线程执行返回的结果
		//面试：异步加载是用了线程池的，执行到多少个线程的时候其实跟new线程是没有区别：    5个    @Override含义  执行子类的表示出来继承自父类
		new AsyncTask<String, Integer, String>(){
			
			//在子线程之中执行的方法
			@Override
			protected String doInBackground(String... params) {
				// TODO Auto-generated method stub
				return null;
			}
			//在子线程之前执行的方法
			@Override
			protected void onPreExecute() {
				// TODO Auto-generated method stub
				super.onPreExecute();
			}
			//在子线程之后执行的方法
			@Override
			protected void onPostExecute(String result) {
				// TODO Auto-generated method stub
				super.onPostExecute(result);
			}
			
		}.execute();
		*/
//		lv_contact_contactlist = (ListView) findViewById(R.id.lv_contact_contactlist);
		lv_contact_contactlist.setOnItemClickListener(this);
	}
	private class MyAdapter extends BaseAdapter{
		
		//条目的数量
		@Override
		public int getCount() {
			// TODO Auto-generated method stub
			return allContacts.size();
		}
		//条目的样式
		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			//将布局文件转化成view对象
			View view = View.inflate(getApplicationContext(), R.layout.item_contact, null);
			TextView tv_itemcontact_name = (TextView) view.findViewById(R.id.tv_itemcontact_name);//是去item的布局文件中找我们的控件
			TextView tv_itemcontact_num = (TextView) view.findViewById(R.id.tv_itemcontact_num);
			tv_itemcontact_name.setText(allContacts.get(position).get("name"));//position代表条目的位置
			tv_itemcontact_num.setText(allContacts.get(position).get("phone"));
			return view;
		}
		
		@Override
		public Object getItem(int position) {
			// TODO Auto-generated method stub
			return null;
		}

		@Override
		public long getItemId(int position) {
			// TODO Auto-generated method stub
			return 0;
		}
	}
	@Override
	public void onItemClick(AdapterView<?> parent, View view, int position,
			long id) {
		Intent intent = new Intent();
		intent.putExtra("num", allContacts.get(position).get("phone"));
		//Call this to set the result that your activity will return to its caller.
		//调用设置结果的方法，会将结果返回给调用你的activity
		setResult(0, intent);
		finish();
	}
}
