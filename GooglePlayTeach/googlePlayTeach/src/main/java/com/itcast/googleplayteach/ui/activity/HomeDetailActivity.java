package com.itcast.googleplayteach.ui.activity;

import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.view.MenuItem;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.HorizontalScrollView;

import com.itcast.googleplayteach.R;
import com.itcast.googleplayteach.domain.AppInfo;
import com.itcast.googleplayteach.http.protocol.HomeDetailProtocol;
import com.itcast.googleplayteach.ui.holder.DetailAppDesHolder;
import com.itcast.googleplayteach.ui.holder.DetailAppInfoHolder;
import com.itcast.googleplayteach.ui.holder.DetailAppPicsHolder;
import com.itcast.googleplayteach.ui.holder.DetailAppSafeHolder;
import com.itcast.googleplayteach.ui.holder.DetailDownloadHolder;
import com.itcast.googleplayteach.ui.widget.LoadingPage;
import com.itcast.googleplayteach.ui.widget.LoadingPage.ResultState;
import com.itcast.googleplayteach.utils.UIUtils;

/**
 * 应用详情页
 * 
 * @author Kevin
 */
public class HomeDetailActivity extends BaseActivity {

	private LoadingPage mLoadingPage;
	private String mPackageName;
	private AppInfo mData;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		// 初始化加载页面
		mLoadingPage = new LoadingPage(UIUtils.getContext()) {

			@Override
			public View onCreateSuccessView() {
				return HomeDetailActivity.this.onCreateSuccessView();
			}

			@Override
			public ResultState onLoad() {
				return HomeDetailActivity.this.onLoad();
			}

		};

		setContentView(mLoadingPage);
		mPackageName = getIntent().getStringExtra("package");
		// 开始加载数据
		mLoadingPage.loadData();

		initActionBar();
	}

	/**
	 * 访问网络数据成功后初始化成功页面
	 * 
	 * @return
	 */
	public View onCreateSuccessView() {
		View view = View.inflate(this, R.layout.layout_home_detail, null);

		// 初始化应用详情信息
		FrameLayout flDetailAppInfo = (FrameLayout) view
				.findViewById(R.id.fl_detail_appinfo);
		DetailAppInfoHolder appInfoHolder = new DetailAppInfoHolder();
		flDetailAppInfo.addView(appInfoHolder.getRootView());
		appInfoHolder.setData(mData);

		// 初始化安全相关信息
		FrameLayout flDetailSafeInfo = (FrameLayout) view
				.findViewById(R.id.fl_detail_safeinfo);
		DetailAppSafeHolder appSafeHolder = new DetailAppSafeHolder();
		flDetailSafeInfo.addView(appSafeHolder.getRootView());
		appSafeHolder.setData(mData);

		// 初始化图片信息
		HorizontalScrollView hsvDetailPics = (HorizontalScrollView) view
				.findViewById(R.id.hsv_detail_pics);
		DetailAppPicsHolder appPicsHolder = new DetailAppPicsHolder();
		hsvDetailPics.addView(appPicsHolder.getRootView());
		appPicsHolder.setData(mData);

		// 初始化描述信息
		FrameLayout flDetailDesInfo = (FrameLayout) view
				.findViewById(R.id.fl_detail_des);
		DetailAppDesHolder appDesHolder = new DetailAppDesHolder();
		flDetailDesInfo.addView(appDesHolder.getRootView());
		appDesHolder.setData(mData);

		// 初始化下载布局
		FrameLayout flDetailDownload = (FrameLayout) view
				.findViewById(R.id.fl_detail_downlod);
		DetailDownloadHolder downloadHolder = new DetailDownloadHolder();
		flDetailDownload.addView(downloadHolder.getRootView());
		downloadHolder.setData(mData);

		return view;
	}

	/**
	 * 初始化actionbar
	 */
	private void initActionBar() {
		// 获取actionbar对象
		ActionBar actionBar = getSupportActionBar();
		// 左上角显示logo
		actionBar.setHomeButtonEnabled(true);
		// 左上角显示返回图标
		actionBar.setDisplayHomeAsUpEnabled(true);
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case android.R.id.home:// 左上角logo处被点击
			finish();
			break;

		default:
			break;
		}

		return super.onOptionsItemSelected(item);
	}

	/**
	 * 加载网络数据
	 * 
	 * @return
	 */
	public ResultState onLoad() {
		HomeDetailProtocol protocol = new HomeDetailProtocol(mPackageName);
		mData = protocol.getData(0);
		if (mData != null) {
			return ResultState.STATE_SUCCESS;
		} else {
			return ResultState.STATE_ERROR;
		}
	}
}
