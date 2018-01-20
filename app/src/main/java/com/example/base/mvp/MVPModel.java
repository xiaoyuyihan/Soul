package com.example.base.mvp;

/**
 * Created by lw on 2017/2/16.
 */

public abstract class MVPModel<T> implements BaseMVPModel {

    public static MVPModel getInstance(){return null;}
    /**
     * 网络加载
     * @param Url
     * @param l
     * @return
     */
    public abstract T NetworkRespect(String Url, final OnLoadRespectListener l);

    /**
     * 本地数据库加载
     * @param URL
     * @param l
     * @return
     */
    public abstract T InsideRespect(String URL,final OnLoadRespectListener l);
    public interface OnLoadRespectListener{
        void onSuccess();
        void onFailure();
    }
}
