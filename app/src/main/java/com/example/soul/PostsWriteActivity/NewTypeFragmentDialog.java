package com.example.soul.PostsWriteActivity;

import android.hardware.display.DisplayManager;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.hardware.display.DisplayManagerCompat;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.TextView;

import com.example.soul.soul.R;

/**
 * Created by lw on 2017/5/18.
 */

public class NewTypeFragmentDialog extends DialogFragment implements View.OnClickListener,AddDialogFragment.AddDialogTypeOnclick{
    //dialog main
    public static final int DIALOG_WRITING_NOTES=0;             //笔记
    public static final int DIALOG_WRITING_EXPERIMENT=1;        //实验
    public static final int DIALOG_WRITING_HOTSPOT=2;           //话题
    public static final int DIALOG_WRITING_REPORT=3;            //报告
    public static final int DIALOG_TYPE_WRITING = 4;            //分享
    public static final int DIALOG_TYPE_QUESTION = 5;           //求助
    //dialog type
    public static final int DIALOG_TYPE=4;
    public static final String NEW_TYPE_KEY="NewTypeFragmentDialog";


    private int mDiglogType;
    private TextView mBackView;
    private TextView mSureView;

    public static NewTypeFragmentDialog newInstance(int type) {

        Bundle args = new Bundle();
        args.putInt(NEW_TYPE_KEY,type);
        NewTypeFragmentDialog fragment = new NewTypeFragmentDialog();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mDiglogType=getArguments().getInt(NEW_TYPE_KEY);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        getDialog().requestWindowFeature(Window.FEATURE_NO_TITLE);
        Log.d("dug","NewTypeFragmentDialog");
        View view=inflater.inflate(R.layout.fragment_dialog_add,container,false);
        mBackView=(TextView) view.findViewById(R.id.fragment_dialog_add_bottom_back);
        mBackView.setOnClickListener(this);
        mSureView=(TextView) view.findViewById(R.id.fragment_dialog_add_sure);
        mSureView.setOnClickListener(this);
        return view;
    }

    @Override
    public void onStart() {
        super.onStart();
        //大标题（模块）
        Fragment fragment=AddDialogFragment.newInstance(mDiglogType);
        setChildFragment(fragment);
    }

    @Override
    public void onResume() {
        super.onResume();
        Display display=getActivity().getWindow().getWindowManager().getDefaultDisplay();
        getDialog().getWindow().setLayout((display.getWidth()),((int)(display.getHeight()*0.4)));
    }

    @Override
    public void onClick(View v) {
        FragmentManager manager=getChildFragmentManager();
        Fragment currentFragment=manager.findFragmentById(R.id.fragment_dialog_add_content);
        switch (v.getId()){
            case R.id.fragment_dialog_add_bottom_back:
                //退出编辑
                if (currentFragment instanceof AddWriteDialogFragment){
                    //大标题（模块）
                    Fragment fragment=AddDialogFragment.newInstance(mDiglogType);
                    setChildFragment(fragment);
                }else{
                    if(getActivity() instanceof DialogBottomOnclick){
                        ((DialogBottomOnclick)getActivity()).onDismissButton();
                    }
                }
                break;
            case R.id.fragment_dialog_add_sure:
                if (currentFragment instanceof AddWriteDialogFragment){
                    //大标题（模块）
                    if(getActivity() instanceof DialogBottomOnclick){
                        ((DialogBottomOnclick)getActivity()).onCommitButton(((AddWriteDialogFragment)currentFragment).getTypeNames());
                    }
                }else{
                    if(getActivity() instanceof DialogBottomOnclick){

                    }
                }
                break;
        }
    }

    @Override
    public void OnClickSkip(int type) {
        //加载类型下的各栏目，给用户选择她期待将文章放置的标签
        Fragment fragment=null;
        switch (type){
            //笔记 or 实验 or 档案
            case DIALOG_WRITING_NOTES:
            case DIALOG_WRITING_EXPERIMENT:
            case DIALOG_WRITING_REPORT:
                fragment=AddWriteDialogFragment.newInstance(AddWriteDialogFragment.SUBJECT_BRANCHES);
                break;
            //话题
            case DIALOG_WRITING_HOTSPOT:
                fragment=AddWriteDialogFragment.newInstance(AddWriteDialogFragment.SUBJECT_LIFE);
                break;
        }
        setChildFragment(fragment);
    }

    public void setChildFragment(Fragment childFragment) {
        if (childFragment == null){
            System.out.println(this.getClass().toString()+"--childFragment is null");
            return;
        }
        FragmentManager manager=getChildFragmentManager();
        FragmentTransaction transaction=manager.beginTransaction();
        transaction.replace(R.id.fragment_dialog_add_content,childFragment);
        transaction.commit();
    }
    interface DialogBottomOnclick{
        void onDismissButton();
        void onCommitButton(String[] typeNames);
    }
}