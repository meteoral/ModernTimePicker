package com.liuqingwei.moderntimepicker;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.res.Resources;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;

/**
 * ModernTimePicker custom view.
 *
 * https://github.com/meteoral
 *
 * @auther Meteoral
 * @created 10.11.2015
 * @Email mail@liuqingwei.com
 * @WebSite http://liuqingwei.com
*/
public class ModernTimePicker extends View {

    private static final String TAG = ModernTimePicker.class.getSimpleName();

    /**
     * The rectangle enclosing the circle.
     */
    private final RectF mCircleBounds = new RectF();

    /**
     * the rect for the thumb square
     */
    private final RectF mSquareRect = new RectF();

    /**
     * The width of the Clock used to paint the circle.
     */
    private int mClockCircleStrokeWidth = 10;

    /**
     * The gravity of the clock. Where should the circle be draw within the given bounds
     */
    private int mGravity = Gravity.CENTER;

    /**
     * Radius of the circle
     *
     * <p> Note: (Re)calculated in {@link #onMeasure(int, int)}. </p>
     */
    private float mRadius;
    /**
     * indicates if the thumb is visible
     */
    private boolean mIsThumbEnabled = true;
    /**
     * The pointer width (in pixels).
     */
    private int mThumbRadius = 20;

    private Drawable mThumb;
    /**
     * The Thumb pos x.
     *
     * Care. the position is not the position of the rotated thumb. The position is only calculated
     * in {@link #onMeasure(int, int)}
     */
    private float mThumbPosX;

    /**
     * The Thumb pos y.
     *
     * Care. the position is not the position of the rotated thumb. The position is only calculated
     * in {@link #onMeasure(int, int)}
     */
    private float mThumbPosY;
    /**
     * The Horizontal inset calcualted in {@link #computeInsets(int, int)} depends on {@link
     * #mGravity}.
     */

    /**
     * The Circle's Center x of the view
     */
    private float mCircleCenterOffsetX;
    /**
     * The Circle's Center y of the view
     */
    private float mCircleCenterOffsetY;

    private int mHorizontalInset = 0;
    /**
     * The Vertical inset calcualted in {@link #computeInsets(int, int)} depends on {@link
     * #mGravity}..
     */
    private int mVerticalInset = 0;
    /**
     * The Clock's Background Paint
     */
    private Paint mBackClockPaint;

    private Paint mProgressPaint;

    private Paint mThumbPaint;

    private int mThumbXPos;
    private int mThumbYPos;
    private float mClockStep = 360 / 12 / 12;
    private String ampm;
    private int mClockHour;
    private int mClockMinute;
    private OnTimeCircleChangeListener mOnTimeCircleChangeListener;

    public interface OnTimeCircleChangeListener{

        void onTimeChanged(ModernTimePicker timePicker,int progress,boolean fromUser);

        void onStartTrackingTouch(ModernTimePicker timePicker);

        void onStopTrackingTouch(ModernTimePicker timePicker);
    }

    public ModernTimePicker(final Context context){
        this(context, null);
    }
    public ModernTimePicker(Context context,AttributeSet attrs){
        this(context, attrs, R.attr.TimeCircularStyle);
    }
    public ModernTimePicker(final Context context,final AttributeSet attrs,final int style){
        super(context,attrs,style);
        final TypedArray attributes = context
                .obtainStyledAttributes(attrs, R.styleable.HoloModernTimePicker,
                        style, 0);
        if(attributes != null){
            try{

            }finally {
                // recycle the attributes
                attributes.recycle();
            }
        }
        final Resources res = getResources();
        int thumbHalfheight = 0;
        int thumbHalfWidth = 0;
        mThumb = res.getDrawable(R.drawable.seek_arc_control_selector);
        thumbHalfheight = (int) mThumb.getIntrinsicHeight() / 2;
        thumbHalfWidth = (int) mThumb.getIntrinsicWidth() / 2;
        mThumb.setBounds(-thumbHalfWidth, -thumbHalfheight, thumbHalfWidth,
                thumbHalfheight);
        mBackClockPaint = new Paint();
        mBackClockPaint.setColor(Color.LTGRAY);
        mBackClockPaint.setAntiAlias(true);
        mBackClockPaint.setStrokeWidth(mClockCircleStrokeWidth);
        mBackClockPaint.setStyle(Paint.Style.STROKE);
    }
    @Override
    protected void onDraw(final Canvas canvas){
        canvas.translate(mCircleCenterOffsetX,mCircleCenterOffsetY);
        canvas.drawArc(mCircleBounds,0,360,false,mBackClockPaint);

        if (isThumbEnabled()) {
            // draw the thumb square at the correct rotated position
            canvas.save();
            canvas.translate(0,-mCircleCenterOffsetY);
            mThumb.draw(canvas);
            canvas.restore();
        }
    }
    @Override
    protected void onMeasure(final int widthMeasureSpec, final int heightMeasureSpec) {
        final int height = getDefaultSize(
                getSuggestedMinimumHeight() + getPaddingTop() + getPaddingBottom(),
                heightMeasureSpec);
        final int width = getDefaultSize(
                getSuggestedMinimumWidth() + getPaddingLeft() + getPaddingRight(),
                widthMeasureSpec);

        final int diameter;
        if (heightMeasureSpec == MeasureSpec.UNSPECIFIED) {
            // ScrollView
            diameter = width;
            computeInsets(0, 0);
        } else if (widthMeasureSpec == MeasureSpec.UNSPECIFIED) {
            // HorizontalScrollView
            diameter = height;
            computeInsets(0, 0);
        } else {
            // Default
            diameter = Math.min(width, height);
            computeInsets(width - diameter, height - diameter);
        }

        setMeasuredDimension(diameter, diameter);

        final float halfWidth = diameter * 0.5f;

        final float drawedWith;
        if (isThumbEnabled()) {
            drawedWith = mThumbRadius * (5f / 6f);
        }else{
            drawedWith = mClockCircleStrokeWidth / 2f;
        }
        // -0.5f for pixel perfect fit inside the viewbounds
        mRadius = halfWidth - drawedWith - 0.5f;

        mCircleBounds.set(-mRadius, -mRadius, mRadius, mRadius);

        mThumbPosX = (float) (mRadius * Math.cos(0));
        mThumbPosY = (float) (mRadius * Math.sin(0));

        mCircleCenterOffsetX = halfWidth + mHorizontalInset;
        mCircleCenterOffsetY = halfWidth + mVerticalInset;
    }

    /**
     * Compute insets.
     *
     * <pre>
     *  ______________________
     * |_________dx/2_________|
     * |......| /'''''\|......|
     * |-dx/2-|| View ||-dx/2-|
     * |______| \_____/|______|
     * |________ dx/2_________|
     * </pre>
     *
     * @param dx the dx the horizontal unfilled space
     * @param dy the dy the horizontal unfilled space
     */
    @SuppressLint("NewApi")
    private void computeInsets(final int dx, final int dy) {
        int absoluteGravity = mGravity;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
            absoluteGravity = Gravity.getAbsoluteGravity(mGravity, getLayoutDirection());
        }

        switch (absoluteGravity & Gravity.HORIZONTAL_GRAVITY_MASK) {
            case Gravity.LEFT:
                mHorizontalInset = 0;
                break;
            case Gravity.RIGHT:
                mHorizontalInset = dx;
                break;
            case Gravity.CENTER_HORIZONTAL:
            default:
                mHorizontalInset = dx / 2;
                break;
        }
        switch (absoluteGravity & Gravity.VERTICAL_GRAVITY_MASK) {
            case Gravity.TOP:
                mVerticalInset = 0;
                break;
            case Gravity.BOTTOM:
                mVerticalInset = dy;
                break;
            case Gravity.CENTER_VERTICAL:
            default:
                mVerticalInset = dy / 2;
                break;
        }
    }

    @Override
    public boolean onTouchEvent(MotionEvent event){
        switch (event.getAction()){
            case MotionEvent.ACTION_DOWN:

                break;
            case MotionEvent.ACTION_MOVE:

                break;
            case MotionEvent.ACTION_UP:

                break;
            case MotionEvent.ACTION_CANCEL:

                break;
        }
        return true;
    }

    /**
     * shows or hides the thumb of the progress bar
     *
     * @param enabled true to show the thumb
     */
    public void setThumbEnabled(final boolean enabled) {
        mIsThumbEnabled = enabled;
    }
    /**
     * @return true if the marker is visible
     */
    public boolean isThumbEnabled() {
        return mIsThumbEnabled;
    }
}
