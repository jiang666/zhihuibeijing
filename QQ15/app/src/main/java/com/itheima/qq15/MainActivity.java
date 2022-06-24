package com.itheima.qq15;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.view.menu.MenuBuilder;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;

import com.ashokvarma.bottomnavigation.BadgeItem;
import com.ashokvarma.bottomnavigation.BottomNavigationBar;
import com.ashokvarma.bottomnavigation.BottomNavigationItem;
import com.hyphenate.chat.EMClient;
import com.hyphenate.chat.EMMessage;
import com.itheima.qq15.utils.FragmentFactory;
import com.itheima.qq15.view.AddFriendActivity;
import com.itheima.qq15.view.BaseActivity;
import com.itheima.qq15.view.BaseFragment;
import com.itheima.qq15.view.ConversationFragment;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import butterknife.ButterKnife;
import butterknife.InjectView;

import static com.hyphenate.chat.a.a.a.i;

public class MainActivity extends BaseActivity implements BottomNavigationBar.OnTabSelectedListener {

    private static final String TAG = "MainActivity";
    @InjectView(R.id.toolbar)
    Toolbar mToolbar;
    @InjectView(R.id.bottom_navigation_bar)
    BottomNavigationBar mBottomNavigationBar;
    @InjectView(R.id.tv_title)
    TextView tvTitle;

    private int[] titleIds = {R.string.conversation,R.string.contact,R.string.plugin};
    private BadgeItem mBadgeItem;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.inject(this);
        initToolbar();
        initBottomNavigation();
        initFirstFragment();
        EventBus.getDefault().register(this);
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onEvent(EMMessage message){
        updateUnreadCount();
    }

    @Override
    protected void onResume() {
        super.onResume();
        updateUnreadCount();
    }

    public void updateUnreadCount() {
        //获取所有的未读消息
        int unreadMsgsCount = EMClient.getInstance().chatManager().getUnreadMsgsCount();
        if (unreadMsgsCount>99){
            mBadgeItem.setText("99+");
            mBadgeItem.show(true);
        }else if (unreadMsgsCount>0){
            mBadgeItem.setText(unreadMsgsCount+"");
            mBadgeItem.show(true);
        }else{
            mBadgeItem.hide(true);
        }

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);
    }

    private void initToolbar() {
        mToolbar.setTitle("");
        setSupportActionBar(mToolbar);
        tvTitle.setText(titleIds[0]);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }

    private void initBottomNavigation() {
        BottomNavigationItem conversationItem = new BottomNavigationItem(R.mipmap.conversation_selected_2,"消息");
        mBadgeItem = new BadgeItem();
        mBadgeItem.setGravity(Gravity.RIGHT);
        mBadgeItem.setTextColor("#ffffff");
        mBadgeItem.setBackgroundColor("#ff0000");
        mBadgeItem.setText("5");
        mBadgeItem.show();

        conversationItem.setBadgeItem(mBadgeItem);

//        conversationItem.setActiveColor(getResources().getColor(R.color.btn_normal));//选中的颜色
//        conversationItem.setInActiveColor(getColoretResources().getColor(R.color.inActive));//没选中的颜色
        mBottomNavigationBar.addItem(conversationItem);

        BottomNavigationItem contactItem = new BottomNavigationItem(R.mipmap.contact_selected_2,"联系人");
//        contactItem.setActiveColor(getResources().getColor(R.color.btn_normal));//选中的颜色
//        contactItem.setInActiveColor(getResources().getColor(R.color.inActive));//没选中的颜色
        mBottomNavigationBar.addItem(contactItem);

        BottomNavigationItem pluginItem = new BottomNavigationItem(R.mipmap.plugin_selected_2,"动态");
//        pluginItem.setActiveColor(getResources().getColor(R.color.btn_normal));//选中的颜色
//        pluginItem.setInActiveColor(getResources().getColor(R.color.inActive));//没选中的颜色
        mBottomNavigationBar.addItem(pluginItem);

        mBottomNavigationBar.setActiveColor(R.color.btn_normal);
        mBottomNavigationBar.setInActiveColor(R.color.inActive);

        mBottomNavigationBar.initialise();

        mBottomNavigationBar.setTabSelectedListener(this);
    }

    private void initFirstFragment() {
        /**
         * 如果这个Activity中已经有老（就是Activity保存的历史的状态，又恢复了）的Fragment，先全部移除
         */
        FragmentManager supportFragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = supportFragmentManager.beginTransaction();
        for(int i=0;i<titleIds.length;i++){
            Fragment fragment = supportFragmentManager.findFragmentByTag(i + "");
            if (fragment!=null){
                fragmentTransaction.remove(fragment);
            }
        }
        fragmentTransaction.commit();

        getSupportFragmentManager().beginTransaction().add(R.id.fl_content, FragmentFactory.getFragment(0),"0").commit();

        tvTitle.setText(R.string.conversation);
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main,menu);
        return true;
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        MenuBuilder builder = (MenuBuilder) menu;
        builder.setOptionalIconsVisible(true);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.menu_add_friend:
               startActivity(AddFriendActivity.class,false);
                break;
            case R.id.menu_scan:
                showToast("分享好友");

                break;
            case R.id.menu_about:
                showToast("关于我们");
                break;
            case android.R.id.home:
                finish();
                break;

        }
        return true;
    }

    @Override
    public void onTabSelected(int position) {
        /**
         * 先判断当前Fragment是否被添加到了MainActivity中
         * 如果添加了则直接显示即可
         * 如果没有添加则添加，然后显示
         */
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        BaseFragment fragment = FragmentFactory.getFragment(position);
        if (!fragment.isAdded()){
            transaction.add(R.id.fl_content,fragment,""+position);
        }
        transaction.show(fragment).commit();

        tvTitle.setText(titleIds[position]);
    }

    @Override
    public void onTabUnselected(int position) {
        getSupportFragmentManager().beginTransaction().hide(FragmentFactory.getFragment(position)).commit();
    }

    @Override
    public void onTabReselected(int position) {
    }
}
