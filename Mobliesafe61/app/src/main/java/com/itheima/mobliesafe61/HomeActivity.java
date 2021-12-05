package com.itheima.mobliesafe61;

import java.security.spec.PSSParameterSpec;

import com.itheima.mobliesafe61.utils.MD5Utls;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.content.ComponentName;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

public class HomeActivity extends Activity {

	private GridView gv_home_gridview;
	private SharedPreferences sp;
	private AlertDialog dialog;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_home);
		sp = getSharedPreferences("config", MODE_PRIVATE);
		gv_home_gridview = (GridView) findViewById(R.id.gv_home_gridview);
		gv_home_gridview.setAdapter(new Myadapter());
		gv_home_gridview.setOnItemClickListener(new OnItemClickListener() {
			//parent gridview
			//view  条目的view对象
			//position 条目的位置
			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				switch (position) {
				case 0:
					//跳转到手机防盗模块
					//第一次点击的时候，弹出设置密码的对话框，设置成功，再次点击的时候，输入密码，密码正确才能跳转
					if(TextUtils.isEmpty(sp.getString("password", ""))){//判断多种情况     null:没有内存		"":有内存，但是没有没有内容
						//弹出设置密码对话框
						showSetPassWordDialog();
					}else{
						//弹出输入密码对话框
						showEnterPassWordDialog();
					}
					break;
				case 1://通讯卫士黑名单
					Intent intent1 = new Intent(HomeActivity.this,CallSmsActivity.class);
					startActivity(intent1);
					break;
				case 2://软件管家
					Intent intent2 = new Intent(HomeActivity.this,AppManagerActivity.class);
					startActivity(intent2);
					break;
				case 3://进程管理
					Intent intent3 = new Intent();
					ComponentName className=new ComponentName(getBaseContext(), ProcessManagerActivity.class.getName());
					intent3.setComponent(className);
					startActivity(intent3);
					break;
				case 5://手机杀毒 
					Intent intent5 = new Intent(getBaseContext(),KillVirusActivity.class);;
					startActivity(intent5);
					break;
				case 4://流量统计 
					Intent intent4 = new Intent(getBaseContext(),NetFlowActivity.class);;
					startActivity(intent4);
					break;
				case 6://清理缓存
					Intent intent6 = new Intent(getBaseContext(),CleanCachActivity.class);;
					startActivity(intent6);
					break;
				case 7:
					Intent intent7 = new Intent(HomeActivity.this,AToolsActivity.class);
					startActivity(intent7);
					break;
				case 8:
					//设置中心
					Intent intent8 = new Intent(HomeActivity.this,HomeSetttingActivity.class);
					startActivity(intent8);
					break;

				default:
					break;
				}
			}
		});
	}
	int count = 0;
	/**
	 * 输入密码对话框
	 */
	protected void showEnterPassWordDialog() {
		AlertDialog.Builder builder = new Builder(this);
		builder.setCancelable(false);//设置对话框不能消息
		View view = View.inflate(getApplicationContext(), R.layout.dialog_enterpassword, null);
		final EditText ed_home_password = (EditText) view.findViewById(R.id.ed_home_password);
		Button btn_ok = (Button) view.findViewById(R.id.btn_ok);
		Button btn_cancel = (Button) view.findViewById(R.id.btn_cancel);
		ImageView iv_enterpassword_imageview = (ImageView)view.findViewById(R.id.iv_enterpassword_imageview);
		iv_enterpassword_imageview.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				//隐藏和显示输入的密码
				if (count%2 == 0) {
					//显示密码
					ed_home_password.setInputType(1);
				}else{
					//隐藏密码
					ed_home_password.setInputType(129);
				}
				count++;
			}
		});
		btn_ok.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				String password = ed_home_password.getText().toString().trim();
				//判断输入的密码时候为空
				if (TextUtils.isEmpty(password)) {
					Toast.makeText(getApplicationContext(), "请输入密码", 0).show();
					return;//不允许用户在执行其他操作
				}
				//输入密码和保存的密码比较
				//获取保存的密码
				String passwrod_sp = sp.getString("password", "");
				if (MD5Utls.digestPassWord(password).equals(passwrod_sp)) {
					//提醒用户，消除对话框，跳转手机防盗界面
					Toast.makeText(getApplicationContext(), "密码正确", 0).show();
					dialog.dismiss();
					//跳转手机防盗界面
					Intent intent = new Intent(HomeActivity.this,LostFindActivity.class);
					startActivity(intent);
				}else{ 
					//提醒用户密码错误
					Toast.makeText(getApplicationContext(), "密码错误", 0).show();
				}
			}
		});
		btn_cancel.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				dialog.dismiss();
			}
		});
		builder.setView(view);
		dialog = builder.create();
		dialog.show();
	}
	/**
	 * 设置密码对话框
	 */
	protected void showSetPassWordDialog() {
		AlertDialog.Builder builder = new Builder(this);
		builder.setCancelable(false);//设置对话框不能消息
		View view = View.inflate(getApplicationContext(), R.layout.dialog_setpassword, null);
		final EditText ed_home_password = (EditText) view.findViewById(R.id.ed_home_password);
		final EditText ed_home_password_confirm = (EditText) view.findViewById(R.id.ed_home_password_confirm);
		Button btn_ok = (Button) view.findViewById(R.id.btn_ok);
		Button btn_cancel = (Button) view.findViewById(R.id.btn_cancel);
		btn_ok.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				String password = ed_home_password.getText().toString().trim();
				//判断输入的密码时候为空
				if (TextUtils.isEmpty(password)) {
					Toast.makeText(getApplicationContext(), "请输入密码", 0).show();
					return;//不允许用户在执行其他操作
				}
				String password_confirm = ed_home_password_confirm.getText().toString().trim();
				if (password.equals(password_confirm)) {
					//保存密码
					Editor edit = sp.edit();
					edit.putString("password", MD5Utls.digestPassWord(password));
					edit.commit();
					dialog.dismiss();
					Toast.makeText(getApplicationContext(), "密码设置成功", 0).show();
				}else{
					//提醒用户
					Toast.makeText(getApplicationContext(), "两次密码输入不一致", 0).show();
				}
			}
		});
		btn_cancel.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				dialog.dismiss();
			}
		});
//		builder.setView(view);//根据dialog.setview的效果一样
		dialog = builder.create();
		//viewSpacingLeft:距离左边的距离
		//viewSpacingTop：距离上部的距离
		//viewSpacingRight：距离右边的距离
		//viewSpacingBottom：距离下部的距离
		dialog.setView(view, 0, 0, 0, 0);
		dialog.show();
	}
	private class Myadapter extends BaseAdapter{
		
		int[] imageId = { R.drawable.safe, R.drawable.callmsgsafe, R.drawable.app,
				R.drawable.taskmanager, R.drawable.netmanager, R.drawable.trojan,
				R.drawable.sysoptimize, R.drawable.atools, R.drawable.settings };
		String[] names = { "手机防盗", "通讯卫士", "软件管理", "进程管理", "流量统计", "手机杀毒", "缓存清理",
				"高级工具", "设置中心" };
		//条目的个数
		@Override
		public int getCount() {
			// TODO Auto-generated method stub
			return 9;
		}
		//条目的样式
		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
//			TextView textView = new TextView(getApplicationContext());
//			textView.setText("第"+position+"条数据");//position代表是条目的位置   0开始    0-8
			//将布局控件转化成view对象
			View view = View.inflate(getApplicationContext(), R.layout.item_home, null);
			ImageView item_home_imageview = (ImageView)view.findViewById(R.id.item_home_imageview);
			TextView item_home_text = (TextView) view.findViewById(R.id.item_home_text);
			item_home_imageview.setImageResource(imageId[position]);//设置条目相应的图片
			item_home_text.setText(names[position]);//设置条目相应的文字
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
}
