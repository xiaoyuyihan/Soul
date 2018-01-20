package com.example.soul.Main;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.soul.soul.R;

/**
 * Created by lw on 2017/5/9.
 */
public class MyselfFragment extends Fragment{
    private static MyselfFragment myselfFragment;
    public static Fragment getInstanceFragment() {
        if (myselfFragment==null){
            myselfFragment=new MyselfFragment();
        }
        return myselfFragment;
    }

    private TextView mTobView;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view=inflater.inflate(R.layout.fragment_myself,container,false);
        init(view);
        return view;
    }

    private void init(View view) {
        mTobView=(TextView)view.findViewById(R.id.view_top_textview);
        mTobView.setText("My");
    }

}
