package com.example.soul.widget.RecyclerUtil;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.support.v4.view.ViewCompat;
import android.support.v7.widget.RecyclerView;
import android.view.View;

/**
 * Created by lw on 2017/2/22.
 * 可以看到类中只有6个方法，并且有3个方法google已经不提倡使用了。其实我们看源码可知，不提倡使用的方法
 也就是比其他方法少了一个参数State state， State记录了RecyclerView中的一些基本信息，这里不深究。
 */

public class TestItemDecoration extends RecyclerView.ItemDecoration {
    /**
     * 水平
     */
    public final static int HORIZONTAL=0;
    /**
     *垂直
     */
    public final static int VERTICAL=1;
    /**
     * 矩形
     */
    public final static  int FRAME=2;
    private Paint mPaint;
    private Drawable mDivider;
    private Rect mBounds=new Rect();
    private int mDefaultDividerHeight = 0;//分割线高度，默认为1px
    private int mDividerTop=0;
    private int mDividerBottom=0;
    private int mDividerLeft=0;
    private int mDividerRight=0;
    private int mOrientation;//列表的方向：LinearLayoutManager.VERTICAL或LinearLayoutManager.HORIZONTAL
    private static final int[] ATTRS = new int[]{android.R.attr.listDivider};
    public TestItemDecoration(Context context, int orientation){
        setItemParameter(context,orientation,2,null);
    }
    public TestItemDecoration(Context context, int orientation,int mDividerHeight){
        setItemParameter(context,orientation,mDividerHeight,null);
    }
    public TestItemDecoration(Context context, int orientation,int mDividerHeight,Drawable mDivider){
        setItemParameter(context,orientation,mDividerHeight,mDivider);
    }
    private void setItemParameter(Context context,int orientation,int mDividerHeight,Drawable mDivider){
        if (mDivider==null){
            final TypedArray a = context.obtainStyledAttributes(ATTRS);
            this.mDivider = a.getDrawable(0);
            a.recycle();
        }else {
            this.mDivider=mDivider;
        }
        this.mDefaultDividerHeight=mDividerHeight;
        setOrientation(orientation);
    }
    /**
     * Sets the orientation for this divider. This should be called if
     * {@link RecyclerView.LayoutManager} changes orientation.
     *
     * @param orientation {@link #HORIZONTAL} or {@link #VERTICAL}
     */
    public void setOrientation(int orientation) {
        if (orientation != HORIZONTAL && orientation != VERTICAL&&orientation!=FRAME) {
            throw new IllegalArgumentException(
                    "Invalid orientation. It should be either HORIZONTAL or VERTICAL");
        }
        mOrientation = orientation;
    }


    @Override
    public void onDraw(Canvas c, RecyclerView parent, RecyclerView.State state) {
        if (mOrientation==TestItemDecoration.HORIZONTAL){
            drawHorizontal(c,parent);
        }else if(mOrientation==TestItemDecoration.VERTICAL){
            drawVertical(c,parent);
        }else if (mOrientation==TestItemDecoration.FRAME){
            drawRectangle(c,parent);
        }
        super.onDraw(c, parent, state);
    }

    /**
     * 矩形边框
     * @param c
     * @param parent
     */
    private void drawRectangle(Canvas c, RecyclerView parent) {

    }

    /**
     * 水平
     * @param c
     * @param parent
     */
    private void drawHorizontal(Canvas c, RecyclerView parent) {
        c.save();

        if (parent.getClipToPadding()){

        }else{

        }
    }

    /**
     * 垂直
     * @param canvas
     * @param parent
     */
    @SuppressLint("NewApi")
    private void drawVertical(Canvas canvas, RecyclerView parent) {

        canvas.save();
        final int left;
        final int right;
        //cliptopadding 被设置为 ture，也就是 padding 部分是不允许绘制的。
        //clipChild 用于定义子控件是否在父控件边界内进行绘制。clipChild 默认为 true。也就是不允许进行扩展绘制。
        if (parent.getClipToPadding()) {
            left = parent.getPaddingLeft();
            right = parent.getWidth() - parent.getPaddingRight();
            canvas.clipRect(left, parent.getPaddingTop(), right,
                    parent.getHeight() - parent.getPaddingBottom());
        } else {
            left = 0;
            right = parent.getWidth();
        }

        final int childCount = parent.getChildCount();
        for (int i = 0; i < childCount; i++) {
            final View child = parent.getChildAt(i);
            parent.getDecoratedBoundsWithMargins(child, mBounds);
            final int bottom = mBounds.bottom + Math.round(ViewCompat.getTranslationY(child));
            final int top = bottom - mDivider.getIntrinsicHeight();
            mDivider.setBounds(left, top, right, bottom);
            mDivider.draw(canvas);
        }
        canvas.restore();
    }


    @Override
    public void onDrawOver(Canvas c, RecyclerView parent, RecyclerView.State state) {
        super.onDrawOver(c, parent, state);
    }

    /**
     *
     * @param outRect
     * @param view
     * @param parent
     * @param state
     */
    @Override
    public void getItemOffsets(Rect outRect, View view, RecyclerView parent, RecyclerView.State state) {
        if (mOrientation==TestItemDecoration.HORIZONTAL){
            outRect.set(0,0,0,mDefaultDividerHeight);
        }else if (mOrientation==TestItemDecoration.VERTICAL){
            outRect.set(0,0,mDefaultDividerHeight,0);
        }else if (mOrientation==TestItemDecoration.FRAME){
            outRect.set(mDividerLeft,mDividerTop,mDividerRight,mDividerBottom);
        }
        super.getItemOffsets(outRect, view, parent, state);
    }
}
