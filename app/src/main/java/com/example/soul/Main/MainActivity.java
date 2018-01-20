package com.example.soul.Main;

import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;

import com.example.soul.soul.R;

/**
 * Created by lw on 2017/5/9.
 */

public class MainActivity extends AppCompatActivity implements View.OnClickListener ,MessageTrunk{
    private FragmentManager fragmentManager;
    private FragmentTransaction transaction;
    private Fragment mDefaultFragment;
    private ImageView mHome;
    private ImageView mFound;
    private ImageView mMessage;
    private ImageView mMyself;
    private ImageView mMental;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().requestFeature(Window.FEATURE_NO_TITLE);
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            Window window = getWindow();
            window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS
                    | WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION);
            window.getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                    | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                    | View.SYSTEM_UI_FLAG_LAYOUT_STABLE);
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
        }
        setContentView(R.layout.activity_main);

        init();
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar_top);
        if (toolbar != null) {
            setSupportActionBar(toolbar);
            getSupportActionBar().setDisplayShowTitleEnabled(false);
        }
        // 设置默认的Fragment
        setDefaultFragment();
    }

    private void init() {
        mHome=(ImageView) findViewById(R.id.image_main_home);
        mHome.setOnClickListener(this);
        mFound=(ImageView)findViewById(R.id.image_main_found);
        mFound.setOnClickListener(this);
        mMental=(ImageView)findViewById(R.id.image_main_mental);
        mMental.setOnClickListener(this);
        mMessage=(ImageView)findViewById(R.id.image_main_message);
        mMessage.setOnClickListener(this);
        mMyself=(ImageView)findViewById(R.id.image_main_myself);
        mMyself.setOnClickListener(this);
    }

    private void setDefaultFragment() {
        fragmentManager = getSupportFragmentManager();
        mDefaultFragment=new HomeFragment();
        transaction = fragmentManager.beginTransaction();//开启一个事务
        transaction.add(R.id.fragment_main, mDefaultFragment);
        transaction.commit();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.image_main_home:
                mDefaultFragment=HomeFragment.getInstanceFragment();
                break;
            case R.id.image_main_found:
                mDefaultFragment=FoundFragment.getInstanceFragment();
                break;
            case R.id.image_main_mental:
                mDefaultFragment=MentalFragment.getInstanceFragment();
                break;
            case R.id.image_main_message:
                mDefaultFragment=MessageFragment.getInstanceFragment();
                break;
            case R.id.image_main_myself:
                mDefaultFragment=MyselfFragment.getInstanceFragment();
                break;
            case R.id.fragment_found_academic:
                mDefaultFragment=AcademicFragment.getInstanceFragment();
                break;
            case R.id.fragment_found_question:
                mDefaultFragment=QuestionFragment.getInstanceFragment();
                break;
            case R.id.fragment_found_knowledge:
                mDefaultFragment=KnowledgeFragment.getInstanceFragment();
                break;
            case R.id.fragment_found_help:
                break;
        }
        transaction = fragmentManager.beginTransaction();
        transaction.replace(R.id.fragment_main, mDefaultFragment);
        transaction.commit();
    }

    @Override
    public void messageForFragment(View v) {
        onClick(v);
    }
}
