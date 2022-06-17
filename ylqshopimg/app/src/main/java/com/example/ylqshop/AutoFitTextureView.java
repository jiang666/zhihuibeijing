package com.example.ylqshop;

import android.content.Context;
import android.util.AttributeSet;
import android.util.Log;
import android.view.TextureView;
import android.view.ViewGroup;
import android.view.WindowManager;

public class AutoFitTextureView extends TextureView {

    private boolean hasSetAspectRatio = false;  //layout测量值为主，后面就不移动布局了
    private int screenW = 0;
    private int mRatioWidth = 0;
    private int mRatioHeight = 0;
    private int widthTrue = 0;
    private int heightTrue = 0;

    public AutoFitTextureView(Context context) {
        this(context, null);
    }

    public AutoFitTextureView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public AutoFitTextureView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        WindowManager wm = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
        screenW = wm.getDefaultDisplay().getWidth();
    }

    public void setAspectRatio(int width, int height) {
        if (width < 0 || height < 0) {
            throw new IllegalArgumentException("Size cannot be negative.");
        }
        hasSetAspectRatio = true;
        mRatioWidth = width;
        mRatioHeight = height;
        requestLayout();
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        int width = MeasureSpec.getSize(widthMeasureSpec);
        int height = MeasureSpec.getSize(heightMeasureSpec);
        Log.e("scaleBitmap: ", "---------------------onmesure触发");
        //初次测量mRatioWidth和mRatioHeight有为0的情况，且最终layout的参数以实际camera预览参数为主
        if (hasSetAspectRatio && 0 != mRatioWidth && 0 != mRatioHeight) {
            widthTrue = mRatioWidth;
            heightTrue = mRatioHeight;
            setMeasuredDimension(mRatioWidth, mRatioHeight);
            ViewGroup.LayoutParams vp = this.getLayoutParams();
            if (vp instanceof ViewGroup.MarginLayoutParams) {
                Log.e("scaleBitmap: ", "----------------左移" + ((mRatioWidth - screenW) / 2));
                ((ViewGroup.MarginLayoutParams) vp).leftMargin = -(mRatioWidth - screenW) / 2;
                requestLayout();
            }
            return;
        }
        if (0 == mRatioWidth || 0 == mRatioHeight) {
            widthTrue = width;
            heightTrue = height;
            setMeasuredDimension(width, height);
        } else {
            if (width > height * mRatioWidth / mRatioHeight) {
                widthTrue = width;
                heightTrue = width * mRatioHeight / mRatioWidth;
                setMeasuredDimension(width, width * mRatioHeight / mRatioWidth);
            } else {
                widthTrue = height * mRatioWidth / mRatioHeight;
                heightTrue = height;
                setMeasuredDimension(height * mRatioWidth / mRatioHeight, height);
            }
        }
    }

    public int getPreViewWidth() {
        return widthTrue;
    }

    public int getPreViewHeight() {
        return heightTrue;
    }
}
