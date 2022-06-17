package com.example.mylibrary.httpUtils.utils;

import android.util.Log;

public class FastClickUtils {

    // 两次点击按钮之间的点击间隔不能少于200毫秒
    private static final int MIN_CLICK_DELAY_TIME = 250;
    private static volatile long lastClickTime;

    public static synchronized boolean isFastClick() {
        boolean flag = false;
        long curClickTime = System.currentTimeMillis();
        if ((curClickTime - lastClickTime) <= MIN_CLICK_DELAY_TIME) {
            Log.d("测试快速点击", "flag = true    lastClickTime = 0;");
            flag = true;
//            lastClickTime = 0;
        }
//        else {
            lastClickTime = curClickTime;
            Log.d("测试快速点击", "lastClickTime="+lastClickTime);
//        }
        return flag;
    }
}
