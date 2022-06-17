package com.example.mylibrary.httpUtils.utils;

import android.annotation.TargetApi;
import android.app.Activity;
import android.graphics.Color;
import android.os.Build;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;

import java.lang.reflect.Field;
import java.lang.reflect.Method;

/**
 * Created by 孟晨 on 2018/5/11.
 */

public class StatusBarUtil {

    /**
     * 修改状态栏为全透明
     */
    @TargetApi(19)
    public static void transparencyBar(Activity activity) {
        //FIXME 1.21以上
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            Window window = activity.getWindow();
            //清除透明状态
            window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
            //FIXME View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
            //Activity全屏显示，状态栏不会被覆盖掉，正常显示
            //Activity顶端布局会被覆盖住（2种方式清除覆盖）
            //1.让状态栏的颜色为透明
            /*状态栏为透明
            <item name="android:windowTranslucentStatus">true</item>
            导航栏为透明
            <item name="android:windowTranslucentNavigation">true</item>
            状态栏颜色为透明
            <item name="android:statusBarColor">@android:color/transparent</item>*/
            //2.不设置android:fitsSystemWindows=“true”,让布局延伸上去,但是布局自己流出状态栏高度的空间20dp
            //FIXME View.SYSTEM_UI_FLAG_LAYOUT_STABLE 不管是否延伸布局，都给状态栏留空间
            window.getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                    | View.SYSTEM_UI_FLAG_LAYOUT_STABLE);
            //API文档：需要同步设置WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS这个Window Flag
            //并且需要保证WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS这个Window Flag没有被设置。否则，不会生效。
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            //状态栏透明
            window.setStatusBarColor(Color.TRANSPARENT);
        }
        //FIXME 2.19版本以上
        else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            Window window = activity.getWindow();
            //状态栏透明
            window.addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
            //这B同时添加2个
//            window.setFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS,
//                    WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        }
    }

    /**
     * 状态栏亮色模式，设置状态栏黑色文字、图标，
     * 适配4.4以上版本MIUIV、Flyme和6.0以上版本其他Android
     *
     * @param activity
     * @return 1:MIUUI 2:Flyme 3:android6.0
     */
    public static int StatusBarLightMode(Activity activity) {
        int result = 0;
        //FIXME 1.19版本以上
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            if (MIUISetStatusBarLightMode(activity, true)) {
                //小米
                result = 1;
            } else if (FlymeSetStatusBarLightMode(activity.getWindow(), true)) {
                //魅族
                result = 2;
            }
            //23版本以上
            else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                //FIXME 黑色的文字
                activity.getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN |
                        View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);
                result = 3;
            } else {
                //其他的都设置状态栏成半透明的,以下设置半透明是调用第三方的，根据个人需求更改
            }
        }
        return result;
    }

    //白色的文字
    public static int StatusBarDarkMode(Activity activity) {
        int result = 0;
        //FIXME 1.19版本以上
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            if (MIUISetStatusBarLightMode(activity, false)) {
                //小米
                result = 1;
            } else if (FlymeSetStatusBarLightMode(activity.getWindow(), false)) {
                //魅族
                result = 2;
            }
            //23版本以上
            else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                //FIXME 清除黑色文字的标记
                activity.getWindow().clearFlags(View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);
                activity.getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN);
                result = 3;
            } else {
                //其他的都设置状态栏成半透明的,以下设置半透明是调用第三方的，根据个人需求更改
            }
        }
        return result;
    }

    /**
     * 需要MIUIV6以上
     * @param activity
     * @param dark     是否把状态栏文字及图标颜色设置为深色
     * @return boolean 成功执行返回true
     */
    public static boolean MIUISetStatusBarLightMode(Activity activity, boolean dark) {
        boolean result = false;
        Window window = activity.getWindow();
        if (window != null) {
            Class clazz = window.getClass();
            try {
                int darkModeFlag = 0;
                Class layoutParams = Class.forName("android.view.MiuiWindowManager$LayoutParams");
                Field field = layoutParams.getField("EXTRA_FLAG_STATUS_BAR_DARK_MODE");
                darkModeFlag = field.getInt(layoutParams);
                Method extraFlagField = clazz.getMethod("setExtraFlags", int.class, int.class);
                if (dark) {
                    extraFlagField.invoke(window, darkModeFlag, darkModeFlag);//状态栏透明且黑色字体
                } else {
                    extraFlagField.invoke(window, 0, darkModeFlag);//清除黑色字体
                }
                result = true;

                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    //开发版 7.7.13 及以后版本采用了系统API，旧方法无效但不会报错，所以两个方式都要加上
                    if (dark) {
                        activity.getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN |
                                View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);
                    } else {
                        activity.getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_VISIBLE);
                    }
                }
            } catch (Exception e) {

            }
        }
        return result;
    }


    /**
     * 设置状态栏图标为深色和魅族特定的文字风格
     * 可以用来判断是否为Flyme用户
     *
     * @param window 需要设置的窗口
     * @param dark   是否把状态栏文字及图标颜色设置为深色
     * @return boolean 成功执行返回true
     */
    public static boolean FlymeSetStatusBarLightMode(Window window, boolean dark) {
        boolean result = false;
        if (window != null) {
            try {
                WindowManager.LayoutParams lp = window.getAttributes();
                Field darkFlag = WindowManager.LayoutParams.class
                        .getDeclaredField("MEIZU_FLAG_DARK_STATUS_BAR_ICON");
                Field meizuFlags = WindowManager.LayoutParams.class
                        .getDeclaredField("meizuFlags");
                darkFlag.setAccessible(true);
                meizuFlags.setAccessible(true);
                int bit = darkFlag.getInt(null);
                int value = meizuFlags.getInt(lp);
                if (dark) {
                    value |= bit;
                } else {
                    value &= ~bit;
                }
                meizuFlags.setInt(lp, value);
                window.setAttributes(lp);
                result = true;
            } catch (Exception e) {

            }
        }
        return result;
    }
}