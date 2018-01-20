package com.example.soul.PlayVideoActivity;

/**
 * Created by lw on 2017/3/29.
 */

public interface VideoPlayerViewReply {
    void updateOverlayPausePlay();
    void encounteredError();
    void updateNavStatus();
    void updateSeekable(boolean seekable);
    void updatePausable(boolean pausable);
    void cleanUI();
    void startLoading();
    int setOverlayProgress();
}
