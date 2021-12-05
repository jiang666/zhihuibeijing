package com.itcast.googleplayteach.ui.fragment;

import java.util.ArrayList;

import android.view.View;

import com.itcast.googleplayteach.domain.AppInfo;
import com.itcast.googleplayteach.http.protocol.AppProtocol;
import com.itcast.googleplayteach.ui.adapter.MyBaseAdapter;
import com.itcast.googleplayteach.ui.holder.AppHolder;
import com.itcast.googleplayteach.ui.holder.BaseHolder;
import com.itcast.googleplayteach.ui.widget.MyListView;
import com.itcast.googleplayteach.ui.widget.LoadingPage.ResultState;
import com.itcast.googleplayteach.utils.UIUtils;

/**
 * 应用
 * 
 * @author Kevin
 * 
 */
public class AppFragment extends BaseFragment {

	ArrayList<AppInfo> mList = null;

	@Override
	public View onCreateSuccessView() {
		MyListView view = new MyListView(UIUtils.getContext());
		view.setAdapter(new AppAdapter(mList));
		return view;
	}

	@Override
	public ResultState onLoad() {
		AppProtocol protocol = new AppProtocol();
		mList = protocol.getData(0);
		return check(mList);
	}

	class AppAdapter extends MyBaseAdapter<AppInfo> {

		public AppAdapter(ArrayList<AppInfo> list) {
			super(list);
		}

		@Override
		public BaseHolder<AppInfo> getHolder(int position) {
			return new AppHolder();
		}

		@Override
		public ArrayList<AppInfo> onLoadMore() {
			AppProtocol protocol = new AppProtocol();
			ArrayList<AppInfo> moreData = protocol.getData(getListSize());
			return moreData;
		}

	}

}
