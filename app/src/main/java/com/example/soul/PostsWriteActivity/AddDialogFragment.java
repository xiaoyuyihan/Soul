package com.example.soul.PostsWriteActivity;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.soul.soul.R;

/**
 * Created by lw on 2017/5/18.
 */

/***
 * 添加Dialog 添加类型
 */
public class AddDialogFragment extends Fragment {
    /**
     * @param key 根据参数转换按钮的图标和名称——分享or求助
     * @return
     */
    public static AddDialogFragment newInstance(int key) {

        Bundle args = new Bundle();
        args.putInt(NewTypeFragmentDialog.NEW_TYPE_KEY, key);
        AddDialogFragment fragment = new AddDialogFragment();
        fragment.setArguments(args);
        return fragment;
    }

    private int mType;
    private String[] mTypeName;
    private TypedArray mTypeDrawable;
    private GridView mWriteTypeGrid;
    private int windowHeight;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mType = getArguments().getInt(NewTypeFragmentDialog.NEW_TYPE_KEY);
        //windowHeight=getActivity().getWindow().getWindowManager().getDefaultDisplay().getHeight();
        if (mType == NewTypeFragmentDialog.DIALOG_TYPE_WRITING) {
            mTypeName = getResources().getStringArray(R.array.fragment_dialog_add_writing_item_string);
            mTypeDrawable = getActivity().getResources().obtainTypedArray(R.array.fragment_dialog_add_writing_item_drawable);
        }else {
            mTypeName = getResources().getStringArray(R.array.fragment_dialog_add_help_item_string);
            mTypeDrawable = getResources().obtainTypedArray(R.array.fragment_dialog_add_help_item_drawable);
        }
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup
            container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_dialog_add_content, container, false);
        mWriteTypeGrid = (GridView) view.findViewById(R.id.view_grid);
        mWriteTypeGrid.setAdapter(new AddDialogAdapter(getActivity()));
        mWriteTypeGrid.setOverScrollMode(View.OVER_SCROLL_NEVER);
        return view;
    }

    public void onClickItem(int position) {
        Fragment mParentFragment = getParentFragment();
        if (mParentFragment instanceof AddDialogTypeOnclick) {
            ((AddDialogTypeOnclick) mParentFragment).OnClickSkip(position);
        }
    }

    class AddDialogAdapter extends BaseAdapter {

        private Context context;

        private AddDialogAdapter(Context context){
            this.context=context;
        }

        @Override
        public int getCount() {
            return mTypeName.length;
        }

        @Override
        public String getItem(int position) {
            return mTypeName[position];
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(final int position, View convertView, ViewGroup parent) {
            TextView mText = new TextView(getActivity());
            mText.setLayoutParams(
                    new GridView.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                            ViewGroup.LayoutParams.MATCH_PARENT));
            if (position < mTypeDrawable.length() && position < mTypeName.length) {
                Drawable drawable = context.getResources().getDrawable(mTypeDrawable.getResourceId(position,0));
                drawable.setBounds(0, 0, (int)(drawable.getMinimumWidth()*1.5f), (int)(drawable.getMinimumHeight()*1.5f));
                mText.setCompoundDrawables(null, drawable, null, null);
                mText.setCompoundDrawablePadding(6);
                mText.setGravity(Gravity.CENTER);
                mText.setTextSize(14);
                mText.setText(mTypeName[position]);
                mText.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        onClickItem(position);
                    }
                });
            }
            return mText;
        }

    }

    interface AddDialogTypeOnclick {
        void OnClickSkip(int type);
    }
}
