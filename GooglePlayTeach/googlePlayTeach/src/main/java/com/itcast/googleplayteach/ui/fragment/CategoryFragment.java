package com.itcast.googleplayteach.ui.fragment;

import java.util.ArrayList;

import android.view.View;

import com.itcast.googleplayteach.domain.CategoryInfo;
import com.itcast.googleplayteach.http.protocol.CategoryProtocol;
import com.itcast.googleplayteach.ui.adapter.MyBaseAdapter;
import com.itcast.googleplayteach.ui.holder.BaseHolder;
import com.itcast.googleplayteach.ui.holder.CategoryHolder;
import com.itcast.googleplayteach.ui.holder.TitleHolder;
import com.itcast.googleplayteach.ui.widget.MyListView;
import com.itcast.googleplayteach.ui.widget.LoadingPage.ResultState;
import com.itcast.googleplayteach.utils.UIUtils;

/**
 * 分类
 * 
 * @author Kevin
 * 
 */
public class CategoryFragment extends BaseFragment {

	private ArrayList<CategoryInfo> mList;

	@Override
	public View onCreateSuccessView() {
		MyListView view = new MyListView(UIUtils.getContext());
		view.setAdapter(new CategoryAdapter(mList));
		return view;
	}

	@Override
	public ResultState onLoad() {
		CategoryProtocol protocol = new CategoryProtocol();
		mList = protocol.getData(0);
		return check(mList);
	}

	class CategoryAdapter extends MyBaseAdapter<CategoryInfo> {

		public CategoryAdapter(ArrayList<CategoryInfo> list) {
			super(list);
		}

		// 根据当前位置,返回相应的Holder对象
		@Override
		public BaseHolder<CategoryInfo> getHolder(int position) {
			CategoryInfo info = getItem(position);
			if (info.isTitle) {
				return new TitleHolder();// 标题栏holder
			}
			return new CategoryHolder();// 普通类型holer
		}

		@Override
		public ArrayList<CategoryInfo> onLoadMore() {
			return null;
		}

		@Override
		public boolean hasMore() {
			return false;// 没有更多数据, 无需加载下一页
		}

		@Override
		public int getViewTypeCount() {
			return super.getViewTypeCount() + 1;// 在原来基础上,新增标题栏类型
		}

		// 根据位置判断当前item的类型
		@Override
		public int getInnerType(int position) {
			CategoryInfo info = getItem(position);
			if (info.isTitle) {// 标题栏类型
				return super.getInnerType(position) + 1;
			} else {// 普通类型
				return super.getInnerType(position);
			}
		}
	}

}
