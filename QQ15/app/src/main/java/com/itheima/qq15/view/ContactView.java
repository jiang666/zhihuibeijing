package com.itheima.qq15.view;

import java.util.List;

/**
 * 作者： itheima
 * 时间：2016-10-17 15:39
 * 网址：http://www.itheima.com
 */

public interface ContactView {

    void onInitContacts(List<String> contacts);

    void updateContacts(boolean success,String msg);

    void onDelete(String contact,boolean success,String msg);
}
