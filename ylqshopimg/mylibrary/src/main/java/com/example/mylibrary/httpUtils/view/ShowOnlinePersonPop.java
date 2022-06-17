package com.example.mylibrary.httpUtils.view;

import android.app.Activity;
import android.content.Context;
import android.graphics.drawable.ColorDrawable;
import android.print.PrinterId;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.alibaba.android.arouter.launcher.ARouter;
import com.example.mylibrary.R;
import com.example.mylibrary.httpUtils.ErrorUtils;
import com.example.mylibrary.httpUtils.callback.HttpListener;
import com.example.mylibrary.httpUtils.http.live.bean.GetHostroomUserlist;
import com.example.mylibrary.httpUtils.http.user.UserInfoLoader;
import com.example.mylibrary.httpUtils.http.user.bean.AddFollowBean;
import com.example.mylibrary.httpUtils.utils.DensityUtil;
import com.example.mylibrary.httpUtils.utils.FastClickUtils;
import com.example.mylibrary.httpUtils.utils.GlideUtils;
import com.example.mylibrary.httpUtils.utils.HttpUtils;
import com.example.mylibrary.httpUtils.utils.LogUtils;
import com.example.mylibrary.httpUtils.utils.RouterUtils;
import com.example.mylibrary.httpUtils.utils.SPUtil;
import com.example.mylibrary.httpUtils.utils.UserUtils;

import java.lang.ref.WeakReference;
import java.util.List;
import java.util.Map;

/**
 * Created by 孟晨 on 2018/8/22.
 */

public class ShowOnlinePersonPop {

    private View popView;
    private PopupWindow popWindow;
    private WeakReference<Context> weakReference;
    private GetHostroomUserlist getHostroomUserlist;

    public ShowOnlinePersonPop(Context baseActivity, GetHostroomUserlist getHostroomUserlist, ClickUserCallback clickUserCallback) {
        if (weakReference != null) {
            weakReference.clear();
            weakReference = null;
        }
        weakReference = new WeakReference(baseActivity);
        this.getHostroomUserlist = getHostroomUserlist;
        this.clickUserCallback = clickUserCallback;
    }

    //显示弹窗
    public PopupWindow showPop() {
        Context baseActivity = weakReference.get();
        if (baseActivity != null) {
            popView = LayoutInflater.from(baseActivity)
                    .inflate(R.layout.layout_online_person, null, false);

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

            ImageView img_lo = popView.findViewById(R.id.img_lo);
            TextView tv_has_no_data = popView.findViewById(R.id.tv_has_no_data);

            //背景图
//            DisplayMetrics displayMetrics = new DisplayMetrics();
//            ((WindowManager) baseActivity.getApplicationContext().getSystemService(Context.WINDOW_SERVICE))
//                    .getDefaultDisplay().getMetrics(displayMetrics);
//            GlideUtils.glideVideoPopBgCornor(baseActivity, (ImageView) popView.findViewById(R.id.img_bg), displayMetrics.widthPixels);

            //列表
            RecyclerView rv_online = popView.findViewById(R.id.rv_online);
            rv_online.setLayoutManager(new LinearLayoutManager(baseActivity));
            OnlineAdapter onlineAdapter = new OnlineAdapter(baseActivity, getHostroomUserlist.getData());
            rv_online.setAdapter(onlineAdapter);

            //无数据
            if(getHostroomUserlist.getData().size()==0){
                rv_online.setVisibility(View.GONE);
                img_lo.setVisibility(View.VISIBLE);
                tv_has_no_data.setVisibility(View.VISIBLE);
            }

            popWindow = new PopupWindow(popView,
                    ViewGroup.LayoutParams.MATCH_PARENT,
                    ViewGroup.LayoutParams.MATCH_PARENT,//(int) (WindowParamUtils.screenHeight(baseActivity) * 0.9
                    true);
            popWindow.setAnimationStyle(R.style.popwin_anim_style);
            //点击外部，popupwindow会消失
            popWindow.setFocusable(false);
            popWindow.setOutsideTouchable(false);
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

    public class OnlineAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

        private List<GetHostroomUserlist.DataBean> mList;
        private Context context;

        public OnlineAdapter(Context context, List<GetHostroomUserlist.DataBean> list) {
            this.context = context;
            this.mList = list;
        }

        @Override
        public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(context).inflate(R.layout.layout_item_online_people, null);
            return new ImgsViewHolder(view);
        }

        @Override
        public void onBindViewHolder(RecyclerView.ViewHolder holder, final int position) {
            holder.setIsRecyclable(false);
            if (holder instanceof ImgsViewHolder) {
                final GetHostroomUserlist.DataBean bean = mList.get(position);
                ((ImgsViewHolder) holder).rl_container.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, DensityUtil.dip2px(context,65)));

                //头像 姓名
                GlideUtils.glideCirclePerson(context, bean.getHead_url(), ((ImgsViewHolder) holder).img_user);
                ((ImgsViewHolder) holder).img_user.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        if(FastClickUtils.isFastClick()){
                            return;
                        }
                        if (ShowOnlinePersonPop.this.clickUserCallback != null) {
                            ShowOnlinePersonPop.this.clickUserCallback.clickUserCallback(bean.getU_id() + "");
                        }
                    }
                });
                ((ImgsViewHolder) holder).tv_name.setText(bean.getNick_name());
                //描述 signature
                if (!bean.getSignature().equals("")) {
                    ((ImgsViewHolder) holder).tv_des.setText(bean.getSignature());
                }
                //性别
                int sex = bean.getSex();
                if (sex == 0) {
                    ((ImgsViewHolder) holder).img_sex.setVisibility(View.GONE);
                } else if (sex == 1) {
                    ((ImgsViewHolder) holder).img_sex.setVisibility(View.VISIBLE);
                    ((ImgsViewHolder) holder).img_sex.setImageResource(R.drawable.img_rect_boy);
                } else if (sex == 2) {
                    ((ImgsViewHolder) holder).img_sex.setVisibility(View.VISIBLE);
                    ((ImgsViewHolder) holder).img_sex.setImageResource(R.drawable.img_rect_girl);
                }
                //用户等级
                if (!bean.getUser_level_icon_url().equals("")) {
                    ((ImgsViewHolder) holder).img_user_dj.setVisibility(View.VISIBLE);
                    GlideUtils.glideDjImg(context, bean.getUser_level_icon_url(), ((ImgsViewHolder) holder).img_user_dj);
                }
                //关注
                int att = bean.getIs_follow();
                if (att == 0) {
                    if ((bean.getU_id() + "").equals(SPUtil.get(context, UserUtils.UID, ""))) {
                        ((ImgsViewHolder) holder).tv_attention.setVisibility(View.GONE);
                        return;
                    }
                    ((ImgsViewHolder) holder).tv_attention.setVisibility(View.VISIBLE);
                    ((ImgsViewHolder) holder).tv_attention.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            if(FastClickUtils.isFastClick()){
                                return;
                            }
                            add_follow(context, bean.getU_id() + "", position, OnlineAdapter.this);
                        }
                    });
                } else {
                    ((ImgsViewHolder) holder).tv_attention.setVisibility(View.GONE);
                }
            }
        }

        public void refreshPoFollow(int po) {
            if (po < mList.size()) {
                mList.get(po).setIs_follow(1);
                notifyDataSetChanged();
            }
        }

        @Override
        public int getItemCount() {
            return mList.size();
        }


        private class ImgsViewHolder extends RecyclerView.ViewHolder {
            public View item;
            private RelativeLayout rl_container;
            private CircleImageView img_user;
            private Button tv_attention;
            private TextView tv_name, tv_des;
            private ImageView img_sex, img_user_dj, img_host_dj;

            public ImgsViewHolder(View itemView) {
                super(itemView);
                item = itemView;
                rl_container = itemView.findViewById(R.id.rl_container);
                img_user = itemView.findViewById(R.id.img_user);
                tv_attention = itemView.findViewById(R.id.tv_attention);
                tv_name = itemView.findViewById(R.id.tv_name);
                tv_des = itemView.findViewById(R.id.tv_des);
                img_sex = itemView.findViewById(R.id.img_sex);
                img_user_dj = itemView.findViewById(R.id.img_user_dj);
                img_host_dj = itemView.findViewById(R.id.img_host_dj);
            }
        }
    }

    public interface ClickUserCallback {
        void clickUserCallback(String uid);
    }

    private ClickUserCallback clickUserCallback;

    //-----------------------------------------关注----------------------------------
    private void add_follow(Context mContext, String id, final int position, final OnlineAdapter onlineAdapter) {
        String token = (String) SPUtil.get(mContext, UserUtils.TOKEN_LOGIN, "");
        Map<String, String> map = HttpUtils.getPublicApiMap(mContext);
        map.put("token", token);
        map.put("u_id_data", id);
        String sign = HttpUtils.getSign(map);
        LogUtils.logE("测试: ", "加密的签名=" + sign);
        HttpListener<AddFollowBean> httpListener = new HttpListener<AddFollowBean>() {
            @Override
            public void onError(String errorMsg) {
            }

            @Override
            public void onSuccess(AddFollowBean addFollowBean) {
                if (addFollowBean.getCode().equals("200")) {
                    onlineAdapter.refreshPoFollow(position);
                    return;
                }
            }
        };
        UserInfoLoader userLoader = new UserInfoLoader(UserInfoLoader.BASEURL);
        userLoader.add_follow(map.get("app_version"), HttpUtils.app_type, map.get("timestamp"), map.get("randomstr"), sign, token, id, httpListener);
    }
}

