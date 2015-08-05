package com.fenjuly.mylibrary;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.os.Bundle;
import android.os.Parcelable;
import android.util.AttributeSet;
import android.view.View;

/**
 * Created by liurongchan with love on 15/8/5.
 */
public class SpinnerLoader extends View {

    private static final int POINTS_COUNT = 9;
    private static final int STEP = 3;
    private static final int DEFAULT_COLOR = Color.rgb(87, 247, 250);
    private static final float DEFAULT_RADUIS = 180;
    private static final float DEFAULT_CIRCLE_RADUIS = 40;
    private static final float DEAFULT_MOVE_RADUIS = 30;

    /**
     * for save and restore instance of view.
     */
    private static final String INSTANCE_STATE = "saved_instance";
    private static final String ANGLE = "angle";
    private static final String BIGCIRCLECENTERX = "bigCircleCenterX";
    private static final String BIGCIRCLECENTERY = "bigCircleCenterY";
    private static final String RADUIS = "raduis";
    private static final String CIRCLERADUIS = "circleRaduis";
    private static final String MOVERADUIS = "moveRaduis";
    private static final String POINTCOLOR = "pointColor";
    private static final String STARTX1 = "startX1";
    private static final String STARTY1 = "startY1";
    private static final String ENDX1 = "endX1";
    private static final String ENDY1 = "endY1";
    private static final String STARTX2 = "startX2";
    private static final String STARTY2 = "startY2";
    private static final String ENDX2 = "endX2";
    private static final String ENDY2 = "endY2";
    private static final String CONTROLX1 = "controlX1";
    private static final String CONTROLY1 = "controlY1";

    private CirclePoint[] circlePoints = new CirclePoint[POINTS_COUNT];

    private int angle = 0;
    float bigCircleCenterX;
    float bigCircleCenterY;
    float raduis;
    float circleRaduis;
    float moveRaduis;
    int pointColor;

    private float startX1;
    private float startY1;
    private float startX2;
    private float startY2;

    private float controlX1;
    private float controlY1;

    private float endX1;
    private float endY1;
    private float endX2;
    private float endY2;

    private Path path1;
    private Paint circlePaint;
    private Paint linePaint;

    private boolean isFirst = true;

    public SpinnerLoader(Context context) {
        this(context, null);
    }

    public SpinnerLoader(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public SpinnerLoader(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);

        final TypedArray attributes = context.getTheme().obtainStyledAttributes(attrs, R.styleable.SpinnerLoader,
                defStyleAttr, 0);
        pointColor = attributes.getColor(R.styleable.SpinnerLoader_point_color, DEFAULT_COLOR);
        attributes.recycle();
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        setMeasuredDimension(measure(widthMeasureSpec, true), measure(heightMeasureSpec, false));
    }

    private int measure(int measureSpec, boolean isWidth) {
        int result;
        int mode = MeasureSpec.getMode(measureSpec);
        int size = MeasureSpec.getSize(measureSpec);
        int padding = isWidth ? getPaddingLeft() + getPaddingRight() : getPaddingTop() + getPaddingBottom();
        if (mode == MeasureSpec.EXACTLY) {
            result = size;
        } else {
            result = isWidth ? getSuggestedMinimumWidth() : getSuggestedMinimumHeight();
            result += padding;
            if (mode == MeasureSpec.AT_MOST) {
                if (isWidth) {
                    result = Math.max(result, size);
                } else {
                    result = Math.min(result, size);
                }
            }
        }
        return result;
    }

    @Override
    protected void onDraw(Canvas canvas) {
        if (isFirst) {
            init();
            isFirst = false;
        }
        for (int i = 0; i < POINTS_COUNT - 1; i ++) {
            CirclePoint p = circlePoints[i];
            canvas.drawCircle(p.x, p.y, p.raduis, circlePaint);
        }
        CirclePoint p = circlePoints[POINTS_COUNT - 1];
        p.x = bigCircleCenterX + (float)Math.cos(Math.toRadians(angle)) * raduis;
        p.y = bigCircleCenterY + (float)Math.sin(Math.toRadians(angle)) * raduis;
        canvas.drawCircle(p.x, p.y, p.raduis, circlePaint);
        for (int i = 0; i < POINTS_COUNT - 1; i++) {
            CirclePoint biggerP1 = circlePoints[i];

            //是否相交
            if (isIntersect(p, biggerP1)) {
                canvas.drawCircle(biggerP1.x, biggerP1.y, biggerP1.raduis + 6*(1-getDistanceRatio(p, biggerP1)), circlePaint);
            }

            if (isConnect(p, biggerP1)) {
                float headOffsetX1 = (float)(circleRaduis*Math.sin(Math.atan((p.y - biggerP1.y) / (p.x - biggerP1.x))));
                float headOffsetY1 = (float)(circleRaduis*Math.cos(Math.atan((p.y - biggerP1.y) / (p.x - biggerP1.x))));
                float footOffsetX1 = (float)(moveRaduis*Math.sin(Math.atan((p.y - biggerP1.y) / (p.x - biggerP1.x))));
                float footOffsetY1 = (float)(moveRaduis*Math.cos(Math.atan((p.y - biggerP1.y) / (p.x - biggerP1.x))));

                startX1 = biggerP1.x - headOffsetX1;
                startY1 = biggerP1.y + headOffsetY1;

                endX1 = biggerP1.x + headOffsetX1;
                endY1 = biggerP1.y - headOffsetY1;

                startX2 = p.x - footOffsetX1;
                startY2 = p.y + footOffsetY1;

                endX2 = p.x + footOffsetX1;
                endY2 = p.y - footOffsetY1;

                controlX1 = (biggerP1.x + p.x) / 2;
                controlY1 = (biggerP1.y + p.y) / 2;

                path1.reset();
                path1.moveTo(startX1, startY1);
                path1.quadTo(controlX1, controlY1, startX2, startY2);
                path1.lineTo(endX2, endY2);
                path1.quadTo(controlX1, controlY1, endX1, endY1);
                path1.lineTo(startX1, startY1);
                canvas.drawPath(path1, linePaint);
            }
        }

        angle = angle + STEP;
        invalidate();
    }

    protected void init() {
        float temp = getHeight() > getWidth() ? getWidth() / 2 : getHeight() / 2;
        raduis = temp - temp / DEFAULT_RADUIS * DEFAULT_CIRCLE_RADUIS;
        circleRaduis = DEFAULT_CIRCLE_RADUIS / DEFAULT_RADUIS * raduis;
        moveRaduis = DEAFULT_MOVE_RADUIS / DEFAULT_RADUIS * raduis;
        bigCircleCenterX = getPaddingLeft() + getWidth() / 2;
        bigCircleCenterY = getPaddingTop() + getHeight() / 2;

        path1 = new Path();
        initializePaints();
        initializePoints();
    }


    protected void initializePoints() {
        for (int i = 0; i < POINTS_COUNT; i++) {
            CirclePoint p = new CirclePoint();
            p.x = getPaddingLeft() + bigCircleCenterX + (float)Math.cos(Math.toRadians(45 * i)) * raduis;
            p.y = getPaddingTop() + bigCircleCenterY + (float)Math.sin(Math.toRadians(45 * i)) * raduis;
            p.color = pointColor;
            p.raduis = circleRaduis;
            if (i == POINTS_COUNT - 1) {
                p.raduis = moveRaduis;
            }
            circlePoints[i] = p;
        }
    }

    protected void initializePaints() {
        circlePaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        circlePaint.setColor(pointColor);

        linePaint = new Paint();
        linePaint.setAntiAlias(true);
        linePaint.setStyle(Paint.Style.FILL_AND_STROKE);
        linePaint.setStrokeWidth(1);
        linePaint.setColor(pointColor);

    }

    private boolean isIntersect(CirclePoint a, CirclePoint b) {
        float distance = (float)Math.sqrt((a.x - b.x)*(a.x - b.x) + (a.y - b.y)*(a.y - b.y));
        return distance < (a.raduis + b.raduis);
    }

    private boolean isConnect(CirclePoint a, CirclePoint b) {
        float distance = (float)Math.sqrt((a.x - b.x)*(a.x - b.x) + (a.y - b.y)*(a.y - b.y));
        return distance < raduis * Math.cos(Math.toRadians(62.5));
    }

    private float getDistanceRatio(CirclePoint a, CirclePoint b) {
        float distance = (float)Math.sqrt((a.x - b.x)*(a.x - b.x) + (a.y - b.y)*(a.y - b.y));
        return distance / (a.raduis + b.raduis);
    }

    protected float dp2px(float dp) {
        final float scale = getResources().getDisplayMetrics().density;
        return  dp * scale + 0.5f;
    }

    @Override
    protected Parcelable onSaveInstanceState() {
        final Bundle bundle = new Bundle();
        bundle.putParcelable(INSTANCE_STATE,super.onSaveInstanceState());
        bundle.putInt(ANGLE, angle);
        bundle.putFloat(BIGCIRCLECENTERX, bigCircleCenterX);
        bundle.putFloat(BIGCIRCLECENTERY, bigCircleCenterY);
        bundle.putFloat(RADUIS, raduis);
        bundle.putFloat(CIRCLERADUIS, circleRaduis);
        bundle.putFloat(MOVERADUIS, moveRaduis);
        bundle.putFloat(STARTX1, startX1);
        bundle.putFloat(STARTY1, startY1);
        bundle.putFloat(ENDX1, endX1);
        bundle.putFloat(ENDY1, endY1);
        bundle.putFloat(STARTX2, startX2);
        bundle.putFloat(STARTY2, startY2);
        bundle.putFloat(ENDX2, endX2);
        bundle.putFloat(ENDY2, endY2);
        bundle.putFloat(CONTROLX1, controlX1);
        bundle.putFloat(CONTROLY1, controlY1);
        bundle.putInt(POINTCOLOR, pointColor);
        return bundle;
    }

    @Override
    protected void onRestoreInstanceState(Parcelable state) {
        if(state instanceof Bundle){
            final Bundle bundle = (Bundle)state;
            angle = bundle.getInt(ANGLE);
            bigCircleCenterX = bundle.getFloat(BIGCIRCLECENTERX);
            bigCircleCenterY = bundle.getFloat(BIGCIRCLECENTERY);
            raduis = bundle.getFloat(RADUIS);
            circleRaduis = bundle.getFloat(CIRCLERADUIS);
            moveRaduis = bundle.getFloat(MOVERADUIS);
            startX1 = bundle.getFloat(STARTX1);
            startY1 = bundle.getFloat(STARTY1);
            endX1 = bundle.getFloat(ENDX1);
            endY1 = bundle.getFloat(ENDY1);
            startX2 = bundle.getFloat(STARTX2);
            startY2 = bundle.getFloat(STARTY2);
            endX2 = bundle.getFloat(ENDX2);
            endY2 = bundle.getFloat(ENDY2);
            controlX1 = bundle.getFloat(CONTROLX1);
            controlY1 = bundle.getFloat(CONTROLY1);
            pointColor = bundle.getInt(POINTCOLOR);
            init();
            super.onRestoreInstanceState(bundle.getParcelable(INSTANCE_STATE));
            return;
        }
        super.onRestoreInstanceState(state);
    }

    static class CirclePoint {
        public float raduis;
        public float x;
        public float y;
        public int color;
    }

}
