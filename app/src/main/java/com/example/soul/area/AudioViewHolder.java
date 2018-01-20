package com.example.soul.area;

import android.view.View;

import com.example.soul.soul.R;
import com.example.soul.widget.RecyclerUtil.BaseViewHolder;
import com.example.widget.ImageSphericView;

/**
 * Created by lw on 2017/4/12.
 * 音频
 */

public class AudioViewHolder extends BaseViewHolder {
    private ImageSphericView mPlayAudio;
    public AudioViewHolder(View itemView) {
        super(itemView);
        mPlayAudio=(ImageSphericView)itemView.findViewById(R.id.view_recycler_item_audio_play);
    }

    public ImageSphericView getPlayAudio() {
        return mPlayAudio;
    }
}
