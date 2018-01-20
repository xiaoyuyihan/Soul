package com.example.soul.Main;

import android.support.v4.app.Fragment;

/**
 * Created by lw on 2017/5/16.
 * 知识
 */
public class KnowledgeFragment extends Fragment{
    private static KnowledgeFragment KnowledgeFragment;
    public static Fragment getInstanceFragment() {
        if (KnowledgeFragment==null){
            KnowledgeFragment=new KnowledgeFragment();
        }
        return KnowledgeFragment;
    }
}
