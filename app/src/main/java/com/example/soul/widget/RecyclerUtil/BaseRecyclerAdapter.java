package com.example.soul.widget.RecyclerUtil;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;

import com.example.soul.area.AreaBean;
import com.example.soul.area.AudioViewHolder;
import com.example.soul.area.ImageViewHolder;
import com.example.soul.area.TextViewHolder;
import com.example.soul.area.VideoViewHolder;
import com.example.soul.soul.R;

import java.util.List;

/**
 * Created by lw on 2017/2/21.
 */

public class BaseRecyclerAdapter<T extends AreaBean,V extends BaseViewHolder> extends RecyclerView.Adapter implements View.OnLongClickListener,View.OnClickListener{

    private List<T> mData;
    private Context mContent;
    private LayoutInflater mInflater;
    private int[] ViewId;
    private OnRecyclerViewItemClickListener onRecyclerViewItemClickListener;
    public final static int VIEW_TYPE_TEXT=0;   //
    public final static int VIEW_TYPE_IMAGE=1;
    public final static int VIEW_TYPE_VIDEO=2;
    public final static int VIEW_TYPE_AUDIO=3;

    public BaseRecyclerAdapter(Context mContent,List<T> mData,int[] ViewId){
        this.mData=mData;
        this.mContent=mContent;
        this.mInflater=LayoutInflater.from(mContent);
        this.ViewId=ViewId;
    }

    //初始化View
    @Override
    public V onCreateViewHolder(ViewGroup parent, int viewType) {
        View view=null;
        RecyclerView.ViewHolder mHolder=null;
        switch (viewType){
            case VIEW_TYPE_TEXT:
                //[0]=1,length=1
                if (ViewId!=null&&VIEW_TYPE_TEXT<ViewId.length) {
                    view = mInflater.inflate(ViewId[VIEW_TYPE_TEXT], parent, false);
                }
                mHolder=new TextViewHolder(view);
                break;
            case VIEW_TYPE_IMAGE:
                if (ViewId!=null&&VIEW_TYPE_IMAGE<ViewId.length) {
                    view = mInflater.inflate(ViewId[VIEW_TYPE_IMAGE], parent, false);
                }
                mHolder=new ImageViewHolder(view);
                break;
            case VIEW_TYPE_VIDEO:
                if (ViewId!=null&&VIEW_TYPE_VIDEO<ViewId.length) {
                    view = mInflater.inflate(ViewId[VIEW_TYPE_VIDEO], parent, false);
                }
                mHolder=new VideoViewHolder(view);
                break;
            case VIEW_TYPE_AUDIO:
                if (ViewId!=null&&VIEW_TYPE_AUDIO<ViewId.length) {
                    view = mInflater.inflate(ViewId[VIEW_TYPE_AUDIO], parent, false);
                }
                mHolder=new AudioViewHolder(view);
                break;
            default:
        }
        return (V) mHolder;
    }
    public void onItemInit(V viewHolder) {

    }

    @Override
    public void onBindViewHolder(final RecyclerView.ViewHolder holder, int position) {
        if (holder instanceof VideoViewHolder){
            ((VideoViewHolder)holder).getPlayVideo().setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    onRecyclerViewItemClickListener.onItemClick(holder);
                }
            });
        }
    }


    //类型判断
    @Override
    public int getItemViewType(int position) {
        return mData.get(position).getmType();
    }

    //data.size
    @Override
    public int getItemCount() {
        return mData.size();
    }


    public T getItem(int position) {
        return mData.get(position);
    }

    public void setRecyclerViewItemClickListener(OnRecyclerViewItemClickListener recyclerViewItemClickListener){
        this.onRecyclerViewItemClickListener=recyclerViewItemClickListener;
    }

    @Override
    public void onClick(View view) {
    }

    @Override
    public boolean onLongClick(View view) {
        if (onRecyclerViewItemClickListener!=null)
            return onRecyclerViewItemClickListener.onItemLongClick(view);
        return false;
    }


    public interface OnRecyclerViewItemClickListener{
        void onItemClick(RecyclerView.ViewHolder holder);
        boolean onItemLongClick(View view);
    }
}
