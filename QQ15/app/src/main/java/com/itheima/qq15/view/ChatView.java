package com.itheima.qq15.view;

import com.hyphenate.chat.EMMessage;

import java.util.List;

/**
 * 作者： itheima
 * 时间：2016-10-18 17:18
 * 网址：http://www.itheima.com
 */

public interface ChatView {

    void onInit(List<EMMessage> emMessageList);

    void onUpdate(int size);
}
