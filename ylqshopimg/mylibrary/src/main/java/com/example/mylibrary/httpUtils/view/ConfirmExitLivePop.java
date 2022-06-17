package com.example.mylibrary.httpUtils.view;

import android.app.Activity;
import android.content.Context;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.example.mylibrary.R;
import com.example.mylibrary.httpUtils.utils.FastClickUtils;
import com.example.mylibrary.httpUtils.utils.SPUtil;
import com.example.mylibrary.httpUtils.utils.UserUtils;

import java.lang.ref.WeakReference;

/**
 * Created by 孟晨 on 2018/8/22.
 */

public class ConfirmExitLivePop {

    private View popView;
    private View shareView = null;
    private PopupWindow popWindow;
    private WeakReference<Context> weakReference;
    private ClickCallback clickCallback;
    private String showTex = null;

    public void setShowTex(String showTex) {
        this.showTex = showTex;
    }

    public ConfirmExitLivePop(Context baseActivity) {
        if (weakReference != null) {
            weakReference.clear();
            weakReference = null;
        }
        weakReference = new WeakReference(baseActivity);
    }

    //显示弹窗
    public PopupWindow showPop(ClickCallback clickCallback) {
        this.clickCallback = clickCallback;
        final Context baseActivity = weakReference.get();
        if (baseActivity != null) {
            popView = LayoutInflater.from(baseActivity)
                    .inflate(R.layout.layout_confirm_exit_live, null, false);
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
                    dismissPop();
                }
            });

            //提示
            final ImageView img_no_ts = popView.findViewById(R.id.img_no_ts);
            final LinearLayout ll_no_ts = popView.findViewById(R.id.ll_no_ts);
            ll_no_ts.setTag(false);
            ll_no_ts.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if(FastClickUtils.isFastClick()){
                        return;
                    }
                    boolean select = (boolean) ll_no_ts.getTag();
                    img_no_ts.setImageResource(select ? R.drawable.img_circle_false : R.drawable.img_circle_true);
                    ll_no_ts.setTag(!select);
                }
            });

            //换个看看
            TextView tv_change_look = popView.findViewById(R.id.tv_change_look);
            tv_change_look.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if(FastClickUtils.isFastClick()){
                        return;
                    }
                    SPUtil.put(baseActivity, UserUtils.SHOW_EXIT_POP, ll_no_ts.getTag() + "");
                    if (ConfirmExitLivePop.this.clickCallback != null) {
                        ConfirmExitLivePop.this.clickCallback.clickLookNext();
                        dismissPop();
                    }
                }
            });

            //残忍退出
            TextView tv_exit = popView.findViewById(R.id.tv_exit);
            tv_exit.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if(FastClickUtils.isFastClick()){
                        return;
                    }
                    SPUtil.put(baseActivity, UserUtils.SHOW_EXIT_POP, ll_no_ts.getTag() + "");
                    if (ConfirmExitLivePop.this.clickCallback != null) {
                        ConfirmExitLivePop.this.clickCallback.clickTrueExit();
                        dismissPop();
                    }
                }
            });

            //退出
            RelativeLayout rl_close = popView.findViewById(R.id.rl_close);
            rl_close.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if(FastClickUtils.isFastClick()){
                        return;
                    }
//                    SPUtil.put(baseActivity, UserUtils.SHOW_EXIT_POP, ll_no_ts.getTag() + "");
                    if (ConfirmExitLivePop.this.clickCallback != null) {
//                        ConfirmExitLivePop.this.clickCallback.clickTrueExit();
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
        void clickTrueExit();

        void clickLookNext();
    }
}

