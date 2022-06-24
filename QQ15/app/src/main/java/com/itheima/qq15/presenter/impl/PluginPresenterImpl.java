package com.itheima.qq15.presenter.impl;

import com.hyphenate.EMCallBack;
import com.hyphenate.chat.EMClient;
import com.itheima.qq15.listener.CallBackListener;
import com.itheima.qq15.presenter.PluginPresenter;
import com.itheima.qq15.utils.ThreadUtils;
import com.itheima.qq15.view.PluginView;

/**
 * 作者： itheima
 * 时间：2016-10-17 11:35
 * 网址：http://www.itheima.com
 */

public class PluginPresenterImpl implements PluginPresenter {

    private PluginView mPluginView;

    public PluginPresenterImpl(PluginView pluginView) {
        mPluginView = pluginView;
    }

    @Override
    public void logout() {
        /**
         * 参数1：true代表解除绑定，不再推送消息
         */
        EMClient.getInstance().logout(true, new CallBackListener() {
            @Override
            public void onMainSuccess() {
//                EMClient.getInstance().contactManager()
                mPluginView.onLogout(EMClient.getInstance().getCurrentUser(),true,null);
            }

            @Override
            public void onMainError(int i, String s) {
                mPluginView.onLogout(EMClient.getInstance().getCurrentUser(),false,s);
            }
        });

    }
}
