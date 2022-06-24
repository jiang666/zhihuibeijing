package com.itheima.qq15.widget;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.os.Build;
import android.support.annotation.RequiresApi;
import android.support.v7.view.menu.MenuPopupHelper;
import android.support.v7.widget.RecyclerView;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewParent;
import android.widget.TextView;

import com.hyphenate.chat.EMClient;
import com.hyphenate.util.DensityUtil;
import com.itheima.qq15.R;
import com.itheima.qq15.adapter.ContactAdapter;
import com.itheima.qq15.adapter.IContactAdapter;
import com.itheima.qq15.db.DBUtils;
import com.itheima.qq15.utils.StringUtils;

import java.util.List;

import static android.R.attr.breadCrumbShortTitle;
import static android.R.attr.x;
import static android.R.attr.y;

/**
 * 作者： itheima
 * 时间：2016-10-17 14:45
 * 网址：http://www.itheima.com
 */

public class SlideBar extends View {

    private static final String[] SECTIONS = {"搜","A","B","C","D","E","F","G","H","I","J"
    ,"K","L","M","N","O","P","Q","R","S","T","U","V","W","X","Y","Z"};
    private int mAvgWidth;
    private int mAvgHeight;
    private Paint mPaint;
    private TextView mTvFloat;
    private RecyclerView mRecyclerView;

    public SlideBar(Context context) {
        this(context,null);
    }

    public SlideBar(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    private void init() {
        mPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        /**
         * 单位是像素
         */
        mPaint.setTextSize(DensityUtil.sp2px(getContext(),10));
        mPaint.setTextAlign(Paint.Align.CENTER);
        mPaint.setColor(Color.parseColor("#9c9c9c"));
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
            case MotionEvent.ACTION_MOVE:
                /**
                 * 1.设置背景为灰色 矩形 圆角
                 * 2. move到哪个文本Section就把floatTitle 修改掉
                 * 3. 判断通讯录中正好有当前Section字母开头的用户，则定位RecyclerView的位置，让用户看见
                 */
                setBackgroundResource(R.drawable.slidebar_bk);
                showFloatAndScrollRecyclerView(event.getY());
                break;
            case MotionEvent.ACTION_UP:
                /**
                 * 1. 隐藏背景(把背景变成全透明的)，隐藏floatTitle
                 */
                setBackgroundColor(Color.TRANSPARENT);
                if (mTvFloat!=null){
                 mTvFloat.setVisibility(GONE);
                }
                break;
        }
        return true;
    }

    private void showFloatAndScrollRecyclerView(float y) {
        /**
         * 根据y坐标计算点中的文本
         */
        int index = (int) (y/mAvgHeight);
        if (index<0){
            index = 0;
        }else if (index>SECTIONS.length-1){
            index = SECTIONS.length - 1;
        }
        String section = SECTIONS[index];
        /**
         * 获取FloatTitle(先让SlideBar找父控件，然后让父控件找FloatTitle)，然后设置section
         */
        if (mTvFloat==null){
            ViewGroup parent = (ViewGroup) getParent();
            mTvFloat = (TextView) parent.findViewById(R.id.tv_float);
            mRecyclerView = (RecyclerView) parent.findViewById(R.id.recyclerView);
        }
        mTvFloat.setVisibility(VISIBLE);
        mTvFloat.setText(section);
        /**
         * 拿到section后去判断这个section在RecyclerView中的所有数据中的脚标（也可能不存在）
         *
         *  通过RecyclerView获取到Adapter，通过Adapter获取到联系人数据
         */
        RecyclerView.Adapter adapter = mRecyclerView.getAdapter();
        if (adapter instanceof IContactAdapter){
            IContactAdapter contactAdapter = (IContactAdapter) adapter;
            List<String> data = contactAdapter.getData();
            for (int i = 0; i < data.size(); i++) {
                String contact = data.get(i);
                if (section.equals(StringUtils.getInitial(contact))){
                    mRecyclerView.smoothScrollToPosition(i);
                    return;
                }
            }
        }else{
            throw  new RuntimeException("使用SlideBar时绑定的Adapter必须实现IContactAdapter接口");
        }
    }

    public SlideBar(Context context, AttributeSet attrs, int defStyleAttr) {
        this(context, attrs);
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    public SlideBar(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        int measuredHeight = getMeasuredHeight();
        int measuredWidth = getMeasuredWidth();
        mAvgWidth = measuredWidth/2;
        mAvgHeight = measuredHeight/SECTIONS.length;
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        for (int i = 0; i < SECTIONS.length; i++) {
            canvas.drawText(SECTIONS[i],mAvgWidth,mAvgHeight*(i+1),mPaint);
        }
    }
}
