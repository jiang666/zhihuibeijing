package com.example.mylibrary.httpUtils.view;

import android.content.Context;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;

public class NestRecyclerView extends RecyclerView {

    private int lastVisibleItemPosition;
    private int firstVisibleItemPosition;
    private float mLastY = 0;// 记录上次Y位置
    private boolean isTopToBottom = false;
    private boolean isBottomToTop = false;
    public NestRecyclerView(Context context) {
        this(context, null);
    }

    public NestRecyclerView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public NestRecyclerView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
//        this.setLayoutFrozen(true);
    }

//    @Override
//    public boolean onInterceptTouchEvent(MotionEvent ev) {
//        switch (ev.getAction()) {
//            case MotionEvent.ACTION_DOWN:
//                getParent().requestDisallowInterceptTouchEvent(true);
//                return false;
//            case MotionEvent.ACTION_MOVE:   //表示父类需要
//                return true;
//            case MotionEvent.ACTION_UP:
//                return true;
//            default:
//                break;
//        }
//        return false;    //如果设置拦截，除了down,其他都是父类处理
//    }

     @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {
        int action = ev.getAction();
        switch (action) {
            case MotionEvent.ACTION_DOWN:
                getParent().requestDisallowInterceptTouchEvent(true);
                break;
            case MotionEvent.ACTION_MOVE:
                break;
            case MotionEvent.ACTION_UP:
            case MotionEvent.ACTION_CANCEL:
                getParent().requestDisallowInterceptTouchEvent(false);
                break;
        }
        return super.onInterceptTouchEvent(ev);
    }


//    @Override
//    public boolean dispatchTouchEvent(MotionEvent event) {
//        int action = event.getAction();
//        switch (action) {
//            case MotionEvent.ACTION_DOWN:
////                recyclerView.setNestedScrollingEnabled(false);
////                mLastY = event.getY();
//                //不允许父View拦截事件
//                getParent().requestDisallowInterceptTouchEvent(true);
//                break;
//            case MotionEvent.ACTION_MOVE:
////                float nowY = event.getY();
////                isIntercept(nowY);
////                if (isBottomToTop||isTopToBottom){
////                    getParent().requestDisallowInterceptTouchEvent(false);
////                    return false;
////                }else{
//                    getParent().requestDisallowInterceptTouchEvent(true);
////                }
////                mLastY = nowY;
//                break;
//            case MotionEvent.ACTION_UP:
//            case MotionEvent.ACTION_CANCEL:
//                getParent().requestDisallowInterceptTouchEvent(false);
////                recyclerView.setNestedScrollingEnabled(true);
//                break;
//        }
//        return super.onTouchEvent(event);
//    }

    private void isIntercept(float nowY){

        isTopToBottom = false;
        isBottomToTop = false;

        LayoutManager layoutManager = getLayoutManager();
        if (layoutManager instanceof GridLayoutManager) {
            //得到当前界面，最后一个子视图对应的position
            lastVisibleItemPosition = ((GridLayoutManager) layoutManager)
                    .findLastVisibleItemPosition();
            //得到当前界面，第一个子视图的position
            firstVisibleItemPosition = ((GridLayoutManager) layoutManager)
                    .findFirstVisibleItemPosition();
        }
        //得到当前界面可见数据的大小
        int visibleItemCount = layoutManager.getChildCount();
        //得到RecyclerView对应所有数据的大小
        int totalItemCount = layoutManager.getItemCount();
        Log.d("nestScrolling","onScrollStateChanged");
        if (visibleItemCount>0) {
            if (lastVisibleItemPosition == totalItemCount - 1) {
                //最后视图对应的position等于总数-1时，说明上一次滑动结束时，触底了
                Log.d("nestScrolling", "触底了");
                if (NestRecyclerView.this.canScrollVertically(-1) && nowY < mLastY) {
                    // 不能向上滑动
                    Log.d("nestScrolling", "不能向上滑动");
                    isBottomToTop = true;
                } else {
                    Log.d("nestScrolling", "向下滑动");
                }
            } else if (firstVisibleItemPosition == 0) {
                //第一个视图的position等于0，说明上一次滑动结束时，触顶了
                Log.d("nestScrolling", "触顶了");
                if (NestRecyclerView.this.canScrollVertically(1) && nowY > mLastY) {
                    // 不能向下滑动
                    Log.d("nestScrolling", "不能向下滑动");
                    isTopToBottom = true;
                } else {
                    Log.d("nestScrolling", "向上滑动");
                }
            }
        }
    }

    private RecyclerView recyclerView;
    public void bindOuterRe(RecyclerView recyclerView) {
        this.recyclerView = recyclerView;
    }
}
