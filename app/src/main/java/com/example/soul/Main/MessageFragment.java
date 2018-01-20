package com.example.soul.Main;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TabHost;
import android.widget.TabWidget;
import android.widget.TextView;

import com.example.soul.soul.R;

/**
 * Created by lw on 2017/5/9.
 */
public class MessageFragment extends Fragment{
    private static MessageFragment messageFragment;
    public static Fragment getInstanceFragment() {
        if (messageFragment==null){
            messageFragment=new MessageFragment();
        }
        return messageFragment;
    }

    private View view;
    private TabHost mTabHost;
    private TabWidget mTabWidget;
    private TabLayout mTabLayout;
    private ViewPager mViewGroup;

    private String[] mTabStrings;
    private int[] mTabId;
    private MessageFragmentPagerAdapter mPagerAdapter;
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view=inflater.inflate(R.layout.fragment_message,container,false);
        //setTabHost();
        //setTabHostItem();
        initView();
        return view;
    }

    private void initView() {
        mTabLayout=(TabLayout)view.findViewById(R.id.fragment_message_tabLayout);
        mViewGroup=(ViewPager)view.findViewById(R.id.fragment_message_viewpager);
        mTabStrings=getResources().getStringArray(R.array.fragment_message_tab_item);
        /**
         * getFragmentManager()是所在fragment 父容器的碎片管理，
         * getChildFragmentManager()是在fragment  里面子容器的碎片管理。
         */
        mPagerAdapter=new MessageFragmentPagerAdapter(getChildFragmentManager());
        mViewGroup.setAdapter(mPagerAdapter);
        mTabLayout.setupWithViewPager(mViewGroup);

    }

    private void setTabHost() {
        mTabStrings=getResources().getStringArray(R.array.fragment_message_tab_item);
        mTabId=new int[]{R.id.tab1,R.id.tab2,R.id.tab3};
        mTabHost=(TabHost)view.findViewById(R.id.fragment_message_tabHost);
        mTabWidget=(TabWidget)view.findViewById(android.R.id.tabs);
        mTabWidget.setDividerDrawable(null);//设置tabWeight没有竖线分割
        mTabHost.setup();
    }

    private void setTabHostItem() {
       for (int i = 0; i<mTabId.length; i++){
           //创建选项卡
           TabHost.TabSpec tab= mTabHost.newTabSpec(mTabStrings[i])
                   //创建标题卡片，参数一：指定标题卡片的文本内容，参数二：指定标题卡片的背景图片
                   .setIndicator(mTabStrings[i], getResources().getDrawable(R.color.colorAccent))
                   //将setContent（int）参数指定的组件（即面板）和上面的卡片标题进行绑定
                   .setContent(mTabId[i]);
           //将上面创建好的一个选项卡（包括面板和卡片标题）添加到tabHost容器中
           mTabHost.addTab(tab);
       }
        for (int i = 0; i < mTabHost.getTabWidget().getChildCount(); i++)
        {
            //获取标题
            TextView tabs_title = (TextView) mTabHost.getTabWidget().getChildAt(i).findViewById(android.R.id.title);
            //定义字体大小
            tabs_title.setTextColor(getResources().getColor(R.color.white));
            tabs_title.setTextSize(14);
            //定义对齐方式
            tabs_title.setGravity(Gravity.CENTER);
            //定义高宽
            tabs_title.getLayoutParams().height = ViewGroup.LayoutParams.MATCH_PARENT;
            tabs_title.getLayoutParams().width = ViewGroup.LayoutParams.MATCH_PARENT;
        }
    }

    class MessageFragmentPagerAdapter extends FragmentPagerAdapter {
        public MessageFragmentPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            return MessagePagerFragment.newInstance(position,R.mipmap.image_test_viedo);
        }

        @Override
        public int getCount() {
            return mTabStrings.length;
        }

        @Override
        public CharSequence getPageTitle(int position) {
            return mTabStrings[position];
        }
    }
}
