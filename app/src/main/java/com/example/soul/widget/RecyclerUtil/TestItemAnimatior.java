package com.example.soul.widget.RecyclerUtil;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.widget.RecyclerView;

import java.util.List;

/**
 * Created by lw on 2017/2/22.
 * 动画
 */

public class TestItemAnimatior extends RecyclerView.ItemAnimator {
    /**
     * 当RecyclerView中的item在屏幕上由可见变为不可见时调用此方法
     * @param viewHolder
     * @param preLayoutInfo
     * @param postLayoutInfo
     * @return
     */
    @Override
    public boolean animateDisappearance(@NonNull RecyclerView.ViewHolder viewHolder, @NonNull ItemHolderInfo preLayoutInfo, @Nullable ItemHolderInfo postLayoutInfo) {
        return false;
    }

    /**
     * 当RecyclerView中的item显示到屏幕上时调用此方法
     * @param viewHolder
     * @param preLayoutInfo
     * @param postLayoutInfo
     * @return
     */
    @Override
    public boolean animateAppearance(@NonNull RecyclerView.ViewHolder viewHolder, @Nullable ItemHolderInfo preLayoutInfo, @NonNull ItemHolderInfo postLayoutInfo) {
        return false;
    }

    /**
     * 并没有调用notifyItemChanged(int) 和 notifyDataSetChanged()但布局发生了改变，比如移动等
     * @param viewHolder
     * @param preLayoutInfo
     * @param postLayoutInfo
     * @return
     */
    @Override
    public boolean animatePersistence(@NonNull RecyclerView.ViewHolder viewHolder, @NonNull ItemHolderInfo preLayoutInfo, @NonNull ItemHolderInfo postLayoutInfo) {
        return false;
    }

    /**
     * 当RecyclerView中的item状态发生改变时调用此方法(notifyItemChanged(position))
     * @param oldHolder
     * @param newHolder
     * @param preLayoutInfo
     * @param postLayoutInfo
     * @return
     */
    @Override
    public boolean animateChange(@NonNull RecyclerView.ViewHolder oldHolder, @NonNull RecyclerView.ViewHolder newHolder, @NonNull ItemHolderInfo preLayoutInfo, @NonNull ItemHolderInfo postLayoutInfo) {
        return false;
    }

    /**
     * recordPreLayoutInformation , recordPostLayoutInformation 这2个 API 用来记录 item view 在 layout 前后的状态信息，这些信息封装在 ItemHolderInfo 或者其子类中，并将传递给上述4个动画API中，以便进行动画展示
     * @param state
     * @param viewHolder
     * @param changeFlags
     * @param payloads
     * @return
     */

    @NonNull
    @Override
    public ItemHolderInfo recordPreLayoutInformation(@NonNull RecyclerView.State state, @NonNull RecyclerView.ViewHolder viewHolder, int changeFlags, @NonNull List<Object> payloads) {
        return super.recordPreLayoutInformation(state, viewHolder, changeFlags, payloads);
    }

    @NonNull
    @Override
    public ItemHolderInfo recordPostLayoutInformation(@NonNull RecyclerView.State state, @NonNull RecyclerView.ViewHolder viewHolder) {
        return super.recordPostLayoutInformation(state, viewHolder);
    }

    /**
     * 可以用来延迟动画到下一帧，此时就需要在上述4个 API 的实现中返回 true ，并且自行记录延迟的动画信息，以便在下一帧时执行
     */
    @Override
    public void runPendingAnimations() {

    }


    @Override
    public void endAnimation(RecyclerView.ViewHolder item) {

    }

    @Override
    public void endAnimations() {

    }

    @Override
    public boolean isRunning() {
        return false;
    }
}
