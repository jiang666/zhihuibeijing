package com.itheima.qq15.presenter.impl;

import com.hyphenate.chat.EMClient;
import com.itheima.qq15.presenter.ISplashPresenter;
import com.itheima.qq15.view.SplashView;

/**
 * 作者： itheima
 * 时间：2016-10-15 10:51
 * 网址：http://www.itheima.com
 */

public class SplashPresenter implements ISplashPresenter {

    private SplashView mSplashView;

    public SplashPresenter(SplashView splashView) {
        mSplashView = splashView;
    }

    @Override
    public void checkLogined() {
        if (EMClient.getInstance().isLoggedInBefore() && EMClient.getInstance().isConnected()) {
            //已经登录过了
            mSplashView.onCheckedLogin(true);
        } else {
            //还未登录
            mSplashView.onCheckedLogin(false);
        }

    }
}
