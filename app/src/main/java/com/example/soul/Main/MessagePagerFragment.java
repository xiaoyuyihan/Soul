package com.example.soul.Main;

import android.content.Loader;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.util.ArrayMap;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.soul.soul.R;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Created by lw on 2017/5/16.
 */

public class MessagePagerFragment extends Fragment {
    private static List<MessagePagerFragment> messagePagerFragmentLoader=new ArrayList<>();
    public static final String INT_FALG="fragment_falg";
    public static final String BACKGROUND_FALG="fragment_background";
    private int mFragmentFalg;
    private int mBackgroundID;
    public static MessagePagerFragment newInstance(int Falg,@Nullable Integer background) {
        MessagePagerFragment fragment;
        for (int i=0;i<messagePagerFragmentLoader.size();i++){
            fragment=messagePagerFragmentLoader.get(i);
            if (Falg==fragment.getFragmentFalg()){
                return fragment;
            }
        }
        Bundle args = new Bundle();
        args.putInt(INT_FALG,Falg);
        if (background==null){
            args.putInt(BACKGROUND_FALG,-1);
        }else {
            args.putInt(BACKGROUND_FALG,background);
        }        fragment = new MessagePagerFragment();
        fragment.setArguments(args);
        messagePagerFragmentLoader.add(fragment);
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mFragmentFalg=getArguments().getInt(INT_FALG);
        mBackgroundID=getArguments().getInt(BACKGROUND_FALG);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view=inflater.inflate(R.layout.view_recycler,container,false);
        ((TextView)view.findViewById(R.id.textView)).setText(String.valueOf(mFragmentFalg));

        if (mBackgroundID!=-1){
            view.setBackground(getActivity().getResources().getDrawable(mBackgroundID));
        }
        return view;
    }

    public int getFragmentFalg() {
        return mFragmentFalg;
    }
}
