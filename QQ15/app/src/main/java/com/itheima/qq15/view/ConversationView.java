package com.itheima.qq15.view;

import com.hyphenate.chat.EMConversation;

import java.util.List;

/**
 * 作者： itheima
 * 时间：2016-10-19 11:55
 * 网址：http://www.itheima.com
 */

public interface ConversationView {
    void onInitConversation(List<EMConversation> emConversationList);
}
