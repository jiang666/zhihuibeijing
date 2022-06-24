package com.itheima.qq15.view;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.itheima.qq15.MainActivity;
import com.itheima.qq15.R;
import com.itheima.qq15.adapter.ContactAdapter;
import com.itheima.qq15.event.OnContactUpdateEvent;
import com.itheima.qq15.presenter.ContactPresenter;
import com.itheima.qq15.presenter.impl.ContactPresenterImpl;
import com.itheima.qq15.utils.ToastUtils;
import com.itheima.qq15.widget.ContactLayout;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 */
public class ContactFragment extends BaseFragment implements ContactView, SwipeRefreshLayout.OnRefreshListener, ContactAdapter.OnItemClickListener {

    private ContactLayout mContactLayout;
    private ContactPresenter mContactPresenter;
    private ContactAdapter mContactAdapter;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_contact, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        mContactLayout = (ContactLayout) view.findViewById(R.id.contactLayout);
        mContactPresenter = new ContactPresenterImpl(this);
        /**
         * 初始化联系人界面
         */
        mContactPresenter.initContacts();
        mContactLayout.setOnRefreshListener(this);

        EventBus.getDefault().register(this);
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onEvent(OnContactUpdateEvent onContactUpdateEvent){
        mContactPresenter.updateContacts();
    }

    @Override
    public void onInitContacts(List<String> contacts) {
        mContactAdapter = new ContactAdapter(contacts);
        mContactLayout.setAdapter(mContactAdapter);
        mContactAdapter.setOnItemClickListener(this);
    }

    @Override
    public void updateContacts(boolean success, String msg) {
        mContactAdapter.notifyDataSetChanged();
        //隐藏下拉刷新
        mContactLayout.setRefreshing(false);
    }

    @Override
    public void onDelete(String contact, boolean success, String msg) {
        if (success){
            ToastUtils.showToast(getActivity(),"友尽");
        }else{
            ToastUtils.showToast(getActivity(),"删除失败，要不再续前缘？");
        }
    }

    @Override
    public void onRefresh() {
        /**
         * 1. 访问网络，获取联系人
         * 2. 如果拿到数据了，更新数据库
         * 3. 更新UI
         * 4. 隐藏下拉刷新
         */
        mContactPresenter.updateContacts();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        EventBus.getDefault().unregister(this);
    }

    @Override
    public void onItemLongClick(final String contact, int position) {
        Snackbar.make(mContactLayout,"您和"+contact+"确定友尽了吗？",Snackbar.LENGTH_LONG)
                .setAction("确定", new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                      mContactPresenter.deleteContact(contact);
                    }
                }).show();
    }

    @Override
    public void onItemClick(String contact, int position) {
        MainActivity activity = (MainActivity) getActivity();
        activity.startActivity(ChatActivity.class,false,contact);
    }
}
