package com.example.mylibrary.httpUtils.view;


import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.RectF;
import android.graphics.Typeface;
import android.support.v4.view.ViewPager;
import android.text.TextPaint;
import android.util.AttributeSet;
import android.util.Log;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.mylibrary.R;
import com.example.mylibrary.httpUtils.utils.DensityUtil;

import java.util.ArrayList;
import java.util.List;


public class NoScrollCaterpillarIndicator extends LinearLayout implements View.OnClickListener {
    private static final String TAG = "CaterpillarIndicator";

    private static final int BASE_ID = 0xffff00;
    private static final int FOOTER_COLOR = 0xFFF7826C;
    private static final int ITEM_TEXT_COLOR_NORMAL = 0xFF999999;
    private static final int ITEM_TEXT_COLOR_SELECT = 0xFF333333;
    private static final int TEXT_CENTER = 0;
    private static final int LINE_CENTER = 1;

    private boolean isRoundRectangleLine = true;
    private int mFootLineColor;
    private int mTextSizeNormal;
    private int mTextSizeSelected;
    private int mTextColorNormal;
    private int mTextColorSelected;
    private int mFooterLineHeight;
    private int mItemLineWidth;
    /**
     * item count
     */
    private int mItemCount = 0;
    private int textCenterFlag;
    private int mCurrentScroll = 0;
    private int mSelectedTab = 0;
    private int linePaddingBottom = 0;
    private boolean isNewClick;
    private List<TitleInfo> mTitles;
    private Context context;
    /**
     * indicator line is Rounded Rectangle
     */

    /**
     * line paint
     */
    private Paint mPaintFooterLine;
    /**
     * line RectF
     */
    private RectF drawLineRect;
    private int viewpagerWidth;

    public NoScrollCaterpillarIndicator(Context context) {
        this(context, null);
    }

    public NoScrollCaterpillarIndicator(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.context = context;
        setFocusable(true);
        TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.CaterpillarIndicator);
        mFootLineColor = a.getColor(R.styleable.CaterpillarIndicator_slide_footer_color, FOOTER_COLOR);
        mTextSizeNormal = a.getDimensionPixelSize(R.styleable.CaterpillarIndicator_slide_text_size_normal, DensityUtil.dip2px(context, mTextSizeNormal));
        mTextSizeSelected = a.getDimensionPixelSize(R.styleable.CaterpillarIndicator_slide_text_size_selected, DensityUtil.dip2px(context, mTextSizeNormal));
        mFooterLineHeight = a.getDimensionPixelOffset(R.styleable.CaterpillarIndicator_slide_footer_line_height, DensityUtil.dip2px(context, 2));
        mTextColorSelected = a.getColor(R.styleable.CaterpillarIndicator_slide_text_color_selected, ITEM_TEXT_COLOR_SELECT);
        mTextColorNormal = a.getColor(R.styleable.CaterpillarIndicator_slide_text_color_normal, ITEM_TEXT_COLOR_NORMAL);
        isRoundRectangleLine = a.getBoolean(R.styleable.CaterpillarIndicator_slide_round, true);
        mItemLineWidth = (int) a.getDimension(R.styleable.CaterpillarIndicator_slide_item_width, DensityUtil.dip2px(context, 22));
        linePaddingBottom = (int) a.getDimension(R.styleable.CaterpillarIndicator_slide_padding_bottom, 0);
        textCenterFlag = a.getInt(R.styleable.CaterpillarIndicator_slide_text_center_flag, TEXT_CENTER);
        setWillNotDraw(false);
        initDraw();
        a.recycle();
    }

    private void initDraw() {
        mPaintFooterLine = new Paint();
        mPaintFooterLine.setAntiAlias(true);
        mPaintFooterLine.setStyle(Paint.Style.FILL);
        drawLineRect = new RectF(0, 0, 0, 0);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        mPaintFooterLine.setColor(mFootLineColor);
        float bottomY = getHeight() - linePaddingBottom;
        float topY = bottomY - mFooterLineHeight;
        drawLineRect.left = mCurrentScroll;
        drawLineRect.right = mCurrentScroll + mItemLineWidth;
        drawLineRect.bottom = bottomY;
        drawLineRect.top = topY;

//        Log.e("测试: ", "drawLineRect.left=" + drawLineRect.left);
//        Log.e("测试: ", "drawLineRect.right=" + drawLineRect.right);
//        Log.e("测试: ", "drawLineRect.bottom=" + drawLineRect.bottom);
//        Log.e("测试: ", "drawLineRect.top=" + drawLineRect.top);

        int roundXY = isRoundRectangleLine ? (mFooterLineHeight / 2) : 0;
        canvas.drawRoundRect(drawLineRect, roundXY, roundXY, mPaintFooterLine);
    }

    public void init(int startPosition, List<TitleInfo> tabs, int vpWidth,ClickTabCallback clickTabCallback) {
        this.clickTabCallback = clickTabCallback;
        this.viewpagerWidth = vpWidth;
        removeAllViews();
        this.mSelectedTab = startPosition;
        this.mTitles = tabs;
        this.mItemCount = tabs.size();
        setWeightSum(mItemCount);
        if (mSelectedTab > tabs.size()) {
            mSelectedTab = tabs.size();
        }
        for (int i = 0; i < mItemCount; i++) {
            add(tabs.get(i).getName(), i);
        }
        mCurrentScroll = (this.viewpagerWidth / mItemCount - mItemLineWidth) / 2;
        invalidate();
        requestLayout();
    }

    //FIXME 做了修改
    List<TextView> tvList = new ArrayList<>();

    protected void add(String label, int position) {
        TextView view = new TextView(getContext());
        tvList.add(view);
        LayoutParams params = new LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT, 1);
        view.setGravity(Gravity.CENTER);
        view.setLayoutParams(params);

        if (textCenterFlag == LINE_CENTER) {
            view.setPadding(0, 0, 0, linePaddingBottom);
        }
        view.setText(label);
        setTabTextSize(view, position == mSelectedTab);
        view.setId(BASE_ID + position);
        view.setOnClickListener(this);
        addView(view);
    }

    @Override
    public void onClick(View v) {
        int position = v.getId() - BASE_ID;
        mCurrentScroll = (this.viewpagerWidth / mItemCount - mItemLineWidth) / 2  + (this.viewpagerWidth / mItemCount) * position;
        invalidate();
        for (int i = 0; i < tvList.size(); i++) {
            tvList.get(i).setTextColor(context.getResources().getColor(R.color.colo_666666));
        }
        tvList.get(position).setTextColor(context.getResources().getColor(R.color.colo_FF5686));
        if(this.clickTabCallback!=null){
            this.clickTabCallback.clickTab(position);
        }
    }


    private void setTabTextSize(View tab, boolean selected) {
        if (tab instanceof TextView) {
            TextView tv = (TextView) tab;
            tv.setTextSize(TypedValue.COMPLEX_UNIT_PX, selected ? mTextSizeSelected : mTextSizeNormal);
            tv.setTextColor(selected ? mTextColorSelected : mTextColorNormal);
            TextPaint tp = tv.getPaint();
            tp.setFakeBoldText(true);
        }
    }

    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {
        super.onLayout(changed, l, t, r, b);
        if (mCurrentScroll == 0 && mSelectedTab != 0) {
            mCurrentScroll = getWidth() * mSelectedTab;
        }
    }

    public static class TitleInfo {
        String name;

        public TitleInfo(String name) {
            this.name = name;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }
    }

    public interface ClickTabCallback{
        public void clickTab(int index);
    }

    private ClickTabCallback clickTabCallback;
}


