package com.itheima.qq15.view;

import android.animation.ObjectAnimator;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.hyphenate.chat.EMClient;
import com.hyphenate.chat.EMConversation;
import com.hyphenate.chat.EMMessage;
import com.itheima.qq15.MainActivity;
import com.itheima.qq15.R;
import com.itheima.qq15.adapter.ConversationAdapter;
import com.itheima.qq15.presenter.IConversationPresenter;
import com.itheima.qq15.presenter.impl.ConversationPresenter;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 */
public class ConversationFragment extends BaseFragment implements View.OnClickListener ,ConversationView, ConversationAdapter.OnItemClickListener {

    private RecyclerView mRecyclerView;
    private FloatingActionButton mFab;
    private IConversationPresenter mConversationPresenter;
    private ConversationAdapter mConversationAdapter;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_conversation, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        mRecyclerView = (RecyclerView) view.findViewById(R.id.recyclerView);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        mFab = (FloatingActionButton) view.findViewById(R.id.fab);
        mFab.setOnClickListener(this);
        /**
         *  初始化会话列表
         */
        mConversationPresenter = new ConversationPresenter(this);
        mConversationPresenter.initConversation();
        EventBus.getDefault().register(this);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        EventBus.getDefault().unregister(this);
        mConversationAdapter = null;
    }

    @Override
    public void onClick(View v) {
        //将所有的会话全部比较为已读
        ObjectAnimator.ofFloat(mFab,"rotation",0,360).setDuration(1000).start();
        EMClient.getInstance().chatManager().markAllConversationsAsRead();
        MainActivity activity = (MainActivity) getActivity();
        activity.updateUnreadCount();
        if (mConversationAdapter!=null){
            mConversationAdapter.notifyDataSetChanged();
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onEvent(EMMessage message){
        MainActivity activity = (MainActivity) getActivity();
        activity.showToast("收到信消息："+message.getBody().toString());
       mConversationPresenter.initConversation();
    }

    @Override
    public void onResume() {
        super.onResume();
        if (mConversationAdapter!=null){
            mConversationAdapter.notifyDataSetChanged();
        }
    }

    @Override
    public void onInitConversation(List<EMConversation> emConversationList) {
        if (mConversationAdapter==null){
            mConversationAdapter = new ConversationAdapter(emConversationList);
            mRecyclerView.setAdapter(mConversationAdapter);
            mConversationAdapter.setOnItemClickListener(this);
        }else{
            mConversationAdapter.notifyDataSetChanged();
        }
    }

    @Override
    public void onItemClick(EMConversation conversation) {
        String userName = conversation.getUserName();
        MainActivity activity = (MainActivity) getActivity();
        activity.startActivity(ChatActivity.class,false,userName);
    }
}
