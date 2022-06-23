package com.hao.diyviewpracticedemo.view;

import android.animation.Animator;
import android.animation.ValueAnimator;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PathMeasure;
import android.graphics.RectF;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;

import com.hao.diyviewpracticedemo.R;

import androidx.annotation.Nullable;

public class PathView extends View {

    private int mPathColor = Color.RED;

    //画线和图片的Paint
    private Paint mPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
    private Paint mCarPaint = new Paint(Paint.ANTI_ALIAS_FLAG);

    //承载图片的Rect
    private RectF mRect = new RectF();
    private Path mMovePath = new Path();

    private float mStartX = 0f;
    private float mStartY = 0f;

    //测量Path
    private PathMeasure pathMeasure = new PathMeasure();

    //点和角度
    private float[] pos = new float[2];
    private float[] tan = new float[2];

    //开始和结束动画
    private boolean isMove = false;

    private int mRectWidth = 30;

    private int mDuration = 5;

    private Drawable mDrawable;

    private Bitmap mBitmap;


    public PathView(Context context) {
        this(context, null);
    }

    public PathView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        //这里是要获取到自定义View的属性值，便不再过多介绍
        TypedArray typedArray = context.obtainStyledAttributes(attrs, R.styleable.MoveView);
        mPathColor = typedArray.getColor(R.styleable.MoveView_MovePathColor, mPathColor);
        mDuration = typedArray.getInt(R.styleable.MoveView_MoveDuration, mDuration);
        mDrawable = typedArray.getDrawable(R.styleable.MoveView_MoveDrawableRes);

        //这里是获取要移动的图片
        if (mDrawable == null){
            mBitmap = BitmapFactory.decodeResource(getResources(), R.mipmap.rocket);
        }else {
            mBitmap = ((BitmapDrawable) mDrawable).getBitmap();
        }
        typedArray.recycle();
        //初始化画线和移动图片的画笔
        initPaint();
    }

    public PathView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }


    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        //画出轨道线
        canvas.drawPath(mMovePath, mPaint);
        //画出移动物体
        drawMove(canvas);
    }

    private void drawMove(Canvas canvas) {
        pathMeasure.setPath(mMovePath, false);
        if (isMove) {
            //计算图片的旋转角度
            float degree = (float) (Math.atan2(tan[1], tan[0]) * 180.0 / Math.PI);
            canvas.rotate(degree, pos[0], pos[1]);
            //设置中心点在运行轨道上
            mRect.set(pos[0] - mRectWidth, pos[1] - mRectWidth, pos[0] + mRectWidth, pos[1] + mRectWidth);
            //绘制draw
            if (mDrawable == null) {
                canvas.drawRect(mRect, mCarPaint);
            } else {
                canvas.drawBitmap(mBitmap, null, mRect, mPaint);
            }
        }
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {

        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                //设置起始点
                mStartX = event.getX();
                mStartY = event.getY();
                //清空原有的轨道
                mMovePath.reset();
                //移动到起始点
                mMovePath.moveTo(mStartX, mStartY);
                return true;
            case MotionEvent.ACTION_MOVE:
                final float endX = (mStartX + event.getX()) / 2;
                final float endY = (mStartY + event.getY()) / 2;
                //画出轨道曲线，quad是二次曲线
                mMovePath.quadTo(mStartX, mStartY, endX, endY);
                mStartX = event.getX();
                mStartY = event.getY();
                //重绘
                invalidate();
                return true;
        }
        return super.onTouchEvent(event);
    }

    public void startAnim() {
        isMove = true;
        //移动动画的实现
        //这里是获取动画的长度也就是画线的长度
        ValueAnimator valueAnimator = ValueAnimator.ofFloat(0f, pathMeasure.getLength());
        //动画间隔(帧率)
        valueAnimator.setDuration(mDuration * 1000L);
        valueAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                //更新绘制下一帧
                float distance = (float) animation.getAnimatedValue();
                pathMeasure.getPosTan(distance, pos, tan);
                invalidate();
            }
        });

        valueAnimator.addListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animation) {

            }

            @Override
            public void onAnimationEnd(Animator animation) {
                isMove = false;
            }

            @Override
            public void onAnimationCancel(Animator animation) {
                isMove = false;
            }

            @Override
            public void onAnimationRepeat(Animator animation) {

            }
        });
        valueAnimator.start();
    }

    private void initPaint() {
        mPaint.setColor(mPathColor);
        mPaint.setStyle(Paint.Style.STROKE);
        mPaint.setStrokeWidth(3);

        mCarPaint.setColor(mPathColor);
        mCarPaint.setStyle(Paint.Style.FILL);
    }

}
