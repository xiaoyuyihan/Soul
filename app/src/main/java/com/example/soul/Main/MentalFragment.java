package com.example.soul.Main;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

/**
 * Created by lw on 2017/5/9.
 */
public class MentalFragment extends Fragment{
    private static MentalFragment mentalFragment;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return super.onCreateView(inflater, container, savedInstanceState);
    }

    public static Fragment getInstanceFragment() {
        if (mentalFragment==null){
            mentalFragment=new MentalFragment();
        }
        return mentalFragment;
    }
}
