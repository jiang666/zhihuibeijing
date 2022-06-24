package com.itheima.qq15.listener;

import com.hyphenate.EMCallBack;
import com.itheima.qq15.utils.ThreadUtils;

/**
 * 作者： itheima
 * 时间：2016-10-17 11:42
 * 网址：http://www.itheima.com
 */

public abstract class CallBackListener implements EMCallBack {

    public  abstract void onMainSuccess();

    public abstract void onMainError(int i, String s);

    @Override
    public void onSuccess() {
        ThreadUtils.runOnMainThread(new Runnable() {
            @Override
            public void run() {
                onMainSuccess();
            }
        });
    }

    @Override
    public void onError(final int i, final String s) {
        ThreadUtils.runOnMainThread(new Runnable() {
            @Override
            public void run() {
                onMainError(i,s);
            }
        });
    }

    @Override
    public void onProgress(int i, String s) {
    }
}
