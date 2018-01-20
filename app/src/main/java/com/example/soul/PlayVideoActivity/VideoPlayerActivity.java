/*****************************************************************************
 * VideoPlayerActivity.java
 * ****************************************************************************
 * Copyright © 2011-2014 VLC authors and VideoLAN
 * <p>
 * This program is free software; you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation; either version 2 of the License, or
 * (at your option) any later version.
 * <p>
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * <p>
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston MA 02110-1301, USA.
 *****************************************************************************/

package com.example.soul.PlayVideoActivity;

import android.annotation.TargetApi;
import android.app.Activity;
import android.bluetooth.BluetoothA2dp;
import android.bluetooth.BluetoothHeadset;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.content.pm.ActivityInfo;
import android.content.res.Configuration;
import android.media.AudioManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.provider.Settings;
import android.provider.Settings.SettingNotFoundException;
import android.support.annotation.NonNull;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v4.view.GestureDetectorCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.telephony.TelephonyManager;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Display;
import android.view.GestureDetector;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.Surface;
import android.view.SurfaceView;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnLayoutChangeListener;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationSet;
import android.view.animation.AnimationUtils;
import android.view.animation.DecelerateInterpolator;
import android.view.animation.RotateAnimation;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.SeekBar.OnSeekBarChangeListener;
import android.widget.TextView;
import android.widget.Toast;

import com.example.soul.TestVideo.VideoPlayerHelper;
import com.example.soul.soul.R;

import org.videolan.libvlc.IVLCVout;
import org.videolan.libvlc.MediaPlayer;
import org.videolan.libvlc.util.AndroidUtil;
import org.videolan.vlc.media.MediaWrapper;
import org.videolan.vlc.util.Permissions;
import org.videolan.vlc.util.Strings;
import org.videolan.vlc.util.Util;
import org.videolan.vlc.util.VLCInstance;

import java.lang.reflect.Method;
import java.util.HashSet;
public class VideoPlayerActivity extends AppCompatActivity implements
        GestureDetector.OnDoubleTapListener, OnClickListener ,VideoPlayerViewReply{

    public final static String TAG = "VLC/VideoPlayerActivity";

    public final static String SLEEP_INTENT = Strings.buildPkgString("SleepIntent");
    public final static String VIDEO_RESUME_TIME = "VideoResumeTime";

    public static boolean hasNavBar;

    // Internal intent identifier to distinguish between internal launch and
    // external intent.
    public final static String PLAY_FROM_VIDEOGRID = Strings.buildPkgString("gui.video.PLAY_FROM_VIDEOGRID");
    public final static String PLAY_FROM_SERVICE = Strings.buildPkgString("gui.video.PLAY_FROM_SERVICE");
    public final static String EXIT_PLAYER = Strings.buildPkgString("gui.video.EXIT_PLAYER");

    public final static String PLAY_EXTRA_ITEM_LOCATION = "item_location";
    public final static String PLAY_EXTRA_SUBTITLES_LOCATION = "subtitles_location";
    public final static String PLAY_EXTRA_ITEM_TITLE = "title";
    public final static String PLAY_EXTRA_FROM_START = "from_start";
    public final static String PLAY_EXTRA_START_TIME = "position";
    public final static String PLAY_EXTRA_OPENED_POSITION = "opened_position";
    public final static String PLAY_DISABLE_HARDWARE = "disable_hardware";

    public final static String ACTION_RESULT = Strings.buildPkgString("player.result");
    public final static String EXTRA_POSITION = "extra_position";
    public final static String EXTRA_DURATION = "extra_duration";
    public final static int RESULT_CONNECTION_FAILED = RESULT_FIRST_USER + 1;
    public final static int RESULT_PLAYBACK_ERROR = RESULT_FIRST_USER + 2;
    public final static int RESULT_HARDWARE_ACCELERATION_ERROR = RESULT_FIRST_USER + 3;
    public final static int RESULT_VIDEO_TRACK_LOST = RESULT_FIRST_USER + 4;

    //private PlaybackService mService;
    private SurfaceView mSurfaceView = null;
    private View mRootView;
    private Uri mUri;
    private boolean mAskResume = true;
    private GestureDetectorCompat mDetector = null;

    private ImageView mPlaylistNext;
    private ImageView mPlaylistPrevious;

    private static final int SURFACE_BEST_FIT = 0;
    private static final int SURFACE_FIT_HORIZONTAL = 1;
    private static final int SURFACE_FIT_VERTICAL = 2;
    private static final int SURFACE_FILL = 3;
    private static final int SURFACE_16_9 = 4;
    private static final int SURFACE_4_3 = 5;
    private static final int SURFACE_ORIGINAL = 6;
    private int mCurrentSize = SURFACE_BEST_FIT;

    private SharedPreferences mSettings;
    private int mTouchControls = 0;

    /** Overlay */
    private View mOverlayProgress;
    private View mOverlayButtons;
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

    private static final int LOADING_ANIMATION_DELAY = 0;

    private boolean mDragging;
    private boolean mShowing;
    private SeekBar mSeekbar;
    private TextView mTime;
    private TextView mLength;
    private boolean mIsLoading=true;
    private boolean mIsPlaying = false;
    private ImageView mPlayPause;
    private View mObjectFocused;
    private boolean mDisplayRemainingTime;
    private int mScreenOrientation;
    private int mScreenOrientationLock;
    private ImageView mLock;
    private String KEY_REMAINING_TIME_DISPLAY = "remaining_time_display";
    private String KEY_BLUETOOTH_DELAY = "key_bluetooth_delay";

    @Override
    public boolean onGenericMotionEvent(MotionEvent event) {
        return super.onGenericMotionEvent(event);
    }

    private boolean mIsLocked = false;
    /* -1 is a valid track (Disable) */
    private int mLastAudioTrack = -2;
    private int mLastSpuTrack = -2;
    private int mOverlayTimeout = 0;
    private boolean mLockBackButton = false;
    boolean mWasPaused = false;

    /**
     * For uninterrupted switching between audio and video mode
     */
    private boolean mSwitchingView;
    private boolean mHardwareAccelerationError;
    private boolean mHasSubItems = false;

    // size of the video
    private int mVideoHeight;
    private int mVideoWidth;
    private int mVideoVisibleHeight;
    private int mVideoVisibleWidth;
    private int mSarNum;
    private int mSarDen;

    //Volume
    private AudioManager mAudioManager;
    private int mAudioMax;
    private boolean mMute = false;
    private int mVolSave;
    private float mVol;

    //Touch Events
    private static final int TOUCH_NONE = 0;
    private static final int TOUCH_VOLUME = 1;
    private static final int TOUCH_BRIGHTNESS = 2;
    private static final int TOUCH_SEEK = 3;
    private int mTouchAction = TOUCH_NONE;
    private int mSurfaceYDisplayRange;
    private float mInitTouchY, mTouchY = -1f, mTouchX = -1f;

    //stick event
    private static final int JOYSTICK_INPUT_DELAY = 300;
    private long mLastMove;

    // Brightness
    private boolean mIsFirstBrightnessGesture = true;
    private float mRestoreAutoBrightness = -1f;


    /**
     * Flag to indicate whether the media should be paused once loaded
     * (e.g. lock screen, or to restore the pause state)
     */
    private boolean mPlaybackStarted = false;

    private static final String PREF_TIPS_SHOWN = "video_player_tips_shown";

    // Navigation handling (DVD, Blu-Ray...)
    private int mMenuIdx = -1;
    private boolean mIsNavMenu = false;

    /* for getTime and seek */
    private long mForcedTime = -1;
    private long mLastTime = -1;

    private OnLayoutChangeListener mOnLayoutChangeListener;
    private AlertDialog mAlertDialog;

    private VideoPlayerHelper mVideoHelper;

    @Override
    @TargetApi(Build.VERSION_CODES.JELLY_BEAN_MR1)
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.d("Video_Tag","onCreate");

        if (!VLCInstance.testCompatibleCPU(this)) {
            //无解码器
         }

        mSettings = PreferenceManager.getDefaultSharedPreferences(this);

        setContentView(R.layout.activity_content_viedo);
        /** initialize Views an their Events */

        mRootView = findViewById(R.id.player_root);
        //下方操作界面
        mOverlayProgress = findViewById(R.id.progress_overlay);
        //Video button
        mOverlayButtons = findViewById(R.id.player_overlay_buttons);
        // Position and remaining time
        mTime = (TextView) findViewById(R.id.player_overlay_time);
        mLength = (TextView) findViewById(R.id.player_overlay_length);

        //屏幕方向
        mScreenOrientation = Integer.valueOf(
                mSettings.getString("screen_orientation_value", "99" /*SCREEN ORIENTATION SENSOR*/));

        //play
        mPlayPause = (ImageView) findViewById(R.id.player_overlay_play);
        mLock = (ImageView) findViewById(R.id.lock_overlay_button);

        mSurfaceView = (SurfaceView) findViewById(R.id.video_play_surfaceView);

        mVideoHelper=VideoPlayerHelper.getInstance(this,mSurfaceView,this);
            //透明度
            //mSubtitlesSurfaceView.getHolder().setFormat(PixelFormat.TRANSLUCENT);

        mSeekbar = (SeekBar) findViewById(R.id.player_overlay_seekbar);

        dimStatusBar(true);
        //启动加载动画
        mVideoHelper.sendEmptyMessageDelayed();

        mSwitchingView = false;
        mHardwareAccelerationError = false;

        mAskResume = mSettings.getBoolean("dialog_confirm_resume", false);
        mDisplayRemainingTime = mSettings.getBoolean(KEY_REMAINING_TIME_DISPLAY, false);


        IntentFilter filter = new IntentFilter();
        filter.addAction(SLEEP_INTENT);
        registerReceiver(mReceiver, filter);

        //播放模式
        setVolumeControlStream(AudioManager.STREAM_MUSIC);

        // 100 is the value for screen_orientation_start_lock
        setRequestedOrientation(getScreenOrientation(mScreenOrientation));

        //提到View最上层
        //mOverlayTips.bringToFront();
        //mOverlayTips.invalidate();
        //resetHudLayout();
    }

    //set listener
    @Override
    protected void onResume() {
        super.onResume();
        Log.d("Video_Tag","onResume");

        /*
         * Set listeners here to avoid NPE when activity is closing
         */
        mSeekbar.setOnSeekBarChangeListener(mSeekListener);
        mLock.setOnClickListener(this);
        mPlayPause.setOnClickListener(this);
        mLength.setOnClickListener(this);
        mTime.setOnClickListener(this);

        if (mIsLocked && mScreenOrientation == 99)
            setRequestedOrientation(mScreenOrientationLock);
    }


    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    @Override
    protected void onPause() {
        super.onPause();
        Log.d("Video_Tag","onPause");
        hideOverlay(true);
        mSeekbar.setOnSeekBarChangeListener(null);
        mLock.setOnClickListener(null);
        mPlayPause.setOnClickListener(null);
        mPlayPause.setOnLongClickListener(null);
        mLength.setOnClickListener(null);
        mTime.setOnClickListener(null);

        /* Stop the earliest possible to avoid vout error */
        if (isFinishing())
            mVideoHelper.stopPlayback();
    }

    /**
     * 当activity被一个半透明activity覆盖，并且这个半透明activity正逐渐不透明时，或另外一个activity正在启动时，这个方法将被调用。
     * 子类覆盖这个方法时，必须调用super.onVisibleBehindCanceled()，否则将抛出异常SuperNotCalledException。
     * 当这个方法被调用时，activity在后台将有500毫秒的时间释放它可见时所使用的所有资源，此后，onStop将被调用。
     * 如果activity没能在500毫秒内返回，系统将销毁这个activity和它所在的进程以为其它进程提供资源。
     */
    @Override
    public void onVisibleBehindCanceled() {
        super.onVisibleBehindCanceled();
        Log.d("Video_Tag","onVisibleBehindCanceled");
        mVideoHelper.getService().stopPlayback();
        exitOK();
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        Log.d("Video_Tag","onConfigurationChanged");
        if (!AndroidUtil.isHoneycombOrLater())
            mVideoHelper.changeSurfaceLayout();
        super.onConfigurationChanged(newConfig);
        resetHudLayout();
    }

    public void resetHudLayout() {

        RelativeLayout.LayoutParams layoutParams = (RelativeLayout.LayoutParams) mOverlayButtons.getLayoutParams();
        int orientation = getScreenOrientation(100);
        Log.d("Video_Tag","resetHudLayout"+orientation);
        boolean portrait = orientation == ActivityInfo.SCREEN_ORIENTATION_PORTRAIT ||
                orientation == ActivityInfo.SCREEN_ORIENTATION_REVERSE_PORTRAIT;
        if (portrait) {
            layoutParams.addRule(RelativeLayout.BELOW, R.id.player_overlay_length);
            layoutParams.addRule(RelativeLayout.RIGHT_OF, 0);
            layoutParams.addRule(RelativeLayout.LEFT_OF, 0);
        } else {
            layoutParams.addRule(RelativeLayout.BELOW, R.id.player_overlay_seekbar);
            layoutParams.addRule(RelativeLayout.RIGHT_OF, R.id.player_overlay_time);
            layoutParams.addRule(RelativeLayout.LEFT_OF, R.id.player_overlay_length);
        }
        mOverlayButtons.setLayoutParams(layoutParams);
    }


    @Override
    protected void onStart() {
        super.onStart();
        Log.d("Video_Tag","onStart");
        mVideoHelper.onStart();
        IntentFilter filter = new IntentFilter(PLAY_FROM_SERVICE);
        filter.addAction(EXIT_PLAYER);
        LocalBroadcastManager.getInstance(this).registerReceiver(
                mServiceReceiver, filter);
        if (mBtReceiver != null) {
            IntentFilter btFilter = new IntentFilter(BluetoothA2dp.ACTION_CONNECTION_STATE_CHANGED);
            btFilter.addAction(BluetoothHeadset.ACTION_CONNECTION_STATE_CHANGED);
            registerReceiver(mBtReceiver, btFilter);
        }
    }

    @TargetApi(Build.VERSION_CODES.JELLY_BEAN_MR1)
    @Override
    protected void onStop() {
        super.onStop();
        Log.d("Video_Tag","onStop");
        LocalBroadcastManager.getInstance(this).unregisterReceiver(mServiceReceiver);

        if (mBtReceiver != null)
            unregisterReceiver(mBtReceiver);
        if (mAlertDialog != null && mAlertDialog.isShowing())
            mAlertDialog.dismiss();
        mVideoHelper.stopPlayback();


        restoreBrightness();
        mVideoHelper.onStop();
    }

    @TargetApi(Build.VERSION_CODES.FROYO)
    private void restoreBrightness() {
        Log.d("Video_Tag","restoreBrightness");
        if (mRestoreAutoBrightness != -1f) {
            int brightness = (int) (mRestoreAutoBrightness * 255f);
            Settings.System.putInt(getContentResolver(),
                    Settings.System.SCREEN_BRIGHTNESS,
                    brightness);
            Settings.System.putInt(getContentResolver(),
                    Settings.System.SCREEN_BRIGHTNESS_MODE,
                    Settings.System.SCREEN_BRIGHTNESS_MODE_AUTOMATIC);
        }
        // Save brightness if user wants to
        if (mSettings.getBoolean("save_brightness", false)) {
            float brightness = getWindow().getAttributes().screenBrightness;
            if (brightness != -1f) {
                Editor editor = mSettings.edit();
                editor.putFloat("brightness_value", brightness);
                Util.commitPreferences(editor);
            }
        }
    }

    @Override
    protected void onDestroy() {
        Log.d("Video_Tag","onDestroy");
        super.onDestroy();
        if (mReceiver != null)
            unregisterReceiver(mReceiver);

        mAudioManager = null;
    }



    public  void cleanUI() {

        if (mRootView != null)
            mRootView.setKeepScreenOn(false);

        if (mDetector != null) {
            mDetector.setOnDoubleTapListener(null);
            mDetector = null;
        }

        if (AndroidUtil.isICSOrLater())
            getWindow().getDecorView().setOnSystemUiVisibilityChangeListener(null);

    }

    public static void start(Context context, Uri uri) {
        start(context, uri, null, false, -1);
    }

    public static void start(Context context, Uri uri, boolean fromStart) {
        start(context, uri, null, fromStart, -1);
    }

    public static void start(Context context, Uri uri, String title) {
        start(context, uri, title, false, -1);
    }

    public static void startOpened(Context context, Uri uri, int openedPosition) {
        start(context, uri, null, false, openedPosition);
    }

    private static void start(Context context, Uri uri, String title, boolean fromStart, int openedPosition) {
        Intent intent = getIntent(context, uri, title, fromStart, openedPosition);

        context.startActivity(intent);
    }

    public static Intent getIntent(String action, Context context, MediaWrapper mw, boolean fromStart, int openedPosition) {
        return getIntent(action, context, mw.getUri(), mw.getTitle(), fromStart, openedPosition);
    }

    @NonNull
    public static Intent getIntent(Context context, Uri uri, String title, boolean fromStart, int openedPosition) {
        return getIntent(PLAY_FROM_VIDEOGRID, context, uri, title, fromStart, openedPosition);
    }

    @NonNull
    public static Intent getIntent(String action, Context context, Uri uri, String title, boolean fromStart, int openedPosition) {
        Intent intent = new Intent(context, VideoPlayerActivity.class);
        intent.setAction(action);
        intent.putExtra(PLAY_EXTRA_ITEM_LOCATION, uri);
        intent.putExtra(PLAY_EXTRA_ITEM_TITLE, title);
        intent.putExtra(PLAY_EXTRA_FROM_START, fromStart);

        if (openedPosition != -1 || !(context instanceof Activity)) {
            if (openedPosition != -1)
                intent.putExtra(PLAY_EXTRA_OPENED_POSITION, openedPosition);
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        }
        return intent;
    }

    private final BroadcastReceiver mReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();

            if (action.equalsIgnoreCase(SLEEP_INTENT)) {
                exitOK();
            }
        }
    };

    /**
     * send time
     * 退出时，保存给上个activity
     * onActivityResult 重写上个
     * @param resultCode
     */
    private void exit(int resultCode) {
        if (isFinishing())
            return;
        Intent resultIntent = new Intent(ACTION_RESULT);
        if (mUri != null && mVideoHelper.getService() != null) {
            resultIntent.setData(mUri);
            resultIntent.putExtra(EXTRA_POSITION, mVideoHelper.getService().getTime());
            resultIntent.putExtra(EXTRA_DURATION, mVideoHelper.getService().getLength());
        }
        setResult(resultCode, resultIntent);
       // finish();
    }

    private void exitOK() {
        exit(RESULT_OK);
    }

    @Override
    public boolean onTrackballEvent(MotionEvent event) {
        if (mIsLoading)
            return false;
        showOverlay();
        return true;
    }

    @Override
    public void onBackPressed() {
        Log.d("Video_Tag","onBackPressed");
        if (mLockBackButton) {
            mLockBackButton = false;
            mVideoHelper.onBackPressed();
            Toast.makeText(this, getString(R.string.back_quit_lock), Toast.LENGTH_SHORT).show();
        }else if (mShowing && !mIsLocked) {
            hideOverlay(true);
        } else {
            exitOK();
            super.onBackPressed();
        }
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        Log.d("Video_Tag","onKeyDown");
        if (keyCode == KeyEvent.KEYCODE_BACK || keyCode == KeyEvent.KEYCODE_BUTTON_B)
            return super.onKeyDown(keyCode, event);
        if (mIsLoading) {
            switch (keyCode) {
                case KeyEvent.KEYCODE_S:
                case KeyEvent.KEYCODE_MEDIA_STOP:
                    exitOK();
                    return true;
            }
            return false;
        }

        if (mShowing || keyCode == KeyEvent.KEYCODE_DPAD_DOWN)
            showOverlayTimeout(OVERLAY_TIMEOUT);
        switch (keyCode) {
            case KeyEvent.KEYCODE_F:
            case KeyEvent.KEYCODE_MEDIA_FAST_FORWARD:
                seekDelta(10000);
                return true;
            case KeyEvent.KEYCODE_R:
            case KeyEvent.KEYCODE_MEDIA_REWIND:
                seekDelta(-10000);
                return true;
            case KeyEvent.KEYCODE_BUTTON_R1:
                seekDelta(60000);
                return true;
            case KeyEvent.KEYCODE_BUTTON_L1:
                seekDelta(-60000);
                return true;
            case KeyEvent.KEYCODE_BUTTON_A:
                if (mOverlayProgress.getVisibility() == View.VISIBLE)
                    return false;
            case KeyEvent.KEYCODE_MEDIA_PLAY_PAUSE:
            case KeyEvent.KEYCODE_MEDIA_PLAY:
            case KeyEvent.KEYCODE_MEDIA_PAUSE:
            case KeyEvent.KEYCODE_SPACE:
                if (mIsNavMenu)
                    return navigateDvdMenu(keyCode);
                else if (keyCode == KeyEvent.KEYCODE_MEDIA_PLAY_PAUSE) //prevent conflict with remote control
                    return super.onKeyDown(keyCode, event);
                else
                    doPlayPause();
                return true;
            case KeyEvent.KEYCODE_O:
            case KeyEvent.KEYCODE_BUTTON_Y:
            case KeyEvent.KEYCODE_MENU:
                //showAdvancedOptions();
                return true;
            case KeyEvent.KEYCODE_V:
            case KeyEvent.KEYCODE_MEDIA_AUDIO_TRACK:
            case KeyEvent.KEYCODE_BUTTON_X:
                //onAudioSubClick(mTracks);
                return true;
            case KeyEvent.KEYCODE_N:
                showNavMenu();
                return true;
            case KeyEvent.KEYCODE_A:
                resizeVideo();
                return true;
            case KeyEvent.KEYCODE_M:
            case KeyEvent.KEYCODE_VOLUME_MUTE:
                updateMute();
                return true;
            case KeyEvent.KEYCODE_S:
            case KeyEvent.KEYCODE_MEDIA_STOP:
                exitOK();
                return true;
            case KeyEvent.KEYCODE_DPAD_LEFT:
                if (!mShowing) {
                    seekDelta(-10000);
                    return true;
                }
            case KeyEvent.KEYCODE_DPAD_RIGHT:
                if (!mShowing) {
                    seekDelta(10000);
                    return true;
                }
            case KeyEvent.KEYCODE_DPAD_UP:
                if (!mShowing) {
                    //showAdvancedOptions();
                    return true;
                }
            case KeyEvent.KEYCODE_DPAD_DOWN:
            case KeyEvent.KEYCODE_DPAD_CENTER:
                if (!mShowing) {
                    doPlayPause();
                    return true;
                }
            case KeyEvent.KEYCODE_ENTER:
                if (mIsNavMenu)
                    return navigateDvdMenu(keyCode);
                else
                    return super.onKeyDown(keyCode, event);
            case KeyEvent.KEYCODE_VOLUME_DOWN:
            case KeyEvent.KEYCODE_VOLUME_UP:
                if (mMute) {
                    updateMute();
                    return true;
                } else
                    return false;
        }
        return super.onKeyDown(keyCode, event);
    }

    private boolean navigateDvdMenu(int keyCode) {
        Log.d("Video_Tag","navigateDvdMenu"+keyCode);
        switch (keyCode) {
            case KeyEvent.KEYCODE_DPAD_UP:
                mVideoHelper.getService().navigate(MediaPlayer.Navigate.Up);
                return true;
            case KeyEvent.KEYCODE_DPAD_DOWN:
                mVideoHelper.getService().navigate(MediaPlayer.Navigate.Down);
                return true;
            case KeyEvent.KEYCODE_DPAD_LEFT:
                mVideoHelper.getService().navigate(MediaPlayer.Navigate.Left);
                return true;
            case KeyEvent.KEYCODE_DPAD_RIGHT:
                mVideoHelper.getService().navigate(MediaPlayer.Navigate.Right);
                return true;
            case KeyEvent.KEYCODE_DPAD_CENTER:
            case KeyEvent.KEYCODE_ENTER:
            case KeyEvent.KEYCODE_BUTTON_X:
            case KeyEvent.KEYCODE_BUTTON_A:
                mVideoHelper.getService().navigate(MediaPlayer.Navigate.Activate);
                return true;
            default:
                return false;
        }
    }

    /**
     * Lock screen rotation
     */
    private void lockScreen() {
        Log.d("Video_Tag","lockScreen");
        if (mScreenOrientation != 100) {
            mScreenOrientationLock = getRequestedOrientation();
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR2)
                //设置屏幕方向
                setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LOCKED);
            else
                setRequestedOrientation(getScreenOrientation(100));
        }
        //多屏显示时显示信息
        showInfo(R.string.locked, 1000);
        mLock.setImageResource(R.drawable.ic_locked_circle);
        //禁用按钮
        mTime.setEnabled(false);
        mSeekbar.setEnabled(false);
        mLength.setEnabled(false);
        if (mPlaylistNext != null)
            mPlaylistNext.setEnabled(false);
        if (mPlaylistPrevious != null)
            mPlaylistPrevious.setEnabled(false);
        hideOverlay(true);
        mLockBackButton = true;
        mIsLocked = true;
    }

    /**
     * Remove screen lock
     */
    private void unlockScreen() {
        if (mScreenOrientation != 100)
            setRequestedOrientation(mScreenOrientationLock);
        showInfo(R.string.unlocked, 1000);
        mLock.setImageResource(R.drawable.ic_lock_circle);
        mTime.setEnabled(true);
        mSeekbar.setEnabled(mVideoHelper.getService() == null || mVideoHelper.getService().isSeekable());
        mLength.setEnabled(true);
        if (mPlaylistNext != null)
            mPlaylistNext.setEnabled(true);
        if (mPlaylistPrevious != null)
            mPlaylistPrevious.setEnabled(true);
        mShowing = false;
        mIsLocked = false;
        showOverlay();
        mLockBackButton = false;
    }

    /**
     * Show text in the info view and vertical progress bar for "duration" milliseconds
     * @param text
     * @param duration
     * @param barNewValue new volume/brightness value (range: 0 - 15)
     */
    private void showInfoWithVerticalBar(String text, int duration, int barNewValue) {
        showInfo(text, duration);
    }

    /**
     * Show text in the info view for "duration" milliseconds
     * @param text
     * @param duration
     */
    private void showInfo(String text, int duration) {
        //mHandler.removeMessages(FADE_OUT_INFO);
        //mHandler.sendEmptyMessageDelayed(FADE_OUT_INFO, duration);
    }

    private void showInfo(int textid, int duration) {
        // setViewVisibility(mVerticalBar, View.GONE);
        // setViewVisibility(mOverlayInfo, View.VISIBLE);
       // mHandler.removeMessages(FADE_OUT_INFO);
        //mHandler.sendEmptyMessageDelayed(FADE_OUT_INFO, duration);
    }

    @Override
    public boolean onSingleTapConfirmed(MotionEvent e) {
        return false;
    }

    //双击
    @Override
    public boolean onDoubleTap(MotionEvent e) {
        if (mVideoHelper.getService() == null)
            return false;
        if (!mIsLocked) {
            doPlayPause();
            return true;
        }
        return false;
    }

    @Override
    public boolean onDoubleTapEvent(MotionEvent e) {
        return false;
    }

    /* PlaybackService.Callback */

    //结束

    public void encounteredError() {
        if (isFinishing())
            return;
        //We may not have the permission to access files
        if (AndroidUtil.isMarshMallowOrLater() && mUri != null &&
                TextUtils.equals(mUri.getScheme(), "file") &&
                !Permissions.canReadStorage(this)) {
            Permissions.checkReadStoragePermission(this, true);
            return;
        }
        /* Encountered Error, exit player with a message */
        mAlertDialog = new AlertDialog.Builder(VideoPlayerActivity.this)
                .setOnCancelListener(new DialogInterface.OnCancelListener() {
                    @Override
                    public void onCancel(DialogInterface dialog) {
                        exit(RESULT_PLAYBACK_ERROR);
                    }
                })
                .setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int id) {
                        exit(RESULT_PLAYBACK_ERROR);
                    }
                })
                .setTitle(R.string.encountered_error_title)
                .setMessage(R.string.encountered_error_message)
                .create();
        mAlertDialog.show();
    }

    @Override
    public void updateNavStatus() {

    }

    @Override
    public void updateSeekable(boolean seekable) {

    }

    @Override
    public void updatePausable(boolean pausable) {

    }

    private void sendMouseEvent(int action, int button, int x, int y) {
        if (mVideoHelper.getService() == null)
            return;
        final IVLCVout vlcVout = mVideoHelper.getService().getVLCVout();
        vlcVout.sendMouseEvent(action, button, x, y);
    }

    /**
     * show/hide the overlay
     */
    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if (mVideoHelper.getService() == null)
            return false;
        if (mTouchControls == 0 || mIsLocked) {
            // locked or swipe disabled, only handle show/hide & ignore all actions
            if (event.getAction() == MotionEvent.ACTION_UP) {
                if (!mShowing) {
                    showOverlay();
                } else {
                    hideOverlay(true);
                }
            }
            return false;
        }
        if (mDetector != null && mDetector.onTouchEvent(event))
            return true;

        DisplayMetrics screen = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(screen);

        if (mSurfaceYDisplayRange == 0)
            mSurfaceYDisplayRange = Math.min(screen.widthPixels, screen.heightPixels);

        float x_changed, y_changed;
        if (mTouchX != -1f && mTouchY != -1f) {
            y_changed = event.getRawY() - mTouchY;
            x_changed = event.getRawX() - mTouchX;
        } else {
            x_changed = 0f;
            y_changed = 0f;
        }


        // coef is the gradient's move to determine a neutral zone
        float coef = Math.abs(y_changed / x_changed);
        float xgesturesize = ((x_changed / screen.xdpi) * 2.54f);
        float delta_y = Math.max(1f, (Math.abs(mInitTouchY - event.getRawY()) / screen.xdpi + 0.5f) * 2f);

        /* Offset for Mouse Events */
        int[] offset = new int[2];
        mSurfaceView.getLocationOnScreen(offset);
        int xTouch = Math.round((event.getRawX() - offset[0]) * mVideoWidth / mSurfaceView.getWidth());
        int yTouch = Math.round((event.getRawY() - offset[1]) * mVideoHeight / mSurfaceView.getHeight());

        switch (event.getAction()) {

            case MotionEvent.ACTION_DOWN:
                // Audio
                mTouchY = mInitTouchY = event.getRawY();
                mVol = mAudioManager.getStreamVolume(AudioManager.STREAM_MUSIC);
                mTouchAction = TOUCH_NONE;
                // Seek
                mTouchX = event.getRawX();
                // Mouse events for the core
                sendMouseEvent(MotionEvent.ACTION_DOWN, 0, xTouch, yTouch);
                break;

            case MotionEvent.ACTION_MOVE:
                // Mouse events for the core
                sendMouseEvent(MotionEvent.ACTION_MOVE, 0, xTouch, yTouch);

                // No volume/brightness action if coef < 2 or a secondary display is connected
                //TODO : Volume action when a secondary display is connected
                if (mTouchAction != TOUCH_SEEK && coef > 2) {
                    if (Math.abs(y_changed / mSurfaceYDisplayRange) < 0.05)
                        return false;
                    mTouchY = event.getRawY();
                    mTouchX = event.getRawX();
                    // Volume (Up or Down - Right side)
                    if (mTouchControls == 1 || (mTouchControls == 3 && (int) mTouchX > (4 * screen.widthPixels / 7))) {
                        doVolumeTouch(y_changed);
                        hideOverlay(true);
                    }
                    // Brightness (Up or Down - Left side)
                    if (mTouchControls == 2 || (mTouchControls == 3 && (int) mTouchX < (3 * screen.widthPixels / 7))) {
                        doBrightnessTouch(y_changed);
                        hideOverlay(true);
                    }
                } else {
                    // Seek (Right or Left move)
                    doSeekTouch(Math.round(delta_y), xgesturesize, false);
                }
                break;

            case MotionEvent.ACTION_UP:
                // Mouse events for the core
                sendMouseEvent(MotionEvent.ACTION_UP, 0, xTouch, yTouch);

                if (mTouchAction == TOUCH_NONE) {
                    if (!mShowing) {
                        showOverlay();
                    } else {
                        hideOverlay(true);
                    }
                }
                // Seek
                if (mTouchAction == TOUCH_SEEK)
                    doSeekTouch(Math.round(delta_y), xgesturesize, true);
                mTouchX = -1f;
                mTouchY = -1f;
                break;
        }
        return mTouchAction != TOUCH_NONE;
    }

    private void doSeekTouch(int coef, float gesturesize, boolean seek) {
        if (coef == 0)
            coef = 1;
        // No seek action if coef > 0.5 and gesturesize < 1cm
        if (Math.abs(gesturesize) < 1 || !mVideoHelper.getService().isSeekable())
            return;

        if (mTouchAction != TOUCH_NONE && mTouchAction != TOUCH_SEEK)
            return;
        mTouchAction = TOUCH_SEEK;

        long length = mVideoHelper.getService().getLength();
        long time = getTime();

        // Size of the jump, 10 minutes max (600000), with a bi-cubic progression, for a 8cm gesture
        int jump = (int) ((Math.signum(gesturesize) * ((600000 * Math.pow((gesturesize / 8), 4)) + 3000)) / coef);

        // Adjust the jump
        if ((jump > 0) && ((time + jump) > length))
            jump = (int) (length - time);
        if ((jump < 0) && ((time + jump) < 0))
            jump = (int) -time;

        //Jump !
        if (seek && length > 0)
            seek(time + jump, length);

        if (length > 0)
            //Show the jump's size
            showInfo(String.format("%s%s (%s)%s",
                    jump >= 0 ? "+" : "",
                    Strings.millisToString(jump),
                    Strings.millisToString(time + jump),
                    coef > 1 ? String.format(" x%.1g", 1.0 / coef) : ""), 50);
        else
            showInfo(R.string.unseekable_stream, 1000);
    }

    private void doVolumeTouch(float y_changed) {
        if (mTouchAction != TOUCH_NONE && mTouchAction != TOUCH_VOLUME)
            return;
        float delta = -((y_changed / mSurfaceYDisplayRange) * mAudioMax);
        mVol += delta;
        int vol = (int) Math.min(Math.max(mVol, 0), mAudioMax);
        if (delta != 0f) {
            setAudioVolume(vol);
        }
    }

    private void setAudioVolume(int vol) {
        mAudioManager.setStreamVolume(AudioManager.STREAM_MUSIC, vol, 0);

        /* Since android 4.3, the safe volume warning dialog is displayed only with the FLAG_SHOW_UI flag.
         * We don't want to always show the default UI volume, so show it only when volume is not set. */
        int newVol = mAudioManager.getStreamVolume(AudioManager.STREAM_MUSIC);
        if (vol != newVol)
            mAudioManager.setStreamVolume(AudioManager.STREAM_MUSIC, vol, AudioManager.FLAG_SHOW_UI);

        mTouchAction = TOUCH_VOLUME;
        vol = vol * 100 / mAudioMax;
        showInfoWithVerticalBar(getString(R.string.volume) + "\n" + Integer.toString(vol) + '%', 1000, vol);
    }

    private void mute(boolean mute) {
        mMute = mute;
        if (mMute)
            mVolSave = mVideoHelper.getService().getVolume();
        mVideoHelper.getService().setVolume(mMute ? 0 : mVolSave);
    }

    private void updateMute() {
        mute(!mMute);
        showInfo(mMute ? R.string.sound_off : R.string.sound_on, 1000);
    }

    private void initBrightnessTouch() {
        WindowManager.LayoutParams lp = getWindow().getAttributes();
        float brightnesstemp = lp.screenBrightness != -1f ? lp.screenBrightness : 0.6f;
        // Initialize the layoutParams screen brightness
        try {
            if (Settings.System.getInt(getContentResolver(), Settings.System.SCREEN_BRIGHTNESS_MODE) == Settings.System.SCREEN_BRIGHTNESS_MODE_AUTOMATIC) {
                if (!Permissions.canWriteSettings(this)) {
                    Permissions.checkWriteSettingsPermission(this, Permissions.PERMISSION_SYSTEM_BRIGHTNESS);
                    return;
                }
                Settings.System.putInt(getContentResolver(),
                        Settings.System.SCREEN_BRIGHTNESS_MODE,
                        Settings.System.SCREEN_BRIGHTNESS_MODE_MANUAL);
                mRestoreAutoBrightness = Settings.System.getInt(getContentResolver(),
                        Settings.System.SCREEN_BRIGHTNESS) / 255.0f;
            } else if (brightnesstemp == 0.6f) {
                brightnesstemp = Settings.System.getInt(getContentResolver(),
                        Settings.System.SCREEN_BRIGHTNESS) / 255.0f;
            }
        } catch (SettingNotFoundException e) {
            e.printStackTrace();
        }
        lp.screenBrightness = brightnesstemp;
        getWindow().setAttributes(lp);
        mIsFirstBrightnessGesture = false;
    }

    private void doBrightnessTouch(float y_changed) {
        if (mTouchAction != TOUCH_NONE && mTouchAction != TOUCH_BRIGHTNESS)
            return;
        if (mIsFirstBrightnessGesture) initBrightnessTouch();
        mTouchAction = TOUCH_BRIGHTNESS;

        // Set delta : 2f is arbitrary for now, it possibly will change in the future
        float delta = -y_changed / mSurfaceYDisplayRange;

        changeBrightness(delta);
    }


    private void changeBrightness(float delta) {
        // Estimate and adjust Brightness
        WindowManager.LayoutParams lp = getWindow().getAttributes();
        float brightness = Math.min(Math.max(lp.screenBrightness + delta, 0.01f), 1f);
        setWindowBrightness(brightness);
        brightness = Math.round(brightness * 100);
        showInfoWithVerticalBar(getString(R.string.brightness) + "\n" + (int) brightness + '%', 1000, (int) brightness);
    }

    /**
     * 亮度
     * @param brightness
     */
    private void setWindowBrightness(float brightness) {
        WindowManager.LayoutParams lp = getWindow().getAttributes();
        lp.screenBrightness = brightness;
        // Set Brightness
        getWindow().setAttributes(lp);
    }

    /**
     * handle changes of the seekbar (slicer)
     */
    private final OnSeekBarChangeListener mSeekListener = new OnSeekBarChangeListener() {

        @Override
        public void onStartTrackingTouch(SeekBar seekBar) {
            mDragging = true;
            showOverlayTimeout(OVERLAY_INFINITE);
        }

        @Override
        public void onStopTrackingTouch(SeekBar seekBar) {
            mDragging = false;
            showOverlay(true);
        }

        @Override
        public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
            if (!isFinishing() && fromUser && mVideoHelper.getService().isSeekable()) {
                seek(progress);
                setOverlayProgress();
                mTime.setText(Strings.millisToString(progress));
                showInfo(Strings.millisToString(progress), 1000);
            }
        }
    };


    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.player_overlay_play:
                doPlayPause();
                break;
            case R.id.player_overlay_forward:
                seekDelta(10000);
                break;
            case R.id.player_overlay_rewind:
                seekDelta(-10000);
                break;
            case R.id.lock_overlay_button:
                //锁屏
                if (mIsLocked)
                    unlockScreen();
                else
                    lockScreen();
                break;
            case R.id.player_overlay_length:
            case R.id.player_overlay_time:
                mDisplayRemainingTime = !mDisplayRemainingTime;
                showOverlay();
                Util.commitPreferences(mSettings.edit().putBoolean(KEY_REMAINING_TIME_DISPLAY, mDisplayRemainingTime));
                break;
        }
    }

    private void showNavMenu() {
        if (mMenuIdx >= 0)
            mVideoHelper.getService().setTitleIdx(mMenuIdx);
    }

    private void doPlayPause() {
        if (!mVideoHelper.getService().isPausable())
            return;

        if (mVideoHelper.getService().isPlaying()) {
            pause();
            showOverlayTimeout(OVERLAY_INFINITE);
        } else {
            play();
            showOverlayTimeout(OVERLAY_TIMEOUT);
        }
        mPlayPause.requestFocus();
    }

    private long getTime() {
        long time = mVideoHelper.getService().getTime();
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
            time = (int) mVideoHelper.getService().getCurrentMediaWrapper().getTime();
        return mForcedTime == -1 ? time : mForcedTime;
    }

    private void seek(long position) {
        seek(position, mVideoHelper.getService().getLength());
    }

    private void seek(long position, long length) {
        mForcedTime = position;
        mLastTime = mVideoHelper.getService().getTime();
        mVideoHelper.getService().seek(position, length);
    }

    private void seekDelta(int delta) {
        // unseekable stream
        if (mVideoHelper.getService().getLength() <= 0 || !mVideoHelper.getService().isSeekable()) return;

        long position = getTime() + delta;
        if (position < 0) position = 0;
        seek(position);
        showInfo(Strings.millisToString(mVideoHelper.getService().getTime()) + "/" + Strings.millisToString(mVideoHelper.getService().getLength()), 1000);
    }

    /**
     * 改变视图大小
     */
    private void resizeVideo() {
        if (mCurrentSize < SURFACE_ORIGINAL) {
            mCurrentSize++;
        } else {
            mCurrentSize = 0;
        }
        mVideoHelper.changeSurfaceLayout();
        switch (mCurrentSize) {
            case SURFACE_BEST_FIT:
                showInfo(R.string.surface_best_fit, 1000);
                break;
            case SURFACE_FIT_HORIZONTAL:
                showInfo(R.string.surface_fit_horizontal, 1000);
                break;
            case SURFACE_FIT_VERTICAL:
                showInfo(R.string.surface_fit_vertical, 1000);
                break;
            case SURFACE_FILL:
                showInfo(R.string.surface_fill, 1000);
                break;
            case SURFACE_16_9:
                showInfo("16:9", 1000);
                break;
            case SURFACE_4_3:
                showInfo("4:3", 1000);
                break;
            case SURFACE_ORIGINAL:
                showInfo(R.string.surface_original, 1000);
                break;
        }
        showOverlay();
    }

    /**
     * show overlay
     * @param forceCheck: adjust the timeout in function of playing state
     */
    private void showOverlay(boolean forceCheck) {
        if (forceCheck)
            mOverlayTimeout = 0;
        showOverlayTimeout(0);
    }

    /**
     * show overlay with the previous timeout value
     */
    private void showOverlay() {
        showOverlay(false);
    }

    /**
     * show overlay
     */
    private void showOverlayTimeout(int timeout) {
        if (mVideoHelper.getService() == null)
            return;
        if (timeout != 0)
            mOverlayTimeout = timeout;
        if (mOverlayTimeout == 0)
            mOverlayTimeout = mVideoHelper.getService().isPlaying() ? OVERLAY_TIMEOUT : OVERLAY_INFINITE;
        if (mIsNavMenu) {
            mShowing = true;
            return;
        }
        //mHandler.sendEmptyMessage(SHOW_PROGRESS);
        if (!mShowing) {
            mShowing = true;
            if (!mIsLocked) {
                mPlayPause.setVisibility(View.VISIBLE);
                setViewVisibility(mPlaylistNext, View.VISIBLE);
                setViewVisibility(mPlaylistPrevious, View.VISIBLE);
            }
            dimStatusBar(false);
            mOverlayProgress.setVisibility(View.VISIBLE);
        }
        //mHandler.removeMessages(FADE_OUT);
        if (mOverlayTimeout != OVERLAY_INFINITE)
            //mHandler.sendMessageDelayed(mHandler.obtainMessage(FADE_OUT), mOverlayTimeout);
        updateOverlayPausePlay();
        if (!(mObjectFocused == null)) {
            if (mObjectFocused.isFocusable())
                mObjectFocused.requestFocus();
            mObjectFocused = null;
        }
    }


    /**
     * hider overlay
     * 隐藏
     */
    private void hideOverlay(boolean fromUser) {
        if (mShowing) {
            //mHandler.removeMessages(FADE_OUT);
            //mHandler.removeMessages(SHOW_PROGRESS);
            Log.i(TAG, "remove View!");
            mObjectFocused = getCurrentFocus();
            if (!fromUser && !mIsLocked) {
                mOverlayProgress.startAnimation(AnimationUtils.loadAnimation(this, android.R.anim.fade_out));
                mPlayPause.startAnimation(AnimationUtils.loadAnimation(this, android.R.anim.fade_out));
                if (mPlaylistNext != null)
                    mPlaylistNext.startAnimation(AnimationUtils.loadAnimation(this, android.R.anim.fade_out));
                if (mPlaylistPrevious != null)
                    mPlaylistPrevious.startAnimation(AnimationUtils.loadAnimation(this, android.R.anim.fade_out));
            }

            mOverlayProgress.setVisibility(View.INVISIBLE);
            mPlayPause.setVisibility(View.INVISIBLE);
            setViewVisibility(mPlaylistNext, View.INVISIBLE);
            setViewVisibility(mPlaylistPrevious, View.INVISIBLE);
            mShowing = false;
            dimStatusBar(true);
        } else if (!fromUser) {
            /*
             * Try to hide the Nav Bar again.
             * It seems that you can't hide the Nav Bar if you previously
             * showed it in the last 1-2 seconds.
             */
            dimStatusBar(true);
        }
    }

    /**
     * Dim the status bar and/or navigation icons when needed on Android 3.x.
     * Hide it on Android 4.0 and later
     */
    @TargetApi(Build.VERSION_CODES.KITKAT)
    private void dimStatusBar(boolean dim) {

        if (!AndroidUtil.isHoneycombOrLater() || mIsNavMenu)
            return;
        int visibility = 0;
        int navbar = 0;

        if (AndroidUtil.isJellyBeanOrLater()) {
            visibility = View.SYSTEM_UI_FLAG_LAYOUT_STABLE | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN;
            navbar = View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION;
        }
        if (dim || mIsLocked) {
            getWindow().addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);
            if (AndroidUtil.isICSOrLater())
                navbar |= View.SYSTEM_UI_FLAG_LOW_PROFILE;
            else
                visibility |= View.STATUS_BAR_HIDDEN;
            if (!hasCombBar(this)) {
                navbar |= View.SYSTEM_UI_FLAG_HIDE_NAVIGATION;
                if (AndroidUtil.isKitKatOrLater())
                    visibility |= View.SYSTEM_UI_FLAG_IMMERSIVE;
                if (AndroidUtil.isJellyBeanOrLater())
                    visibility |= View.SYSTEM_UI_FLAG_FULLSCREEN;
            }
        } else {
            getWindow().clearFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);
            if (AndroidUtil.isICSOrLater())
                visibility |= View.SYSTEM_UI_FLAG_VISIBLE;
            else
                visibility |= View.STATUS_BAR_VISIBLE;
        }

        if (hasNavBar)
            visibility |= navbar;
        getWindow().getDecorView().setSystemUiVisibility(visibility);
    }

    private void showTitle() {
        if (!AndroidUtil.isHoneycombOrLater() || mIsNavMenu)
            return;
        int visibility = 0;
        int navbar = 0;

        if (AndroidUtil.isJellyBeanOrLater()) {
            visibility = View.SYSTEM_UI_FLAG_LAYOUT_STABLE | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN;
            navbar = View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION;
        }
        if (AndroidUtil.isICSOrLater())
            navbar |= View.SYSTEM_UI_FLAG_HIDE_NAVIGATION;

        if (hasNavBar)
            visibility |= navbar;
        getWindow().getDecorView().setSystemUiVisibility(visibility);

    }

    public void updateOverlayPausePlay() {
        if (mVideoHelper.getService() == null)
            return;
        if (mVideoHelper.getService().isPausable())
            mPlayPause.setImageResource(mVideoHelper.getService().isPlaying() ? R.drawable.ic_pause_circle
                    : R.drawable.ic_play_circle);
    }

    /**
     * update the overlay
     */
    public int setOverlayProgress() {
        if (mVideoHelper.getService() == null) {
            return 0;
        }
        int time = (int) getTime();
        int length = (int) mVideoHelper.getService().getLength();

        // Update all view elements
        mSeekbar.setMax(length);
        mSeekbar.setProgress(time);

        if (time >= 0) mTime.setText(Strings.millisToString(time));
        if (length >= 0) mLength.setText(mDisplayRemainingTime && length > 0
                ? "-" + '\u00A0' + Strings.millisToString(length - time)
                : Strings.millisToString(length));

        return time;
    }

    private void setESTracks() {
        if (mLastAudioTrack >= -1) {
            mVideoHelper.getService().setAudioTrack(mLastAudioTrack);
            mLastAudioTrack = -2;
        }
        if (mLastSpuTrack >= -1) {
            mVideoHelper.getService().setSpuTrack(mLastSpuTrack);
            mLastSpuTrack = -2;
        }
    }

    /**
     *
     */
    private void play() {
        mVideoHelper.getService().play();
        if (mRootView != null)
            mRootView.setKeepScreenOn(true);
    }

    /**
     *
     */
    private void pause() {
        mVideoHelper.getService().pause();
        if (mRootView != null)
            mRootView.setKeepScreenOn(false);
    }

    /*
     * Additionnal method to prevent alert dialog to pop up
     */
    @SuppressWarnings({"unchecked"})
    private void loadMedia(boolean fromStart) {
        mAskResume = false;
        getIntent().putExtra(PLAY_EXTRA_FROM_START, fromStart);
        //loadMedia();
    }
    @SuppressWarnings("deprecation")
    private int getScreenRotation() {
        WindowManager wm = (WindowManager) getSystemService(Context.WINDOW_SERVICE);
        Display display = wm.getDefaultDisplay();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.FROYO /* Android 2.2 has getRotation */) {
            try {
                Method m = display.getClass().getDeclaredMethod("getRotation");
                return (Integer) m.invoke(display);
            } catch (Exception e) {
                return Surface.ROTATION_0;
            }
        } else {
            return display.getOrientation();
        }
    }

    //获取屏幕方向
    @TargetApi(Build.VERSION_CODES.JELLY_BEAN_MR2)
    private int getScreenOrientation(int mode) {
        switch (mode) {
            case 99: //screen orientation user
                //传感器
                return AndroidUtil.isJellyBeanMR2OrLater() ?
                        ActivityInfo.SCREEN_ORIENTATION_FULL_SENSOR :
                        ActivityInfo.SCREEN_ORIENTATION_SENSOR;
            case 101: //screen orientation landscape 横屏
                if (AndroidUtil.isGingerbreadOrLater())
                    return ActivityInfo.SCREEN_ORIENTATION_SENSOR_LANDSCAPE;
                else
                    return ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE;
            case 102: //screen orientation portrait  竖屏
                if (AndroidUtil.isGingerbreadOrLater())
                    return ActivityInfo.SCREEN_ORIENTATION_SENSOR_PORTRAIT;
                else

                    return ActivityInfo.SCREEN_ORIENTATION_PORTRAIT;
        }
        /*
         mScreenOrientation = 100, we lock screen at its current orientation
         */
        WindowManager wm = (WindowManager) getSystemService(Context.WINDOW_SERVICE);
        Display display = wm.getDefaultDisplay();
        int rot = getScreenRotation();
        /*
         * Since getRotation() returns the screen's "natural" orientation,
         * which is not guaranteed to be SCREEN_ORIENTATION_PORTRAIT,
         * we have to invert the SCREEN_ORIENTATION value if it is "naturally"
         * landscape.
         */
        @SuppressWarnings("deprecation")
        boolean defaultWide = display.getWidth() > display.getHeight();
        if (rot == Surface.ROTATION_90 || rot == Surface.ROTATION_270)
            defaultWide = !defaultWide;
        if (defaultWide) {
            switch (rot) {
                case Surface.ROTATION_0:
                    return ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE;
                case Surface.ROTATION_90:
                    return ActivityInfo.SCREEN_ORIENTATION_PORTRAIT;
                case Surface.ROTATION_180:
                    // SCREEN_ORIENTATION_REVERSE_PORTRAIT only available since API
                    // Level 9+
                    return (AndroidUtil.isGingerbreadOrLater() ? ActivityInfo.SCREEN_ORIENTATION_REVERSE_LANDSCAPE
                            : ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
                case Surface.ROTATION_270:
                    // SCREEN_ORIENTATION_REVERSE_LANDSCAPE only available since API
                    // Level 9+
                    return (AndroidUtil.isGingerbreadOrLater() ? ActivityInfo.SCREEN_ORIENTATION_REVERSE_PORTRAIT
                            : ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
                default:
                    return 0;
            }
        } else {
            switch (rot) {
                case Surface.ROTATION_0:
                    return ActivityInfo.SCREEN_ORIENTATION_PORTRAIT;
                case Surface.ROTATION_90:
                    return ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE;
                case Surface.ROTATION_180:
                    // SCREEN_ORIENTATION_REVERSE_PORTRAIT only available since API
                    // Level 9+
                    return (AndroidUtil.isGingerbreadOrLater() ? ActivityInfo.SCREEN_ORIENTATION_REVERSE_PORTRAIT
                            : ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
                case Surface.ROTATION_270:
                    // SCREEN_ORIENTATION_REVERSE_LANDSCAPE only available since API
                    // Level 9+
                    return (AndroidUtil.isGingerbreadOrLater() ? ActivityInfo.SCREEN_ORIENTATION_REVERSE_LANDSCAPE
                            : ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
                default:
                    return 0;
            }
        }
    }

    public void showConfirmResumeDialog() {
        if (isFinishing())
            return;
        mVideoHelper.getService().pause();
        /* Encountered Error, exit player with a message */
        mAlertDialog = new AlertDialog.Builder(VideoPlayerActivity.this)
                .setMessage(R.string.confirm_resume)
                .setPositiveButton(R.string.resume_from_position, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        loadMedia(false);
                    }
                })
                .setNegativeButton(R.string.play_from_start, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        loadMedia(true);
                    }
                })
                .create();
        mAlertDialog.setCancelable(false);
        mAlertDialog.show();
    }

    //显示高级选项
    /*
    public void showAdvancedOptions() {
        FragmentManager fm = getSupportFragmentManager();
        AdvOptionsDialog advOptionsDialog = new AdvOptionsDialog();
        advOptionsDialog.setOnDismissListener(new DialogInterface.OnDismissListener() {
            @Override
            public void onDismiss(DialogInterface dialog) {
                dimStatusBar(true);
            }
        });
        advOptionsDialog.show(fm, "fragment_adv_options");
        hideOverlay(false);
    }
*/


    private BroadcastReceiver mBtReceiver = AndroidUtil.isICSOrLater() ? new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            switch (intent.getAction()) {
                case BluetoothA2dp.ACTION_CONNECTION_STATE_CHANGED:
                case BluetoothHeadset.ACTION_CONNECTION_STATE_CHANGED:
                    long savedDelay = mSettings.getLong(KEY_BLUETOOTH_DELAY, 0l);
                    long currentDelay = mVideoHelper.getService().getAudioDelay();
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

    private void toggleBtDelay(boolean connected) {
        mVideoHelper.getService().setAudioDelay(connected ? mSettings.getLong(KEY_BLUETOOTH_DELAY, 0) : 0l);
    }

    /**
     * Start the video loading animation.
     */
    public void startLoading() {
        if (mIsLoading)
            return;
        mIsLoading = true;
        AnimationSet anim = new AnimationSet(true);
        RotateAnimation rotate = new RotateAnimation(0f, 360f, Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f);
        rotate.setDuration(800);
        rotate.setInterpolator(new DecelerateInterpolator());
        rotate.setRepeatCount(RotateAnimation.INFINITE);
        anim.addAnimation(rotate);
    }

    /**
     * Stop the video loading animation.
     */
    private void stopLoading() {
        //mHandler.removeMessages(LOADING_ANIMATION);
        if (!mIsLoading)
            return;
        mIsLoading = false;

    }

    public void onClickOverlayTips(View v) {

    }

    public void onClickDismissTips(View v) {
        Editor editor = mSettings.edit();
        editor.putBoolean(PREF_TIPS_SHOWN, true);
        Util.commitPreferences(editor);
    }


    /**
     * 双击
     */
    private GestureDetector.OnGestureListener mGestureListener = new GestureDetector.OnGestureListener() {
        @Override
        public boolean onDown(MotionEvent e) {
            return false;
        }

        @Override
        public void onShowPress(MotionEvent e) {
        }

        @Override
        public boolean onSingleTapUp(MotionEvent e) {
            return false;
        }

        @Override
        public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX, float distanceY) {
            return false;
        }

        @Override
        public void onLongPress(MotionEvent e) {
        }

        @Override
        public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {
            return false;
        }
    };


    private BroadcastReceiver mServiceReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (TextUtils.equals(intent.getAction(), PLAY_FROM_SERVICE))
                onNewIntent(intent);
            else if (TextUtils.equals(intent.getAction(), EXIT_PLAYER))
                exitOK();
        }
    };

    private void setViewVisibility(View view, int visibility) {
        if (view != null)
            view.setVisibility(visibility);
    }

    /**
     * hasCombBar test if device has Combined Bar : only for tablet with Honeycomb or ICS
     */
    public static boolean hasCombBar(Context context) {
        return (!isPhone(context)
                && ((Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) &&
                (Build.VERSION.SDK_INT <= Build.VERSION_CODES.JELLY_BEAN)));
    }

    public static boolean isPhone(Context context) {
        TelephonyManager manager = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
        return manager.getPhoneType() != TelephonyManager.PHONE_TYPE_NONE;
    }

    public static boolean isICSOrLater() {
        return Build.VERSION.SDK_INT >= Build.VERSION_CODES.ICE_CREAM_SANDWICH;
    }

    static {
        HashSet<String> devicesWithoutNavBar = new HashSet<String>();
        devicesWithoutNavBar.add("HTC One V");
        devicesWithoutNavBar.add("HTC One S");
        devicesWithoutNavBar.add("HTC One X");
        devicesWithoutNavBar.add("HTC One XL");
        hasNavBar = AndroidUtil.isICSOrLater()
                && !devicesWithoutNavBar.contains(Build.MODEL);
    }
}
