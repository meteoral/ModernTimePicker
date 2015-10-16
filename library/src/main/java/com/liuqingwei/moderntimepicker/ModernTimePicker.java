package com.liuqingwei.moderntimepicker;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.view.Gravity;
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
     * The Clock's Background Paint
     */
    private Paint mBackClockPaint;

    public ModernTimePicker(final Context context){
        super(context);
    }
    @Override
    protected void onDraw(final Canvas canvas){
        mBackClockPaint = new Paint();
        mBackClockPaint.setColor(Color.BLUE);
        mBackClockPaint.setStrokeWidth(mClockCircleStrokeWidth);
        canvas.drawArc(mCircleBounds,230,0,false,mBackClockPaint);
        super.onDraw(canvas);

    }
    @Override
    protected void onMeasure(final int widthMeasureSpec, final int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec,heightMeasureSpec);

    }
}
