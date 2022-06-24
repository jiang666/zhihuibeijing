package com.itheima.qq15.presenter.impl;

import com.hyphenate.EMCallBack;
import com.hyphenate.chat.EMClient;
import com.itheima.qq15.listener.CallBackListener;
import com.itheima.qq15.presenter.LoginPresenter;
import com.itheima.qq15.utils.ThreadUtils;
import com.itheima.qq15.view.LoginView;

/**
 * 作者： itheima
 * 时间：2016-10-15 17:14
 * 网址：http://www.itheima.com
 */

public class LoginPresenterImpl implements LoginPresenter {
    private LoginView mLoginView;

    public LoginPresenterImpl(LoginView loginView) {
        mLoginView = loginView;
    }

    @Override
    public void login(final String username, final String pwd) {
        //环信目前（3.5.x）的所有回调方法都是在子线程中回调的
        EMClient.getInstance().login(username, pwd, new CallBackListener() {
            @Override
            public void onMainSuccess() {
                mLoginView.onLogin(username,pwd,true,null);
            }

            @Override
            public void onMainError(int i, String s) {
                mLoginView.onLogin(username,pwd,false,s);
            }
        });

    }
}
