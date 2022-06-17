package com.example.mylibrary.httpUtils.view;

import android.app.Activity;
import android.content.Context;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.PopupWindow;
import android.widget.TextView;

import com.example.mylibrary.R;
import com.example.mylibrary.httpUtils.utils.FastClickUtils;

import java.lang.ref.WeakReference;

/**
 * Created by 孟晨 on 2018/8/22.
 */

public class OutPersonPop {

    private View popView;
    private View shareView = null;
    private PopupWindow popWindow;
    private WeakReference<Context> weakReference;
    private ClickCallback clickCallback;
    private String topText;
    private int isNoSpeck;

    public OutPersonPop(Context baseActivity,int isNoSpeck) {
        if (weakReference != null) {
            weakReference.clear();
            weakReference = null;
        }
        weakReference = new WeakReference(baseActivity);
        this.isNoSpeck = isNoSpeck;
    }

    //显示弹窗
    public PopupWindow showPop(ClickCallback clickCallback) {
        this.clickCallback = clickCallback;
        Context baseActivity = weakReference.get();
        if (baseActivity != null) {
            popView = LayoutInflater.from(baseActivity)
                    .inflate(R.layout.pop_out_person, null, false);


            //踢人
            TextView tv_jb = popView.findViewById(R.id.tv_jb);
            tv_jb.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if(FastClickUtils.isFastClick()){
                        return;
                    }
                    if (OutPersonPop.this.clickCallback != null) {
                        OutPersonPop.this.clickCallback.outPerson();
                        dismissPop();
                    }
                }
            });

            //禁言
            TextView tv_lh = popView.findViewById(R.id.tv_lh);
            if(OutPersonPop.this.isNoSpeck==0){
                tv_lh.setText("禁言");
            }else {
                tv_lh.setText("取消禁言");
            }
            tv_lh.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if(FastClickUtils.isFastClick()){
                        return;
                    }
                    if (OutPersonPop.this.clickCallback != null) {
                        OutPersonPop.this.clickCallback.notSpeck(OutPersonPop.this.isNoSpeck);
                        dismissPop();
                    }
                }
            });

            //取消
            TextView tv_bg = popView.findViewById(R.id.tv_bg);
            tv_bg.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if(FastClickUtils.isFastClick()){
                        return;
                    }
                    if (OutPersonPop.this.clickCallback != null) {
                        dismissPop();
                    }
                }
            });

            //取消
            TextView tv_cancel = popView.findViewById(R.id.tv_cancel);
            tv_cancel.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if(FastClickUtils.isFastClick()){
                        return;
                    }
                    if (OutPersonPop.this.clickCallback != null) {
                        dismissPop();
                    }
                }
            });


            popWindow = new PopupWindow(popView,
                    ViewGroup.LayoutParams.MATCH_PARENT,
                    ViewGroup.LayoutParams.MATCH_PARENT,//(int) (WindowParamUtils.screenHeight(baseActivity) * 0.9
                    true);
            popWindow.setAnimationStyle(R.style.popwin_anim_style);
            //点击外部，popupwindow会消失
            popWindow.setFocusable(false);
            popWindow.setOutsideTouchable(false);
//            popWindow.setBackgroundDrawable(new ColorDrawable(0xffffffff));
            popWindow.showAtLocation(popView, Gravity.BOTTOM | Gravity.CENTER_HORIZONTAL, 0, 0);
            showPopBlackBg(baseActivity);
        }
        return popWindow;
    }

    //----------------------------------------------背景颜色的改变----------------------------------
    //初始化弹窗的背景色
    public void showPopBlackBg(Context baseActivity) {
        if (baseActivity != null) {
            changePopBlackBg(0.8F);//越大越浅
        }
    }

    //恢复弹窗的背景色
    public void backNormalPopBg() {
        Context baseActivity = weakReference.get();
        if (baseActivity != null) {
            WindowManager.LayoutParams lp = ((Activity) baseActivity).getWindow().getAttributes();
            lp.alpha = 1f;
            ((Activity) baseActivity).getWindow().setAttributes(lp);
        }
    }

    //弹窗背景颜色
    public void changePopBlackBg(float blackBg) {
        Context baseActivity = weakReference.get();
        if (baseActivity != null) {
            WindowManager.LayoutParams lp = ((Activity) baseActivity).getWindow().getAttributes();
            lp.alpha = blackBg;
            ((Activity) baseActivity).getWindow().setAttributes(lp);
        }
    }

    public void dismissPop() {
        if (popWindow.isShowing()) {
            popWindow.dismiss();
            popWindow = null;
            backNormalPopBg();
        }
    }


    //-----------------------------------回调-----------------------------------
    public interface ClickCallback {
        void outPerson();

        void notSpeck(int isNoSpeck);
    }
}

