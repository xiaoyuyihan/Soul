package com.example.soul.PostsWriteActivity;

import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.example.soul.soul.R;
import com.example.widget.FlowLayout;
import com.yalantis.contextmenu.lib.ContextMenuDialogFragment;
import com.yalantis.contextmenu.lib.MenuObject;
import com.yalantis.contextmenu.lib.MenuParams;
import com.yalantis.contextmenu.lib.interfaces.OnMenuItemClickListener;
import com.yalantis.contextmenu.lib.interfaces.OnMenuItemLongClickListener;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by lw on 2017/4/1.
 */

public class PostsWriteActivity extends AppCompatActivity implements OnMenuItemClickListener,OnMenuItemLongClickListener,NewTypeFragmentDialog.DialogBottomOnclick{
    private FragmentManager fragmentManager;
    private CustomizeContextMenuDialogFragment mMenuDialogFragment;
    private Toolbar mToolbar;
    private Drawable mBackground;
    private RelativeLayout.LayoutParams mLayoutParams;
    private View.OnClickListener mAllFlowClick;
    private FlowLayout mTypeFlowLayout;
    private NewTypeFragmentDialog newTypeFragmentDialog;

    private int writeFlag;
    public static final String WRITE_FLAG="PostsWriteActivity";

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().requestFeature(Window.FEATURE_NO_TITLE);
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            Window window = getWindow();
            window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS
                    | WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION);
            window.getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                    | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                    | View.SYSTEM_UI_FLAG_LAYOUT_STABLE);
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
        }
        setContentView(R.layout.activity_posts_write);
        mLayoutParams=new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT,
                ViewGroup.LayoutParams.WRAP_CONTENT);
        mLayoutParams.setMargins(2,2,2,2);
        mBackground=getResources().getDrawable(R.drawable.shape_background_gray_round_textview);
        mAllFlowClick=new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ((ViewGroup)v.getParent()).removeView(v);
                v.invalidate();
            }
        };
        mTypeFlowLayout=(FlowLayout)findViewById(R.id.posts_write_type);
        writeFlag=getIntent().getExtras().getInt(PostsWriteActivity.WRITE_FLAG);
        fragmentManager = getSupportFragmentManager();
        initToolbar();
        initMenuFragment();
    }

    private void initToolbar() {
        mToolbar = (Toolbar) findViewById(R.id.toolbar_top);
        TextView mToolBarTextView = (TextView) findViewById(R.id.text_view_toolbar_title);
        setSupportActionBar(mToolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setHomeButtonEnabled(true);
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowTitleEnabled(false);
        }
        mToolbar.setNavigationIcon(R.drawable.btn_back);
        mToolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();//回退键
            }
        });
        mToolBarTextView.setText("发布");
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    protected void onStart() {
        super.onStart();
        //进入时类型选择
        newTypeFragmentDialog=NewTypeFragmentDialog.newInstance(writeFlag);
        newTypeFragmentDialog.show(fragmentManager,"NewTypeFragmentDialog");
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.context_menu:
                int height=mToolbar.getHeight();
                mMenuDialogFragment.setViewYPadding(height);
                if (fragmentManager.findFragmentByTag(ContextMenuDialogFragment.TAG) == null) {
                    mMenuDialogFragment.show(fragmentManager, ContextMenuDialogFragment.TAG);
                }
                break;
        }
        return super.onOptionsItemSelected(item);
    }



    private void initMenuFragment() {
        MenuParams menuParams = new MenuParams();
        menuParams.setActionBarSize((int) getResources().getDimension(R.dimen.tool_bar_height));
        menuParams.setMenuObjects(getMenuObjects());
        menuParams.setClosableOutside(false);
        mMenuDialogFragment = CustomizeContextMenuDialogFragment.newInstance(menuParams);
        mMenuDialogFragment.setItemClickListener(this);
        mMenuDialogFragment.setItemLongClickListener(this);
    }
    private List<MenuObject> getMenuObjects() {
        // You can use any [resource, bitmap, drawable, color] as image:
        // item.setResource(...)
        // item.setBitmap(...)
        // item.setDrawable(...)
        // item.setColor(...)
        // You can set image ScaleType:
        // item.setScaleType(ScaleType.FIT_XY)
        // You can use any [resource, drawable, color] as background:
        // item.setBgResource(...)
        // item.setBgDrawable(...)
        // item.setBgColor(...)
        // You can use any [color] as text color:
        // item.setTextColor(...)
        // You can set any [color] as divider color:
        // item.setDividerColor(...)

        List<MenuObject> menuObjects = new ArrayList<>();

        MenuObject close = new MenuObject();
        close.setResource(R.drawable.image_btn_close);

        MenuObject send = new MenuObject("选择下划线颜色");
        send.setResource(R.drawable.image_btn_color);

        MenuObject like = new MenuObject("双击拍照，单击选择照片");
        like.setResource(R.drawable.image_btn_photo);

        MenuObject addFr = new MenuObject("Add to friends");
        BitmapDrawable bd = new BitmapDrawable(getResources(),
                BitmapFactory.decodeResource(getResources(), R.drawable.image_btn_voice));
        addFr.setDrawable(bd);

        MenuObject addFav = new MenuObject("Add to favorites");
        addFav.setResource(R.drawable.image_btn_video);

        menuObjects.add(close);
        menuObjects.add(send);
        menuObjects.add(like);
        menuObjects.add(addFr);
        menuObjects.add(addFav);
        return menuObjects;
    }

    @Override
    public void onMenuItemClick(View clickedView, int position) {

    }

    @Override
    public void onMenuItemLongClick(View clickedView, int position) {

    }

    @Override
    public void onDismissButton() {
        onBackPressed();
    }

    @Override
    public void onCommitButton(String[] typeNames) {
        newTypeFragmentDialog.dismiss();
        for (String typeName:typeNames){
            if (typeName!=null)
                createTextView(mTypeFlowLayout,typeName,typeNames.length);
        }
        mTypeFlowLayout.invalidate();
    }
    private void createTextView(ViewGroup fatherView, String name, int size) {
        TextView mTextView=new TextView(this);
        mTextView.setTag(size);
        mTextView.setLayoutParams(mLayoutParams);
        mTextView.setTextSize(10);
        mTextView.setPadding(6,6,6,6);
        mTextView.setBackground(mBackground);
        mTextView.setText(name);
        mTextView.setOnClickListener(mAllFlowClick);
        fatherView.addView(mTextView);
    }
}
