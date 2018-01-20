package test;

import android.content.Context;
import android.support.annotation.Nullable;
import android.support.v7.widget.RecyclerView;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;

/**
 * Created by lw on 2017/4/15.
 */

public class TestRecyclerView extends RecyclerView {
    private final static String Tag="TestRecyclerView";
    private float lostX;
    public TestRecyclerView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    public boolean onTouchEvent(MotionEvent e) {

        switch (e.getAction()){
            case MotionEvent.ACTION_DOWN:
                lostX=e.getX();
                Log.d(Tag,"ACTION_DOWN");
                break;
            case MotionEvent.ACTION_MOVE:
                Log.d(Tag,"ACTION_MOVE--------"+String.valueOf(e.getX()));
                break;
            case MotionEvent.ACTION_UP:
                Log.d(Tag,"ACTION_UP");
                break;
        }
        return super.onTouchEvent(e);
    }

}
