package com.example.mylibrary.httpUtils.view;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.RectF;
import android.os.Handler;
import android.os.Message;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;

import java.util.Random;

public class AudioWaveView extends View {

    private Paint paint;
    private RectF rectF1;
    private RectF rectF2;
    private RectF rectF3;
    private int viewWidth;
    private int viewHeight;
    /**
     * 每个条的宽度
     */
    private int rectWidth;
    /**
     * 条数
     */
    private int columnCount = 3;
    /**
     * 条间距
     */
    private int space;
    /**
     * 条随机高度
     */
    private int randomHeight;
    private Random random;
    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            invalidate();
        }
    };
    private volatile boolean needMove = false;//运行

    public AudioWaveView(Context context) {
        super(context);
        init();
    }

    public AudioWaveView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);

        viewWidth = MeasureSpec.getSize(widthMeasureSpec);
        viewHeight = MeasureSpec.getSize(heightMeasureSpec);

        rectWidth = (viewWidth - space * (columnCount - 1)) / columnCount;
    }

    private void init() {
        space = dip2px(getContext(), 2.3F);
        paint = new Paint();
        paint.setStyle(Paint.Style.FILL);
        random = new Random();
        initRect();
    }

    public int dip2px(final Context context, final float n) {
        return (int) (n * context.getResources().getDisplayMetrics().density + 0.5f);
    }

    private void initRect() {
        rectF1 = new RectF();
        rectF2 = new RectF();
        rectF3 = new RectF();
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
//        Log.e("测试onDraw: ", "needMove=" + needMove);
        //清空
        if (!needMove) {
            canvas.drawColor(0, PorterDuff.Mode.CLEAR);
            return;
        }
        int left = rectWidth + space;

        //画每个条之前高度都重新随机生成
        randomHeight = random.nextInt(viewHeight);
        rectF1.set(left * 0, randomHeight, left * 0 + rectWidth, viewHeight);
        randomHeight = random.nextInt(viewHeight);
        rectF2.set(left * 1, randomHeight, left * 1 + rectWidth, viewHeight);
        randomHeight = random.nextInt(viewHeight);
        rectF3.set(left * 2, randomHeight, left * 2 + rectWidth, viewHeight);

        canvas.drawRect(rectF1, paint);
        canvas.drawRect(rectF2, paint);
        canvas.drawRect(rectF3, paint);
        //移动
        if (needMove) {
            handler.removeCallbacksAndMessages(null);
            handler.sendEmptyMessageDelayed(0, 150); //每间隔200毫秒发送消息刷新
        }
//        Log.e("测试onDraw: ", "测试onDraw: ");
    }

    //开始 1:ff5686 2:ffffff
    public void startMove(int type) {
        if (paint != null) {
//            Log.e("测试onDraw: ", "startMove:paint != null ");
            if (type == 1) {
                paint.setColor(Color.parseColor("#ff5686"));
            } else {
                paint.setColor(Color.parseColor("#ffffff"));
            }
        }
        if (handler != null) {
//            Log.e("测试onDraw: ", "startMove:handler != null ");
            handler.removeCallbacksAndMessages(null);
        }
        needMove = true;
        invalidate();
//        Log.e("测试onDraw: ", "startMove:needMove = true     invalidate");
    }

    //终止
    public void stopMove() {
        needMove = false;
        if (handler != null) {
            handler.removeCallbacksAndMessages(null);
        }
        invalidate();
    }

    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        stopMove();
    }

    @Override
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();
        if (handler != null) {
//            Log.e("测试onDraw: ", "startMove:handler != null ");
            handler.removeCallbacksAndMessages(null);
        }
        needMove = true;
        invalidate();
    }
}
