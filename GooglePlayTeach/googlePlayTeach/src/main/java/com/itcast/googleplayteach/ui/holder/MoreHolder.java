package com.itcast.googleplayteach.ui.holder;

import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.itcast.googleplayteach.R;
import com.itcast.googleplayteach.utils.UIUtils;
/**
 * MoreHolder的数据类型为整数,用来表示加载的几种状态
 *
 */
public class MoreHolder extends BaseHolder<Integer> {

	public static final int TYPE_HAS_MORE = 0;// 可以加载更多
	public static final int TYPE_NO_MORE = 1;// 不能加载更多
	public static final int TYPE_LOAD_ERROR = 2;// 加载更多失败

	private LinearLayout llLoadingMore;
	private TextView tvLoadError;

	public MoreHolder(boolean hasMore) {
		// 将加载类型以数据的方式设置进去
		setData(hasMore ? TYPE_HAS_MORE : TYPE_NO_MORE);
	}

	@Override
	public View initView() {
		View view = View.inflate(UIUtils.getContext(),
				R.layout.layout_loading_more, null);
		llLoadingMore = (LinearLayout) view.findViewById(R.id.ll_loading_more);
		tvLoadError = (TextView) view.findViewById(R.id.tv_load_error);
		return view;
	}

	@Override
	public void refreshView(Integer data) {
		// 根据当前状态,来更新界面展示
		switch (data) {
		case TYPE_HAS_MORE:
			llLoadingMore.setVisibility(View.VISIBLE);
			tvLoadError.setVisibility(View.GONE);
			break;
		case TYPE_NO_MORE:
			llLoadingMore.setVisibility(View.GONE);
			tvLoadError.setVisibility(View.GONE);
			break;
		case TYPE_LOAD_ERROR:
			llLoadingMore.setVisibility(View.GONE);
			tvLoadError.setVisibility(View.VISIBLE);
			break;
		}
	}
}
