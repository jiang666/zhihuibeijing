package com.example.mylibrary.httpUtils.utils;

import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.util.Log;

import java.security.MessageDigest;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Comparator;
import java.util.Date;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.util.TimeZone;
import java.util.TreeMap;

//键值对排序：https://www.cnblogs.com/ltb6w/p/7862251.html
public class HttpUtils {

    //android
    public static final String app_type = "2";
    //取多少位的随机数
    public static final int numSize = 16;

    //获取版本号
    public static String getVersionCode(Context context) {
        //获取包管理器
        PackageManager pm = context.getPackageManager();
        //获取包信息
        try {
            PackageInfo packageInfo = pm.getPackageInfo(context.getPackageName(), 0);
            //返回版本号
            return packageInfo.versionCode + "";
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        return "0";
    }

    //获取网络时间戳
    public static String getNetTimeC() {
        SimpleDateFormat dff = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        dff.setTimeZone(TimeZone.getTimeZone("GMT+08"));
        try {
            return dateToStamp(dff.format(new Date()));
        } catch (ParseException e) {
            e.printStackTrace();
            return "";
        }
    }

    //将时间转换为时间戳
    public static String dateToStamp(String s) throws ParseException {
        String res;
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        Date date = simpleDateFormat.parse(s);
        long ts = date.getTime();
        res = String.valueOf(ts);
        return res;
    }

    //将时间戳转换为时间
    public static String stampToDate(String s) {
        String res;
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        long lt = new Long(s);
        Date date = new Date(lt);
        res = simpleDateFormat.format(date);
        return res;
    }

    /*产生numSize位16进制的数*/
    public static String getRandomValue() {
        String str = "";
        for (int i = 0; i < numSize; i++) {
            char temp = 0;
            int key = (int) (Math.random() * 2);
            switch (key) {
                case 0:
                    temp = (char) (Math.random() * 10 + 48);//产生随机数字
                    break;
                case 1:
                    temp = (char) (Math.random() * 6 + 'a');//产生a-f
                    break;
                default:
                    break;
            }
            str = str + temp;
        }
        return str;
    }

    //获取签名sign
    public static String getSign(Map<String, String> map) {
        StringBuilder sign = new StringBuilder();
        Set<String> keySet = map.keySet();
        Iterator<String> iter = keySet.iterator();
        while (iter.hasNext()) {
            String key = iter.next();
            sign.append(key + "=" + map.get(key) + "&");
        }
        sign.append("dbdbf3eeebd4f4f54f6256e9cf9de5ed");//app_key
        LogUtils.logE("测试: ", "签名=" + sign.toString());
        return encrypt32(sign.toString());
    }

    //MD5 32位小写加密
    public static String encrypt32(String encryptStr) {
        MessageDigest md5;
        try {
            md5 = MessageDigest.getInstance("MD5");
            byte[] md5Bytes = md5.digest(encryptStr.getBytes());
            StringBuffer hexValue = new StringBuffer();
            for (int i = 0; i < md5Bytes.length; i++) {
                int val = ((int) md5Bytes[i]) & 0xff;
                if (val < 16)
                    hexValue.append("0");
                hexValue.append(Integer.toHexString(val));
            }
            encryptStr = hexValue.toString();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        return encryptStr;
    }

    //获取公共的网络参数map
    public static Map<String, String> getPublicApiMap(Context context) {
        Map<String, String> map = new TreeMap<String, String>(new Comparator<String>() {
            public int compare(String obj1, String obj2) {
                return obj2.compareTo(obj1);//降序排序
            }
        });
        map.put("app_version", HttpUtils.getVersionCode(context));
        map.put("app_type", HttpUtils.app_type);
        map.put("timestamp", HttpUtils.getNetTimeC());
        map.put("randomstr", HttpUtils.getRandomValue());
        return map;
    }
}
