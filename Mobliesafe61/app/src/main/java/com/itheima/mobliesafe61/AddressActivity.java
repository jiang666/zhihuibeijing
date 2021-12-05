package com.itheima.mobliesafe61;

import android.app.Activity;
import android.os.Bundle;
import android.os.Vibrator;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.animation.Interpolator;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import com.itheima.mobliesafe61.db.dao.AddressDao;
import com.lidroid.xutils.ViewUtils;
import com.lidroid.xutils.view.annotation.ViewInject;

public class AddressActivity extends Activity {
	@ViewInject(R.id.ed_address_phone)
	private EditText ed_address_phone;
	@ViewInject(R.id.ed_address_phoneaddress)
	private TextView ed_address_phoneaddress;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_address);
		ViewUtils.inject(this);
		//监听输入框输入的状态
		ed_address_phone.addTextChangedListener(new TextWatcher() {
			//在输入内容变化的时候调用
			@Override
			public void onTextChanged(CharSequence s, int start, int before, int count) {
				String queryAddress = AddressDao.queryAddress(AddressActivity.this, s.toString());
				if (!TextUtils.isEmpty(queryAddress)) {
					ed_address_phoneaddress.setText(queryAddress);
				}
			}
			//在输入内容之前调用
			@Override
			public void beforeTextChanged(CharSequence s, int start, int count,
					int after) {
				// TODO Auto-generated method stub
				
			}
			//在输入内容之后调用
			@Override
			public void afterTextChanged(Editable s) {
				// TODO Auto-generated method stub
				
			}
		});
	}
	public void queryAddress(View v){
		//获取要查询归属地的号码
		String phone = ed_address_phone.getText().toString().trim();
		if (!TextUtils.isEmpty(phone)) {
			//查询号码归属地的操作
			String queryAddress = AddressDao.queryAddress(this, phone);
			if (!TextUtils.isEmpty(queryAddress)) {
				ed_address_phoneaddress.setText(queryAddress);
			}
		}else{
			Toast.makeText(getApplicationContext(), "请输入查询号码", 0).show();
			 
			Animation shake = AnimationUtils.loadAnimation(this, R.anim.shake);
//			shake.setInterpolator(new Interpolator() {
//				
//				@Override
//				public float getInterpolation(float x) {
//					return 0;//返回y的值,根据x的值获取y的值     x*x
//				}
//			});
			ed_address_phone.startAnimation(shake);
			//振动的管理者
			Vibrator vibrator = (Vibrator) getSystemService(VIBRATOR_SERVICE);
			vibrator.vibrate(100);//设置振动的时间，参数：振动多长时间,振动0.1秒
		}
	}
}
