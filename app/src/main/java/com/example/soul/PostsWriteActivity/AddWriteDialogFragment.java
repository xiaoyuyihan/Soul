package com.example.soul.PostsWriteActivity;

import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.util.ArrayMap;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.soul.soul.R;
import com.example.widget.FlowLayout;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Created by lw on 2017/5/19.
 */

public class AddWriteDialogFragment extends Fragment {
    public static final String WRITE_TYPE="add_write_type";
    public static final int SUBJECT_BRANCHES=0;         //学科
    public static final int SUBJECT_LIFE=1;             //生活
    public static AddWriteDialogFragment newInstance(int type) {

        Bundle args = new Bundle();
        args.putInt(WRITE_TYPE,type);
        AddWriteDialogFragment fragment = new AddWriteDialogFragment();
        fragment.setArguments(args);
        return fragment;
    }

    private int mWriteType;
    private List<String> mBranchesNames;
    private LinearLayout mTopLinear;
    private FlowLayout mContentLayout;
    private Drawable mBackground;
    private RelativeLayout.LayoutParams mLayoutParams;
    private View.OnClickListener mAllFlowClick;
    private ArrayMap<String,Integer> mAddWriteType;
    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mWriteType=getArguments().getInt(WRITE_TYPE);
        mAddWriteType=new ArrayMap<>();
        mLayoutParams=new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT,
                ViewGroup.LayoutParams.WRAP_CONTENT);
        mLayoutParams.setMargins(2,2,2,2);
        mBackground=getResources().getDrawable(R.drawable.shape_background_gray_round_textview);
        /**
         * 常常使用Arrays.asLisvt()后调用add，remove这些method时出现UnsupportedOperationException异常。
         * 这是由于：
         * Arrays.asLisvt() 返回java.util.Arrays$ArrayList,而不是ArrayList。
         * Arrays$ArrayList和ArrayList都是继承AbstractList，remove，add等 method在AbstractList中是默认throw UnsupportedOperationException而且不作任何操作。ArrayList override这些method来对list进行操作，但是Arrays$ArrayList没有override remove(int)，add(int)等，所以throw UnsupportedOperationException。
         */
        if (mWriteType==AddWriteDialogFragment.SUBJECT_BRANCHES){
            mBranchesNames= new ArrayList<>(Arrays.asList(getResources().getStringArray(R.array.subject_branches_psychology_name)));
        }else {
            mBranchesNames=new ArrayList<>(Arrays.asList(getResources().getStringArray(R.array.life_hotspot)));
        }
        mAllFlowClick=new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mAddWriteType.size()>3){
                    Toast.makeText(getActivity(),"标签只能选择4个",Toast.LENGTH_SHORT).show();
                    return;
                }
                if (v instanceof TextView){
                    String vName=((TextView) v).getText().toString();
                    int size=(Integer) v.getTag();
                    //内容
                    if ( v.getParent() instanceof FlowLayout){
                        mAddWriteType.put(vName,size);//top view
                        mBranchesNames.remove(size);
                    }//选中
                    else if(v.getParent() instanceof LinearLayout){
                        Integer site = mAddWriteType.remove(vName);
                        mBranchesNames.add(site,vName);
                    }
                    //全面跟新view 可以转换为跟新两个，删除，和添加
                    setContentChildView();
                    setTopChildView();
                    v.invalidate();
                }
            }
        };
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view =inflater.inflate(R.layout.fragment_dialog_write_add_content,container,false);
        mTopLinear=(LinearLayout) view.findViewById(R.id.dialog_write_add_content_tag_designate);
        mContentLayout=(FlowLayout)view.findViewById(R.id.dialog_write_add_content_tag_all);
        setContentChildView();
        setTopChildView();
        return view;
    }

    private void setTopChildView() {
        mTopLinear.removeAllViews();
        for(Map.Entry<String,Integer> entry:mAddWriteType.entrySet()){
            createTextView(mTopLinear,entry.getKey(),entry.getValue());
        }
    }

    private void setContentChildView() {
        mContentLayout.removeAllViews();
        for(String name: mBranchesNames){
            createTextView(mContentLayout,name,mBranchesNames.indexOf(name));
        }
    }

    private void createTextView(ViewGroup fatherView,String name,int size) {
        TextView mTextView=new TextView(getActivity());
        mTextView.setTag(size);
        mTextView.setLayoutParams(mLayoutParams);
        mTextView.setTextSize(10);
        mTextView.setPadding(6,6,6,6);
        mTextView.setBackground(mBackground);
        mTextView.setText(name);
        mTextView.setOnClickListener(mAllFlowClick);
        fatherView.addView(mTextView);
    }
    public String[] getTypeNames(){
        Set<String> mKeys=mAddWriteType.keySet();
        String[] name=new String[5];
        int i=0;
        for (String key:mKeys){
            name[i++]=key;
        }
        return name;
    }
}
