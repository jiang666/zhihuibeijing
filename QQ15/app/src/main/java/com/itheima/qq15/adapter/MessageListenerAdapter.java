package com.itheima.qq15.adapter;

import com.hyphenate.EMMessageListener;
import com.hyphenate.chat.EMMessage;

import java.util.List;

/**
 * 作者： itheima
 * 时间：2016-10-19 10:49
 * 网址：http://www.itheima.com
 */

public class MessageListenerAdapter implements EMMessageListener {
    @Override
    public void onMessageReceived(List<EMMessage> list) {
    }

    @Override
    public void onCmdMessageReceived(List<EMMessage> list) {
    }

    @Override
    public void onMessageReadAckReceived(List<EMMessage> list) {
    }

    @Override
    public void onMessageDeliveryAckReceived(List<EMMessage> list) {
    }

    @Override
    public void onMessageChanged(EMMessage emMessage, Object o) {
    }
}
