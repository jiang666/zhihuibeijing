package com.itheima.qq15.presenter.impl;

import com.hyphenate.chat.EMClient;
import com.hyphenate.chat.EMConversation;
import com.itheima.qq15.presenter.ConversationPresenter;
import com.itheima.qq15.view.ConversationView;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Map;

/**
 * 作者： itheima
 * 时间：2016-10-19 11:56
 * 网址：http://www.itheima.com
 */

public class ConversationPresenterImpl implements ConversationPresenter {
    private ConversationView mConversationView;
    private List<EMConversation> mEMConversationList = new ArrayList<>();

    public ConversationPresenterImpl(ConversationView conversationView) {
        mConversationView = conversationView;
    }

    @Override
    public  void initConversation() {
        Map<String, EMConversation> allConversations = EMClient.getInstance().chatManager().getAllConversations();
        mEMConversationList.clear();
        mEMConversationList.addAll(allConversations.values());
        /**
         * 排序，最近的时间在最上面(时间的倒序)
         * 回传到View层
         */
        Collections.sort(mEMConversationList, new Comparator<EMConversation>() {
            @Override
            public int compare(EMConversation o1, EMConversation o2) {

                return (int) (o2.getLastMessage().getMsgTime()-o1.getLastMessage().getMsgTime());
            }
        });
        mConversationView.onInitConversation(mEMConversationList);
    }

}
