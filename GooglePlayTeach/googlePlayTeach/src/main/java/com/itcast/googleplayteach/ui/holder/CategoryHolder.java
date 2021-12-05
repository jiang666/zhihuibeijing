package com.itcast.googleplayteach.ui.holder;

import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.itcast.googleplayteach.R;
import com.itcast.googleplayteach.domain.CategoryInfo;
import com.itcast.googleplayteach.http.HttpHelper;
import com.itcast.googleplayteach.utils.BitmapHelper;
import com.itcast.googleplayteach.utils.UIUtils;
import com.lidroid.xutils.BitmapUtils;

/**
 * 分类holder
 * 
 * @author Kevin
 */
public class CategoryHolder extends BaseHolder<CategoryInfo> implements
		OnClickListener {

	private ImageView ivIcon1, ivIcon2, ivIcon3;
	private TextView tvName1, tvName2, tvName3;
	private LinearLayout llGrid1, llGrid2, llGrid3;

	private BitmapUtils mBitmapUtils;

	@Override
	public View initView() {
		View view = View.inflate(UIUtils.getContext(),
				R.layout.list_item_category, null);

		mBitmapUtils = BitmapHelper.getBitmapUtils();
		mBitmapUtils.configDefaultLoadingImage(R.drawable.ic_default);

		ivIcon1 = (ImageView) view.findViewById(R.id.iv_icon1);
		ivIcon2 = (ImageView) view.findViewById(R.id.iv_icon2);
		ivIcon3 = (ImageView) view.findViewById(R.id.iv_icon3);

		tvName1 = (TextView) view.findViewById(R.id.tv_name1);
		tvName2 = (TextView) view.findViewById(R.id.tv_name2);
		tvName3 = (TextView) view.findViewById(R.id.tv_name3);

		llGrid1 = (LinearLayout) view.findViewById(R.id.ll_grid1);
		llGrid2 = (LinearLayout) view.findViewById(R.id.ll_grid2);
		llGrid3 = (LinearLayout) view.findViewById(R.id.ll_grid3);
		llGrid1.setOnClickListener(this);
		llGrid2.setOnClickListener(this);
		llGrid3.setOnClickListener(this);

		return view;
	}

	@Override
	public void refreshView(CategoryInfo data) {
		if (data != null) {
			tvName1.setText(data.name1);
			tvName2.setText(data.name2);
			tvName3.setText(data.name3);

			mBitmapUtils.display(ivIcon1, HttpHelper.URL + "image?name="
					+ data.url1);
			mBitmapUtils.display(ivIcon2, HttpHelper.URL + "image?name="
					+ data.url2);
			mBitmapUtils.display(ivIcon3, HttpHelper.URL + "image?name="
					+ data.url3);
		}
	}

	@Override
	public void onClick(View v) {
		CategoryInfo data = getData();

		switch (v.getId()) {
		case R.id.ll_grid1:
			Toast.makeText(UIUtils.getContext(), data.name1, Toast.LENGTH_SHORT)
					.show();
			break;
		case R.id.ll_grid2:
			Toast.makeText(UIUtils.getContext(), data.name2, Toast.LENGTH_SHORT)
					.show();
			break;
		case R.id.ll_grid3:
			Toast.makeText(UIUtils.getContext(), data.name3, Toast.LENGTH_SHORT)
					.show();
			break;

		default:
			break;
		}
	}

}
