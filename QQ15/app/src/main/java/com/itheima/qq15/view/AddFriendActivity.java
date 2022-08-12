package com.itheima.qq15.view;

import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.ImageView;

import com.itheima.qq15.R;
import com.itheima.qq15.adapter.AddFriendAdapter;
import com.itheima.qq15.model.User;
import com.itheima.qq15.presenter.IAddFriendPresenter;
import com.itheima.qq15.presenter.impl.AddFriendPresenter;

import java.util.List;

import butterknife.ButterKnife;
import butterknife.InjectView;

public class AddFriendActivity extends BaseActivity implements SearchView.OnQueryTextListener ,AddFriendView, AddFriendAdapter.OnAddFriendClickListener {

    @InjectView(R.id.toolbar)
    Toolbar mToolbar;
    @InjectView(R.id.iv_nodata)
    ImageView mIvNodata;
    @InjectView(R.id.recyclerView)
    RecyclerView mRecyclerView;

    private IAddFriendPresenter mAddFriendPresenter;
    private SearchView mSearchView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_friend);
        ButterKnife.inject(this);
        mToolbar.setTitle("搜好友");
        setSupportActionBar(mToolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        mAddFriendPresenter = new AddFriendPresenter(this);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.add_friend_menu,menu);
        /**
         * 初始化菜单中的SearchView
         */
        MenuItem menuItem = menu.findItem(R.id.search);
        mSearchView = (SearchView) menuItem.getActionView();
        /**
         * 在SearchView中添加提示
         */
        mSearchView.setQueryHint("搜好友");
        mSearchView.setOnQueryTextListener(this);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                break;
        }
        return true;
    }

    @Override
    public boolean onQueryTextSubmit(String query) {
        if (TextUtils.isEmpty(query)){
            showToast("请输入用户名再搜索！");
            return false;
        }
        mAddFriendPresenter.searchFriend(query);
        //隐藏软键盘
        InputMethodManager inputMethodManager = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
        inputMethodManager.hideSoftInputFromWindow(mSearchView.getWindowToken(),0);
        return true;
    }

    @Override
    public boolean onQueryTextChange(String newText) {
        if (!TextUtils.isEmpty(newText )){
            showToast(newText);
        }
        return true;
    }

    @Override
    public void onSearchResult(List<User> userList,List<String> contactsList, boolean success, String msg) {
        if (success){
            mIvNodata.setVisibility(View.GONE);
            mRecyclerView.setVisibility(View.VISIBLE);
            mRecyclerView.setLayoutManager(new LinearLayoutManager(this));
            AddFriendAdapter addFriendAdapter = new AddFriendAdapter(userList, contactsList);
            addFriendAdapter.setOnAddFriendClickListener(this);
            mRecyclerView.setAdapter(addFriendAdapter);
        }else {
            mIvNodata.setVisibility(View.VISIBLE);
            mRecyclerView.setVisibility(View.GONE);
        }
    }

    @Override
    public void onAddResult(String username, boolean success, String msg) {
        Snackbar.make(mToolbar,"添加好友"+username+(success?"成功":"失败:"+msg),Snackbar.LENGTH_LONG).show();
    }

    @Override
    public void onAddClick(String username) {
        mAddFriendPresenter.addFriend(username);
    }
}
