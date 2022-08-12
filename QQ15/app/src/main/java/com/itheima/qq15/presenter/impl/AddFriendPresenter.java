package com.itheima.qq15.presenter.impl;

import com.hyphenate.chat.EMClient;
import com.hyphenate.exceptions.HyphenateException;
import com.itheima.qq15.db.DBUtils;
import com.itheima.qq15.model.User;
import com.itheima.qq15.presenter.IAddFriendPresenter;
import com.itheima.qq15.utils.ThreadUtils;
import com.itheima.qq15.view.AddFriendView;

import java.util.List;

import cn.bmob.v3.BmobQuery;
import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.FindListener;

/**
 * 作者： itheima
 * 时间：2016-10-18 14:52
 * 网址：http://www.itheima.com
 */

public class AddFriendPresenter implements IAddFriendPresenter {

    private AddFriendView mAddFriendView;

    public AddFriendPresenter(AddFriendView addFriendView) {
        mAddFriendView = addFriendView;
    }

    @Override
    public void searchFriend(String keyword) {
        final String currentUser = EMClient.getInstance().getCurrentUser();
        BmobQuery<User> query = new BmobQuery<User>();
        /**
         * 参数1：数据库字段的名称
         */
        query.addWhereStartsWith("username", keyword)
                .addWhereNotEqualTo("username", currentUser)
                .findObjects(new FindListener<User>() {
                    @Override
                    public void done(List<User> list, BmobException e) {
                        if (e == null && list != null && list.size() > 0) {
                            List<String> contacts = DBUtils.getContacts(currentUser);
                            //获取到数据
                            mAddFriendView.onSearchResult(list, contacts, true, null);
                        } else {
                            //没有找到数据
                            if (e == null) {
                                mAddFriendView.onSearchResult(null, null, false, "没有找到对应的用户。");
                            } else {
                                mAddFriendView.onSearchResult(null, null, false, e.getMessage());
                            }
                        }
                    }
                });

    }

    @Override
    public void addFriend(final String username) {
        ThreadUtils.runOnSubThread(new Runnable() {
            @Override
            public void run() {
                try {
                    EMClient.getInstance().contactManager().addContact(username,"想加您为好友，一起写代码！");
                    //添加成功（仅仅代表这个请求发送成功了，对方有没有同意是另外一会儿事儿）
                    onAddResult(username,true,null);
                } catch (HyphenateException e) {
                    //添加失败
                    e.printStackTrace();
                    onAddResult(username,false,e.getMessage());

                }
            }
        });
    }

    private void onAddResult(final String username, final boolean success, final String msg) {
        ThreadUtils.runOnMainThread(new Runnable() {
            @Override
            public void run() {
                mAddFriendView.onAddResult(username, success, msg);
            }
        });
    }
}
