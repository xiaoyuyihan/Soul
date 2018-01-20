package com.example.soul.area;

import android.annotation.TargetApi;
import android.app.Activity;
import android.app.KeyguardManager;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.media.AudioManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.SurfaceView;
import android.view.View;

import com.example.base.mvp.BaseMVPView;
import com.example.base.mvp.MVPModel;
import com.example.base.mvp.MVPPresenter;
import com.example.base.mvp.MVPView;
import com.example.soul.soul.R;

import org.videolan.libvlc.IVLCVout;
import org.videolan.libvlc.Media;
import org.videolan.libvlc.MediaPlayer;
import org.videolan.vlc.PlaybackService;
import org.videolan.vlc.media.MediaWrapper;
import org.videolan.vlc.util.Util;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by lw on 2017/2/21.
 */

public class AreaPresenter<M extends MVPModel> extends MVPPresenter implements
        IVLCVout.Callback, GestureDetector.OnDoubleTapListener, PlaybackService.Client.Callback,
        PlaybackService.Callback {

    private static final int OVERLAY_TIMEOUT = 4000;
    private static final int OVERLAY_INFINITE = -1;
    private static final int FADE_OUT = 1;
    private static final int SHOW_PROGRESS = 2;
    private static final int FADE_OUT_INFO = 3;
    private static final int START_PLAYBACK = 4;
    private static final int AUDIO_SERVICE_CONNECTION_FAILED = 5;
    private static final int RESET_BACK_LOCK = 6;
    private static final int CHECK_VIDEO_TRACKS = 7;
    private static final int LOADING_ANIMATION = 8;
    private static final int HW_ERROR = 1000; // TODO REMOVE
    private static final String TAG = AreaPresenter.class.getCanonicalName();

    private static final int SURFACE_BEST_FIT = 0;
    private static final int SURFACE_FIT_HORIZONTAL = 1;
    private static final int SURFACE_FIT_VERTICAL = 2;
    private static final int SURFACE_FILL = 3;
    private static final int SURFACE_16_9 = 4;
    private static final int SURFACE_4_3 = 5;
    private static final int SURFACE_ORIGINAL = 6;
    private int mCurrentSize = SURFACE_BEST_FIT;

    /* for getTime and seek */
    private long mForcedTime = -1;
    private long mLastTime = -1;
    private boolean mPlaybackStarted = false;

    /* -1 is a valid track (Disable) */
    private int mLastAudioTrack = -2;
    private int mLastSpuTrack = -2;

    private static final int LOADING_ANIMATION_DELAY = 1000;
    // size of the video
    private int mVideoHeight;
    private int mVideoWidth;
    private int mVideoVisibleHeight;
    private int mVideoVisibleWidth;
    private int mSarNum;
    private int mSarDen;

    private MVPView mView;
    private M mModel;
    private List<AreaBean> mData = new ArrayList<>();
    private static AreaPresenter mAreaPresenter;
    private static Context mApplication = null;
    private PlaybackService playbackService = null;
    private AudioManager mAudioManager;
    private int mAudioMax;
    private boolean mWasPaused=false;

    public AreaPresenter(Context mContext) {
        mApplication = mContext;
        mModel = (M) M.getInstance();
        setData();
    }

    private void setData() {
        int i = 10;
        for (; i > 0; i--) {
            AreaBean mBean = new AreaBean();
            mBean.setmType(i % 3);
            mData.add(mBean);
        }
    }

    public static AreaPresenter getInstance(Context mContext) {
        if (mAreaPresenter == null)
            mAreaPresenter = new AreaPresenter(mContext);
        return mAreaPresenter;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        PlaybackService.mAppContext=mApplication.getApplicationContext();
        boolean isService = Util.isServiceRunning(mApplication, "org.videolan.vlc.PlaybackService");
    }

    @Override
    public void attachView(BaseMVPView view) {
        mView = (MVPView) view;
    }

    /**
     * if true detach view
     *
     * @param retainInstance
     */
    @Override
    public void detachView(boolean retainInstance) {
        if (retainInstance) {
            mView = null;
        }
    }

    public List<AreaBean> getData() {
        return mData;
    }

    public PlaybackService getPlaybackService() {
        return playbackService;
    }

    /**
     * @param from
     * @param to
     */
    public void swapListPosition(int from, int to) {
        AreaBean moveItem = mData.get(from);
        mData.remove(from);
        mData.add(to, moveItem);//交换数据链表中数据的位置
    }

    public void removeData(int position) {
        mData.remove(position);
    }

    public void setDataLike(int dataLike) {
        mData.get(dataLike).setmLike(1);
    }


    public void doPlayPause(View view) {
        if (!playbackService.isPausable())
            return;

        if (playbackService.isPlaying()) {
            pause();

        } else {
            play();

        }
        view.requestFocus();
    }


    @TargetApi(Build.VERSION_CODES.JELLY_BEAN_MR1)
    public void startPlayback(SurfaceView mSurfaceView) {
        /* start playback only when audio service and both surfaces are ready */
        if (mPlaybackStarted || playbackService == null)
            return;

        mPlaybackStarted = true;


        final IVLCVout vlcVout = playbackService.getVLCVout();
        if (vlcVout.areViewsAttached())
            playbackService.stopPlayback();
        vlcVout.detachViews();
        vlcVout.setVideoView(mSurfaceView);
        vlcVout.addCallback(this);
        vlcVout.attachViews();
        playbackService.setVideoTrackEnabled(true);

        loadMedia();
        playbackService.setRate(1.0F, false);

    }
    private void loadMedia() {
        if (playbackService == null)
            return;
        Uri mUri = null;
        boolean fromStart = false;
        int positionInPlaylist = -1;
        long savedTime =  0L; // position passed in by intent (ms)

        /*
         * If the activity has been paused by pressing the power button, then
         * pressing it again will show the lock screen.
         * But onResume will also be called, even if vlc-android is still in
         * the background.
         * To workaround this, pause playback if the lockscreen is displayed.
         */
        final KeyguardManager km = (KeyguardManager) mApplication.getSystemService(Context.KEYGUARD_SERVICE);

        if (km.inKeyguardRestrictedInputMode())
            mWasPaused = true;
        if (mWasPaused)
            Log.d(TAG, "Video was previously paused, resuming in paused mode");
        if (positionInPlaylist != -1 && playbackService.hasMedia()) {
            // Provided externally from AudioService
            Log.d(TAG, "Continuing playback from PlaybackService at index " + positionInPlaylist);
            MediaWrapper openedMedia = playbackService.getMedias().get(positionInPlaylist);
            if (openedMedia == null) {
                //encounteredError();
                return;
            }
            mUri = openedMedia.getUri();
            //updateSeekable(playbackService.isSeekable());
            //updatePausable(playbackService.isPausable());
        }

        if (mUri != null) {
            if (playbackService.hasMedia() && !mUri.equals(playbackService.getCurrentMediaWrapper().getUri()))
                playbackService.stop();
            // restore last position
            MediaWrapper media = null;
            if (media == null && TextUtils.equals(mUri.getScheme(), "file") &&
                    mUri.getPath() != null && mUri.getPath().startsWith("/sdcard")) {

            }
            if (media != null) {
                // in media library
                if (fromStart || playbackService.isPlaying())
                    media.setTime(0L);
                else if (savedTime <= 0L)
                    savedTime = media.getTime();
                mLastAudioTrack = media.getAudioTrack();
                mLastSpuTrack = media.getSpuTrack();
            } else {
                // not in media library

                if (savedTime > 0L ) {

                    return;
                }
            }

            // Start playback & seek
            playbackService.addCallback(this);
            /* prepare playback */
            boolean hasMedia = playbackService.hasMedia();
            if (hasMedia)
                media = playbackService.getCurrentMediaWrapper();
            else if (media == null)
                media = new MediaWrapper(mUri);
            if (mWasPaused)
                media.addFlags(MediaWrapper.MEDIA_PAUSED);
            //if (mHardwareAccelerationError || intent.hasExtra(PLAY_DISABLE_HARDWARE))
              //  media.addFlags(MediaWrapper.MEDIA_NO_HWACCEL);
            media.removeFlags(MediaWrapper.MEDIA_FORCE_AUDIO);
            media.addFlags(MediaWrapper.MEDIA_VIDEO);

            if (savedTime <= 0L && media != null && media.getTime() > 0L)
                savedTime = media.getTime();
            if (savedTime > 0L && !playbackService.isPlaying())
                playbackService.saveTimeToSeek(savedTime);

            // Handle playback
            if (!hasMedia)
                playbackService.load(media);
            else if (!playbackService.isPlaying())
                playbackService.playIndex(positionInPlaylist);
            else {
                onPlaying();
            }

        } else {
            playbackService.addCallback(this);
            //playbackService.loadLastPlaylist(PlaybackService.TYPE_VIDEO);
            MediaWrapper mw = playbackService.getCurrentMediaWrapper();
            if (mw == null) {
                //finish();
                return;
            }
            mUri = playbackService.getCurrentMediaWrapper().getUri();
        }
    }

    private void onPlaying() {
        if (mLastAudioTrack >= -1) {
            playbackService.setAudioTrack(mLastAudioTrack);
            mLastAudioTrack = -2;
        }
        if (mLastSpuTrack >= -1) {
            playbackService.setSpuTrack(mLastSpuTrack);
            mLastSpuTrack = -2;
        }
    }

    public void stopPlayback(){
        playbackService.setVideoTrackEnabled(false);
        playbackService.removeCallback(this);
        final IVLCVout vlcVout = playbackService.getVLCVout();
        vlcVout.removeCallback(this);
        vlcVout.detachViews();
    }


    private void pause() {
        playbackService.pause();
        if (mView != null && mView.getContentView() != null)
            mView.getContentView().setKeepScreenOn(false);
    }

    /**
     *
     */
    private void play() {
        playbackService.play();
        if (mView != null && mView.getContentView() != null)
            mView.getContentView().setKeepScreenOn(true);
    }

    private long getTime() {
        long time = playbackService.getTime();
        if (mForcedTime != -1 && mLastTime != -1) {
            /* XXX: After a seek, playbackService.getTime can return the position before or after
             * the seek position. Therefore we return mForcedTime in order to avoid the seekBar
             * to move between seek position and the actual position.
             * We have to wait for a valid position (that is after the seek position).
             * to re-init mLastTime and mForcedTime to -1 and return the actual position.
             */
            if (mLastTime > mForcedTime) {
                if (time <= mLastTime && time > mForcedTime || time > mLastTime)
                    mLastTime = mForcedTime = -1;
            } else {
                if (time > mForcedTime)
                    mLastTime = mForcedTime = -1;
            }
        } else if (time == 0)
            time = (int) playbackService.getCurrentMediaWrapper().getTime();
        return mForcedTime == -1 ? time : mForcedTime;
    }

    //********************___IVLCVout.Callback___**********************//
    @Override
    public void onNewLayout(IVLCVout vlcVout, int width, int height, int visibleWidth, int visibleHeight, int sarNum, int sarDen) {
        if (width * height == 0)
            return;

        // store video size
        mVideoWidth = width;
        mVideoHeight = height;
        mVideoVisibleWidth = visibleWidth;
        mVideoVisibleHeight = visibleHeight;
        mSarNum = sarNum;
        mSarDen = sarDen;
        changeSurfaceLayout();
    }

    @Override
    public void onSurfacesCreated(IVLCVout vlcVout) {

    }

    @Override
    public void onSurfacesDestroyed(IVLCVout vlcVout) {

    }

    @Override
    public void onHardwareAccelerationError(IVLCVout vlcVout) {

    }

    @TargetApi(Build.VERSION_CODES.JELLY_BEAN_MR1)
    private void changeSurfaceLayout() {
        int sw;
        int sh;

        // get screen size
        sw = mView.getActivity().getWindow().getDecorView().getWidth();
        sh = mView.getActivity().getWindow().getDecorView().getHeight();

        if (playbackService != null) {
            final IVLCVout vlcVout = playbackService.getVLCVout();
            vlcVout.setWindowSize(sw, sh);
        }

        double dw = sw, dh = sh;
        boolean isPortrait;

        // getWindow().getDecorView() doesn't always take orientation into account, we have to correct the values
        isPortrait = mView.getActivity().getResources().getConfiguration().orientation == Configuration.ORIENTATION_PORTRAIT;

        if (sw > sh && isPortrait || sw < sh && !isPortrait) {
            dw = sh;
            dh = sw;
        }

        // sanity check
        if (dw * dh == 0 || mVideoWidth * mVideoHeight == 0) {
            Log.e(TAG, "Invalid surface size");
            return;
        }

        // compute the aspect ratio
        double ar, vw;
        if (mSarDen == mSarNum) {
            /* No indication about the density, assuming 1:1 */
            vw = mVideoVisibleWidth;
            ar = (double) mVideoVisibleWidth / (double) mVideoVisibleHeight;
        } else {
            /* Use the specified aspect ratio */
            vw = mVideoVisibleWidth * (double) mSarNum / mSarDen;
            ar = vw / mVideoVisibleHeight;
        }

        // compute the display aspect ratio
        double dar = dw / dh;

        switch (mCurrentSize) {
            case SURFACE_BEST_FIT:
                if (dar < ar)
                    dh = dw / ar;
                else
                    dw = dh * ar;
                break;
            case SURFACE_FIT_HORIZONTAL:
                dh = dw / ar;
                break;
            case SURFACE_FIT_VERTICAL:
                dw = dh * ar;
                break;
            case SURFACE_FILL:
                break;
            case SURFACE_16_9:
                ar = 16.0 / 9.0;
                if (dar < ar)
                    dh = dw / ar;
                else
                    dw = dh * ar;
                break;
            case SURFACE_4_3:
                ar = 4.0 / 3.0;
                if (dar < ar)
                    dh = dw / ar;
                else
                    dw = dh * ar;
                break;
            case SURFACE_ORIGINAL:
                dh = mVideoVisibleHeight;
                dw = vw;
                break;
        }
    }
    //********************GestureDetector.OnDoubleTapListener__double touch*********//


    @Override
    public boolean onSingleTapConfirmed(MotionEvent motionEvent) {
        return false;
    }

    @Override
    public boolean onDoubleTap(MotionEvent motionEvent) {
        return false;
    }

    @Override
    public boolean onDoubleTapEvent(MotionEvent motionEvent) {
        return false;
    }

    //************PlaybackService.Client.Callback*********//

    @Override
    public void onConnected(PlaybackService service) {
        playbackService = service;
    }

    @Override
    public void onDisconnected() {
        playbackService = null;
    }

    //***********PlaybackService.Callback*********//

    @Override
    public void update() {

    }

    @Override
    public void updateProgress() {

    }

    @Override
    public void onMediaEvent(Media.Event event) {

    }

    @Override
    public void onMediaPlayerEvent(MediaPlayer.Event event) {

    }
}
