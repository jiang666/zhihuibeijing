package com.itheima.qq15.widget;

import android.content.Context;
import android.os.Build;
import android.support.annotation.RequiresApi;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.itheima.qq15.R;

/**
 * 作者： itheima
 * 时间：2016-10-17 14:36
 * 网址：http://www.itheima.com
 */

public class ContactLayout extends RelativeLayout {

    private RecyclerView mRecyclerView;
    private TextView mTextView;
    private SlideBar mSlideBar;
    private SwipeRefreshLayout mSwipeRefreshLayout;

    public ContactLayout(Context context) {
        this(context,null);
    }

    public ContactLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
        /**
         * 将R.layout.contact_layout添加到了当前ViewGroup中
         */
        LayoutInflater.from(context).inflate(R.layout.contact_layout, this, true);
        mRecyclerView = (RecyclerView) findViewById(R.id.recyclerView);
        mTextView = (TextView) findViewById(R.id.tv_float);
        mSlideBar = (SlideBar) findViewById(R.id.slideBar);
        mSwipeRefreshLayout = (SwipeRefreshLayout) findViewById(R.id.swipeRefreshLayout);
        mSwipeRefreshLayout.setColorSchemeColors(getResources().getColor(R.color.colorAccent));
    }

    public void setOnRefreshListener(SwipeRefreshLayout.OnRefreshListener listener){
        mSwipeRefreshLayout.setOnRefreshListener(listener);
    }

    public void setRefreshing(boolean refreshing){
        mSwipeRefreshLayout.setRefreshing(refreshing);
    }

    public ContactLayout(Context context, AttributeSet attrs, int defStyleAttr) {
        this(context, attrs);
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    public ContactLayout(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
    }

    public void setAdapter(RecyclerView.Adapter adapter){
        mRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        mRecyclerView.setAdapter(adapter);
    }

}
