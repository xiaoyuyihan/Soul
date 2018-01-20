package com.example.soul.area;

import android.graphics.Bitmap;

import java.util.List;

/**
 * Created by lw on 2017/2/21.
 */

public class AreaBean {
    //上级菜单名
    private String AreaFatherName;
    private String AreaFatherID;
    //标题
    private String AreaName;
    private String AreaId;
    //子标题
    private String AreaChildName;
    //图片，视频，音频 路径
    private String[] AreaImageUrl;
    private String[] AreaVideoUrl;
    private String[] AreaAudioUrl;
    //内容
    private String AreaContent;
    //点赞数
    private int AreaApplaudNumber;
    //踩
    private int AreaDemoteNumber;
    //评论数
    private int AreaCommentNumber;
    //发布时间
    private String AreaTime;
    private Bitmap AreaFristImage;
    //评论list
    private List<AreaCommentBean> mCommentData;
    //类型
    private int mType;
    private int mLike=0;

    public void setAreaFatherName(String areaFatherName) {
        AreaFatherName = areaFatherName;
    }

    public void setAreaName(String areaName) {
        AreaName = areaName;
    }

    public void setAreaId(String areaId) {
        AreaId = areaId;
    }

    public void setAreaFatherID(String areaFatherID) {
        AreaFatherID = areaFatherID;
    }

    public void setAreaImageUrl(String[] areaImageUrl) {
        AreaImageUrl = areaImageUrl;
    }

    public void setAreaChildName(String areaChildName) {
        AreaChildName = areaChildName;
    }

    public void setAreaVideoUrl(String[] areaVideoUrl) {
        AreaVideoUrl = areaVideoUrl;
    }

    public void setAreaAudioUrl(String[] areaAudioUrl) {
        AreaAudioUrl = areaAudioUrl;
    }

    public void setAreaContent(String areaContent) {
        AreaContent = areaContent;
    }

    public void setAreaApplaudNumber(int areaApplaudNumber) {
        AreaApplaudNumber = areaApplaudNumber;
    }

    public void setAreaDemoteNumber(int areaDemoteNumber) {
        AreaDemoteNumber = areaDemoteNumber;
    }

    public void setAreaCommentNumber(int areaCommentNumber) {
        AreaCommentNumber = areaCommentNumber;
    }

    public void setAreaTime(String areaTime) {
        AreaTime = areaTime;
    }

    public void setAreaFristImage(Bitmap areaFristImage) {
        AreaFristImage = areaFristImage;
    }

    public void setmCommentData(List<AreaCommentBean> mCommentData) {
        this.mCommentData = mCommentData;
    }

    public void setmLike(int mLike) {
        this.mLike = mLike;
    }

    public void setmType(int mType) {
        this.mType = mType;
    }

    public String getAreaFatherID() {

        return AreaFatherID;
    }

    public String getAreaId() {
        return AreaId;
    }

    public String[] getAreaImageUrl() {
        return AreaImageUrl;
    }

    public String getAreaName() {
        return AreaName;
    }

    public String getAreaChildName() {
        return AreaChildName;
    }

    public String[] getAreaVideoUrl() {
        return AreaVideoUrl;
    }

    public String[] getAreaAudioUrl() {
        return AreaAudioUrl;
    }

    public int getAreaApplaudNumber() {
        return AreaApplaudNumber;
    }

    public int getAreaCommentNumber() {
        return AreaCommentNumber;
    }

    public Bitmap getAreaFristImage() {
        return AreaFristImage;
    }

    public String getAreaTime() {
        return AreaTime;
    }

    public int getAreaDemoteNumber() {
        return AreaDemoteNumber;
    }

    public String getAreaContent() {
        return AreaContent;
    }

    public List<AreaCommentBean> getmCommentData() {
        return mCommentData;
    }

    public int getmLike() {
        return mLike;
    }

    public int getmType() {
        return mType;
    }

    public String getAreaFatherName() {


        return AreaFatherName;
    }
}
