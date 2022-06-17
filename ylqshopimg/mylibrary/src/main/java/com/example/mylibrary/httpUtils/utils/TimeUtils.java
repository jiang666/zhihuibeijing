package com.example.mylibrary.httpUtils.utils;

import android.util.Log;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class TimeUtils {

    public static SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
    public static SimpleDateFormat format1 = new SimpleDateFormat("yy-MM-dd");
    public static SimpleDateFormat format2 = new SimpleDateFormat("yyyy年MM月");
    public static SimpleDateFormat format3 = new SimpleDateFormat("yyyy-MM-dd HH:mm");

    public static String getTimeStart(long startTime) {
        try {
            long nowTime = System.currentTimeMillis();
//            LogUtils.logE("测试时间: ", "当前时间戳=" + nowTime);
            String forS = format.format(startTime*1000);
            String forE = format.format(nowTime);
//            LogUtils.logE("测试时间: ", "开始时间=" + forS);
//            LogUtils.logE("测试时间: ", "当前时间=" + forE);
            Date dateS = format.parse(forS);
            Date dateE = format.parse(forE);
            int date = (int) (dateE.getTime() - dateS.getTime());
            int num = date / 1000;//总共多少s
//            LogUtils.logE("测试时间: ", "s=" + num);
            //0-60 s
            if (num < 60) {
                return "刚刚";
            }
            //0-60 min
            num = date / 1000 / 60;//总共多少m
//            LogUtils.logE("测试时间: ", "m=" + num);
            if (num < 60) {
                return num + "分钟前";
            }
            num = date / 1000 / 60 / 60;//总共多少h
//            LogUtils.logE("测试时间: ", "h=" + num);
            if (num < 24) {
                return num + "小时前";
            }
            num = date / 1000 / 60 / 60 / 24;//总共多少d
//           LogUtils.logE("测试时间: ", "d=" + num);
//            if (num < 31) {
//                return num + "天前";
//            }
//            num = date / 1000 / 60 / 60 / 24 / 31;//总共多少y
////            Log.e("测试时间: ", "y=" + num);
//            if (num < 12) {
//                return num + "月前";
//            } else {
//                return format1.format(startTime*1000);
//            }
            if(num<4){
                return num + "天前";
            }else{
                return format1.format(startTime*1000);
            }
        } catch (ParseException e) {
            e.printStackTrace();
            return format1.format(startTime*1000);
        }
    }

    public static String getCurrentFormatTime(){
        long nowTime = System.currentTimeMillis();
        String forE = format2.format(nowTime);
        return forE;
    }

    public static String fotmatTime(long timestamp){
        return format2.format(timestamp);
    }

    //7位用法
    public static String formatTimeYYr7(long timestamp){
        return format3.format(timestamp*1000);
    }
}
