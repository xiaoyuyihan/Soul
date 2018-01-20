package com.example.base.mvp;

import android.app.Activity;
import android.view.View;

/**
 * Created by lw on 2017/2/15.
 */

public interface MVPView<T> extends BaseMVPView {

    void showMessage();
    void showDialog();
    void dismissDialog();
    View getContentView();
    Activity getActivity();

}
