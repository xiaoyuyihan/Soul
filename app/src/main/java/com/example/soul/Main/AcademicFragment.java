package com.example.soul.Main;

import android.support.v4.app.Fragment;

/**
 * Created by lw on 2017/5/16.
 * 学术
 */
public class AcademicFragment extends Fragment{
    private static AcademicFragment AcademicFragment;
    public static Fragment getInstanceFragment() {
        if (AcademicFragment==null){
            AcademicFragment=new AcademicFragment();
        }
        return AcademicFragment;
    }
}
