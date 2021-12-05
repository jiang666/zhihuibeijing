package com.itcast.googleplayteach.ui.fragment;

import java.util.ArrayList;

import android.content.Intent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;

import com.itcast.googleplayteach.domain.AppInfo;
import com.itcast.googleplayteach.http.protocol.HomeProtocol;
import com.itcast.googleplayteach.ui.activity.HomeDetailActivity;
import com.itcast.googleplayteach.ui.adapter.MyBaseAdapter;
import com.itcast.googleplayteach.ui.holder.BaseHolder;
import com.itcast.googleplayteach.ui.holder.HomeHeaderHolder;
import com.itcast.googleplayteach.ui.holder.HomeHolder;
import com.itcast.googleplayteach.ui.widget.MyListView;
import com.itcast.googleplayteach.ui.widget.LoadingPage.ResultState;
import com.itcast.googleplayteach.utils.UIUtils;

/**
 * 首页
 * 
 * @author Kevin
 * 
 */
public class HomeFragment extends BaseFragment {

	private ArrayList<AppInfo> mList = null;
	private ArrayList<String> mPicList;// 广告条图片url集合

	@Override
	public View onCreateSuccessView() {
		MyListView view = new MyListView(UIUtils.getContext());

		// 添加头布局
		HomeHeaderHolder header = new HomeHeaderHolder();
		view.addHeaderView(header.getRootView());
		// 设置头布局数据
		header.setData(mPicList);

		view.setAdapter(new HomeAdapter(mList));
		view.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				//跳转到首页详情页面
				AppInfo appInfo = mList.get(position - 1);
				Intent intent = new Intent(UIUtils.getContext(),
						HomeDetailActivity.class);
				//将包名传递到详情页
				intent.putExtra("package", appInfo.packageName);
				startActivity(intent);
			}
		});
		return view;
	}

	@Override
	public ResultState onLoad() {
		// 从网络加载数据
		HomeProtocol protocol = new HomeProtocol();
		mList = protocol.getData(0);// 加载第一页数据
		mPicList = protocol.getPicList();

		return check(mList);
	}

	class HomeAdapter extends MyBaseAdapter<AppInfo> {

		public HomeAdapter(ArrayList<AppInfo> list) {
			super(list);
		}

		@Override
		public BaseHolder<AppInfo> getHolder(int position) {
			return new HomeHolder();
		}

		@Override
		public ArrayList<AppInfo> onLoadMore() {
			// 从网络加载数据
			HomeProtocol protocol = new HomeProtocol();
			ArrayList<AppInfo> moreData = protocol.getData(getListSize());// 加载下一页数据
			return moreData;
		}
	}
}
