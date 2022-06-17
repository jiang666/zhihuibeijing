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
import android.widget.TextView;

import com.example.mylibrary.R;
import com.example.mylibrary.httpUtils.utils.FastClickUtils;
import com.example.mylibrary.httpUtils.utils.SPUtil;
import com.example.mylibrary.httpUtils.utils.UserUtils;

import java.lang.ref.WeakReference;

/**
 * Created by 孟晨 on 2018/8/22.
 */

public class LiveToolsPop {

    private View popView;
    private PopupWindow popWindow;
    private WeakReference<Context> weakReference;
    private ClickCallback clickCallback;
    //公告
    private TextView tv_gg;
    //闪光头
    private TextView tv_flash;

    public LiveToolsPop(Context baseActivity) {
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
                    .inflate(R.layout.layout_live_tools, null, false);

            TextView tv_bg = popView.findViewById(R.id.tv_bg);
            tv_bg.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if(FastClickUtils.isFastClick()){
                        return;
                    }
                    dismissPop();
                }
            });

            //分享
            LinearLayout ll_share = popView.findViewById(R.id.ll_share);
            ll_share.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if(FastClickUtils.isFastClick()){
                        return;
                    }
                    if (LiveToolsPop.this.clickCallback != null) {
                        LiveToolsPop.this.clickCallback.share();
                        dismissPop();
                    }
                }
            });

            //直播间公告
            final String gg = (SPUtil.get(baseActivity, UserUtils.TOOLS_GG, "0")).equals("0") ? "直播间公告" : "删除公告";
            LinearLayout ll_gg = popView.findViewById(R.id.ll_gg);
            tv_gg = popView.findViewById(R.id.tv_gg);
            tv_gg.setText(gg);
            ll_gg.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if(FastClickUtils.isFastClick()){
                        return;
                    }
                    if (LiveToolsPop.this.clickCallback != null) {
                        LiveToolsPop.this.clickCallback.gg(tv_gg.getText().toString());
                    }
                }
            });

            //美颜 fixme 隐藏
            LinearLayout ll_my = popView.findViewById(R.id.ll_my);
            ll_my.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if(FastClickUtils.isFastClick()){
                        return;
                    }
                    if (LiveToolsPop.this.clickCallback != null) {
                        LiveToolsPop.this.clickCallback.my();
                        dismissPop();
                    }
                }
            });

            //闪光头
            final String flashTex = (SPUtil.get(baseActivity, UserUtils.TOOLS_FLASH, "0")).equals("0") ? "闪光头" : "关闪光头";
            LinearLayout ll_flash = popView.findViewById(R.id.ll_flash);
            tv_flash = popView.findViewById(R.id.tv_flash);
            tv_flash.setText(flashTex);
            ll_flash.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if(FastClickUtils.isFastClick()){
                        return;
                    }
                    if (LiveToolsPop.this.clickCallback != null) {
                        LiveToolsPop.this.clickCallback.flash(flashTex);
                        dismissPop();
                    }
                }
            });

            //关镜像
            final String jxTex = (SPUtil.get(baseActivity, UserUtils.TOOLS_JX, "0")).equals("0") ? "开镜像" : "关镜像";
            LinearLayout ll_jx = popView.findViewById(R.id.ll_jx);
            TextView tv_jx = popView.findViewById(R.id.tv_jx);
            tv_jx.setText(jxTex);
            ll_jx.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if(FastClickUtils.isFastClick()){
                        return;
                    }
                    if (LiveToolsPop.this.clickCallback != null) {
                        LiveToolsPop.this.clickCallback.jx(jxTex);
                        dismissPop();
                    }
                }
            });

            //翻转
            LinearLayout ll_camrea = popView.findViewById(R.id.ll_camrea);
            ll_camrea.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if(FastClickUtils.isFastClick()){
                        return;
                    }
                    if (LiveToolsPop.this.clickCallback != null) {
                        LiveToolsPop.this.clickCallback.camera();
                        dismissPop();
                    }
                }
            });

            //静音
            final String jyTex = (SPUtil.get(baseActivity, UserUtils.TOOLS_NO_SOUND, "0")).equals("0") ? "静音" : "开声音";
            LinearLayout ll_jy = popView.findViewById(R.id.ll_jy);
            TextView tv_jy = popView.findViewById(R.id.tv_jy);
            ImageView img_jy = popView.findViewById(R.id.img_jy);
            if (jyTex.equals("静音")) {
                img_jy.setImageResource(R.drawable.img_tools_jy);
            } else {
                img_jy.setImageResource(R.drawable.img_tools_not_ky);
            }
            tv_jy.setText(jyTex);
            ll_jy.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if(FastClickUtils.isFastClick()){
                        return;
                    }
                    if (LiveToolsPop.this.clickCallback != null) {
                        LiveToolsPop.this.clickCallback.jy(jyTex);
                        dismissPop();
                    }
                }
            });

            //大字体
            final String bigTex = (SPUtil.get(baseActivity, UserUtils.TOOLS_BIG_TEX, "0")).equals("0") ? "大字体" : "小字体";
            TextView tv_size = popView.findViewById(R.id.tv_size);
            tv_size.setText(bigTex);
            ImageView img_bigtex = popView.findViewById(R.id.img_bigtex);
            if (bigTex.equals("大字体")) {
                img_bigtex.setImageResource(R.drawable.img_tools_big_tex);
            } else {
                img_bigtex.setImageResource(R.drawable.img_tools_push_small_tex);
            }
            LinearLayout ll_big_tex = popView.findViewById(R.id.ll_big_tex);
            ll_big_tex.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if(FastClickUtils.isFastClick()){
                        return;
                    }
                    if (LiveToolsPop.this.clickCallback != null) {
                        LiveToolsPop.this.clickCallback.bigTex(bigTex);
                        dismissPop();
                    }
                }
            });

            //清屏
            LinearLayout ll_qp = popView.findViewById(R.id.ll_qp);
            ll_qp.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if(FastClickUtils.isFastClick()){
                        return;
                    }
                    if (LiveToolsPop.this.clickCallback != null) {
                        LiveToolsPop.this.clickCallback.qp();
                        dismissPop();
                    }
                }
            });

            //唱歌
            LinearLayout ll_song = popView.findViewById(R.id.ll_song);
            ll_song.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if(FastClickUtils.isFastClick()){
                        return;
                    }
                    if (LiveToolsPop.this.clickCallback != null) {
                        LiveToolsPop.this.clickCallback.song();
                        dismissPop();
                    }
                }
            });

            //背景音乐
            LinearLayout ll_bg_music = popView.findViewById(R.id.ll_bg_music);
            ll_bg_music.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if(FastClickUtils.isFastClick()){
                        return;
                    }
                    if (LiveToolsPop.this.clickCallback != null) {
                        LiveToolsPop.this.clickCallback.bgMusic();
                        dismissPop();
                    }
                }
            });

            //礼物转盘
            LinearLayout ll_lv = popView.findViewById(R.id.ll_lv);
            ll_lv.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if(FastClickUtils.isFastClick()){
                        return;
                    }
                    if (LiveToolsPop.this.clickCallback != null) {
                        LiveToolsPop.this.clickCallback.lw();
                        dismissPop();
                    }
                }
            });

            //ll_xyd
            LinearLayout ll_xyd = popView.findViewById(R.id.ll_xyd);
            ll_xyd.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if(FastClickUtils.isFastClick()){
                        return;
                    }
                    if (LiveToolsPop.this.clickCallback != null) {
                        LiveToolsPop.this.clickCallback.xyd();
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

    //更新为未可发布公告
    public void updateGG(String tex) {
        tv_gg.setText(tex);
    }

    //-----------------------------------回调-----------------------------------
    public interface ClickCallback {
        void share();

        void gg(String tex);

        void my();

        void jx(String jxTex);

        void camera();

        void jy(String jyTex);

        void bigTex(String bigTex);

        void qp();

        void flash(String tex);

        void song();

        void bgMusic();

        void lw();

        void xyd();
    }
}

