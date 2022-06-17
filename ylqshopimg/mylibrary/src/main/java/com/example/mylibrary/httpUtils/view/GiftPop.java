package com.example.mylibrary.httpUtils.view;

import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Context;
import android.os.Build;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.GridView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.TextView;

import com.example.mylibrary.R;
import com.example.mylibrary.httpUtils.adapter.GridViewAdapter;
import com.example.mylibrary.httpUtils.adapter.ViewPagerAdapter;
import com.example.mylibrary.httpUtils.http.gift.bean.GetGiftlist;
import com.example.mylibrary.httpUtils.utils.DensityUtil;
import com.example.mylibrary.httpUtils.utils.FastClickUtils;
import com.example.mylibrary.httpUtils.utils.LogUtils;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by 孟晨 on 2018/8/22.
 */

public class GiftPop implements View.OnClickListener {

    private View popView;
    private PopupWindow popWindow;
    private WeakReference<Context> weakReference;
    private ClickCallback clickCallback;
    private NoScrollCaterpillarIndicator caterpillarIndicator;
    private ViewPager viewPager;
    private GetGiftlist getGiftlist;
    private LayoutInflater mInflater;
    private List<View> mPagerList;//页面集合
    private int pageCount;/*总的页数*/
    private int pageSize = 8;/*每一页显示的个数*/
    private int curIndex = 0;/*当前显示的是第几页*/
    private GridViewAdapter[] arr;
    private LinearLayout idotLayout;//知识圆点
    private TextView tv_show_msg;//知识圆点
    private int indicatorIndex = 0;//指示器下标
    private GetGiftlist.DataBean.GiftlistBean model;//选中的礼物bean
    private String sendGiftNum = "1";//赠送礼物的数量
    private TextView tv_send_num1;
    private TextView tv_send_num10;
    private TextView tv_send_num99;
    private TextView tv_send_num520;
    private TextView tv_send_num999;
    //用户钻石
    private int userZs;
    //用户积分
    private int userJf;
    //钻石积分
    private TextView tv_zs_num;
    private TextView tv_jf_num;
    //数量textview
    private TextView tvs[] = new TextView[5];

    public GiftPop(Context baseActivity, GetGiftlist getGiftlist, int userZs, int userJf) {
        if (weakReference != null) {
            weakReference.clear();
            weakReference = null;
        }
        weakReference = new WeakReference(baseActivity);
        this.getGiftlist = getGiftlist;
        this.userZs = userZs;
        this.userJf = userJf;
    }

    //显示弹窗
    public PopupWindow showPop(ClickCallback clickCallback) {
        this.clickCallback = clickCallback;
        final Context baseActivity = weakReference.get();
        if (baseActivity != null) {
            if (this.getGiftlist.getData().size() == 0) {
                return null;
            }
            mInflater = LayoutInflater.from(baseActivity);
            popView = mInflater.inflate(R.layout.layout_gift, null, false);

            //防止点击穿透
            popView.findViewById(R.id.tv_bg).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (GiftPop.this.clickCallback != null) {
                        if(FastClickUtils.isFastClick()){
                            return;
                        }
                        dismissPop();
                    }
                }
            });

            //充值
            popView.findViewById(R.id.tv_charge).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (GiftPop.this.clickCallback != null) {
                        if(FastClickUtils.isFastClick()){
                            return;
                        }
                        dismissPop();
                        GiftPop.this.clickCallback.charge();
                    }
                }
            });

            //id
            tv_show_msg = popView.findViewById(R.id.tv_show_msg);
            idotLayout = popView.findViewById(R.id.ll_dot);
            viewPager = popView.findViewById(R.id.viewpager);
            caterpillarIndicator = popView.findViewById(R.id.tab_gift_type);
            ViewGroup.LayoutParams vp = caterpillarIndicator.getLayoutParams();
            vp.width = DensityUtil.dip2px(baseActivity, 60) * this.getGiftlist.getData().size();
            caterpillarIndicator.setLayoutParams(vp);

            //钻石
            tv_zs_num = popView.findViewById(R.id.tv_zs_num);
            tv_zs_num.setText("钻石:" + userZs);
            tv_jf_num = popView.findViewById(R.id.tv_jf_num);
            tv_jf_num.setText("积分:" + userJf);

            //指示器名称
            List<NoScrollCaterpillarIndicator.TitleInfo> titleInfos = new ArrayList<>();
            for (int i = 0; i < this.getGiftlist.getData().size(); i++) {
                titleInfos.add(new NoScrollCaterpillarIndicator.TitleInfo(this.getGiftlist.getData().get(i).getType_name()));
            }
            caterpillarIndicator.init(0, titleInfos, vp.width, new NoScrollCaterpillarIndicator.ClickTabCallback() {
                @Override
                public void clickTab(int index) {
                    if (indicatorIndex == index) {
                        return;
                    }
                    //清空选中的数据
                    model = null;
                    for (int i = 0; i < GiftPop.this.getGiftlist.getData().get(indicatorIndex).getGiftlist().size(); i++) {
                        GiftPop.this.getGiftlist.getData().get(indicatorIndex).getGiftlist().get(i).setSelected(false);
                    }
                    indicatorIndex = index;
                    showTypeGift(baseActivity);
                }
            });

            //送礼物
            tv_send_num1 = popView.findViewById(R.id.tv_send_num1);
            tv_send_num10 = popView.findViewById(R.id.tv_send_num10);
            tv_send_num99 = popView.findViewById(R.id.tv_send_num99);
            tv_send_num520 = popView.findViewById(R.id.tv_send_num520);
            tv_send_num999 = popView.findViewById(R.id.tv_send_num999);
            tvs[0] = tv_send_num1;
            tvs[1] = tv_send_num10;
            tvs[2] = tv_send_num99;
            tvs[3] = tv_send_num520;
            tvs[4] = tv_send_num999;
            tv_send_num1.setOnClickListener(this);
            tv_send_num10.setOnClickListener(this);
            tv_send_num99.setOnClickListener(this);
            tv_send_num520.setOnClickListener(this);
            tv_send_num999.setOnClickListener(this);

            //展示首tab的数据
            showTypeGift(baseActivity);

            Button bt_send = popView.findViewById(R.id.bt_send);
            bt_send.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if(FastClickUtils.isFastClick()){
                        return;
                    }
                    if (model == null) {
                        LogUtils.logE("测试日志: ", "model == null");
                        setTv_show_msg("请先选择需要赠送的礼物");
                        return;
                    }
                    if (GiftPop.this.clickCallback != null) {
                        LogUtils.logE("测试日志: ", "sendGift监听");
                        GiftPop.this.clickCallback.sendGift(sendGiftNum, model);
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

    @Override
    public void onClick(View view) {
        if (view.getId() == R.id.tv_send_num1) {
            selectWhichNum(0);
        } else if (view.getId() == R.id.tv_send_num10) {
            selectWhichNum(1);
        } else if (view.getId() == R.id.tv_send_num99) {
            selectWhichNum(2);
        } else if (view.getId() == R.id.tv_send_num520) {
            selectWhichNum(3);
        } else if (view.getId() == R.id.tv_send_num999) {
            selectWhichNum(4);
        }
    }

    public void setTv_show_msg(String msg) {
        try {
            if (tv_show_msg != null) {
                tv_show_msg.setVisibility(View.VISIBLE);
                tv_show_msg.setText(msg);
                tv_show_msg.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        if (tv_show_msg != null) {
                            tv_show_msg.setVisibility(View.INVISIBLE);
                        }
                    }
                }, 2000);
            }
        }catch (Exception e){
        }
    }

    //切换赠送的数量
    private void selectWhichNum(int which) {
        final Context baseActivity = weakReference.get();
        if (baseActivity != null) {
            for (int i = 0; i < tvs.length; i++) {
                tvs[i].setTextColor(baseActivity.getResources().getColor(R.color.colo_666666));
            }
            sendGiftNum = tvs[which].getText().toString();
            tvs[which].setTextColor(baseActivity.getResources().getColor(R.color.colo_FF5686));
        }
    }

    //更新显示的数量
    private void updateSendGiftNum(Context baseActivity, GetGiftlist.DataBean.GiftlistBean bean) {
        for (int i = 0; i < tvs.length; i++) {
            tvs[i].setTextColor(baseActivity.getResources().getColor(R.color.colo_666666));
            tvs[i].setVisibility(View.GONE);
        }
        for (int i = 0; i < bean.getCount_object().size(); i++) {
            tvs[i].setVisibility(View.VISIBLE);
            tvs[i].setText(bean.getCount_object().get(i));
            if (i == 0) {
                sendGiftNum = bean.getCount_object().get(0);
                tvs[0].setTextColor(baseActivity.getResources().getColor(R.color.colo_FF5686));
            }
        }
    }

    //显示不同指示器对应的数据
    private void showTypeGift(final Context baseActivity) {
        //首页数据页数  总的页数=总数/每页数量，并取整
        pageCount = (int) Math.ceil(this.getGiftlist.getData().get(indicatorIndex).getGiftlist().size() * 1.0 / pageSize);
        //没有数据
        if(pageCount==0){
            //隐藏数量
            for (int i = 0; i < tvs.length; i++) {
                tvs[i].setTextColor(baseActivity.getResources().getColor(R.color.colo_666666));
                tvs[i].setVisibility(View.GONE);
            }
            //移除原点
            if (idotLayout.getChildCount() > 0) {
                idotLayout.removeAllViews();
            }
            viewPager.setAdapter(new ViewPagerAdapter(new ArrayList<View>(), baseActivity));
            return;
        }

        arr = new GridViewAdapter[pageCount];
        //viewpager
        mPagerList = new ArrayList<View>();
        //fixme 默认首位选中
        GiftPop.this.getGiftlist.getData().get(indicatorIndex).getGiftlist().get(0).setSelected(true);
        model = GiftPop.this.getGiftlist.getData().get(indicatorIndex).getGiftlist().get(0);
        for (int j = 0; j < pageCount; j++) {
            final GridView gridview = (GridView) mInflater.inflate(R.layout.bottom_vp_gridview, viewPager, false);
            final GridViewAdapter gridAdapter = new GridViewAdapter(baseActivity, this.getGiftlist.getData().get(indicatorIndex).getGiftlist(), j);
            gridview.setAdapter(gridAdapter);
            arr[j] = gridAdapter;
            gridview.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @TargetApi(Build.VERSION_CODES.JELLY_BEAN)
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    Animation shake = AnimationUtils.loadAnimation(baseActivity,R.anim.shake);//加载动画资源文件  
                    view.findViewById(R.id.img_ly).startAnimation(shake);//给组件播放动画效果  
                    LogUtils.logE("测试", "position="+position +"    id="+id);
                    //不允许取消选中
                    if (GiftPop.this.getGiftlist.getData().get(indicatorIndex).getGiftlist().get((int)id).isSelected()) {
//                        GiftPop.this.getGiftlist.getData().get(indicatorIndex).getGiftlist().get(position).setSelected(false);
//                        model = null;
                    } else {
                        for (int i = 0; i < GiftPop.this.getGiftlist.getData().get(indicatorIndex).getGiftlist().size(); i++) {
                            GiftPop.this.getGiftlist.getData().get(indicatorIndex).getGiftlist().get(i).setSelected(false);
                        }
                        GiftPop.this.getGiftlist.getData().get(indicatorIndex).getGiftlist().get((int) id).setSelected(true);
                        model = GiftPop.this.getGiftlist.getData().get(indicatorIndex).getGiftlist().get((int) id);
                        for (int i = 0; i < arr.length; i++) {
                            arr[i].notifyDataSetChanged();
                        }
                        //更新数量
                        updateSendGiftNum(baseActivity, GiftPop.this.getGiftlist.getData().get(indicatorIndex).getGiftlist().get((int) id));
                    }
                }
            });
            mPagerList.add(gridview);
        }
        viewPager.setAdapter(new ViewPagerAdapter(mPagerList, baseActivity));

        //显示数量 默认首位
        updateSendGiftNum(baseActivity,this.getGiftlist.getData().get(indicatorIndex).getGiftlist().get(0));

        //移除原点
        if (idotLayout.getChildCount() > 0) {
            idotLayout.removeAllViews();
        }
        //添加原点
        if (pageCount > 0) {
            for (int i = 0; i < pageCount; i++) {
                idotLayout.addView(mInflater.inflate(R.layout.dot, null));
            }
            curIndex = 0;
            idotLayout.getChildAt(curIndex).findViewById(R.id.v_dot).setBackgroundResource(R.drawable.dot_selected);// 默认显示第一页
            viewPager.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {
                public void onPageSelected(int position) {
                    for (int i = 0; i < arr.length; i++) {
                        arr[i].notifyDataSetChanged();
                    }
                    idotLayout.getChildAt(curIndex).findViewById(R.id.v_dot).setBackgroundResource(R.drawable.dot_normal);// 取消圆点选中
                    idotLayout.getChildAt(position).findViewById(R.id.v_dot).setBackgroundResource(R.drawable.dot_selected);// 圆点选中
                    curIndex = position;
                }

                public void onPageScrolled(int arg0, float arg1, int arg2) {
                }

                public void onPageScrollStateChanged(int arg0) {
                }
            });
        }
    }

    //更新用户钻石
    public void updateZs(int plusZs) {
        try {
            userZs -= plusZs;
            tv_zs_num.setText("钻石:" + userZs);
        }catch (Exception e){
        }
    }

    //更新用户积分
    public void updateJf(int plusJf){
        try {
            userJf -= plusJf;
            tv_jf_num.setText("积分:" + userJf);
        }catch (Exception e){
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

    //-----------------------------------回调-----------------------------------
    public interface ClickCallback {

        void sendGift(String sendGiftNum, GetGiftlist.DataBean.GiftlistBean model);

        void charge();
    }
}

