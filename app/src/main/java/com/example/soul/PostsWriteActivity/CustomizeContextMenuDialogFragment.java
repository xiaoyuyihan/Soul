package com.example.soul.PostsWriteActivity;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;

import com.yalantis.contextmenu.lib.ContextMenuDialogFragment;
import com.yalantis.contextmenu.lib.MenuParams;

/**
 * Created by lw on 2017/4/6.
 */

public class CustomizeContextMenuDialogFragment extends ContextMenuDialogFragment {
    private int mViewYPadding=INT_NULL;
    private int mViewXPadding=INT_NULL;
    private int mViewAlpha=INT_NULL;
    private int mGravity=INT_NULL;
    private static final int INT_NULL=-1024;
    private static final String BUNDLE_MENU_PARAMS = "BUNDLE_MENU_PARAMS";
    public static CustomizeContextMenuDialogFragment newInstance(MenuParams menuParams) {
        CustomizeContextMenuDialogFragment fragment = new CustomizeContextMenuDialogFragment();
        Bundle args = new Bundle();
        args.putParcelable(BUNDLE_MENU_PARAMS, menuParams);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onResume() {
        super.onResume();
        Window mWindow=getDialog().getWindow();
        if (mGravity!=INT_NULL){
            mWindow.setGravity(mGravity);}
        WindowManager.LayoutParams layoutParams=mWindow.getAttributes();
        if (mViewYPadding!=INT_NULL)
            layoutParams.y=mViewYPadding;
        if (mViewAlpha!=INT_NULL&&0.0f<=mViewAlpha&&mViewAlpha<=1.0f)
            layoutParams.alpha=mViewAlpha;
        if (mViewXPadding!=INT_NULL)
            layoutParams.x=mViewXPadding;
        mWindow.setAttributes(layoutParams);
    }

    public void setViewXPadding(int mViewXPadding) {
        this.mViewXPadding = mViewXPadding;
    }

    public void setViewAlpha(int mViewAlpha) {
        this.mViewAlpha = mViewAlpha;
    }

    public void setViewYPadding(int mViewYPadding) {
        this.mViewYPadding = mViewYPadding;
    }

    public void setGravity(int mGravity) {
        this.mGravity = mGravity;
    }
}
