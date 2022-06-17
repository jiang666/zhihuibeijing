package com.example.mylibrary.httpUtils.http;

import android.util.Log;

import com.example.mylibrary.httpUtils.callback.HttpListener;
import com.example.mylibrary.httpUtils.http.live.LiverLoader;
import com.example.mylibrary.httpUtils.http.live.bean.StopHstroom;
import com.example.mylibrary.httpUtils.utils.HttpUtils;
import com.example.mylibrary.httpUtils.utils.SPUtil;
import com.example.mylibrary.httpUtils.utils.UserUtils;

import java.util.Map;

public class LiveHttpManager {

//    public static String BaseHttpUrl = "http://local.api.yu6.live/index.php/v1/";
    //正常
    public static String BaseHttpUrl = "https://api.ylqzb.com/index.php/v1/";
    //支付
    public static String BasePayHttpUrl = "https://pay.ylqzb.com/index.php/v1/";

    private static volatile LiveHttpManager sInstance;

    private LiveHttpManager() {
    }

    public static LiveHttpManager instance() {
        if (sInstance == null) {
            synchronized (LiveHttpManager.class) {
                if (sInstance == null) {
                    sInstance = new LiveHttpManager();
                }
            }
        }
        return sInstance;
    }


}
