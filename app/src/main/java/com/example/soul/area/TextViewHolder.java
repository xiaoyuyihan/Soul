package com.example.soul.area;

import android.view.View;
import android.widget.TextView;

import com.example.soul.soul.R;
import com.example.soul.widget.RecyclerUtil.BaseViewHolder;

/**
 * Created by lw on 2017/4/12.
 */

public class TextViewHolder extends BaseViewHolder {
    private TextView mContentText;
    public TextViewHolder(View itemView) {
        super(itemView);
        mContentText=(TextView)itemView.findViewById(R.id.view_recycler_item_contentText);
    }

    public TextView getContentText() {
        return mContentText;
    }
}
