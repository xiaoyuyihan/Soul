package com.example.base.mvp;

/**
 * Created by lw on 2017/2/15.
 * 定义一些与界面相关的方法的接口给Presenter调用
 * 如动画，视图
 * （view）是显示数据（model）并且将用户指令（events）传送到presenter以便作用于那些数据的一个接口
 */

public interface BaseMVPView<T extends BaseMVPPresenter> {
    /**
     * 获取Presenter（）单例
     * @return
     */
    T getPresenterInstance();
}
