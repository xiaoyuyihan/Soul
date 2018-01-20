package com.example.widget;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.widget.EditText;

import com.example.soul.soul.R;

/**
 * Created by lw on 2017/4/1.
 */

public class StreakEditText extends EditText {
    private Paint mStreakPaint;
    private Rect mRect;
    private float mPadding;
    public StreakEditText(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context,attrs);
    }
    private void init(Context context,AttributeSet attributeSet){
        TypedArray a = context.obtainStyledAttributes(attributeSet, R.styleable.StreakEditText);
        mStreakPaint=new Paint();
        mRect=new Rect();
        mStreakPaint.setStrokeWidth(a.getDimension(R.styleable.StreakEditText_streakWidth,1));
        mStreakPaint.setColor(a.getColor(R.styleable.StreakEditText_streakColor,context.getResources().getColor(R.color.color_black)));
        mPadding=a.getDimension(R.styleable.StreakEditText_streakPaddingHeight,1);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        //获取text的行数这个行数是基于当前输入的文本的内容的行数
        int lineCount = getLineCount();
        //获取EditText组件的高度
        int height = getHeight();
        //获取EditText组件一行的高度
        int lineHeight = getLineHeight();
        int m = 1 + height / lineHeight;
        if (lineCount < m)
            lineCount = m;
        //返回指定的的baseline这里的baseline指的是0-getLineHeight()-1;
        Rect mRect=new Rect();
        //返回指定的的baseline这里的baseline指的是0-getLineHeight()-1;
        //getLineBounds返回的值是baseline的y坐标
        int n = getLineBounds(0, mRect);
        canvas.drawLine(0, n+8, getRight(), n+8, mStreakPaint);
        for (int i = 0;; i++) {
            if (i >= lineCount) {
                setPadding(10 , 0, 0, 0);
                super.onDraw(canvas);
                canvas.restore();
                return;
            }
            //绘制每一行的分割线
            n += lineHeight;
            canvas.drawLine(0.0F, n+8, getRight(), n+8, mStreakPaint);
            canvas.save();
        }
    }
}
