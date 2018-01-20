/*****************************************************************************
 * public class IVLCVout.java
 *****************************************************************************
 * Copyright © 2015 VLC authors, VideoLAN and VideoLabs
 *
 * This program is free software; you can redistribute it and/or modify it
 * under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation; either version 2.1 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with this program; if not, write to the Free Software Foundation,
 * Inc., 51 Franklin Street, Fifth Floor, Boston MA 02110-1301, USA.
 *****************************************************************************/

package org.videolan.libvlc;

import android.annotation.TargetApi;
import android.graphics.SurfaceTexture;
import android.os.Build;
import android.support.annotation.MainThread;
import android.view.Surface;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.TextureView;

@SuppressWarnings("unused")
public interface IVLCVout {
    interface Callback {
        /**
         * This callback is called when the native vout call request a new Layout.
         *  当本地vout调用请求新的布局时，将调用此回调。
         * @param vlcVout vlcVout
         * @param width Frame width
         * @param height Frame height
         * @param visibleWidth Visible frame width  可见框架宽度
         * @param visibleHeight Visible frame height
         * @param sarNum Surface aspect ratio numerator     宽高比分子
         * @param sarDen Surface aspect ratio denominator
         */
        @MainThread
        void onNewLayout(IVLCVout vlcVout, int width, int height, int visibleWidth, int visibleHeight, int sarNum, int sarDen);

        /**
         * This callback is called when surfaces are created.
         */
        @MainThread
        void onSurfacesCreated(IVLCVout vlcVout);

        /**
         * This callback is called when surfaces are destroyed.
         */
        @MainThread
        void onSurfacesDestroyed(IVLCVout vlcVout);

        /**
         * TODO: temporary method, will be removed when VLC can handle decoder fallback
         */
        @MainThread
        void onHardwareAccelerationError(IVLCVout vlcVout);
    }

    /**
     * Set a surfaceView used for video out.
     * 设置用于视频输出的surfaceView。
     * @see #attachViews()
     */
    @MainThread
    void setVideoView(SurfaceView videoSurfaceView);

    /**
     * Set a TextureView used for video out.
     * 设置用于视频输出的TextureView。
     * @see #attachViews()
     */
    @MainThread
    @TargetApi(Build.VERSION_CODES.ICE_CREAM_SANDWICH)
    void setVideoView(TextureView videoTextureView);

    /**
     * Set a surface used for video out.
     * 设置用于视频输出的曲面。
     * @param videoSurface if surfaceHolder is null, this surface must be valid and attached.
     *                     如果surfaceHolder为null，则此Surface必须有效并附加。
     * @param surfaceHolder optional, used to configure buffers geometry before Android ICS
     * and to get notified when surface is destroyed.
     *                      可选，用于在Android ICS之前配置缓冲区几何，并在Surface被破坏时获得通知。
     * @see #attachViews()
     */
    @MainThread
    void setVideoSurface(Surface videoSurface, SurfaceHolder surfaceHolder);

    /**
     * Set a SurfaceTexture used for video out.
     * 设置用于视频输出的SurfaceTexture。
     * @param videoSurfaceTexture this surface must be valid and attached.
     * @see #attachViews()
     */
    @MainThread
    @TargetApi(Build.VERSION_CODES.ICE_CREAM_SANDWICH)
    void setVideoSurface(SurfaceTexture videoSurfaceTexture);

    /**
     * Set a surfaceView used for subtitles out.
     * 设置一个用于字幕的surfaceView。
     * @see #attachViews()
     */
    @MainThread
    void setSubtitlesView(SurfaceView subtitlesSurfaceView);

    /**
     * Set a TextureView used for subtitles out.
     * @see #attachViews()
     */
    @MainThread
    @TargetApi(Build.VERSION_CODES.ICE_CREAM_SANDWICH)
    void setSubtitlesView(TextureView subtitlesTextureView);

    /**
     * Set a surface used for subtitles out.
     * 设置用于字幕的surface。
     * @param subtitlesSurface if surfaceHolder is null, this surface must be valid and attached.
     * @param surfaceHolder optional, used to configure buffers geometry before Android ICS
     * and to get notified when surface is destroyed.
     * @see #attachViews()
     */
    @MainThread
    void setSubtitlesSurface(Surface subtitlesSurface, SurfaceHolder surfaceHolder);

    /**
     * Set a SurfaceTexture used for subtitles out.
     * @param subtitlesSurfaceTexture this surface must be valid and attached.
     * @see #attachViews()
     */
    @MainThread
    @TargetApi(Build.VERSION_CODES.ICE_CREAM_SANDWICH)
    void setSubtitlesSurface(SurfaceTexture subtitlesSurfaceTexture);

    /**
     * Attach views previously set by setVideoView, setSubtitlesView, setVideoSurface, setSubtitleSurface
     * 附加先前由setVideoView，setSubtitlesView，setVideoSurface，setSubtitleSurface设置的视图
     * @see #setVideoView(SurfaceView)
     * @see #setVideoView(TextureView)
     * @see #setVideoSurface(Surface, SurfaceHolder)
     * @see #setSubtitlesView(SurfaceView)
     * @see #setSubtitlesView(TextureView)
     * @see #setSubtitlesSurface(Surface, SurfaceHolder)
     */
    @MainThread
    void attachViews();

    /**
     * Detach views previously attached.
     * This will be called automatically when surfaces are destroyed.
     */
    @MainThread
    void detachViews();

    /**
     * Return true if views are attached. If surfaces were destroyed, this will return false.
     * 如果附加了视图，则返回true。 如果表面被破坏，这将返回false
     */
    @MainThread
    boolean areViewsAttached();

    /**
     * Add a callback to receive {@link Callback#onNewLayout} events.
     */
    @MainThread
    void addCallback(Callback callback);

    /**
     * Remove a callback.
     */
    @MainThread
    void removeCallback(Callback callback);

    /**
     * Send a mouse event to the native vout.
     * 将鼠标事件发送到native
     * @param action see ACTION_* in {@link android.view.MotionEvent}.
     * @param button see BUTTON_* in {@link android.view.MotionEvent}.
     * @param x x coordinate.
     * @param y y coordinate.
     */
    @MainThread
    void sendMouseEvent(int action, int button, int x, int y);

    /**
     * Send the the window size to the native vout.
     * 将窗口大小发送到native。
     * @param width width of the window.
     * @param height height of the window.
     */
    @MainThread
    void setWindowSize(int width, int height);
}
