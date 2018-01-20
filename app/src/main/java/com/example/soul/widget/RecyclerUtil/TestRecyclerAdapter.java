package com.example.soul.widget.RecyclerUtil;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.soul.soul.R;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.ParameterizedType;
import java.util.List;

/**
 * Created by lw on 2017/2/20.
 * 要使用自动填充数据，自动绑定view功能，则必须让ViewHolder的字段名，javabean中的字段名，布局文件中的id一样
 * android:id="@+id/image" <n/> public TextView title;<n/>private String title;
 * 缺乏灵活性
 */

public class TestRecyclerAdapter<T extends RecyclerView.ViewHolder,V> extends RecyclerView.Adapter<T> {

    private LayoutInflater mInflater;
    private int mChildView;
    private List<V> mData;

    public TestRecyclerAdapter(Context mContext, int mChildView, List<V> mData){
        this.mInflater=LayoutInflater.from(mContext);
        this.mChildView=mChildView;
        this.mData=mData;
    }
    //创建view
    @Override
    public T onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = mInflater.inflate(mChildView, parent, false);
        return getInstanceOfH(view);
    }

    //通过反射泛型，生成指定的ViewHolder，注意反射出来的ViewHolder需要使用指定的构造函数
    private T getInstanceOfH(View view) {
        //反射泛型
        ParameterizedType superClass = (ParameterizedType) getClass().getGenericSuperclass();
        //注意，此处的1表示泛型数组中的第二个参数。
        Class<T> type = (Class<T>) superClass.getActualTypeArguments()[1];
        try {
            //获取ViewHolder的构造方法
            Constructor<T> constructor = type.getConstructor(View.class);
            //创建ViewHolder
            return constructor.newInstance(view);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    //绑定数据
    @Override
    public void onBindViewHolder(T holder, int position) {
        V itemData=mData.get(position);
        bindData(itemData, holder);
    }
    public void bindData(V v, T t) {
        setDataToHolder(v, t);
    }
    private void setDataToHolder(V v, T t) {
        //获取ViewHolder中的字段
        Field[] fields = t.getClass().getDeclaredFields();
        Object o ;
        //遍历字段
        for (Field f : fields) {
            f.setAccessible(true);
            o = null;
            try {
                o = f.get(t);
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            }
            //判断字段类型
            if (o instanceof TextView) {
                TextView tv = (TextView) o;
                //通过字段名，获取javaBean中的数据，前提是javabean中的名字与ViewHolder中的名称一样
                Object value = getValueFromFiled(v, f.getName());
                if (value instanceof CharSequence) {
                    tv.setText((CharSequence) value);
                }
            } else if (o instanceof ImageView) {
                ImageView iv = (ImageView) o;
                Object value = getValueFromFiled(v, f.getName());
                if (value instanceof String) {
                    String url = (String) value;
                    //Glide.with().load(url).placeholder(R.drawable.class).into(iv);
                }
            }
        }
    }
    public Object getValueFromFiled(V v, String name) {
        try {
            Field hf =v.getClass().getDeclaredField(name);
            hf.setAccessible(true);
            return hf.get(v);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    //定义当前item的类型
    @Override
    public int getItemViewType(int position) {
        return super.getItemViewType(position);
    }

    //当前item被回收时调用，可用来释放绑定在view上的大数据，比方说Bitmap
    @Override
    public void onViewRecycled(T holder) {
        super.onViewRecycled(holder);
    }

    //header和footer不应该在此数量里
    @Override
    public int getItemCount() {
        return mData.size();
    }
}
