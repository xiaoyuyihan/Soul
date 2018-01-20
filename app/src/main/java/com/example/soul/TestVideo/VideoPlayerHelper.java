package com.example.soul.TestVideo;

import android.annotation.TargetApi;
import android.app.Activity;
import android.app.KeyguardManager;
import android.bluetooth.BluetoothA2dp;
import android.bluetooth.BluetoothHeadset;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.media.AudioManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.support.annotation.MainThread;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.util.Log;
import android.view.SurfaceView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.example.soul.PlayVideoActivity.VideoPlayerViewReply;
import com.example.soul.soul.R;

import org.videolan.libvlc.IVLCVout;
import org.videolan.libvlc.Media;
import org.videolan.libvlc.MediaPlayer;
import org.videolan.libvlc.util.AndroidUtil;
import org.videolan.vlc.PlaybackService;
import org.videolan.vlc.media.MediaWrapper;
import org.videolan.vlc.util.Strings;
import org.videolan.vlc.util.Util;

import java.net.URL;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by lw on 2017/3/20.
 */

public class VideoPlayerHelper implements IVLCVout.Callback,PlaybackService.Callback{

    private static final String TAG =VideoPlayerHelper.class.getCanonicalName();

    private boolean mHasSubItems = false;
    private boolean mHardwareAccelerationError=false;

    private static final int FADE_OUT = 1;
    private static final int SHOW_PROGRESS = 2;
    private static final int FADE_OUT_INFO = 3;
    private static final int START_PLAYBACK = 4;
    private static final int AUDIO_SERVICE_CONNECTION_FAILED = 5;
    private static final int RESET_BACK_LOCK = 6;
    private static final int CHECK_VIDEO_TRACKS = 7;
    private static final int LOADING_ANIMATION = 8;
    private static final int HW_ERROR = 1000; // TODO REMOVE
    private static final int LOADING_ANIMATION_DELAY = 0;

    public final static String PLAY_EXTRA_ITEM_LOCATION = "item_location";
    public final static String PLAY_EXTRA_SUBTITLES_LOCATION = "subtitles_location";
    public final static String PLAY_EXTRA_ITEM_TITLE = "title";
    public final static String PLAY_EXTRA_FROM_START = "from_start";
    public final static String PLAY_EXTRA_START_TIME = "position";
    public final static String PLAY_EXTRA_OPENED_POSITION = "opened_position";
    public final static String PLAY_DISABLE_HARDWARE = "disable_hardware";

    // size of the video
    private int mVideoHeight;
    private int mVideoWidth;
    private int mVideoVisibleHeight;
    private int mVideoVisibleWidth;
    private int mSarNum;
    private int mSarDen;

    //video 适配
    private static final int SURFACE_BEST_FIT = 0;
    private static final int SURFACE_FIT_HORIZONTAL = 1;
    private static final int SURFACE_FIT_VERTICAL = 2;
    private static final int SURFACE_FILL = 3;
    private static final int SURFACE_16_9 = 4;
    private static final int SURFACE_4_3 = 5;
    private static final int SURFACE_ORIGINAL = 6;
    private int mCurrentSize = SURFACE_BEST_FIT;

    private boolean mIsPlaying = false;
    private boolean mIsLoading=true;
    //touch status    //Touch Events
    private static final int TOUCH_NONE = 0;
    private static final int TOUCH_VOLUME = 1;
    private static final int TOUCH_BRIGHTNESS = 2;
    private static final int TOUCH_SEEK = 3;

    private int mTouchAction = TOUCH_NONE;

    private long OVERLAY_TIMEOUT=1000;

    //拖动 seek
    private boolean mDragging;

    //time video play
    private long mForcedTime = -1;
    private long mLastTime = -1;

    //音频管理器
    //Volume
    private AudioManager mAudioManager;
    private int mAudioMax;
    private boolean mMute = false;//is 静音
    private int mVolSave;
    private float mVol;

    /**
     * Flag to indicate whether the media should be paused once loaded
     * (e.g. lock screen, or to restore the pause state)
     */
    private boolean mPlaybackStarted = false;
    private boolean mLockBackButton;
    private boolean mWasPaused=false;//is 锁屏

    private boolean mSwitchingView=false;

    private static VideoPlayerHelper mVideoPlayerHelper;
    private SurfaceView mSurfaceView;
    private SurfaceView mSubtitlesSurfaceView = null;//截图
    private Activity mActivity;
    private PlaybackService mService;
    private VideoPlayerViewReply mVideoViewReply;

    private View.OnLayoutChangeListener mOnLayoutChangeListener;

    private Helper mServiceHelper;
    private PlaybackService.Client.Callback mPlaybackCallback=new PlaybackService.Client.Callback() {
        @Override
        public void onConnected(PlaybackService service) {
            mService=service;
            mHandler.sendEmptyMessage(START_PLAYBACK);
        }

        @Override
        public void onDisconnected() {
            mService=null;
        }
    };
    private int mMenuIdx = -1;

    public void setSurfaceView(SurfaceView mSurfaceView) {
        this.mSurfaceView = mSurfaceView;
    }

    public void setActivity(Activity mActivity) {
        this.mActivity = mActivity;
    }

    /**
     *
     */
    public VideoPlayerHelper(Activity activity, @Nullable SurfaceView surfaceView, VideoPlayerViewReply mViewReply){
        mActivity=activity;
        mSurfaceView=surfaceView;
        mVideoViewReply=mViewReply;
    }

    public static VideoPlayerHelper getInstance(Activity activity,SurfaceView surfaceView,VideoPlayerViewReply mVideoViewReply){
        if (mVideoPlayerHelper==null){
            mVideoPlayerHelper=new VideoPlayerHelper(activity,surfaceView,mVideoViewReply);
        }else {
            //判断参数，赋值
            mVideoPlayerHelper.setActivity(activity);
            mVideoPlayerHelper.setSurfaceView(surfaceView);
        }
        PlaybackService.mAppContext = activity;
        mVideoPlayerHelper.init();
        return mVideoPlayerHelper;
    }
    public void onStart(){mServiceHelper.onStart();}
    public void onStop(){
        if (mService != null)
            mService.removeCallback(this);
        mServiceHelper.onStop();
    }

    public void onBackPressed(){ mHandler.sendEmptyMessageDelayed(RESET_BACK_LOCK, 2000);}

    //bind Service
    private void init(){
        mServiceHelper=new Helper(mActivity,mPlaybackCallback);
        initParameter();
    }
    public void sendEmptyMessageDelayed(){
        mHandler.sendEmptyMessageDelayed(LOADING_ANIMATION, LOADING_ANIMATION_DELAY);
    }

    private void initParameter() {
        //音频管理器
        mAudioManager = (AudioManager) mActivity.getSystemService(Context.AUDIO_SERVICE);
        mAudioMax = mAudioManager.getStreamMaxVolume(AudioManager.STREAM_MUSIC);//音乐音量

        /*//监听蓝牙事件
        IntentFilter filter = new IntentFilter(PLAY_FROM_SERVICE);
        filter.addAction(EXIT_PLAYER);
        LocalBroadcastManager.getInstance(this).registerReceiver(
                mServiceReceiver, filter);
        if (mBtReceiver != null) {
            IntentFilter btFilter = new IntentFilter(BluetoothA2dp.ACTION_CONNECTION_STATE_CHANGED);
            btFilter.addAction(BluetoothHeadset.ACTION_CONNECTION_STATE_CHANGED);
            registerReceiver(mBtReceiver, btFilter);
        }*/
    }


    /**
     * Handle resize of the surface and the overlay
     */

    //service helper bindService
    public static class Helper {
        private ArrayList<PlaybackService.Client.Callback> mFragmentCallbacks = new ArrayList<PlaybackService.Client.Callback>();
        final private PlaybackService.Client.Callback mActivityCallback;
        private PlaybackService.Client mClient;
        protected PlaybackService mService;

        public Helper(Context context, PlaybackService.Client.Callback activityCallback) {
            mClient = new PlaybackService.Client(context, mClientCallback);
            mActivityCallback = activityCallback;
        }

        @MainThread
        public void registerFragment(PlaybackService.Client.Callback connectCb) {
            if (connectCb == null)
                throw new IllegalArgumentException("connectCb can't be null");
            mFragmentCallbacks.add(connectCb);
            if (mService != null)
                connectCb.onConnected(mService);

        }

        @MainThread
        public void unregisterFragment(PlaybackService.Client.Callback connectCb) {
            if (mService != null)
                connectCb.onDisconnected();
            mFragmentCallbacks.remove(connectCb);
        }

        @MainThread
        public void onStart() {
            mClient.connect();
        }

        @MainThread
        public void onStop() {
            mClientCallback.onDisconnected();
            mClient.disconnect();
        }

        private final PlaybackService.Client.Callback mClientCallback = new PlaybackService.Client.Callback() {
            @Override
            public void onConnected(PlaybackService service) {
                mService = service;
                mActivityCallback.onConnected(service);
                for (PlaybackService.Client.Callback connectCb : mFragmentCallbacks)
                    connectCb.onConnected(mService);
            }

            @Override
            public void onDisconnected() {
                mService = null;
                mActivityCallback.onDisconnected();
                for (PlaybackService.Client.Callback connectCb : mFragmentCallbacks)
                    connectCb.onDisconnected();
            }
        };
    }

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

    @TargetApi(Build.VERSION_CODES.JELLY_BEAN_MR1)
    public void changeSurfaceLayout() {
        int sw;
        int sh;

        // get screen size
        sw = mActivity.getWindow().getDecorView().getWidth();
        sh = mActivity.getWindow().getDecorView().getHeight();
        if (mService != null) {
            final IVLCVout vlcVout = mService.getVLCVout();
            vlcVout.setWindowSize(sw, sh);
        }

        double dw = sw, dh = sh;
        boolean isPortrait;

        // getWindow().getDecorView() doesn't always take orientation into account, we have to correct the values
        isPortrait = mActivity.getResources().getConfiguration().orientation == Configuration.ORIENTATION_PORTRAIT;


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

        SurfaceView surface;
        surface = mSurfaceView;

        // set display size
        ViewGroup.LayoutParams lp = surface.getLayoutParams();
        lp.width = (int) Math.ceil(dw * mVideoWidth / mVideoVisibleWidth);
        lp.height = (int) Math.ceil(dh * mVideoHeight / mVideoVisibleHeight);
        surface.setLayoutParams(lp);

        surface.invalidate();
    }

    @Override
    public void onSurfacesCreated(IVLCVout vlcVout) {

    }

    @Override
    public void onSurfacesDestroyed(IVLCVout vlcVout) {

    }

    @Override
    public void onHardwareAccelerationError(IVLCVout vlcVout) {
        mHandler.sendEmptyMessage(HW_ERROR);
    }

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
        switch (event.type) {
            case MediaPlayer.Event.Opening:
                mHasSubItems = false;
                break;
            case MediaPlayer.Event.Playing:
                onPlaying();
                break;
            case MediaPlayer.Event.Paused:
                if (mVideoViewReply!=null)
                mVideoViewReply.updateOverlayPausePlay();
                break;
            case MediaPlayer.Event.Stopped:
                exitOK();
                break;
            case MediaPlayer.Event.EndReached:
                /* Don't end the activity if the media has subitems since the next child will be
                 * loaded by the PlaybackService */
                if (!mHasSubItems)
                    endReached();
                break;
            case MediaPlayer.Event.EncounteredError:
                if (mVideoViewReply!=null)
                mVideoViewReply.encounteredError();
                break;
            case MediaPlayer.Event.TimeChanged:
                break;
            case MediaPlayer.Event.Vout:
                if (mVideoViewReply!=null)
                mVideoViewReply.updateNavStatus();
                if (mMenuIdx == -1)
                    handleVout(event.getVoutCount());
                break;
            case MediaPlayer.Event.ESAdded:
            case MediaPlayer.Event.ESDeleted:
                if (mMenuIdx == -1 && event.getEsChangedType() == Media.Track.Type.Video) {
                    mHandler.removeMessages(CHECK_VIDEO_TRACKS);
                    mHandler.sendEmptyMessageDelayed(CHECK_VIDEO_TRACKS, 1000);
                }
                break;
            case MediaPlayer.Event.SeekableChanged:
                if (mVideoViewReply!=null)
                    mVideoViewReply.updateSeekable(event.getSeekable());
                break;
            case MediaPlayer.Event.PausableChanged:
                if (mVideoViewReply!=null)
                mVideoViewReply.updatePausable(event.getPausable());
                break;
            case MediaPlayer.Event.Buffering:
                if (!mIsPlaying)
                    break;
                if (event.getBuffering() == 100f){
                    stopLoading();
                    }
                else if (!mHandler.hasMessages(LOADING_ANIMATION) && !mIsLoading
                        && mTouchAction != TOUCH_SEEK && !mDragging)
                    mHandler.sendEmptyMessageDelayed(LOADING_ANIMATION, LOADING_ANIMATION_DELAY);
                break;
        }
    }
    public PlaybackService getService(){
        return mService;
    }
    private void mute(boolean mute) {
        mMute = mute;
        if (mMute)
            mVolSave = mService.getVolume();
        mService.setVolume(mMute ? 0 : mVolSave);
    }
    public void stopPlayback(){
        if (!mPlaybackStarted)
            return;

        mWasPaused = !mService.isPlaying();

        if (mMute)
            mute(false);

        mPlaybackStarted = false;

        mService.setVideoTrackEnabled(false);
        mService.removeCallback(this);

        mHandler.removeCallbacksAndMessages(null);
        final IVLCVout vlcVout = mService.getVLCVout();
        vlcVout.removeCallback(this);
        vlcVout.detachViews();
        if (mVideoViewReply!=null)
            mVideoViewReply.cleanUI();

        long time = getTime();
        long length = mService.getLength();
        //remove saved position if in the last 5 seconds
        if (length - time < 5000)
            time = 0;
        else
            time -= 2000; // go back 2 seconds, to compensate loading time
        if (mActivity.isFinishing())
            mService.stop();
        else
            mService.pause();
        //save video state
    }

    /**
     * Stop the video loading animation.
     */
    private void stopLoading() {
        mHandler.removeMessages(LOADING_ANIMATION);
        if (!mIsLoading)
            return;
        mIsLoading = false;
        //diss load
    }

    private void onPlaying() {
        mIsPlaying = true;
        //停止加载
        stopLoading();
        //更新播放按钮
        if (mVideoViewReply!=null)
            mVideoViewReply.updateOverlayPausePlay();
        //updateNavStatus();
        if (!mService.getCurrentMediaWrapper().hasFlag(MediaWrapper.MEDIA_PAUSED))
            mHandler.sendEmptyMessageDelayed(FADE_OUT, OVERLAY_TIMEOUT);
        //setESTracks();
    }
    private void exitOK() {
        exit(mActivity.RESULT_OK);
    }
    //结束
    private void endReached() {
        if (mService == null)
            return;
        //播放模式
        if (mService.getRepeatType() == PlaybackService.REPEAT_ONE) {
            seek(0);
            return;
        }
        if (mService.expand() == 0) {
            mHandler.removeMessages(LOADING_ANIMATION);
            mHandler.sendEmptyMessageDelayed(LOADING_ANIMATION, LOADING_ANIMATION_DELAY);
            Log.d(TAG, "Found a video playlist, expanding it");
            mHandler.post(new Runnable() {
                @Override
                public void run() {
                    loadMedia();
                }
            });
        }
    }
    private void seek(long position) {
        seek(position, mService.getLength());
    }

    private void seek(long position, long length) {
        mForcedTime = position;
        mLastTime = mService.getTime();
        mService.seek(position, length);
    }
    /**
     * send time
     * 退出时，保存给上个activity
     * onActivityResult 重写上个
     * @param resultCode
     */
    private void exit(int resultCode) {
        //is finish
        if (mActivity.isFinishing())
            return;
        //mActivity.finish();
    }
    //is vout exist
    private void handleVout(int voutCount) {
        final IVLCVout vlcVout = mService.getVLCVout();
        if (vlcVout.areViewsAttached() && voutCount == 0) {
            /* Video track lost, open in audio mode */
            Log.i(TAG, "Video track lost, switching to audio");
            //切换试图
            mSwitchingView = true;
            //exit(RESULT_VIDEO_TRACK_LOST);
        }
    }

    /**
     * Handle resize of the surface and the overlay
     */

    private final Handler mHandler = new Handler(Looper.getMainLooper(), new Handler.Callback() {
        @Override
        public boolean handleMessage(Message msg) {
            if (mService == null)
                return true;

            switch (msg.what) {
                case FADE_OUT:
                    //hideOverlay(false);
                    break;
                case SHOW_PROGRESS:
                    int pos = mVideoViewReply.setOverlayProgress();
                    if (canShowProgress()) {
                        msg = mHandler.obtainMessage(SHOW_PROGRESS);
                        mHandler.sendMessageDelayed(msg, 1000 - (pos % 1000));
                    }
                    break;
                case FADE_OUT_INFO:
                    break;
                case START_PLAYBACK:
                    startPlayback();
                    break;
                case AUDIO_SERVICE_CONNECTION_FAILED:
                    //exit(RESULT_CONNECTION_FAILED);
                    break;
                case RESET_BACK_LOCK:
                    mLockBackButton = true;
                    break;
                case CHECK_VIDEO_TRACKS:
                    if (mService.getVideoTracksCount() < 1 && mService.getAudioTracksCount() > 0) {
                        Log.i(TAG, "No video track, open in audio mode");
                        //switchToAudioMode(true);
                    }
                    break;
                case LOADING_ANIMATION:
                    if (mVideoViewReply!=null)
                        mVideoViewReply.startLoading();
                    break;
                case HW_ERROR:
                    handleHardwareAccelerationError();
                    break;
            }
            return true;
        }
    });

    private void handleHardwareAccelerationError() {
        mHardwareAccelerationError = true;
        if (mSwitchingView)
            return;
        Toast.makeText(mActivity, R.string.hardware_acceleration_error, Toast.LENGTH_LONG).show();
        final boolean wasPaused = !mService.isPlaying();
        final long oldTime = mService.getTime();
        int position = mService.getCurrentMediaPosition();
        List<MediaWrapper> list = new ArrayList<>(mService.getMedias());
        final MediaWrapper mw = list.get(position);
        mService.stop();
        if (!mActivity.isFinishing()) {
            if (wasPaused)
                mw.addFlags(MediaWrapper.MEDIA_PAUSED);
            mw.addFlags(MediaWrapper.MEDIA_NO_HWACCEL);
            mw.addFlags(MediaWrapper.MEDIA_VIDEO);
            mService.load(list, position);
            if (oldTime > 0)
                seek(oldTime);
        }
    }

    private long getTime() {
        long time = mService.getTime();
        if (mForcedTime != -1 && mLastTime != -1) {
            /* XXX: After a seek, mService.getTime can return the position before or after
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
            time = (int) mService.getCurrentMediaWrapper().getTime();
        return mForcedTime == -1 ? time : mForcedTime;
    }

    private boolean canShowProgress() {
        return !mDragging && mService != null && mService.isPlaying();
    }

    @TargetApi(Build.VERSION_CODES.JELLY_BEAN_MR1)
    private void startPlayback() {
        /* start playback only when audio service and both surfaces are ready */
        if (mPlaybackStarted || mService == null)
            return;

        mPlaybackStarted = true;

        final IVLCVout vlcVout = mService.getVLCVout();
        if (vlcVout.areViewsAttached())
            mService.stopPlayback();
        vlcVout.detachViews();
        vlcVout.setVideoView(mSurfaceView);
        if (mSubtitlesSurfaceView!=null&&mSubtitlesSurfaceView.getVisibility() != View.GONE)
            vlcVout.setSubtitlesView(mSubtitlesSurfaceView);
        vlcVout.addCallback(this);
        vlcVout.attachViews();
        mService.setVideoTrackEnabled(true);

        initUI();

        loadMedia();

        mService.setRate(1.0F, false);

        if (mBtReceiver != null && (mAudioManager.isBluetoothA2dpOn() || mAudioManager.isBluetoothScoOn()))
            toggleBtDelay(true);
    }

    /**
     * External extras:
     * - position (long) - position of the video to start with (in ms)
     * - subtitles_location (String) - location of a subtitles file to load
     * - from_start (boolean) - Whether playback should start from start or from resume point
     * - title (String) - video title, will be guessed from file if not set.
     */
    @TargetApi(12)
    @SuppressWarnings({"unchecked"})
    private void loadMedia() {
        if (mService == null)
            return;
        android.net.Uri mUri = null;
        mIsPlaying = false;
        boolean fromStart = false;
        int positionInPlaylist = -1;
        Intent intent = mActivity.getIntent();
        Bundle extras = intent.getExtras();
        //播放记录
        long savedTime = 0L; // position passed in by intent (ms)

        /*
         * If the activity has been paused by pressing the power button, then
         * pressing it again will show the lock screen.
         * But onResume will also be called, even if vlc-android is still in
         * the background.
         * To workaround this, pause playback if the lockscreen is displayed.
         */
        final KeyguardManager km = (KeyguardManager) mActivity.getSystemService(Context.KEYGUARD_SERVICE);
        //判断锁屏状态
        if (km.inKeyguardRestrictedInputMode())
            mWasPaused = true;
        if (mWasPaused)
            Log.d(TAG, "Video was previously paused, resuming in paused mode");

        if (intent.getData() != null)
            mUri = intent.getData();
        if (extras != null) {
            if (intent.hasExtra(PLAY_EXTRA_ITEM_LOCATION))
                mUri = extras.getParcelable(PLAY_EXTRA_ITEM_LOCATION);
            //fromStart = extras.getBoolean(PLAY_EXTRA_FROM_START, true);
            //mAskResume &= !fromStart;
            positionInPlaylist = extras.getInt(PLAY_EXTRA_OPENED_POSITION, -1);
        }

        if (positionInPlaylist != -1 && mService.hasMedia()) {
            // Provided externally from AudioService
            Log.d(TAG, "Continuing playback from PlaybackService at index " + positionInPlaylist);
            MediaWrapper openedMedia = mService.getMedias().get(positionInPlaylist);
            if (openedMedia == null) {
                //encounteredError();
                return;
            }
            mUri = openedMedia.getUri();
           // updateSeekable(mService.isSeekable());
            //updatePausable(mService.isPausable());
        }

        if (mUri != null) {
            if (mService.hasMedia() && !mUri.equals(mService.getCurrentMediaWrapper().getUri()))
                mService.stop();
            // restore last position
            MediaWrapper media = null;
            // Start playback & seek
            mService.addCallback(this);
            /* prepare playback */
            boolean hasMedia = mService.hasMedia();
            if (hasMedia)
                media = mService.getCurrentMediaWrapper();
            else if (media == null)
                media = new MediaWrapper(mUri);
            if (mWasPaused)
                media.addFlags(MediaWrapper.MEDIA_PAUSED);
            if (mHardwareAccelerationError || intent.hasExtra(PLAY_DISABLE_HARDWARE))
                media.addFlags(MediaWrapper.MEDIA_NO_HWACCEL);
            media.removeFlags(MediaWrapper.MEDIA_FORCE_AUDIO);
            media.addFlags(MediaWrapper.MEDIA_VIDEO);

            if (savedTime <= 0L && media != null && media.getTime() > 0L)
                savedTime = media.getTime();
            if (savedTime > 0L && !mService.isPlaying())
                mService.saveTimeToSeek(savedTime);

            // Handle playback
            if (!hasMedia)
                mService.load(media);
            else if (!mService.isPlaying())
                mService.playIndex(positionInPlaylist);
            else {
                onPlaying();
            }
        } else {
            mService.addCallback(this);
            //mService.loadLastPlaylist(PlaybackService.TYPE_VIDEO);
            MediaWrapper mw = mService.getCurrentMediaWrapper();
            if (mw == null) {
                mActivity.finish();
                return;
            }
            mUri = mService.getCurrentMediaWrapper().getUri();
        }

        //if (mWasPaused)
            //showOverlay(true);
    }

    private void initUI() {
        mVideoViewReply.cleanUI();

        if (AndroidUtil.isHoneycombOrLater()) {
            if (mOnLayoutChangeListener == null) {
                mOnLayoutChangeListener = new View.OnLayoutChangeListener() {
                    private final Runnable mRunnable = new Runnable() {
                        @Override
                        public void run() {
                            changeSurfaceLayout();
                        }
                    };

                    @Override
                    public void onLayoutChange(View v, int left, int top, int right,
                                               int bottom, int oldLeft, int oldTop, int oldRight, int oldBottom) {
                        if (left != oldLeft || top != oldTop || right != oldRight || bottom != oldBottom) {
                            /* changeSurfaceLayout need to be called after the layout changed */
                            mHandler.removeCallbacks(mRunnable);
                            mHandler.post(mRunnable);
                        }
                    }
                };
            }
            //实例化视图时回调
            mSurfaceView.addOnLayoutChangeListener(mOnLayoutChangeListener);
        }
        changeSurfaceLayout();
        /* //屏幕打开
        if (mRootView != null)
            mRootView.setKeepScreenOn(true);
            */
    }

    /**
     * set 音频延迟
     * @param connected
     */
    private void toggleBtDelay(boolean connected) {
        mService.setAudioDelay(connected ? 0 : 0l);
    }


    private BroadcastReceiver mBtReceiver = AndroidUtil.isICSOrLater() ? new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            switch (intent.getAction()) {
                case BluetoothA2dp.ACTION_CONNECTION_STATE_CHANGED:
                case BluetoothHeadset.ACTION_CONNECTION_STATE_CHANGED:
                    long savedDelay = 01;
                    long currentDelay = mService.getAudioDelay();
                    if (savedDelay != 0l) {
                        boolean connected = intent.getIntExtra(BluetoothA2dp.EXTRA_STATE, -1) == BluetoothA2dp.STATE_CONNECTED;
                        if (connected && currentDelay == 0l)
                            toggleBtDelay(true);
                        else if (!connected && savedDelay == currentDelay)
                            toggleBtDelay(false);
                    }
            }
        }
    } : null;
}
