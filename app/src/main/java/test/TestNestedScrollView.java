package test;

import android.content.Context;
import android.content.Intent;
import android.support.v4.widget.NestedScrollView;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;

import com.example.soul.widget.RecyclerUtil.FocusResizeScrollListener;

/**
 * Created by lw on 2017/4/15.
 */

public class TestNestedScrollView extends NestedScrollView {
    private final static String Tag="TestNestedScrollView";
    private RecyclerView.OnScrollListener l=null;
    public TestNestedScrollView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }
    public TestNestedScrollView(Context context,AttributeSet attributeSet,int defStyleAttr){
        super(context,attributeSet,defStyleAttr);
    }
    private int lastY;
    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
        return super.dispatchTouchEvent(ev);
    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {
        Log.d(Tag, "onInterceptTouchEvent");

        NestedScrollView.OnScrollChangeListener listener=new OnScrollChangeListener() {
            @Override
            public void onScrollChange(NestedScrollView v, int scrollX, int scrollY, int oldScrollX, int oldScrollY) {
                View chaildView =v.getChildAt(0);
                if(chaildView instanceof RecyclerView){
                    if (l!=null){
                        Log.d(Tag, "RecyclerView---"+"X---"+scrollX+"Y---"+scrollY+"oldX---"+oldScrollX+"oldY---"+oldScrollY);
                        l.onScrolled((RecyclerView)chaildView,scrollX,scrollY-oldScrollY);
                    }
            }
        }};
        setOnScrollChangeListener(listener);
        return super.onInterceptTouchEvent(ev);
    }

    public void setOnScrollListener(RecyclerView.OnScrollListener listener) {
        this.l=listener;
    }

    @Override
    public boolean onTouchEvent(MotionEvent ev) {

        switch (ev.getAction()) {
            case MotionEvent.ACTION_DOWN:
                Log.d(Tag, "ACTION_DOWN");
                break;
            case MotionEvent.ACTION_MOVE:
                Log.d(Tag, "ACTION_MOVE");
                break;
            case MotionEvent.ACTION_UP:
                Log.d(Tag, "ACTION_UP");
                break;
        }
        return super.onTouchEvent(ev);
    }

}
