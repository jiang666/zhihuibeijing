package com.example.mylibrary.httpUtils.utils;

import android.util.Log;

public class LogUtils {

    public static boolean needLog= true;

    public static void logE(String tag, String content) {
        if(needLog){
            Log.e(tag, content);
        }
    }
}
