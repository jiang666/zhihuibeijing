package com.itheima.qq15.presenter;

/**
 * 作者： itheima
 * 时间：2016-10-18 17:19
 * 网址：http://www.itheima.com
 */

public interface ChatPresenter {

    void initChat(String contact);

    void updateData(String username);

    void sendMessage(String username, String msg);
}
