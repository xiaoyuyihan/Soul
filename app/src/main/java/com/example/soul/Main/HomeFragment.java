package com.example.soul.Main;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.soul.PostsWriteActivity.NewTypeFragmentDialog;
import com.example.soul.PostsWriteActivity.PostsWriteActivity;
import com.example.soul.soul.R;
import com.getbase.floatingactionbutton.FloatingActionButton;

/**
 * Created by lw on 2017/5/9.
 */
public class HomeFragment extends Fragment implements View.OnClickListener{
    private static HomeFragment homeFragment;
    public static HomeFragment getInstanceFragment(){
        if (homeFragment==null){
            homeFragment=new HomeFragment();
        }
        return homeFragment;
    }
    private TextView m;
    private RecyclerView mContentRecycyer;
    private FloatingActionButton mHomeWriting;
    private FloatingActionButton mHomeQuestion;
    private FloatingActionButton mHomeHelp;
    private FloatingActionButton mHomeArchives;


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view=inflater.inflate(R.layout.fragment_home,container,false);
        initView(view);
        return view;
    }

    private void initView(View v) {
        m=(TextView)v.findViewById(R.id.fragment_home_top_search);
        mHomeArchives=(FloatingActionButton)v.findViewById(R.id.fragment_home_archives);
        mHomeArchives.setOnClickListener(this);
        mHomeWriting=(FloatingActionButton)v.findViewById(R.id.fragment_home_writing);
        mHomeWriting.setOnClickListener(this);
        mHomeQuestion=(FloatingActionButton)v.findViewById(R.id.fragment_home_question);
        mHomeQuestion.setOnClickListener(this);
        mHomeHelp=(FloatingActionButton)v.findViewById(R.id.fragment_home_help);
        mHomeHelp.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        Intent intent=new Intent();
        switch (v.getId()){
            case R.id.fragment_home_writing:
                //分享 新建各种模块文件
                intent.setClass(getActivity(), PostsWriteActivity.class);
                intent.putExtra(PostsWriteActivity.WRITE_FLAG,NewTypeFragmentDialog.DIALOG_TYPE_WRITING);
                break;
            case R.id.fragment_home_question:
                //求助
                intent.setClass(getActivity(), PostsWriteActivity.class);
                intent.putExtra(PostsWriteActivity.WRITE_FLAG,NewTypeFragmentDialog.DIALOG_TYPE_QUESTION);
                break;
            case R.id.fragment_home_help:
                //回答
                break;

        }
        startActivity(intent);

    }
}
