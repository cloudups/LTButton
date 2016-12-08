package com.shifeiqi.library;

import android.animation.ValueAnimator;
import android.annotation.TargetApi;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Point;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.os.Build;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.animation.AnticipateOvershootInterpolator;

/**
 * Created by shifeiqi on 2016/11/30.
 */

public class LTButton extends View {

    private static final String TAG = "ChoiceView";
    private Paint paint;
    private int width;
    private int height;
    private Point center = new Point();
    private int bigLinearHeight;
    private int bigLinearWidth;
    private int smallCircleWidth;

    private boolean open = false;
    int openColor = Color.GREEN;
    int closeColor = Color.RED;

    private Point bigLinearStart = new Point();
    private Point bigLinearEnd = new Point();
    private Point smallCirclePoint = new Point();


    private void initData() {
        width = getMeasuredWidth();
        height = getMeasuredHeight();

        center.x = width / 2;
        center.y = height / 2;

        bigLinearHeight = height / 3;
        bigLinearWidth = bigLinearHeight;
        bigLinearStart.x = center.x - bigLinearWidth / 2;
        bigLinearStart.y = center.y;
        bigLinearEnd.x = center.x + bigLinearWidth / 2;
        bigLinearEnd.y = center.y;
        Log.i(TAG, "center.x :" + center.x);
        Log.i(TAG, "point x: " + bigLinearStart.x);

        smallCircleWidth = bigLinearWidth / 2;
        smallCirclePoint.x = center.x - smallCircleWidth;
        smallCirclePoint.y = bigLinearStart.y;


        Log.i(TAG, "width: " + bigLinearWidth + " height: " + bigLinearHeight);
        //set the paint
        paint = new Paint();
        paint.setAntiAlias(true);
        paint.setColor(closeColor);
    }

    public LTButton(Context context) {
        super(context);
    }

    public LTButton(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public LTButton(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    public LTButton(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        initData();
    }

    @Override
    protected void onDraw(Canvas canvas) {


        int layerId = canvas.saveLayer(0, 0, width, height, null, Canvas.ALL_SAVE_FLAG);

        paint.setStrokeWidth(bigLinearHeight);
        paint.setStrokeCap(Paint.Cap.ROUND);
        paint.setStyle(Paint.Style.FILL);
        canvas.drawLine(bigLinearStart.x, bigLinearStart.y, bigLinearEnd.x, bigLinearEnd.y, paint);


        paint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.CLEAR));
        paint.setStrokeWidth(1);
        paint.setStyle(Paint.Style.FILL);

        canvas.drawCircle(smallCirclePoint.x, smallCirclePoint.y, smallCircleWidth / 2, paint);
        paint.setXfermode(null);


        canvas.restoreToCount(layerId);
    }


    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    public void startAnimation() {


        ValueAnimator animator = ValueAnimator.ofFloat(0f, (float) Math.PI);
        animator.setDuration(1000);
        animator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator valueAnimator) {
                float angle = (float) valueAnimator.getAnimatedValue();

                bigLinearStart.x = (int) (center.x - bigLinearWidth / 2 * Math.cos(angle));
                bigLinearStart.y = (int) (center.y + bigLinearWidth / 2 * Math.sin(angle));
                bigLinearEnd.x = 2 * center.x - bigLinearStart.x;
                bigLinearEnd.y = 2 * center.y - bigLinearStart.y;

                if (open) {

                    smallCirclePoint.x = (int) (center.x - bigLinearWidth / 2 * Math.cos(angle));
                    smallCirclePoint.y = (int) (center.y - bigLinearWidth / 2 * Math.sin(angle));
                } else {

                    smallCirclePoint.x = (int) (center.x - bigLinearWidth / 2 * Math.cos(Math.PI - angle));
                    smallCirclePoint.y = (int) (center.y + bigLinearWidth / 2 * Math.sin(angle));
                }

                smallCircleWidth = (int) (bigLinearWidth / 2 + bigLinearWidth / 2 * Math.sin(angle));

                invalidate();
            }
        });
        animator.setInterpolator(new AnticipateOvershootInterpolator());
        animator.start();

        ValueAnimator colorAnimator = null;

        if (open) {
            colorAnimator = ValueAnimator.ofArgb(openColor, closeColor);
        } else {
            colorAnimator = ValueAnimator.ofArgb(closeColor, openColor);
        }


        colorAnimator.setDuration(1000);
        colorAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator valueAnimator) {
                int color = (int) valueAnimator.getAnimatedValue();
                paint.setColor(color);
            }
        });
        colorAnimator.start();

        open = !open;
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if (event.getAction() == MotionEvent.ACTION_DOWN)
            startAnimation();

        return super.onTouchEvent(event);
    }

    public boolean isOpen() {
        return open;
    }

    public void setOpen(boolean open) {
        this.open = open;
    }
}
