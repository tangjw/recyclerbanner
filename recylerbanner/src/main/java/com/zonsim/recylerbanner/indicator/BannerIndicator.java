package com.zonsim.recylerbanner.indicator;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.view.View;

import com.zonsim.recylerbanner.R;


public class BannerIndicator extends View implements Indicator{
    
    public static final String TAG = "BannerIndicator";
    
    private float mRadius;
    private float mIndicatorRadius;
    
    private final Paint mPaintFill = new Paint(Paint.ANTI_ALIAS_FLAG);
    private final Paint mPaintStroke = new Paint(Paint.ANTI_ALIAS_FLAG);
    private final Paint mPaintIndicator = new Paint(Paint.ANTI_ALIAS_FLAG);
    
    private int mCurrentPage;
    private int mFollowPage;
    
    private boolean mCenterHorizontal;
    private float mIndicatorSpace;
    
    private int mCount;
    
    public BannerIndicator(Context context) {
        this(context, null);
    }
    
    public BannerIndicator(Context context, AttributeSet attrs) {
        super(context, attrs);
        
        TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.BannerIndicator);
        
        mCenterHorizontal = a.getBoolean(R.styleable.BannerIndicator_indicator_centerHorizontal, true);
        
        mPaintFill.setStyle(Paint.Style.FILL);
        mPaintFill.setColor(a.getColor(R.styleable.BannerIndicator_circle_indicator_color, 0xffffff));
        mPaintStroke.setStyle(Paint.Style.STROKE);
        mPaintStroke.setColor(a.getColor(R.styleable.BannerIndicator_circle_indicator_stroke_color, 0x000000));
        mPaintStroke.setStrokeWidth(a.getDimension(R.styleable.BannerIndicator_circle_indicator_stroke_width, 0));
        mPaintIndicator.setStyle(Paint.Style.FILL);
        mPaintIndicator.setColor(a.getColor(R.styleable.BannerIndicator_circle_indicator_fill_color, 0x3f51b5));
        mRadius = a.getDimension(R.styleable.BannerIndicator_circle_indicator_radius, 4);
        mIndicatorSpace = a.getDimension(R.styleable.BannerIndicator_circle_indicator_space, 20);
        mIndicatorRadius = a.getDimension(R.styleable.BannerIndicator_circle_indicator_indicator_radius, 5);
        
        if (mIndicatorRadius < mRadius) mIndicatorRadius = mRadius;
        
        a.recycle();
    }
    
    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        
        final int count = mCount;
        if (count == 0) {
            return;
        }
        
        if (mCurrentPage >= count) {
            setCurrentPosition(count - 1);
            return;
        }
        
        int width = getWidth();
        int paddingLeft = getPaddingLeft();
        
        final float circleAndSpace = 2 * mRadius + mIndicatorSpace;//直径+圆的间隔
        final float yOffset = getHeight() / 2;//竖直方向圆心偏移量，剧中对齐
        float xOffset = paddingLeft + mRadius;//水平方向圆心偏移量
        
        //如果采用水平居中对齐
        if (mCenterHorizontal) {
            //xOffset += ((width - paddingLeft - paddingRight) - (count * circleAndSpace)) / 2.0f;
            xOffset = (width - count * 2 * mRadius - (count - 1) * mIndicatorSpace) / 2 - mRadius;
        }
        
        float cX;
        float cY;
        
        float strokeRadius = mRadius;
        //如果绘制外圆
        if (mPaintStroke.getStrokeWidth() > 0) {
            strokeRadius -= mPaintStroke.getStrokeWidth() / 2.0f;
        }
        
        //绘制所有圆点
        for (int i = 0; i < count; i++) {
            
            cX = xOffset + (i * circleAndSpace);//计算下个圆绘制起点偏移量
            cY = yOffset;
            
            //绘制圆
            if (mPaintFill.getAlpha() > 0) {
                canvas.drawCircle(cX, cY, strokeRadius, mPaintFill);
            }
            
            //绘制外圆
            if (strokeRadius != mRadius) {
                canvas.drawCircle(cX, cY, mRadius, mPaintStroke);
            }
        }
    
        float cx = mFollowPage * circleAndSpace;
        
        cX = xOffset + cx;
        cY = yOffset;
        canvas.drawCircle(cX, cY, mIndicatorRadius, mPaintIndicator);
    }
    
    @Override
    public void setCount(int count) {
        this.mCount = count;
    }
    
    @Override
    public void setCurrentPosition(int position) {
        mCurrentPage = mCount == 0 ? mCount : position % mCount;
        mFollowPage = mCount == 0 ? mCount : position % mCount;
    
        invalidate();
    }
    
    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        setMeasuredDimension(measureWidth(widthMeasureSpec), measureHeight(heightMeasureSpec));
    }
    
    private int measureWidth(int measureSpec) {
        int width;
        int specMode = MeasureSpec.getMode(measureSpec);
        int specSize = MeasureSpec.getSize(measureSpec);
        
        if (specMode == MeasureSpec.EXACTLY) {
            width = specSize;
        } else {
            final int count = mCount;
            width = (int) (getPaddingLeft() + getPaddingRight()
                    + (count * 2 * mRadius) + (mIndicatorRadius - mRadius) * 2 + (count - 1) * mIndicatorSpace);
            if (specMode == MeasureSpec.AT_MOST) {
                width = Math.min(width, specSize);
            }
        }
        return width;
    }
    
    private int measureHeight(int measureSpec) {
        int height;
        int specMode = MeasureSpec.getMode(measureSpec);
        int specSize = MeasureSpec.getSize(measureSpec);
        
        if (specMode == MeasureSpec.EXACTLY) {
            height = specSize;
        } else {
            height = (int) (2 * mRadius + getPaddingTop() + getPaddingBottom() + 1);
            if (specMode == MeasureSpec.AT_MOST) {
                height = Math.min(height, specSize);
            }
        }
        return height;
    }
}
