package com.itcast.googleplayteach.ui.fragment;

import java.util.ArrayList;

import android.view.View;

import com.itcast.googleplayteach.domain.SubjectInfo;
import com.itcast.googleplayteach.http.protocol.SubjectProtocol;
import com.itcast.googleplayteach.ui.adapter.MyBaseAdapter;
import com.itcast.googleplayteach.ui.holder.BaseHolder;
import com.itcast.googleplayteach.ui.holder.SubjectHolder;
import com.itcast.googleplayteach.ui.widget.MyListView;
import com.itcast.googleplayteach.ui.widget.LoadingPage.ResultState;
import com.itcast.googleplayteach.utils.UIUtils;

/**
 * 专题
 * 
 * @author Kevin
 * 
 */
public class SubjectFragment extends BaseFragment {

	private ArrayList<SubjectInfo> mList;

	@Override
	public View onCreateSuccessView() {
		MyListView view = new MyListView(UIUtils.getContext());
		view.setAdapter(new SubjectAdapter(mList));
		return view;
	}

	@Override
	public ResultState onLoad() {
		SubjectProtocol protocol = new SubjectProtocol();
		mList = protocol.getData(0);
		return check(mList);
	}

	class SubjectAdapter extends MyBaseAdapter<SubjectInfo> {

		public SubjectAdapter(ArrayList<SubjectInfo> list) {
			super(list);
		}

		@Override
		public BaseHolder<SubjectInfo> getHolder(int position) {
			return new SubjectHolder();
		}

		@Override
		public ArrayList<SubjectInfo> onLoadMore() {
			SubjectProtocol protocol = new SubjectProtocol();
			ArrayList<SubjectInfo> moreData = protocol.getData(getListSize());
			return moreData;
		}
	}

}
