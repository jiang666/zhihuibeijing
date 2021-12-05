package com.itcast.googleplayteach.ui.holder;

import android.view.View;
import android.widget.TextView;

import com.itcast.googleplayteach.R;
import com.itcast.googleplayteach.domain.CategoryInfo;
import com.itcast.googleplayteach.utils.UIUtils;

/**
 * 分类页标题栏holder
 * 
 * @author Kevin
 */
public class TitleHolder extends BaseHolder<CategoryInfo> {

	private TextView tvTitle;

	@Override
	public View initView() {
		View view = View.inflate(UIUtils.getContext(),
				R.layout.list_item_title, null);
		tvTitle = (TextView) view.findViewById(R.id.tv_title);
		return view;
	}

	@Override
	public void refreshView(CategoryInfo data) {
		tvTitle.setText(data.title);
	}

}
