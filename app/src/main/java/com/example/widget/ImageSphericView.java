package com.example.widget;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.NinePatchDrawable;
import android.text.TextPaint;
import android.util.AttributeSet;
import android.widget.ImageView;

import com.example.soul.soul.R;

/**
 * Created by lw on 2017/3/29.
 * 自定义Image
 * 圆形，文字，padding
 */

public class ImageSphericView extends ImageView {

    private Context mContext;
    private int defaultColor = 0x77FFFFFF;
    // 如果只有其中一个有值，则只画一个圆形边框
    private int mBorderOutsideColor = 0;
    private int mBorderInsideColor = 0;
    // 控件默认长、宽
    private int defaultWidth = 0;
    private int defaultHeight = 0;
    private CharSequence text=null;
    private boolean isRoundFalg=false;
    private float mBorderOutsideSize=1;
    private float mBorderInsideSize=1;
    private Paint mTextPaint;
    private float mTextSize;
    private int mTextColor;

    //构造方法，参数上下文
    public ImageSphericView(Context context) {
        super(context);
        mContext = context;
    }

    public ImageSphericView(Context context, AttributeSet attrs) {
        super(context, attrs);
        mContext = context;
        setCustomAttributes(attrs);
    }

    public ImageSphericView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        mContext = context;
        setCustomAttributes(attrs);
    }

    private void setCustomAttributes(AttributeSet attrs) {
        TypedArray a = mContext.obtainStyledAttributes(attrs, R.styleable.ImageSphericView);
        mBorderOutsideColor = a.getColor(R.styleable.ImageSphericView_borderOutsideColor, defaultColor);
        mBorderOutsideSize = a.getDimension(R.styleable.ImageSphericView_borderOutThickness,0);
        mBorderInsideColor = a.getColor(R.styleable.ImageSphericView_borderInsideColor, defaultColor);
        mBorderInsideSize =a.getDimension(R.styleable.ImageSphericView_borderInsideThickness,0);
        text = a.getText(R.styleable.ImageSphericView_text);
        mTextSize=a.getDimension(R.styleable.ImageSphericView_textSize,12);
        mTextColor=a.getColor(R.styleable.ImageSphericView_textColor,Color.BLACK);
        mTextPaint=new TextPaint();
        mTextPaint.setColor(mTextColor);
        mTextPaint.setTextSize(mTextSize);
        a.recycle();

    }


    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int measuredWidth = measure(widthMeasureSpec);
        int measuredHeight = measure(heightMeasureSpec);

        int d = Math.min(measuredWidth, measuredHeight);

        setMeasuredDimension(d, d);
    }

    private int measure(int measureSpec) {
        int result = 0;
        int specMode = MeasureSpec.getMode(measureSpec);
        int specsize = MeasureSpec.getSize(measureSpec);
        if (specMode == MeasureSpec.UNSPECIFIED) {
            result = 200;
        } else {
            result = specsize;
        }
        return result;
    }

    @Override
    protected void onDraw(Canvas canvas) {
        Drawable drawable = getDrawable();
        if (drawable == null) {
            return;
        }
        if (getWidth() == 0 || getHeight() == 0) {
            return;
        }
        if (drawable.getClass() == NinePatchDrawable.class)
            return;
        Bitmap b =null;
        if (drawable instanceof BitmapDrawable){
            b = ((BitmapDrawable) drawable).getBitmap();
        }else if (drawable instanceof ColorDrawable){
            Bitmap output = Bitmap.createBitmap(getWidth(),getHeight(),
                    Bitmap.Config.ARGB_8888);
            Canvas canvasBitmap=new Canvas(output);
            canvasBitmap.drawColor(((ColorDrawable)drawable).getColor());
            b=output;
        }
        if (b ==null)
            return;
        Bitmap newBitmap=getSquareBitmap(b);

        if (defaultWidth == 0) {
            defaultWidth = getWidth();
        }
        if (defaultHeight == 0) {
            defaultHeight = getHeight();
        }
        int radius = 0;
        if (mBorderInsideColor != defaultColor && mBorderOutsideColor != defaultColor) {// 定义画两个边框，分别为外圆边框和内圆边框
            radius = (defaultWidth < defaultHeight ? defaultWidth : defaultHeight) / 2 -
                    (int)(mBorderInsideSize+mBorderOutsideSize);
            // 画内圆
            drawCircleBorder(canvas, radius, mBorderInsideColor,(int) mBorderInsideSize);
            // 画外圆
            drawCircleBorder(canvas, radius + (int)mBorderInsideSize, mBorderOutsideColor,(int) mBorderOutsideSize);
        } else if (mBorderInsideColor != defaultColor && mBorderOutsideColor == defaultColor) {// 定义画一个边框
            radius = (defaultWidth < defaultHeight ? defaultWidth : defaultHeight) / 2 - (int)mBorderInsideSize;
            drawCircleBorder(canvas, radius, mBorderInsideColor,mBorderInsideColor);
        } else if (mBorderInsideColor == defaultColor && mBorderOutsideColor != defaultColor) {// 定义画一个边框
            radius = (defaultWidth < defaultHeight ? defaultWidth : defaultHeight) / 2 - (int)mBorderOutsideSize;
            drawCircleBorder(canvas, radius, mBorderOutsideColor,(int)mBorderOutsideSize);
        } else {// 没有边框
            radius = (defaultWidth < defaultHeight ? defaultWidth : defaultHeight) / 2;
        }
        Bitmap roundBitmap = getCroppedRoundBitmap(newBitmap, radius);

        final int saveCount = canvas.getSaveCount();
        canvas.save();
       // if (matrix != null)
            //canvas.concat(matrix);
        canvas.drawBitmap(roundBitmap,(mBorderInsideSize+mBorderOutsideSize),(mBorderInsideSize+mBorderOutsideSize),null);
        if (text!=null){
            mTextPaint.setTextAlign(Paint.Align.CENTER);
            float baseLineX=getWidth()/2;
            float baseLineY=getHeight()-(getHeight()-mTextSize)/2;
            canvas.drawText(text.toString(),baseLineX,baseLineY, mTextPaint);
        }
        canvas.restoreToCount(saveCount);
    }

    private Bitmap getSquareBitmap(Bitmap b) {
        Bitmap newBitmap;
        int bmpWidth=b.getWidth();
        int bmpHeight=b.getHeight();
        if (bmpHeight > bmpWidth) {// 高大于宽
            // 截取正方形图片
            newBitmap = Bitmap.createBitmap(b, 0, (bmpHeight - bmpWidth) / 2, bmpWidth, bmpWidth);
        } else if (bmpHeight < bmpWidth) {// 宽大于高
            newBitmap = Bitmap.createBitmap(b, (bmpWidth - bmpHeight) / 2, 0, bmpHeight, bmpHeight);
        } else {
            newBitmap = Bitmap.createBitmap(b, 0, 0, bmpWidth, bmpHeight);
        }
         return setBtimapMatrix(newBitmap);
    }

    private Bitmap setBtimapMatrix(Bitmap newBitmap) {
        Matrix matrix=new Matrix();
        matrix.postScale((float)getWidth()/newBitmap.getWidth(),(float)getHeight()/newBitmap.getHeight());
        return  Bitmap.createBitmap(newBitmap,0,0,newBitmap.getWidth(),newBitmap.getHeight(),matrix,true);
    }

    /**
     * 获取裁剪后的圆形图片
     *
     * @param
     */
//    radius半径
    public Bitmap getCroppedRoundBitmap(Bitmap bmp, int radius) {
        Bitmap scaledSrcBmp;
        int diameter = radius * 2;
        if (bmp.getWidth() != diameter || bmp.getHeight() != diameter) {
            scaledSrcBmp = Bitmap.createScaledBitmap(bmp, diameter, diameter, true);
        } else {
            scaledSrcBmp = bmp;
        }
        Bitmap output = Bitmap.createBitmap(scaledSrcBmp.getWidth(),
                scaledSrcBmp.getHeight(),
                Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(output);

        Paint paint = new Paint();
        Rect rect = new Rect(0, 0, scaledSrcBmp.getWidth(), scaledSrcBmp.getHeight());

        paint.setAntiAlias(true);
        paint.setFilterBitmap(true);
        paint.setDither(true);
        canvas.drawARGB(0, 0, 0, 0);
        canvas.drawCircle(scaledSrcBmp.getWidth() / 2,
                scaledSrcBmp.getHeight() / 2,
                scaledSrcBmp.getWidth() / 2,
                paint);
        paint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.SRC_IN));
        canvas.drawBitmap(scaledSrcBmp, rect, rect, paint);
        bmp = null;
        scaledSrcBmp = null;
        return output;
    }

    /**
     * 边缘画圆
     */
    private void drawCircleBorder(Canvas canvas, int radius, int color,int border) {
        Paint paint = new Paint();
            /* 去锯齿 */
        paint.setAntiAlias(true);
        paint.setFilterBitmap(true);
        paint.setDither(true);
        paint.setColor(color);
            /* 设置paint的    style    为STROKE：空心 */
        paint.setStyle(Paint.Style.STROKE);
            /* 设置paint的外框宽度 */
        paint.setStrokeWidth(border);
        canvas.drawCircle(defaultWidth / 2, defaultHeight / 2, radius, paint);
    }
}
