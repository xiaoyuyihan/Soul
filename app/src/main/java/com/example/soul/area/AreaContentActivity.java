package com.example.soul.area;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.LoaderManager;
import android.content.Intent;
import android.content.Loader;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.widget.NestedScrollView;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.util.Log;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.example.base.mvp.MVPView;
import com.example.base.mvp.PresenterLoader;
import com.example.soul.PostsWriteActivity.PostsWriteActivity;
import com.example.soul.soul.R;
import com.example.soul.widget.RecyclerUtil.BaseRecyclerAdapter;
import com.example.soul.widget.RecyclerUtil.BaseViewHolder;
import com.example.soul.widget.RecyclerUtil.FocusResizeScrollListener;
import com.example.soul.widget.RecyclerUtil.SimpleItemTouchHelperCallback;

import org.videolan.vlc.PlaybackService;
import org.videolan.vlc.media.MediaUtils;

public class AreaContentActivity extends AppCompatActivity implements MVPView,LoaderManager.LoaderCallbacks<AreaPresenter>{

    private RecyclerView mAreaContent;
    private AreaPresenter mAreaPresenter;
    private BaseRecyclerAdapter mAdapter;
    private NestedScrollView mNestedScrollView;
    private ItemTouchHelper itemTouchHelper;


    @SuppressLint("NewApi")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_scrolling_area);

        //getLoaderManager().initLoader(0,null,this);
        mAreaPresenter=new AreaPresenter(this);
        mAreaPresenter.attachView(this);
        mAreaPresenter.onCreate(savedInstanceState);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent =new Intent(AreaContentActivity.this,PostsWriteActivity.class);
                startActivity(intent);
            }
        });

        mAreaContent=(RecyclerView)findViewById(R.id.area_content);
        mNestedScrollView=(NestedScrollView) findViewById(R.id.area_srollview);

        mAdapter=new BaseRecyclerAdapter(this,mAreaPresenter.getData()
                ,new int[]{R.layout.view_recycler_text_item,R.layout.view_recycler_image_item,R.layout.view_recycler_viedo_item});

        mAdapter.setRecyclerViewItemClickListener(new BaseRecyclerAdapter.OnRecyclerViewItemClickListener(){
            @Override
            public void onItemClick(RecyclerView.ViewHolder holder) {
                //mAreaPresenter.startPlayback(((VideoViewHolder)holder).getVideoView());
                playVideo(holder);
            }
            @Override
            public boolean onItemLongClick(View view) {
                return false;
            }
        });
        mAreaContent.setAdapter(mAdapter);
        //配置RecyclerView
        setRecyclerViewConfiguration();
    }

    /**
     * 先设置状态栏透明属性；
     * 给根布局加上一个和状态栏一样大小的矩形View（色块），添加到顶上；
     * 然后设置根布局的 FitsSystemWindows 属性为 true,此时根布局会延伸到状态栏，处在状态栏位置的就是之前添加的色块，这样就给状态栏设置上颜色了。
     * @param color
     * @return
     */
    private View createStatusView(int color) {
        int i;
        // 获得状态栏高度
        int resourceId = getResources().getIdentifier("status_bar_height", "dimen", "android");
        int statusBarHeight = getResources().getDimensionPixelSize(resourceId);

        // 绘制一个和状态栏一样高的矩形
        View statusView = new View(this);
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                statusBarHeight);
        statusView.setLayoutParams(params);
        statusView.setBackgroundColor(color);
        return statusView;
    }

    private void setRecyclerViewConfiguration() {
        //RecyclerView需要通过setLayoutManager()方法设置布局管理器，
        // RecyclerView有三个默认布局管理器LinearLayoutManager、GridLayoutManager、StaggeredGridLayoutManager，
        // 都支持横向和纵向排列以及反向滑动。
        LinearLayoutManager linearLayoutManager=new LinearLayoutManager(this);
        mAreaContent.setLayoutManager(linearLayoutManager);
        //设置禁止嵌套滑动,可以改变RecyclerView在5.0以上版本滑动不流畅,没有惯性效果
        mAreaContent.setNestedScrollingEnabled(false);
        mAreaContent.stopNestedScroll();
        //设置item加载或移除时的动画
        mAreaContent.setItemAnimator(new DefaultItemAnimator());

        /*
        //分割线
        mAreaContent.addItemDecoration(new DividerItemDecoration(
                AreaContentActivity.this, DividerItemDecoration.VERTICAL));
        */
        //转到哪个item
        //mAreaContent.smoothScrollToPosition(1);

        //recyclerview滚动监听
        mAreaContent.setHasFixedSize(true);
        FocusResizeScrollListener m=new FocusResizeScrollListener(linearLayoutManager,240);
        //mNestedScrollView.setOnScrollListener(m);
        mAreaContent.addOnScrollListener(m);
        SimpleItemTouchHelperCallback mCallback=new SimpleItemTouchHelperCallback<>(mAdapter,mAreaPresenter);
        mCallback.setSwipeState(new SimpleItemTouchHelperCallback.SwipeStateAlterHelper() {
            @Override
            public void onLeftMove(float moveX, RecyclerView.ViewHolder holder) {
                float width = (float) holder.itemView.getWidth();
                float contentAlpha = 1.0f - Math.abs(moveX) / width;
                if (contentAlpha<0.50f){
                    contentAlpha=0.5f;
                }
                ((BaseViewHolder)holder).getContent().setAlpha(contentAlpha);
                float alpha=Math.abs(moveX)/(holder.itemView.getWidth()/4);
                ((BaseViewHolder)holder).getDelete().setAlpha(alpha);
                holder.itemView.setTranslationX(moveX);
                float ScaleX=Math.abs(moveX)/(width/4);
                Log.d("ScaleL",""+ScaleX);
                ImageView mDelete=((BaseViewHolder) holder).getDelete();
                if (ScaleX>1){
                    mDelete.setScaleX(1);
                    mDelete.setScaleY(1);
                }else {
                    mDelete.setScaleX(ScaleX);
                    mDelete.setScaleY(ScaleX);
                    mDelete.setPivotX(mDelete.getWidth()-ScaleX);
                    mDelete.setPivotY(ScaleX);
                }
            }

            @Override
            public void onRightMove(float moveX, RecyclerView.ViewHolder holder) {
                float alpha=Math.abs(moveX)/(holder.itemView.getWidth()/4);
                ((BaseViewHolder)holder).getLike().setAlpha(alpha);
                float width = (float) holder.itemView.getWidth();
                float contentAlpha = 1.0f - Math.abs(moveX) / width;
                if (contentAlpha<.5f){
                    contentAlpha=.5f;
                }
                ((BaseViewHolder)holder).getContent().setAlpha(contentAlpha);
                Log.d("Move_moveX",moveX+"");
                if (Math.abs(moveX)>holder.itemView.getWidth()/4){
                    holder.itemView.setTranslationX(holder.itemView.getWidth()/4);
                }else {
                    holder.itemView.setTranslationX(moveX);
                }
                float ScaleX=Math.abs(moveX)/(width/4);
                Log.d("ScaleR",""+ScaleX);
                if (ScaleX>1){
                    ((BaseViewHolder) holder).getLike().setScaleX(1);
                    ((BaseViewHolder) holder).getLike().setScaleY(1);
                }else {
                    ((BaseViewHolder) holder).getLike().setScaleX(ScaleX);
                    ((BaseViewHolder) holder).getLike().setScaleY(ScaleX);
                    ((BaseViewHolder) holder).getLike().setPivotX(ScaleX);
                    ((BaseViewHolder) holder).getLike().setPivotY(ScaleX);
                }
            }

            @Override
            public void onMoveConsummation(RecyclerView.ViewHolder viewHolder,int direction) {
                if (direction==ItemTouchHelper.START){
                    int position = viewHolder.getAdapterPosition();
                    mAreaPresenter.removeData(position);
                    mAdapter.notifyItemRemoved(position);
                }else if (direction==ItemTouchHelper.END){
                    //viewHolder.itemView.scrollBy();
                    ((BaseViewHolder)viewHolder).getContent().setAlpha(1);
                    viewHolder.itemView.setTranslationX(0);
                    itemTouchHelper.startDrag(viewHolder);

                    mAreaPresenter.setDataLike(mAreaContent.getChildAdapterPosition(viewHolder.itemView));

                    //viewHolder.itemView.setScrollX(0);
                    //viewHolder.itemView.scrollBy((int)viewHolder.itemView.getWidth()/4,0);
                }
            }
        });
        itemTouchHelper=new ItemTouchHelper(mCallback);
        itemTouchHelper.attachToRecyclerView(mAreaContent);

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_scrolling, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public Loader<AreaPresenter> onCreateLoader(int i, Bundle bundle) {
        Log.d("AreaContent","onCreateLoader");
        return new PresenterLoader<>(this,this);
    }

    @Override
    public void onLoadFinished(Loader<AreaPresenter> loader, AreaPresenter areaPresenter) {
        Log.d("AreaContent","onLoadFinished");
        mAreaPresenter=areaPresenter;
    }

    private void playVideo(final RecyclerView.ViewHolder view) {

        new MediaUtils.DialogCallback(this, new MediaUtils.DialogCallback.Runnable() {
            @Override
            public void run(PlaybackService service) {
                service.loadLocation("http://flv2.bn.netease.com/videolib3/1702/27/LozpB9238/SD/LozpB9238-mobile.mp4");
            }
        }); /*
        mAreaPresenter.getPlaybackService().loadLocation("http://flv2.bn.netease.com/videolib3/1702/27/LozpB9238/SD/LozpB9238-mobile.mp4");
        //mAreaPresenter.stopPlayback();
        mAreaPresenter.startPlayback(((VideoViewHolder)view).getVideoView());
        mAreaPresenter.doPlayPause(mAreaContent);*/
    }
    @Override
    public void onLoaderReset(Loader<AreaPresenter> loader) {
        Log.d("AreaContent","onLoaderReset");
    mAreaPresenter=null;
    }

    @Override
    public void showMessage() {

    }

    @Override
    public void showDialog() {

    }

    @Override
    public void dismissDialog() {

    }

    @Override
    public View getContentView() {
        return mAreaContent;
    }

    @Override
    public Activity getActivity() {
        return this;
    }

    @Override
    public AreaPresenter getPresenterInstance() {
        return mAreaPresenter;
    }
}
