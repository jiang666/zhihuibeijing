package com.itheima.qq15.presenter.impl;

import com.hyphenate.chat.EMClient;
import com.hyphenate.exceptions.HyphenateException;
import com.itheima.qq15.model.User;
import com.itheima.qq15.presenter.IRegistPresenter;
import com.itheima.qq15.utils.ThreadUtils;
import com.itheima.qq15.view.RegistView;

import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.SaveListener;

/**
 * 作者： itheima
 * 时间：2016-10-15 11:48
 * 网址：http://www.itheima.com
 */

public class RegistPresenter implements IRegistPresenter {

    private RegistView mRegistView;

    public RegistPresenter(RegistView registView) {
        mRegistView = registView;
    }

    @Override
    public void regist(final String username, final String pwd) {
        /**
         * 1. 先注册Bmob云数据库
         * 2. 如果Bmob成功了再去注册环信平台
         * 3. 如果Bmob成功了，环信失败了，则再去把Bmob上的数据给删除掉
         */
        User user = new User();
        user.setPassword(pwd);
        user.setUsername(username);

        user.signUp(new SaveListener<User>() {
            //Bmob中的回调方法都是在主线程中被调用的
            @Override
            public void done(final User user, BmobException e) {
                if (e==null){
                    //成功了再去注册环信平台
                    ThreadUtils.runOnSubThread(new Runnable() {
                        @Override
                        public void run() {
                            try {
                                EMClient.getInstance().createAccount(username, pwd);
                                //环信注册成功
                                ThreadUtils.runOnMainThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        mRegistView.onRegist(username,pwd,true,null);
                                    }
                                });
                            } catch (final HyphenateException e1) {
                                e1.printStackTrace();
                                //将Bmob上注册的user给删除掉
                                user.delete();
                                //环信注册失败了
                                ThreadUtils.runOnMainThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        mRegistView.onRegist(username,pwd,false,e1.toString());
                                    }
                                });
                            }
                        }
                    });
                }else {
                    //失败了，将结果告诉Activity
                    mRegistView.onRegist(username,pwd,false,e.getMessage());
                }
            }
        });



    }
}
