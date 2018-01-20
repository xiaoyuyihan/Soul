package com.example.soul.area;

import com.example.base.mvp.MVPModel;

/**
 * Created by lw on 2017/2/21.
 */

public class AreaModel extends MVPModel {
    public static AreaModel mAreaModel;
    public static AreaModel getInstance(){
        if (mAreaModel==null){
            mAreaModel=new AreaModel();
        }
        return  mAreaModel;
    }

    @Override
    public Object NetworkRespect(String Url, OnLoadRespectListener l) {
        return null;
    }

    @Override
    public Object InsideRespect(String URL, OnLoadRespectListener l) {
        return null;
    }

}
