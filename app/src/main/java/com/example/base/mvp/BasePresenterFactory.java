package com.example.base.mvp;

/**
 * Created by lw on 2017/2/15.
 */

public interface BasePresenterFactory<T extends BaseMVPPresenter> {
    T create();
}
