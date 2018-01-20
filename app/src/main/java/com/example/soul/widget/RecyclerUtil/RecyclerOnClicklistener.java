package com.example.soul.widget.RecyclerUtil;

import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;

/**
 * Created by lw on 2017/2/21.
 * 子View监听
 */

public class RecyclerOnClicklistener implements RecyclerView.OnItemTouchListener {
    private int mLastDownX,mLastDownY;
    //该值记录了最小滑动距离
    private int touchSlop=10 ;
    private OnItemClickListener mListener;
    //是否是单击事件
    private boolean isSingleTapUp = false;
    //是否是长按事件
    private boolean isLongPressUp = false;
    private boolean isMove = false;
    private long mDownTime;


    @Override
    public boolean onInterceptTouchEvent(RecyclerView rv, MotionEvent e) {
        int x = (int) e.getRawX();
        int y = (int) e.getRawY();
        switch (e.getAction()){

            case MotionEvent.ACTION_DOWN:
                mLastDownX = x;
                mLastDownY = y;
                onReset();
                break;

            case MotionEvent.ACTION_MOVE:
                if((x - mLastDownX)>touchSlop){
                    isMove=true;
                }
                break;

            case MotionEvent.ACTION_UP:
                if(isMove){
                    break;
                }
                if(System.currentTimeMillis()-mDownTime > 1000){
                    isLongPressUp = true;
                }else {
                    isSingleTapUp = true;
                }
                break;
        }
        if(isSingleTapUp ){
            //根据触摸坐标来获取childView
            View childView = rv.findChildViewUnder(e.getX(),e.getY());
            isSingleTapUp = false;
            if(childView != null&&mListener!=null){
                //回调mListener#onItemClick方法
               mListener.onItemClick(childView,rv.getChildLayoutPosition(childView));
                return false;
            }
            return false;
        }
        if (isLongPressUp ){
            View childView = rv.findChildViewUnder(e.getX(),e.getY());
            isLongPressUp = false;
            if(childView != null&&mListener!=null){
                mListener.onItemLongClick(childView, rv.getChildLayoutPosition(childView));
            }
            return false;
        }
        return false;
    }

    @Override
    public void onTouchEvent(RecyclerView rv, MotionEvent e) {

    }

    private void onReset() {
        mDownTime = System.currentTimeMillis();
        isMove=false;
        isLongPressUp=false;
        isSingleTapUp=false;
    }

    @Override
    public void onRequestDisallowInterceptTouchEvent(boolean disallowIntercept) {

    }
    public void setOnItemClick(OnItemClickListener mItemOnclick){
        this.mListener=mItemOnclick;
    }
    //内部接口，定义点击方法以及长按方法
    public interface OnItemClickListener {
        void onItemClick(View view, int position);

        void onItemLongClick(View view, int position);
    }
}
