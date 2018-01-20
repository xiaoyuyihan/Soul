package com.example.base.mvp;

import android.content.Context;
import android.content.Loader;

import com.example.soul.area.AreaPresenter;

/**
 * Created by lw on 2017/2/15.
 */

public class PresenterLoader<V extends MVPView> extends Loader {
    private AreaPresenter mPresenter;
    private V mView;
    private Context context;
    public PresenterLoader(Context context,V View) {
        super(context);
        this.context=context;
        mView=View;
    }

    @Override
    protected void onStartLoading() {
        if (mPresenter!=null){
            deliverResult(mPresenter);
        }else {
        forceLoad();}
        super.onStartLoading();

    }

    @Override
    protected void onForceLoad() {

        mPresenter= AreaPresenter.getInstance(context);
        mPresenter.attachView(mView);
        super.onForceLoad();
    }

    @Override
    protected void onReset() {
        mPresenter.detachView(false);
        super.onReset();
    }
    public AreaPresenter getmPresenter(){
        return mPresenter;
    }
}
