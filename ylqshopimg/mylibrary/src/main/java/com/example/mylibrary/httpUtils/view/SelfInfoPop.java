package com.example.mylibrary.httpUtils.view;

import android.app.Activity;
import android.content.Context;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.PopupWindow;
import android.widget.TextView;

import com.example.mylibrary.R;
import com.example.mylibrary.httpUtils.http.live.bean.HostroomUserInfo;
import com.example.mylibrary.httpUtils.utils.FastClickUtils;
import com.example.mylibrary.httpUtils.utils.GlideUtils;

import java.lang.ref.WeakReference;

/**
 * Created by 孟晨 on 2018/8/22.
 */

public class SelfInfoPop {

    private View popView;
    private PopupWindow popWindow;
    private WeakReference<Context> weakReference;
    private ClickCallback clickCallback;
    private HostroomUserInfo hostroomUserInfo;
    private boolean isHost;//是主播
    private boolean isSelf;//是自己
    private TextView tv_attention;
    private TextView sGift_and_setManager;
    private String hostId = "";//主播id

    public SelfInfoPop(Context baseActivity, HostroomUserInfo hostroomUserInfo, boolean isHost, boolean isSelf) {
        if (weakReference != null) {
            weakReference.clear();
            weakReference = null;
        }
        weakReference = new WeakReference(baseActivity);
        this.hostroomUserInfo = hostroomUserInfo;
        this.isHost = isHost;
        this.isSelf = isSelf;
    }

    //显示弹窗
    public PopupWindow showPop(ClickCallback clickCallback) {
        this.clickCallback = clickCallback;
        final Context baseActivity = weakReference.get();
        if (baseActivity != null) {
            popView = LayoutInflater.from(baseActivity)
                    .inflate(R.layout.layout_self_info, null, false);

            TextView tv_bg = popView.findViewById(R.id.tv_bg);
            tv_bg.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if(FastClickUtils.isFastClick()){
                        return;
                    }
                    if (SelfInfoPop.this.clickCallback != null) {
                        dismissPop();
                    }
                }
            });

            //自己框不展示线
            TextView tv_line = popView.findViewById(R.id.tv_line);

            //头像
            CircleImageView img_user = popView.findViewById(R.id.img_user);
            GlideUtils.glideCirclePerson(baseActivity, hostroomUserInfo.getData().getHead_url(), img_user);
            img_user.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if(FastClickUtils.isFastClick()){
                        return;
                    }
                    if (SelfInfoPop.this.clickCallback != null) {
                        SelfInfoPop.this.clickCallback.clickImg(hostroomUserInfo.getData().getU_id() + "");
                    }
                }
            });

            //姓名
            TextView tv_name = popView.findViewById(R.id.tv_name);
            tv_name.setText(hostroomUserInfo.getData().getNick_name());

            //性别 （0保密，1男，2女）
            ImageView img_sex = popView.findViewById(R.id.img_sex);
            int sex = hostroomUserInfo.getData().getSex();
            if (sex == 0) {
                img_sex.setVisibility(View.GONE);
            } else if (sex == 1) {
                img_sex.setVisibility(View.VISIBLE);
                img_sex.setImageResource(R.drawable.img_rect_boy);
            } else if (sex == 2) {
                img_sex.setVisibility(View.VISIBLE);
                img_sex.setImageResource(R.drawable.img_rect_girl);
            }

            //签名
            TextView tv_des = popView.findViewById(R.id.tv_des);
            tv_des.setVisibility(View.VISIBLE);
            if(!hostroomUserInfo.getData().getSignature().equals("")){
                tv_des.setText(hostroomUserInfo.getData().getSignature());
            }

            //用户等级
            ImageView img_user_dj = popView.findViewById(R.id.img_user_dj);
            if (hostroomUserInfo.getData().getUser_level_icon_url().equals("")) {
                img_user_dj.setVisibility(View.GONE);
            } else {
                GlideUtils.glideDjImg(baseActivity, hostroomUserInfo.getData().getUser_level_icon_url(), img_user_dj);
                img_user_dj.setVisibility(View.VISIBLE);
            }

            //主播等级
            ImageView img_host_dj = popView.findViewById(R.id.img_host_dj);
            if (hostroomUserInfo.getData().getHost_level_icon_url().equals("")) {
                img_host_dj.setVisibility(View.GONE);
            } else {
                GlideUtils.glideDjImg(baseActivity, hostroomUserInfo.getData().getHost_level_icon_url(), img_host_dj);
                img_host_dj.setVisibility(View.VISIBLE);
            }

            //粉丝
            TextView tv_fans_num = popView.findViewById(R.id.tv_fans_num);
            tv_fans_num.setText("粉丝" + hostroomUserInfo.getData().getFans_count());

            //鱼络号
            TextView tv_ylh = popView.findViewById(R.id.tv_ylh);
            tv_ylh.setText("鱼络号:" + hostroomUserInfo.getData().getYu_num());

            //地点
            if (!hostroomUserInfo.getData().getCity_name().equals("")) {
                ImageView img_address = popView.findViewById(R.id.img_address);
                TextView tv_address = popView.findViewById(R.id.tv_address);
                img_address.setVisibility(View.VISIBLE);
                tv_address.setText(hostroomUserInfo.getData().getCity_name());
            }

            //财产
            TextView tv_yz = popView.findViewById(R.id.tv_yz);
            tv_yz.setText(hostroomUserInfo.getData().getHost_pc_gold_total_earn_count() + "");
            TextView tv_yb = popView.findViewById(R.id.tv_yb);
            tv_yb.setText(hostroomUserInfo.getData().getHost_pc_gift_total_eran_integral_count() + "");
            TextView tv_zs = popView.findViewById(R.id.tv_zs);
            tv_zs.setText(hostroomUserInfo.getData().getUser_diamond_total_spend_count() + "");
            TextView tv_jf = popView.findViewById(R.id.tv_jf);
            tv_jf.setText(hostroomUserInfo.getData().getUser_use_gift_total_integral_count() + "");

            //看自己
            if (isSelf) {
                popView.findViewById(R.id.ll_bottom_button).setVisibility(View.GONE);
                popView.findViewById(R.id.tv_jb).setVisibility(View.GONE);
                tv_line.setVisibility(View.GONE);
            } else {
                popView.findViewById(R.id.ll_bottom_button).setVisibility(View.VISIBLE);
                popView.findViewById(R.id.tv_jb).setVisibility(View.VISIBLE);
                //@
                TextView tv_tx_who = popView.findViewById(R.id.tv_tx_who);
                tv_tx_who.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        if(FastClickUtils.isFastClick()){
                            return;
                        }
                        if (SelfInfoPop.this.clickCallback != null) {
                            SelfInfoPop.this.clickCallback.slComment(hostroomUserInfo.getData().getNick_name());
                            dismissPop();
                        }
                    }
                });

                //私聊
                TextView tv_sl = popView.findViewById(R.id.tv_sl);
                tv_sl.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        if(FastClickUtils.isFastClick()){
                            return;
                        }
                        if (SelfInfoPop.this.clickCallback != null) {
                            SelfInfoPop.this.clickCallback.sl(hostroomUserInfo.getData().getU_id(),
                                    hostroomUserInfo.getData().getNick_name(),
                                    hostroomUserInfo.getData().getHead_url());
                            dismissPop();
                        }
                    }
                });

                //关注
                tv_attention = popView.findViewById(R.id.tv_attention);
                final int isFollow = hostroomUserInfo.getData().getIs_follow();//是否关注（0否，1是）
                tv_attention.setTag(isFollow);
                tv_attention.setText((isFollow == 1) ? "已关注" : "+关注");
                tv_attention.setBackground(baseActivity.getResources().getDrawable((isFollow == 1) ? R.drawable.shape_22_5conrner_bdbdbd : R.drawable.shape__22_5_ff5686));
                tv_attention.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        if(FastClickUtils.isFastClick()){
                            return;
                        }
                        if (SelfInfoPop.this.clickCallback != null) {
                            if (isFollow == 0) {
                                SelfInfoPop.this.clickCallback.attentionStats(isFollow, hostroomUserInfo.getData().getU_id() + "");
                            }
                        }
                    }
                });


                TextView tv_jb = popView.findViewById(R.id.tv_jb);
                sGift_and_setManager = popView.findViewById(R.id.tv_gift);
                //主播 看他人
                if (isHost) {
                    //踢人/禁言
                    tv_jb.setText("踢人禁言");
                    tv_jb.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            if(FastClickUtils.isFastClick()){
                                return;
                            }
                            if (SelfInfoPop.this.clickCallback != null) {
                                SelfInfoPop.this.clickCallback.tWho(hostroomUserInfo.getData().getNick_name(), hostroomUserInfo.getData().getU_id() + "", hostroomUserInfo.getData().getIs_nospeak());
                            }
                        }
                    });
                    //设置房管
                    sGift_and_setManager.setTag(hostroomUserInfo.getData().getIs_room_admin());//是否是房管标志
                    sGift_and_setManager.setText((hostroomUserInfo.getData().getIs_room_admin() == 0) ? "设置房管" : "撤销房管");
                    sGift_and_setManager.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            if(FastClickUtils.isFastClick()){
                                return;
                            }
                            if (SelfInfoPop.this.clickCallback != null) {
                                SelfInfoPop.this.clickCallback.setHomeManager(hostroomUserInfo.getData().getIs_room_admin(), hostroomUserInfo.getData().getU_id() + "",
                                        hostroomUserInfo.getData().getNick_name());
                            }
                        }
                    });

                    //fixme 左边增加 举报拉黑
                    TextView tv_t_left = popView.findViewById(R.id.tv_t_left);
                    tv_t_left.setText("举报拉黑");
                    tv_t_left.setVisibility(View.VISIBLE);
                    tv_t_left.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            if(FastClickUtils.isFastClick()){
                                return;
                            }
                            if (SelfInfoPop.this.clickCallback != null) {
                                SelfInfoPop.this.clickCallback.jb(hostroomUserInfo.getData().getU_id() + "",hostroomUserInfo.getData().getIs_black());
                            }
                        }
                    });
                }
                //自己 看他人
                else {
                    final String otherId = hostroomUserInfo.getData().getU_id() + "";

                    //fixme 房管看主播-隐藏举报拉黑功能
                    if(hostroomUserInfo.getData().getIs_room_admin()==1&&!hostId.equals("")&&otherId.equals(hostId)){
                        tv_jb.setVisibility(View.GONE);
                    }else{
                        tv_jb.setVisibility(View.VISIBLE);
                        tv_jb.setText("举报拉黑");
                        tv_jb.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                if(FastClickUtils.isFastClick()){
                                    return;
                                }
                                if (SelfInfoPop.this.clickCallback != null) {
                                    SelfInfoPop.this.clickCallback.jb(hostroomUserInfo.getData().getU_id() + "",hostroomUserInfo.getData().getIs_black());
                                }
                            }
                        });
                    }

                    //fixme 房管看主播-隐藏踢人禁言  看其他用户要
                    if (hostroomUserInfo.getData().getIs_room_admin() == 1) {
                        TextView tv_t_left = popView.findViewById(R.id.tv_t_left);
                        if(!hostId.equals("")&&otherId.equals(hostId)){
                            tv_t_left.setVisibility(View.GONE);
                        }else{
                            tv_t_left.setVisibility(View.VISIBLE);
                            tv_t_left.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View view) {
                                    if(FastClickUtils.isFastClick()){
                                        return;
                                    }
                                    if (SelfInfoPop.this.clickCallback != null) {
                                        SelfInfoPop.this.clickCallback.tWho(hostroomUserInfo.getData().getNick_name(), hostroomUserInfo.getData().getU_id() + "", hostroomUserInfo.getData().getIs_nospeak());
                                    }
                                }
                            });
                        }
                    }

                    if (!hostId.equals("")) {
                        //看主播-送礼
                        if (otherId.equals(hostId)) {
                            sGift_and_setManager.setText("送礼");
                            sGift_and_setManager.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View view) {
                                    if(FastClickUtils.isFastClick()){
                                        return;
                                    }
                                    if (SelfInfoPop.this.clickCallback != null) {
                                        SelfInfoPop.this.clickCallback.sGift();
                                        dismissPop();
                                    }
                                }
                            });
                        }else{
                            sGift_and_setManager.setText("主页");
                            sGift_and_setManager.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View view) {
                                    if(FastClickUtils.isFastClick()){
                                        return;
                                    }
                                    if (SelfInfoPop.this.clickCallback != null) {
                                        SelfInfoPop.this.clickCallback.userDetails(otherId);
                                        dismissPop();
                                    }
                                }
                            });
                        }
                    }
                }
            }


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

    public void setHasAttention(boolean hasAttention, Context context) {
        if (hasAttention) {
            tv_attention.setTag(1);
            tv_attention.setText("已关注");
            tv_attention.setBackground(context.getResources().getDrawable(R.drawable.shape_22_5conrner_bdbdbd));
        }else{
            tv_attention.setTag(0);
            tv_attention.setText("+关注");
            tv_attention.setBackground(context.getResources().getDrawable(R.drawable.shape__22_5_ff5686));
        }
        final int isFollow = (int) tv_attention.getTag();//是否关注（0否，1是）
        tv_attention.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(FastClickUtils.isFastClick()){
                    return;
                }
                if (SelfInfoPop.this.clickCallback != null) {
                    if (isFollow == 0) {
                        SelfInfoPop.this.clickCallback.attentionStats(isFollow, hostroomUserInfo.getData().getU_id() + "");
                    }
                }
            }
        });
    }

    public void setHome(int type) {
        sGift_and_setManager.setTag(type);//是否是房管标志
        hostroomUserInfo.getData().setIs_room_admin(type);
        sGift_and_setManager.setText((type == 0) ? "设置房管" : "撤销房管");
    }

    //拉黑-取消拉黑    1：添加进入黑名单  2：取消了黑名单
    public void lhState(String type){
        if(hostroomUserInfo!=null){
            //添加进入黑名单
            if(type.equals("1")){
                hostroomUserInfo.getData().setIs_black(1);
            }
            //黑名单取消
            else{
                hostroomUserInfo.getData().setIs_black(0);
            }
        }
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

    //禁言状态
    public void updateNoSpeck(int i) {
        this.hostroomUserInfo.getData().setIs_nospeak(i);
    }

    //设置主播id
    public void setHostId(String hostId) {
        this.hostId = hostId;
    }

    //-----------------------------------回调-----------------------------------
    public interface ClickCallback {
        void clickImg(String uId);

        //举报
        void jb(String uid,int isBlack);

        //T人
        void tWho(String name, String id, int isNoSpeck);

        //关注/取消
        void attentionStats(int type, String uid);

        //@
        void slComment(String otherName);

        //私聊谁
        void sl(int id, String name, String url);

        void sGift();

        //设置/撤销房管
        void setHomeManager(int type, String uid, String name);

        //主页
        void userDetails(String uid);
    }
}

