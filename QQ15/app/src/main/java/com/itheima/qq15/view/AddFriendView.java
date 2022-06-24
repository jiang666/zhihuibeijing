package com.itheima.qq15.view;

import com.itheima.qq15.model.User;

import java.util.List;

/**
 * 作者： itheima
 * 时间：2016-10-18 14:51
 * 网址：http://www.itheima.com
 */

public interface AddFriendView {
    void onSearchResult(List<User> userList,List<String> contactsList,boolean success,String msg);

    void onAddResult(String username, boolean success, String msg);
}
