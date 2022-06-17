package com.example.mylibrary.httpUtils.view;

import android.app.Activity;
import android.content.Context;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.example.mylibrary.R;
import com.example.mylibrary.httpUtils.utils.FastClickUtils;

import java.lang.ref.WeakReference;

/**
 * Created by 孟晨 on 2018/8/22.
 */

public class ConfirmOutNoSpeckPop {

    private View popView;
    private int isNoSpeck;
    private View shareView = null;
    private PopupWindow popWindow;
    private WeakReference<Context> weakReference;
    private ClickCallback clickCallback;
    private String showTex = null;

    public void setShowTex(String showTex) {
        this.showTex = showTex;
    }

    public ConfirmOutNoSpeckPop(Context baseActivity, final int isNoSpeck) {
        if (weakReference != null) {
            weakReference.clear();
            weakReference = null;
        }
        weakReference = new WeakReference(baseActivity);
        this.isNoSpeck = isNoSpeck;
    }

    //显示弹窗
    public PopupWindow showPop(int type,ClickCallback clickCallback,String name) {
        this.clickCallback = clickCallback;
        Context baseActivity = weakReference.get();
        if (baseActivity != null) {
            popView = LayoutInflater.from(baseActivity)
                    .inflate(R.layout.layout_confirm_outperson_nospeck, null, false);

            if (this.showTex != null) {
                ((TextView) popView.findViewById(R.id.tv_tex)).setText(this.showTex);
            }

            //布局
            RelativeLayout rl_container = popView.findViewById(R.id.ll_container);
            rl_container.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if(FastClickUtils.isFastClick()){
                        return;
                    }
                    if (ConfirmOutNoSpeckPop.this.clickCallback != null) {
                        ConfirmOutNoSpeckPop.this.clickCallback.clickClose();
                        dismissPop();
                    }
                }
            });

            if(name.length()>6){
                name = name.substring(0, 6);
            }

            TextView tv_tex = popView.findViewById(R.id.tv_tex);
            TextView tv_tex1 = popView.findViewById(R.id.tv_tex1);
            if(type==1){
                //禁言
                if(isNoSpeck==0){
                    tv_tex.setText("您确定将"+name+"禁言吗？");
                    tv_tex1.setText("禁言后，此用户15分钟内不可在此房间发言");
                }else{
                    tv_tex.setText("确定取消"+name+"的禁言吗？");
                    tv_tex1.setVisibility(View.GONE);
                }
            }else if(type==2){
                tv_tex.setText("您确定将"+name+"踢出吗？");
                tv_tex1.setText("踢出后，此用户60分钟内不可进入此房间");
            }

            //取消
            TextView tv_cancel = popView.findViewById(R.id.tv_cancel);
            tv_cancel.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if(FastClickUtils.isFastClick()){
                        return;
                    }
                    if (ConfirmOutNoSpeckPop.this.clickCallback != null) {
                        ConfirmOutNoSpeckPop.this.clickCallback.clickClose();
                        dismissPop();
                    }
                }
            });

            //确定
            TextView tv_true = popView.findViewById(R.id.tv_true);
            tv_true.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if(FastClickUtils.isFastClick()){
                        return;
                    }
                    if (ConfirmOutNoSpeckPop.this.clickCallback != null) {
                        ConfirmOutNoSpeckPop.this.clickCallback.clickTrueDelect();
                        dismissPop();
                    }
                }
            });

            popWindow = new PopupWindow(popView,
                    ViewGroup.LayoutParams.MATCH_PARENT,
                    ViewGroup.LayoutParams.MATCH_PARENT,//(int) (WindowParamUtils.screenHeight(baseActivity) * 0.9
                    true);
//            popWindow.setAnimationStyle(R.style.popwin_anim_style);
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
            WindowManager.LayoutParams lp = ((Activity)baseActivity).getWindow().getAttributes();
            lp.alpha = 1f;
            ((Activity)baseActivity).getWindow().setAttributes(lp);
        }
    }

    //弹窗背景颜色
    public void changePopBlackBg(float blackBg) {
        Context baseActivity = weakReference.get();
        if (baseActivity != null) {
            WindowManager.LayoutParams lp = ((Activity)baseActivity).getWindow().getAttributes();
            lp.alpha = blackBg;
            ((Activity)baseActivity).getWindow().setAttributes(lp);
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
        void clickTrueDelect();

        void clickClose();
    }
}

