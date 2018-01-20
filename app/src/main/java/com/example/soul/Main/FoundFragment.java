package com.example.soul.Main;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.GestureDetector;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.Animation;
import android.view.animation.TranslateAnimation;
import android.widget.TextView;
import android.widget.ViewFlipper;

import com.example.soul.soul.R;

/**
 * Created by lw on 2017/5/9.
 */

public class FoundFragment extends Fragment implements View.OnClickListener {
    private static FoundFragment foundFragment;

    private static final float FLING_MIN_DISTANCE = 100;//最小滑动像素
    private static final float FLING_MIN_VELOCITY = 150;//最小滑动速度

    private ViewFlipper foundCarousel;
    private TextView foundKnowledge;//知识
    private TextView foundQuestion;//疑问
    private TextView foundAcademic;//学术
    private TextView foundHelp;
    private RecyclerView foundContent;
    private GestureDetector mGestureDetector;
    private TextView mTobView;

    private Handler mMessageHanlder=new Handler();

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_found, container, false);
        init(v);
        setCarouselParameter();
        return v;
    }

    private void init(View v) {
        mTobView=(TextView)v.findViewById(R.id.view_top_textview);
        mTobView.setText("发现");
        foundCarousel = (ViewFlipper) v.findViewById(R.id.fragment_found_carousel);
        foundCarousel.startFlipping();
        foundKnowledge = (TextView) v.findViewById(R.id.fragment_found_knowledge);
        foundCarousel.setOnClickListener(this);
        foundQuestion = (TextView) v.findViewById(R.id.fragment_found_question);
        foundQuestion.setOnClickListener(this);
        foundAcademic = (TextView) v.findViewById(R.id.fragment_found_academic);
        foundAcademic.setOnClickListener(this);
        foundHelp = (TextView) v.findViewById(R.id.fragment_found_help);
        foundHelp.setOnClickListener(this);
        foundContent = (RecyclerView) v.findViewById(R.id.fragment_found_recycler_content);
    }

    public static Fragment getInstanceFragment() {
        if (foundFragment == null) {
            foundFragment = new FoundFragment();
        }
        return foundFragment;
    }

    @Override
    public void onClick(View v) {
        if (getActivity() instanceof MessageTrunk) {
            ((MainActivity) getActivity()).messageForFragment(v);
        }
    }


    private void setCarouselParameter() {
        mGestureDetector = new GestureDetector(getActivity(), new GestureDetector.OnGestureListener() {
            @Override
            public boolean onDown(MotionEvent e) {
                foundCarousel.stopFlipping();
                return true;
            }

            @Override
            public void onShowPress(MotionEvent e) {

            }

            @Override
            public boolean onSingleTapUp(MotionEvent e) {
                Log.d(getClass().toString(),"onSingleTapUp------");
                // 启动 新activity
                foundCarousel.showNext();
                foundCarousel.startFlipping();
                return true;
            }

            @Override
            public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX, float distanceY) {
                Log.d(getClass().toString(),"onScroll------"+distanceX);
                return false;
            }

            @Override
            public void onLongPress(MotionEvent e) {
               mMessageHanlder.postDelayed(new Runnable() {
                   @Override
                   public void run() {
                       foundCarousel.showNext();
                       foundCarousel.startFlipping();
                   }
               },4000);
            }
            /**
             * 用户按下触摸屏、快速移动后松开即触发这个事件
             * e1：第1个ACTION_DOWN MotionEvent
             * e2：最后一个ACTION_MOVE MotionEvent
             * velocityX：X轴上的移动速度，像素/秒
             * velocityY：Y轴上的移动速度，像素/秒
             * 触发条件 ：X轴的坐标位移大于FLING_MIN_DISTANCE，且移动速度大于FLING_MIN_VELOCITY个像素/秒
             */
            @Override
            public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {
                Log.d(getClass().toString(),"onFling");
                // Fling left
                if (e1.getX() - e2.getX() > FLING_MIN_DISTANCE
                        && Math.abs(velocityX) > FLING_MIN_VELOCITY) {
                    foundCarousel.showNext();
                } else if (e2.getX() - e1.getX() > FLING_MIN_DISTANCE
                        && Math.abs(velocityX) > FLING_MIN_VELOCITY) {
                    // Fling right
                    foundCarousel.showPrevious();
                }
                return false;
            }
        });
        foundCarousel.setInAnimation(inFromRightAnimation());
        foundCarousel.setOutAnimation(outToLeftAnimation());
        foundCarousel.setFlipInterval(2000);//设置自动切换的间隔时间
        foundCarousel.startFlipping();//开启切换效果
        foundCarousel.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {

                return mGestureDetector.onTouchEvent(event);
            }
        });
    }

    /**
     * 定义从右侧进入的动画效果
     *
     * @return
     */
    protected Animation inFromRightAnimation() {
        //x type ,from 点，
        Animation inFromRight = new TranslateAnimation(
                Animation.RELATIVE_TO_PARENT, +1.0f,
                Animation.RELATIVE_TO_PARENT, 0.0f,
                Animation.RELATIVE_TO_PARENT, 0.0f,
                Animation.RELATIVE_TO_PARENT, 0.0f);
        inFromRight.setDuration(200);
        inFromRight.setInterpolator(new AccelerateInterpolator());
        return inFromRight;
    }

    /**
     * 定义从左侧退出的动画效果
     *
     * @return
     */
    protected Animation outToLeftAnimation() {
        Animation outtoLeft = new TranslateAnimation(
                Animation.RELATIVE_TO_PARENT, 0.0f,
                Animation.RELATIVE_TO_PARENT, -1.0f,
                Animation.RELATIVE_TO_PARENT, 0.0f,
                Animation.RELATIVE_TO_PARENT, 0.0f);
        outtoLeft.setDuration(200);
        outtoLeft.setInterpolator(new AccelerateInterpolator());
        return outtoLeft;
    }

    /**
     * 定义从左侧进入的动画效果
     *
     * @return
     */
    protected Animation inFromLeftAnimation() {
        Animation inFromLeft = new TranslateAnimation(
                Animation.RELATIVE_TO_PARENT, -1.0f,
                Animation.RELATIVE_TO_PARENT, 0.0f,
                Animation.RELATIVE_TO_PARENT, 0.0f,
                Animation.RELATIVE_TO_PARENT, 0.0f);
        inFromLeft.setDuration(200);
        inFromLeft.setInterpolator(new AccelerateInterpolator());
        return inFromLeft;
    }

    /**
     * 定义从右侧退出时的动画效果
     *
     * @return
     */
    protected Animation outToRightAnimation() {
        Animation outtoRight = new TranslateAnimation(
                Animation.RELATIVE_TO_PARENT, 0.0f,
                Animation.RELATIVE_TO_PARENT, +1.0f,
                Animation.RELATIVE_TO_PARENT, 0.0f,
                Animation.RELATIVE_TO_PARENT, 0.0f);
        outtoRight.setDuration(200);
        outtoRight.setInterpolator(new AccelerateInterpolator());
        return outtoRight;
    }
}