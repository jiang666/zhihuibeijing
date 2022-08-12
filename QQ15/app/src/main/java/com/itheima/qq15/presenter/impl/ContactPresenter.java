package com.itheima.qq15.presenter.impl;

import com.hyphenate.chat.EMClient;
import com.hyphenate.exceptions.HyphenateException;
import com.itheima.qq15.db.DBUtils;
import com.itheima.qq15.presenter.IContactPresenter;
import com.itheima.qq15.utils.ThreadUtils;
import com.itheima.qq15.view.ContactView;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.TreeMap;

/**
 * 作者： itheima
 * 时间：2016-10-17 15:40
 * 网址：http://www.itheima.com
 */

public class ContactPresenter implements IContactPresenter {
    private ContactView mContactView;
    private List<String> contactList = new ArrayList<>();

    public ContactPresenter(ContactView contactView) {
        mContactView = contactView;
    }

    @Override
    public void initContacts() {
        /**
         * 1. 首先访问本地的缓存联系人
         * 2. 然后开辟子线程去环信后台获取当前用户的联系人
         * 3. 更新本地的缓存，刷新UI
         */
        final  String currentUser = EMClient.getInstance().getCurrentUser();

        List<String> contacts = DBUtils.getContacts(currentUser);
        contactList.clear();
        contactList.addAll(contacts);
        mContactView.onInitContacts(contactList);

        updateContactsFromServer(currentUser);
    }

    private void updateContactsFromServer(final String currentUser) {
        //然后开辟子线程去环信后台获取当前用户的联系人
        ThreadUtils.runOnSubThread(new Runnable() {
            @Override
            public void run() {
                try {
                    List<String> contactsFromServer = EMClient.getInstance().contactManager().getAllContactsFromServer();
                    //排序
                    Collections.sort(contactsFromServer, new Comparator<String>() {
                        @Override
                        public int compare(String o1, String o2) {
                            return o1.compareTo(o2);
                        }
                    });
                    //更新本地的缓存，
                    DBUtils.updateContacts(currentUser,contactsFromServer);
                    contactList.clear();
                    contactList.addAll(contactsFromServer);
                    //通知View刷新UI
                    ThreadUtils.runOnMainThread(new Runnable() {
                        @Override
                        public void run() {
                            mContactView.updateContacts(true,null);
                        }
                    });

                } catch (final HyphenateException e) {
                    e.printStackTrace();
                    ThreadUtils.runOnMainThread(new Runnable() {
                        @Override
                        public void run() {
                            mContactView.updateContacts(false,e.getMessage());
                        }
                    });
                }
            }
        });
    }

    @Override
    public void updateContacts() {
        updateContactsFromServer(EMClient.getInstance().getCurrentUser());
    }

    @Override
    public void deleteContact(final String contact) {
        ThreadUtils.runOnSubThread(new Runnable() {
            @Override
            public void run() {
                try {
                    EMClient.getInstance().contactManager().deleteContact(contact);
                   afterDelete(contact, true,null);
                } catch (final HyphenateException e) {
                    e.printStackTrace();
                    afterDelete(contact,false,e.toString());
                }

            }
        });
    }

    private void afterDelete(final String contact, final boolean success, final String msg) {
        ThreadUtils.runOnMainThread(new Runnable() {
            @Override
            public void run() {
                mContactView.onDelete(contact, success, msg);
            }
        });
    }
}
