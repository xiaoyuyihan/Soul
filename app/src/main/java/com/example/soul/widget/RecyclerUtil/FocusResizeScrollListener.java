package com.example.soul.widget.RecyclerUtil;

import android.graphics.ImageFormat;
import android.nfc.Tag;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;

/**
 * Created by lw on 2017/4/12.
 */

public class FocusResizeScrollListener extends RecyclerView.OnScrollListener {

    private final static String TAG = "FocusResizeScrollListener";

    private int mScrollFlag = -1;//滑动标志位：1为上滑，0为下滑
    private int mdy;
    private int mItemHeight;
    private int mMaxItemHeight;
    private boolean init = false;
    private LinearLayoutManager mLayoutManager;
    private int mDisplayedViewLocation = 0;
    private int totalItemCount;
    private int mScrollY = 0;
    private int mViewDiffenerce = 0;
    private final static int FLAG_SLIDE_DOWN = 0;
    private final static int FLAG_SLIDE_ON = 1;

    public FocusResizeScrollListener(LinearLayoutManager layoutManager, int ItemHeight) {
        mLayoutManager = layoutManager;
        mItemHeight = ItemHeight;
        mMaxItemHeight = ItemHeight * 3;
    }

    /**
     * @param recyclerView
     * @param dx
     * @param dy
     */
    @Override
    public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
        super.onScrolled(recyclerView, dx, dy);
        Log.d("TestScrolllistener", "true" + "X----" + dx + "Y---" + dy);
        mScrollFlag = dy > 0 ? FLAG_SLIDE_ON : FLAG_SLIDE_DOWN;
        totalItemCount = mLayoutManager.getItemCount();
        mdy = Math.abs(dy);
        mScrollY += mdy;
        mViewDiffenerce = (mMaxItemHeight - mItemHeight) / 2;

        initFocusResize(recyclerView);
        calculationViewLocation();
        calculateScrolledPosition(totalItemCount, recyclerView);
    }

    //滑动容易错位，父视图会吃掉子视图的滑动，导致top or bottom item view 无法放大
    private void calculationViewLocation() {
        if (mScrollY > mViewDiffenerce) {
            mScrollY = mScrollY % mItemHeight;
            if (mScrollFlag == FLAG_SLIDE_DOWN && mDisplayedViewLocation > 0)
                mDisplayedViewLocation--;
            if (mScrollFlag == FLAG_SLIDE_ON && mDisplayedViewLocation < totalItemCount)
                mDisplayedViewLocation++;
        }
        Log.d(TAG, "mScrollY:++" + mScrollY + "*****mDisplayedViewLocation:--" + mDisplayedViewLocation+"====size:"+((LinearLayoutManager)mLayoutManager).findFirstVisibleItemPosition());
    }

    private void calculateScrolledPosition(int totalItemCount, RecyclerView recyclerView) {
        for (int i = 0; i < totalItemCount - 1; i++) {
            View view = recyclerView.getChildAt(i);
            if (view != null) {
                if (i == mDisplayedViewLocation) {
                    onItemBigResize(view, recyclerView);
                } else {
                    onItemSmallResize(view, recyclerView);
                }
                //requestLayout：当view确定自身已经不再适合现有的区域时，
                // 该view本身调用这个方法要求parent view重新调用他的onMeasure onLayout来对重新设置自己位置。
                view.requestLayout();
            }
        }
    }

    private void onItemSmallResize(View view, RecyclerView recyclerView) {
        if (view.getLayoutParams().height - mdy <= mItemHeight) {
            view.getLayoutParams().height = mItemHeight;
        } else if (view.getLayoutParams().height >= mItemHeight) {
            view.getLayoutParams().height -= (mdy * 3);
        }
    }

    private void onItemBigResize(View view, RecyclerView recyclerView) {
        if (view.getLayoutParams().height + mdy >= mMaxItemHeight) {
            view.getLayoutParams().height = mMaxItemHeight;
        } else {
            view.getLayoutParams().height += (mdy * 3);
        }
    }

    private void initFocusResize(RecyclerView recyclerView) {
        if (!init) {
            init = true;
            View view = recyclerView.getChildAt(0);
            view.getLayoutParams().height = mMaxItemHeight;
        }
    }

    /**
     * @param recyclerView
     * @param newState     scroll state
     *                     SCROLL_STATE_IDLE  静止
     *                     SCROLL_STATE_DRAGGING 外部滑动
     *                     SCROLL_STATE_SETTLING 自动滑动
     */
    @Override
    public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
        super.onScrollStateChanged(recyclerView, newState);
        if (newState == RecyclerView.SCROLL_STATE_IDLE) {
            if (((LinearLayoutManager) mLayoutManager).getOrientation() == LinearLayoutManager.VERTICAL) {
                int positionScrolled = (mScrollFlag == 1) ? calculatePositionScrolledDown(recyclerView) :
                        calculatePositionScrolledUp(recyclerView);
                for (int j = 0; j < mLayoutManager.getItemCount() - 1; j++) {
                    View view = recyclerView.getChildAt(j);
                    if (view != null) {
                        forceScrollItem(recyclerView, view, j, positionScrolled);
                    }
                }
            }
        } else {

        }
    }

    private void forceScrollItem(RecyclerView recyclerView, View view, int j, int positionScrolled) {
    }

    private int calculatePositionScrolledUp(RecyclerView recyclerView) {
        int positionScrolled;
        if (recyclerView.getChildAt(0).getHeight() > recyclerView.getChildAt(1).getHeight()) {
            positionScrolled = mScrollFlag;
            mLayoutManager.scrollToPositionWithOffset(mLayoutManager.findFirstVisibleItemPosition(), 0);
        } else {
            positionScrolled = mScrollFlag + 1;
            mLayoutManager.scrollToPositionWithOffset(
                    mLayoutManager.findFirstCompletelyVisibleItemPosition(), 0);
        }
        return positionScrolled;
    }

    /**
     * mScrolledFalg=1, if item visi 0 return 0
     *
     * @param recyclerView
     * @return
     */
    private int calculatePositionScrolledDown(RecyclerView recyclerView) {
        int positionScrolled;
        //最后一个
        if (mLayoutManager.findFirstCompletelyVisibleItemPosition()
                == mLayoutManager.getItemCount() - 1) {
            positionScrolled = mScrollFlag - 1;
            mLayoutManager.scrollToPositionWithOffset(
                    mLayoutManager.findFirstVisibleItemPosition(), 0);
        } else {
            //第二个item比第一个高
            if (recyclerView.getChildAt(mScrollFlag).getHeight() > recyclerView.getChildAt(
                    mScrollFlag - 1).getHeight()) {
                positionScrolled = mScrollFlag;
                mLayoutManager.scrollToPositionWithOffset(
                        //首个完全可见的View位置
                        mLayoutManager.findFirstCompletelyVisibleItemPosition(), 0);
            } else {
                positionScrolled = mScrollFlag - 1;
                mLayoutManager.scrollToPositionWithOffset(
                        //首个可见位置(即使部份可见)
                        mLayoutManager.findFirstVisibleItemPosition(), 0);
            }
        }
        return positionScrolled;
    }
}
