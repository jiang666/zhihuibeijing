package com.itcast.googleplayteach.ui.holder;

import android.text.format.Formatter;
import android.view.View;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;

import com.itcast.googleplayteach.R;
import com.itcast.googleplayteach.domain.AppInfo;
import com.itcast.googleplayteach.http.HttpHelper;
import com.itcast.googleplayteach.utils.BitmapHelper;
import com.itcast.googleplayteach.utils.UIUtils;
import com.lidroid.xutils.BitmapUtils;

/**
 * 应用页holder
 * 
 * @author Kevin
 * 
 */
public class AppHolder extends BaseHolder<AppInfo> {

	private TextView tvName;
	private ImageView ivIcon;
	private TextView tvSize;
	private TextView tvDesc;
	private RatingBar rbStar;
	private BitmapUtils mBitmapUtils;

	@Override
	public View initView() {
		View view = View.inflate(UIUtils.getContext(), R.layout.list_item_home,
				null);
		tvName = (TextView) view.findViewById(R.id.tv_name);
		ivIcon = (ImageView) view.findViewById(R.id.iv_icon);
		tvSize = (TextView) view.findViewById(R.id.tv_size);
		tvDesc = (TextView) view.findViewById(R.id.tv_desc);
		rbStar = (RatingBar) view.findViewById(R.id.rb_star);

		mBitmapUtils = BitmapHelper.getBitmapUtils();
		mBitmapUtils.configDefaultLoadingImage(R.drawable.ic_default);
		return view;
	}

	@Override
	public void refreshView(AppInfo data) {
		if (data != null) {
			tvName.setText(data.name);
			tvSize.setText(Formatter.formatFileSize(UIUtils.getContext(),
					data.size));
			tvDesc.setText(data.des);
			rbStar.setRating((float) data.stars);
			mBitmapUtils.display(ivIcon, HttpHelper.URL + "image?name="
					+ data.iconUrl);
		}
	}
}
