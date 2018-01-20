package com.example.soul.widget.RecyclerUtil;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.example.soul.soul.R;

/**
 * Created by lw on 2017/2/23.
 */

public class BaseViewHolder extends RecyclerView.ViewHolder {
    private ImageView mDelete;
    private ImageView mLike;
    private RelativeLayout mContent;
    private ImageView mBackground;
    private TextView mTime;
    private TextView mReview;
    private TextView mTobView;
    public BaseViewHolder(View itemView) {
        super(itemView);
        mDelete=(ImageView)itemView.findViewById(R.id.view_recycler_item_delete);
        mLike=(ImageView)itemView.findViewById(R.id.view_recycler_item_like);
        mContent=(RelativeLayout)itemView.findViewById(R.id.view_recycler_item_content);
        mBackground=(ImageView) itemView.findViewById(R.id.view_recycler_item_background_image);
        mTime=(TextView)itemView.findViewById(R.id.view_recycler_item_time);
        mReview=(TextView)itemView.findViewById(R.id.view_recycler_item_review);
        mTobView=(TextView)itemView.findViewById(R.id.view_recycler_item_TopTextView);
    }

    public ImageView getDelete() {
        return mDelete;
    }

    public ImageView getLike() {
        return mLike;
    }

    public RelativeLayout getContent() {
        return mContent;
    }

    public ImageView getBackground() {
        return mBackground;
    }

    public TextView getReview() {
        return mReview;
    }

    public TextView getTime() {
        return mTime;
    }

    public TextView getTobView() {
        return mTobView;
    }
}
