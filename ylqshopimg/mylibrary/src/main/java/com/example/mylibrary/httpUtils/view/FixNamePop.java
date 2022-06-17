package com.example.mylibrary.httpUtils.view;

import android.app.Activity;
import android.content.Context;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.mylibrary.R;
import com.example.mylibrary.httpUtils.utils.FastClickUtils;

import java.lang.ref.WeakReference;

/**
 * Created by 孟晨 on 2018/8/22.
 */

public class FixNamePop {

    private View popView;
    private PopupWindow popWindow;
    private WeakReference<Context> weakReference;
    private ClickCallback clickCallback;


    public FixNamePop(Context baseActivity) {
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
                    .inflate(R.layout.layout_fix_name, null, false);

            //布局
            RelativeLayout rl_container = popView.findViewById(R.id.ll_container);
            rl_container.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if(FastClickUtils.isFastClick()){
                        return;
                    }
                    if (FixNamePop.this.clickCallback != null) {
                        FixNamePop.this.clickCallback.clickClose();
                        dismissPop();
                    }
                }
            });

            final EditText et_name = popView.findViewById(R.id.et_name);
            if(name!=null&&!name.equals("")){
                et_name.setHint(name);
            }

            //取消
            TextView tv_cancel = popView.findViewById(R.id.tv_cancel);
            tv_cancel.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if(FastClickUtils.isFastClick()){
                        return;
                    }
                    if (FixNamePop.this.clickCallback != null) {
                        FixNamePop.this.clickCallback.clickClose();
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
                    String tex = et_name.getText().toString();
                    if(tex.length()>15){
                        Toast.makeText(baseActivity, "昵称长度不可以超过15个字符!", Toast.LENGTH_SHORT).show();
                        return;
                    }
                    if(et_name.getText().toString().trim().equals("")){
                        Toast.makeText(baseActivity.getApplicationContext(), "请先输入昵称", Toast.LENGTH_SHORT).show();
                        return;
                    }
                    if (FixNamePop.this.clickCallback != null) {
                        FixNamePop.this.clickCallback.clickTrueDelect(et_name.getText().toString());
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
            popWindow.setFocusable(true);
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

    private String name;
    public void setName(String toString) {
        this.name = toString;
    }

    //-----------------------------------回调-----------------------------------
    public interface ClickCallback {
        void clickTrueDelect(String name);

        void clickClose();
    }
}

