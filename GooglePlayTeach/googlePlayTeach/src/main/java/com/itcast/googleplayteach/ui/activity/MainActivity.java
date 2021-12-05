package com.itcast.googleplayteach.ui.activity;

import android.os.Bundle;
import android.support.v4.app.ActionBarDrawerToggle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.view.MenuItem;

import com.itcast.googleplayteach.R;
import com.itcast.googleplayteach.ui.fragment.BaseFragment;
import com.itcast.googleplayteach.ui.fragment.FragmentFactory;
import com.itcast.googleplayteach.ui.widget.PagerTab;
import com.itcast.googleplayteach.utils.UIUtils;

public class MainActivity extends BaseActivity {

	private PagerTab mPagerTab;
	private ViewPager mViewPager;
	private DrawerLayout mDrawerLayout;// 侧边栏布局
	private ActionBarDrawerToggle mToggle;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

		mPagerTab = (PagerTab) findViewById(R.id.pager_tab);
		mViewPager = (ViewPager) findViewById(R.id.vp_pager);

		mViewPager.setAdapter(new MyPagerAdapter(getSupportFragmentManager()));
		mPagerTab.setViewPager(mViewPager);

		mPagerTab.setOnPageChangeListener(new OnPageChangeListener() {

			@Override
			public void onPageSelected(int position) {
				BaseFragment fragment = FragmentFactory
						.createFragment(position);
				fragment.loadData();
			}

			@Override
			public void onPageScrolled(int arg0, float arg1, int arg2) {
			}

			@Override
			public void onPageScrollStateChanged(int arg0) {
			}
		});

		mDrawerLayout = (DrawerLayout) findViewById(R.id.drawerlayout);
		initActionBar();
	}

	/**
	 * 初始化actionbar
	 */
	private void initActionBar() {
		// 获取actionbar对象
		ActionBar actionBar = getSupportActionBar();
		// 左上角显示logo
		actionBar.setHomeButtonEnabled(true);
		// 左上角显示返回图标, 和侧边栏绑定后显示侧边栏图标
		actionBar.setDisplayHomeAsUpEnabled(true);

		// 初始化侧边栏开关
		mToggle = new ActionBarDrawerToggle(this, mDrawerLayout,
				R.drawable.ic_drawer_am, R.string.drawer_open,
				R.string.drawer_close);// 参2:DrawerLayout对象, 参3:侧边栏开关图标,
										// 参4:打开侧边栏文本描述;参5:关闭侧边栏文本描述
		// 调用当前同步方法，才可以将侧拉菜单和按钮的点击操作绑定起来
		mToggle.syncState();
	}

	// ActionBar上的按钮被点击后的回调方法
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case android.R.id.home:// 左上角logo处被点击
			mToggle.onOptionsItemSelected(item);//侧边栏收起或者关闭
			break;

		default:
			break;
		}

		return super.onOptionsItemSelected(item);
	}

	class MyPagerAdapter extends FragmentPagerAdapter {

		private String[] mTabNames;// 页签名称集合

		public MyPagerAdapter(FragmentManager fm) {
			super(fm);
			mTabNames = UIUtils.getStringArray(R.array.tab_names);
		}

		// 加载每个页签标题
		@Override
		public CharSequence getPageTitle(int position) {
			return mTabNames[position];
		}

		// 返回Fragment对象,将onCreateView方法的返回view填充给ViewPager
		// 此方法类似instantiateItem
		@Override
		public Fragment getItem(int position) {
			// 从工厂类中生产Fragment并返回
			return FragmentFactory.createFragment(position);
		}

		// 返回item个数
		@Override
		public int getCount() {
			return mTabNames.length;
		}
	}
}
