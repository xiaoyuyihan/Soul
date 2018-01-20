package com.example.soul.area;

import android.view.SurfaceView;
import android.view.View;
import android.widget.ImageView;

import com.example.soul.soul.R;
import com.example.soul.widget.RecyclerUtil.BaseViewHolder;

/**
 * Created by lw on 2017/4/12.
 */

public class VideoViewHolder extends BaseViewHolder {
    private SurfaceView mPlaySurface;
    private ImageView mPlayVideo;
    public VideoViewHolder(View itemView) {
        super(itemView);
        mPlaySurface=(SurfaceView)itemView.findViewById(R.id.view_recycler_item_video);
        mPlayVideo=(ImageView)itemView.findViewById(R.id.view_recycler_item_play);

    }

    public SurfaceView getPlaySurface() {
        return mPlaySurface;
    }

    public ImageView getPlayVideo() {
        return mPlayVideo;
    }
}
