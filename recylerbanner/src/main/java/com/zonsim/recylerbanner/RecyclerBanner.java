package com.zonsim.recylerbanner;

import android.content.Context;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.LinearSmoothScroller;
import android.support.v7.widget.LinearSnapHelper;
import android.support.v7.widget.RecyclerView;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;

import com.zonsim.recylerbanner.adapter.BannerAdapter;
import com.zonsim.recylerbanner.entity.Banner;
import com.zonsim.recylerbanner.indicator.Indicator;

import java.util.ArrayList;
import java.util.List;

/**
 * ^-^
 * Created by tang-jw on 2017/6/27.
 */

public class RecyclerBanner extends FrameLayout {
    
    private BannerAdapter mAdapter;
    private List<Banner> mBanners;
    private RecyclerView mRecyclerView;
    private Indicator mIndicator;
    
    private int mCurrentPosition;
    private Handler mHandler;
    private boolean isPlaying;
    private int startX = 0, startY = 0;
    
    /**
     * 播放下一张的间隔 默认 5000ms
     */
    private final long mPlayDuration = 2400L;
    
    /**
     * 平滑滚动的速率 越大越慢 默认90f
     */
    private final float mSmoothRate = 90f;
    
    public RecyclerBanner(Context context) {
        this(context, null);
    }
    
    public RecyclerBanner(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }
    
    public RecyclerBanner(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }
    
    public void replaceBanners(@NonNull List<Banner> banners) {
        
        mBanners = banners;
        if (mBanners.size() == 0) return;
        mAdapter.replaceData(mBanners);
        mCurrentPosition = Integer.MAX_VALUE / 2 - (Integer.MAX_VALUE / 2 % mBanners.size());
        
        if (mIndicator != null) {
            mIndicator.setCount(mBanners.size());
            mIndicator.setCurrentPosition(mCurrentPosition);
        }
        mRecyclerView.scrollToPosition(mCurrentPosition);
        
        if (mHandler == null) {
            mHandler = new Handler();
        }
        
        setPlaying(true);
    }
    
    
    public void bindIndicator(@NonNull Indicator indicator) {
        mIndicator = indicator;
    }
    
    private void init() {
        
        mBanners = new ArrayList<>();
        mAdapter = new BannerAdapter(mBanners);
        
        mRecyclerView = new RecyclerView(getContext());
        
        addView(mRecyclerView, new LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
        
        mRecyclerView.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false) {
            
            @Override
            public void smoothScrollToPosition(RecyclerView recyclerView, RecyclerView.State state, int position) {
                LinearSmoothScroller linearSmoothScroller =
                        new LinearSmoothScroller(recyclerView.getContext()) {
                            @Override
                            protected float calculateSpeedPerPixel(DisplayMetrics displayMetrics) {
                                return mSmoothRate / displayMetrics.densityDpi;
                            }
                        };
                
                linearSmoothScroller.setTargetPosition(position);
                startSmoothScroll(linearSmoothScroller);
            }
        });
        
        mRecyclerView.setAdapter(mAdapter);
        
        BannerSnapHelper snapHelper = new BannerSnapHelper();
        snapHelper.attachToRecyclerView(mRecyclerView);
        
    }
    
    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
        
        switch (ev.getAction()) {
            case MotionEvent.ACTION_DOWN:
                startX = (int) ev.getX();
                startY = (int) ev.getY();
                getParent().requestDisallowInterceptTouchEvent(true);
                setPlaying(false);
                break;
            case MotionEvent.ACTION_MOVE:
                int moveX = (int) ev.getX();
                int moveY = (int) ev.getY();
                int disX = moveX - startX;
                int disY = moveY - startY;
                getParent().requestDisallowInterceptTouchEvent(2 * Math.abs(disX) > Math.abs(disY));
                setPlaying(false);
                break;
            case MotionEvent.ACTION_UP:
            case MotionEvent.ACTION_CANCEL:
                setPlaying(true);
                break;
        }
        return super.dispatchTouchEvent(ev);
    }
    
    public synchronized void setPlaying(boolean active) {
        if (!isPlaying && active && mAdapter.getItemCount() > 1) {
            mHandler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    mRecyclerView.smoothScrollToPosition(++mCurrentPosition);
                    if (mIndicator != null) {
                        mIndicator.setCurrentPosition(mCurrentPosition);
                    }
                    mHandler.postDelayed(this, mPlayDuration);
                }
            }, mPlayDuration);
            isPlaying = true;
        } else if (isPlaying && !active) {
            if (mHandler != null) {
                mHandler.removeCallbacksAndMessages(null);
            }
            isPlaying = false;
        }
    }
    
    private class BannerSnapHelper extends LinearSnapHelper {
        
        @Override
        public int[] calculateDistanceToFinalSnap(@NonNull RecyclerView.LayoutManager layoutManager, @NonNull View targetView) {
            
            mCurrentPosition = layoutManager.getPosition(targetView);
            
            if (mIndicator != null) {
                mIndicator.setCurrentPosition(mCurrentPosition);
            }
            
            return super.calculateDistanceToFinalSnap(layoutManager, targetView);
        }
        
        @Override
        public int findTargetSnapPosition(RecyclerView.LayoutManager layoutManager, int velocityX, int velocityY) {
            
            int targetSnapPosition = super.findTargetSnapPosition(layoutManager, velocityX, velocityY);
            
            View snapView = findSnapView(layoutManager);
            
            LinearLayoutManager manager = (LinearLayoutManager) layoutManager;
            if (snapView != null) {
                int currentPosition = mCurrentPosition;
                
                int first = manager.findFirstVisibleItemPosition();
                int last = manager.findLastVisibleItemPosition();
                
                if (targetSnapPosition != 0) {
                    if (targetSnapPosition == -1) {
                        
                        if (first == last) {
                            //上下滑动情况
                            targetSnapPosition = currentPosition;
                        } else if (currentPosition == first) {
                            targetSnapPosition = currentPosition < manager.getItemCount() ? currentPosition + 1 : currentPosition;
                        } else if (currentPosition == last) {
                            targetSnapPosition = currentPosition > 0 ? currentPosition - 1 : currentPosition;
                        }
                        
                    } else {
                        currentPosition = targetSnapPosition < currentPosition ? last : (targetSnapPosition > currentPosition ? first : currentPosition);
                        targetSnapPosition = targetSnapPosition < currentPosition ?
                                (currentPosition > 0 ? currentPosition - 1 : currentPosition)
                                : (currentPosition < manager.getItemCount() ? currentPosition + 1 : currentPosition);
                    }
                }
                
            }
            
            return targetSnapPosition;
        }
        
    }
    
    @Override
    protected void onDetachedFromWindow() {
        if (mHandler != null) {
            mHandler.removeCallbacksAndMessages(null);
            mHandler = null;
        }
        super.onDetachedFromWindow();
    }
    
}
