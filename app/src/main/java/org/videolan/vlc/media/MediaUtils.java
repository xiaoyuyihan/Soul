package org.videolan.vlc.media;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;

import com.example.soul.soul.R;

import org.videolan.vlc.PlaybackService;

//工具类——字幕
public class MediaUtils {

    public static String getMediaGenre(Context ctx, MediaWrapper media) {
        final String genre = media.getGenre();
        return genre != null ? genre : getMediaString(ctx, R.string.unknown_genre);
    }
    public static String getMediaArtist(Context ctx, MediaWrapper media) {
        final String artist = media.getArtist();
        return artist != null ? artist : getMediaString(ctx, R.string.unknown_artist);
    }
    public static String getMediaReferenceArtist(Context ctx, MediaWrapper media) {
        final String artist = media.getReferenceArtist();
        return artist != null ? artist : getMediaString(ctx, R.string.unknown_artist);
    }
    public static String getMediaAlbum(Context ctx, MediaWrapper media) {
        final String album = media.getAlbum();
        return album != null ? album : getMediaString(ctx, R.string.unknown_album);

    }
    private static String getMediaString(Context ctx, int id) {
        if (ctx != null)
            return ctx.getResources().getString(id);
        else {
            switch (id) {
                case R.string.unknown_artist:
                    return "Unknown Artist";
                case R.string.unknown_album:
                    return "Unknown Album";
                case R.string.unknown_genre:
                    return "Unknown Genre";
                default:
                    return "";
            }
        }
    }


    private static abstract class BaseCallBack implements PlaybackService.Client.Callback {
        protected PlaybackService.Client mClient;

        private BaseCallBack(Context context) {
            mClient = new PlaybackService.Client(context, this);
            mClient.connect();
        }

        protected BaseCallBack() {}

        @Override
        public void onDisconnected() {}
    }

    //对话回调
    public static class DialogCallback extends BaseCallBack {
        //加载图标Dialog
        private final ProgressDialog dialog;
        private final Runnable mRunnable;

        public interface Runnable {
            void run(PlaybackService service);
        }

        public DialogCallback(Context context, Runnable runnable) {
            mClient = new PlaybackService.Client(context, this);
            mRunnable = runnable;
            this.dialog = ProgressDialog.show(
                    context,
                    context.getApplicationContext().getString(R.string.loading) + "…",
                    context.getApplicationContext().getString(R.string.please_wait), true);
            dialog.setCancelable(true);
            //监听后退
            dialog.setOnCancelListener(new DialogInterface.OnCancelListener() {
                @Override
                public void onCancel(DialogInterface dialog) {
                    synchronized (this) {
                        //解除绑定
                        mClient.disconnect();
                    }
                }
            });
            synchronized (this) {
                //绑定
                mClient.connect();
            }
        }

        //连接URL
        @Override
        public void onConnected(PlaybackService service) {
            synchronized (this) {
                mRunnable.run(service);
            }
            dialog.cancel();
        }

        @Override
        public void onDisconnected() {
            dialog.dismiss();
        }
    }
}
