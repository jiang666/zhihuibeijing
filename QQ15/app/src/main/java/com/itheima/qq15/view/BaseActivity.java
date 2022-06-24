package com.itheima.qq15.view;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;

import com.itheima.qq15.QQApplication;
import com.itheima.qq15.model.User;
import com.itheima.qq15.utils.Constant;
import com.itheima.qq15.utils.ToastUtils;

/**
 * 作者： itheima
 * 时间：2016-10-15 11:13
 * 网址：http://www.itheima.com
 */

public class BaseActivity extends AppCompatActivity {
    private ProgressDialog mProgressDialog;

    private SharedPreferences mSharedPreferences;
    private QQApplication mApplication;

    public void startActivity(Class clazz,boolean isFinish){
       startActivity(clazz,isFinish,null);
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        /**
         *  所有的Activity都依附于一个Application，在Activity中只要通过 getApplication（）方法，就能拿到当前应用中的Application对象
         *
         */
        mApplication = (QQApplication) getApplication();
        mApplication.addActivity(this);

        mProgressDialog = new ProgressDialog(this);
        mProgressDialog.setCancelable(false);
        mSharedPreferences = getSharedPreferences("config",MODE_PRIVATE);
    }



    public void saveUser(String username,String pwd){
        mSharedPreferences.edit()
                .putString(Constant.SP_KEY_USERNAME,username)
                .putString(Constant.SP_KEY_PWD,pwd)
                .commit();
    }

    public String getUserName(){
        return mSharedPreferences.getString(Constant.SP_KEY_USERNAME,"");
    }
    public String getPwd(){
        return mSharedPreferences.getString(Constant.SP_KEY_PWD,"");
    }



    public void showDialog(String msg){
        mProgressDialog.setMessage(msg);
        mProgressDialog.show();
    }

    public void hideDialog(){
        mProgressDialog.hide();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mApplication.removeActivity(this);
        mProgressDialog.dismiss();
    }

    public void showToast(String msg){
        ToastUtils.showToast(this,msg);
    }

    public void startActivity(Class clazz, boolean isFinish, String contact) {
        Intent intent = new Intent(this,clazz);
        if (contact!=null){
            intent.putExtra("username",contact);
        }
        startActivity(intent);
        if (isFinish){
            finish();
        }
    }
}
