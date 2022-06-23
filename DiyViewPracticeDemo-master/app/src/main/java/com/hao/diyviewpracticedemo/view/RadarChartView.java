package com.hao.diyviewpracticedemo.view;

import android.animation.Animator;
import android.animation.ValueAnimator;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.DashPathEffect;
import android.graphics.Paint;
import android.graphics.Path;
import android.util.AttributeSet;
import android.view.View;

import java.util.Locale;

import androidx.annotation.Nullable;

public class RadarChartView extends View {

    //总共的边界数
    private int dataCount = 6;
    //每个的角度
    private float angle = (float) (Math.PI * 2 / dataCount);
    //半径
    private float radius;
    //中心点
    private int centerX;
    private int centerY;
    //雷达图边界显示文字
    private String[] strings = {"Jup", "Kum", "Pok", "Tut", "Kak", "Buu"};
    private double[] drawed = new double[6];
    //边界值
    private double[] data = {100, 60, 60, 60, 100, 30};
    //最大值
    private final float MAX_Value = 100;

    //绘制雷达的path
    private Path radarPath;
    //绘制虚线用
    private DashPathEffect dashPathEffect;

    //绘制覆盖区域用
    private Path regionPath;

    //测量文字大小
    private Paint.FontMetrics fontMetrics;

    //绘制雷达边界
    private Paint radarPaint;
    //绘制实际数值
    private Paint valuePaint;
    //绘制边界文字
    private Paint textPaint;
    //绘制中心文字
    private Paint centerTextPaint;


    public RadarChartView(Context context) {
        super(context);
        init();
    }

    public RadarChartView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public RadarChartView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    private void init() {

        radarPath = new Path();
        dashPathEffect = new DashPathEffect(new float[]{20, 10, 15, 5}, 0);
        regionPath = new Path();

        radarPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        radarPaint.setStyle(Paint.Style.STROKE);
        radarPaint.setStrokeWidth(2);
        radarPaint.setColor(Color.parseColor("#D3D3D3"));

        valuePaint = new Paint();
        valuePaint.setStyle(Paint.Style.STROKE);
        valuePaint.setColor(Color.parseColor("#AFEEEE"));
        valuePaint.setStrokeWidth(5);

        textPaint = new Paint();
        textPaint.setColor(Color.parseColor("#D3D3D3"));
        textPaint.setTextSize(30);
        fontMetrics = textPaint.getFontMetrics();

        centerTextPaint = new Paint();
        centerTextPaint.setTextSize(40);
        centerTextPaint.setColor(Color.WHITE);

        startAnim(0);
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        radius = Math.min(h, w) / 2.0f * 0.9f;

        centerX = w / 2;
        centerY = h / 2;
        invalidate();

    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        drawRader(canvas);
        drawText(canvas);
        drawRegion(canvas);
    }

    /**
     * 绘制雷达图背景
     */
    private void drawRader(Canvas canvas) {
        radarPaint.setPathEffect(null);
        float gap = radius / (dataCount - 1);
        for (int i = 1; i < dataCount; i++) {
            float currentR = gap * i;
            radarPath.reset();
            for (int j = 0; j < dataCount; j++) {
                if (j == 0) {
                    radarPath.moveTo(centerX + currentR, centerY);
                } else {
                    radarPath.lineTo((float) (centerX + currentR * Math.cos(angle * j)), (float) (centerY + currentR * Math.sin(angle * j)));
                }
            }
            radarPath.close();
            canvas.drawPath(radarPath, radarPaint);
        }
        radarPaint.setPathEffect(dashPathEffect);
        for (int i = 0; i < dataCount; i++) {
            radarPath.reset();
            radarPath.moveTo(centerX, centerY);
            radarPath.lineTo((float) (centerX + radius * Math.cos(angle * i)), (float) (centerY + radius * Math.sin(angle * i)));
            canvas.drawPath(radarPath, radarPaint);
        }
    }


    private void drawText(Canvas canvas) {
        float fontBaseLine = fontMetrics.descent - fontMetrics.ascent;
        for (int i = 0; i < dataCount; i++) {
            float x = (float) (centerX + (radius + fontBaseLine / 2) * Math.cos(angle * i));
            float y = (float) (centerY + (radius + fontBaseLine / 2) * Math.sin(angle * i));
            if (angle * i >= 0 && angle * i <= Math.PI / 2) {
                canvas.drawText(strings[i], x, y, textPaint);
            } else if (angle * i >= 3 * Math.PI / 2 && angle * i <= Math.PI * 2) {
                canvas.drawText(strings[i], x, y, textPaint);
            } else if (angle * i > Math.PI / 2 && angle * i <= Math.PI) {//第2象限
                float dis = textPaint.measureText(strings[i]);//文本长度
                canvas.drawText(strings[i], x - dis, y, textPaint);
            } else if (angle * i >= Math.PI && angle * i < 3 * Math.PI / 2) {//第1象限
                float dis = textPaint.measureText(strings[i]);//文本长度
                canvas.drawText(strings[i], x - dis, y, textPaint);
            }
        }
    }

    private void drawRegion(Canvas canvas) {
        int sum = 0;
        for (int i = 0; i < dataCount; i++) {
            double percent = drawed[i] / MAX_Value;
            sum += drawed[i];

            float x = (float) (centerX + radius * Math.cos(angle * i) * percent);
            float y = (float) (centerY + radius * Math.sin(angle * i) * percent);
            if (i == 0) {
                regionPath.moveTo(x, centerY);
            } else {
                regionPath.lineTo(x, y);
            }
            canvas.drawCircle(x, y, 5, valuePaint);
        }
        regionPath.close();
        canvas.drawPath(regionPath, valuePaint);
        valuePaint.setAlpha(255 / 2);
        valuePaint.setStyle(Paint.Style.FILL_AND_STROKE);
        canvas.drawPath(regionPath, valuePaint);
        canvas.save();
        String centerText = "能力值:%d";
        canvas.drawText(String.format(Locale.getDefault(), centerText, sum), centerX - centerTextPaint.measureText(centerText) / 2, centerY, centerTextPaint);
        canvas.restore();
    }

    private void startAnim(final int index) {
        if (index > 5) {
            return;
        }
        ValueAnimator valueAnimator;
        valueAnimator = ValueAnimator.ofFloat(0f, (float) data[index]);
        valueAnimator.setDuration(500);
        valueAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                drawed[index] = (float) animation.getAnimatedValue() + 1;
                if (drawed[index] > data[index]) {
                    drawed[index] = data[index];
                }
                invalidate();
            }
        });
        valueAnimator.addListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animation) {

            }

            @Override
            public void onAnimationEnd(Animator animation) {
                startAnim(index + 1);
            }

            @Override
            public void onAnimationCancel(Animator animation) {

            }

            @Override
            public void onAnimationRepeat(Animator animation) {

            }
        });
        valueAnimator.start();
    }
}
