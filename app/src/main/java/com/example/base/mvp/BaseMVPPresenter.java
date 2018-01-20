package com.example.base.mvp;

/**
 * Created by lw on 2017/2/15.
 *
 */

public interface BaseMVPPresenter<T extends BaseMVPView> {
    /**
     * 绑定View
     * @param view
     */
     void attachView(T view);

     void detachView(boolean retainInstance);
}
