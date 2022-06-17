package com.example.mylibrary.httpUtils.utils;


import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.os.Build;
import android.util.Log;
import android.view.Display;

import java.io.UnsupportedEncodingException;
import java.lang.reflect.Method;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.List;

public class AppUtils {

    //不受系统显示大小影响
    public static void setDefaultDisplay(Context context) {
        if (Build.VERSION.SDK_INT > 23) {
            Configuration origConfig = context.getResources().getConfiguration();
            origConfig.densityDpi = getDefaultDisplayDensity();//获取手机出厂时默认的densityDpi
            context.getResources().updateConfiguration(origConfig, context.getResources().getDisplayMetrics());
        }
    }

    //获取手机出厂时默认的densityDpi
    public static int getDefaultDisplayDensity() {
        try {
            Class<?> aClass = Class.forName("android.view.WindowManagerGlobal");
            Method method = aClass.getMethod("getWindowManagerService");
            method.setAccessible(true);
            Object iwm = method.invoke(aClass);
            Method getInitialDisplayDensity = iwm.getClass().getMethod("getInitialDisplayDensity", int.class);
            getInitialDisplayDensity.setAccessible(true);
            Object densityDpi = getInitialDisplayDensity.invoke(iwm, Display.DEFAULT_DISPLAY);
            return (int) densityDpi;
        } catch (Exception e) {
            e.printStackTrace();
            return -1;
        }
    }

    public static void saveLiveTitle(Context context, List<String> list) {
        if (list.size() > 0) {
            StringBuilder builder = new StringBuilder();
            for (int i = 0; i < list.size(); i++) {
                builder.append(list.get(i) + ",");
            }
            SPUtil.put(context, UserUtils.LIVE_BQS, builder.toString());
        }
    }

    //对key进行md5加密
    public static String getMD5(String string) {
        byte[] hash;
        try {
            hash = MessageDigest.getInstance("MD5").digest(string.getBytes("UTF-8"));
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
            return null;
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
            return null;
        }
        StringBuilder hex = new StringBuilder(hash.length * 2);
        for (byte b : hash) {
            if ((b & 0xFF) < 0x10) {
                hex.append("0");
            }
            hex.append(Integer.toHexString(b & 0xFF));
        }
        return hex.toString();
    }

    //处理流的地址
    public static String getUri(String url) {
        String result = "";
        String temp = url.substring(7);
        if (temp != null && !temp.isEmpty()) {
            result = temp.substring(temp.indexOf("/"));
        }
        return result;
    }

    //判断是否是自己的uid
    public static boolean isSelfUid(Context context, String OtherUid) {
        String selfUid = (String) SPUtil.get(context, UserUtils.UID, "");
        if (selfUid.equals(OtherUid)) {
            return true;
        }
        return false;
    }

    //打印数组
    public static void logArray(Object[] array, String logTex) {
        for (int i = 0; i < array.length; i++) {
            LogUtils.logE(logTex, "数据" + i + "=" + array[i]);
        }
    }

    //打印集合
    public static void logList(List<String> list, String logTex) {
        for (int i = 0; i < list.size(); i++) {
            LogUtils.logE(logTex, "数据" + i + "=" + list.get(i));
        }
    }

    public static boolean isHuaWei_Vivo() {
        String manufacturer = Build.MANUFACTURER;
        if ("huawei".equalsIgnoreCase(manufacturer)) {
            return true;
        }
        return false;
    }

    public static boolean isMEIZU() {
        String manufacturer = Build.MANUFACTURER;
        if ("meizu".equalsIgnoreCase(manufacturer)) {
            return true;
        }
        return false;
    }

    /**
     * 获取版本名称
     *
     * @param context 上下文
     * @return 版本名称
     */
    public static String getVersionName(Context context) {

        //获取包管理器
        PackageManager pm = context.getPackageManager();
        //获取包信息
        try {
            PackageInfo packageInfo = pm.getPackageInfo(context.getPackageName(), 0);
            //返回版本号
            return packageInfo.versionName;
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }

        return null;

    }

    /**
     * 获取版本号
     *
     * @param context 上下文
     * @return 版本号
     */
    public static int getVersionCode(Context context) {

        //获取包管理器
        PackageManager pm = context.getPackageManager();
        //获取包信息
        try {
            PackageInfo packageInfo = pm.getPackageInfo(context.getPackageName(), 0);
            //返回版本号
            return packageInfo.versionCode;
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }

        return 0;

    }

    /**
     * 获取App的名称
     *
     * @param context 上下文
     * @return 名称
     */
    public static String getAppName(Context context) {
        PackageManager pm = context.getPackageManager();
        //获取包信息
        try {
            PackageInfo packageInfo = pm.getPackageInfo(context.getPackageName(), 0);
            //获取应用 信息
            ApplicationInfo applicationInfo = packageInfo.applicationInfo;
            //获取albelRes
            int labelRes = applicationInfo.labelRes;
            //返回App的名称
            return context.getResources().getString(labelRes);
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }

        return null;
    }
}
