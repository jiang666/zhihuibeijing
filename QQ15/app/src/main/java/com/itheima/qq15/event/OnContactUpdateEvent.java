package com.itheima.qq15.event;

/**
 * 作者： itheima
 * 时间：2016-10-18 10:59
 * 网址：http://www.itheima.com
 */

public class OnContactUpdateEvent {
    public String contact;
    public boolean isAdded;

    public OnContactUpdateEvent(String contact, boolean isAdded) {
        this.contact = contact;
        this.isAdded = isAdded;
    }
}
