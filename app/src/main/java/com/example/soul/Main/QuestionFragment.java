package com.example.soul.Main;

import android.support.v4.app.Fragment;

/**
 * Created by lw on 2017/5/16.
 */
public class QuestionFragment extends Fragment{
    private static QuestionFragment QuestionFragment;
    public static Fragment getInstanceFragment() {
        if (QuestionFragment==null){
            QuestionFragment=new QuestionFragment();
        }
        return QuestionFragment;
    }

}
