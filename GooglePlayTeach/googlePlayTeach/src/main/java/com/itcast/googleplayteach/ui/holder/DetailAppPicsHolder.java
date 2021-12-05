package com.itcast.googleplayteach.ui.holder;

import java.util.ArrayList;

import android.view.View;
import android.widget.ImageView;

import com.itcast.googleplayteach.R;
import com.itcast.googleplayteach.domain.AppInfo;
import com.itcast.googleplayteach.http.HttpHelper;
import com.itcast.googleplayteach.utils.BitmapHelper;
import com.itcast.googleplayteach.utils.UIUtils;
import com.lidroid.xutils.BitmapUtils;

/**
 * 详情页-应用图片
 * 
 * @author Kevin
 */
public class DetailAppPicsHolder extends BaseHolder<AppInfo> {

	private ImageView[] mImageViews;
	private BitmapUtils mBitmapUtils;

	@Override
	public View initView() {
		View view = View.inflate(UIUtils.getContext(),
				R.layout.layout_detail_picinfo, null);

		mImageViews = new ImageView[5];
		mImageViews[0] = (ImageView) view.findViewById(R.id.iv_pic1);
		mImageViews[1] = (ImageView) view.findViewById(R.id.iv_pic2);
		mImageViews[2] = (ImageView) view.findViewById(R.id.iv_pic3);
		mImageViews[3] = (ImageView) view.findViewById(R.id.iv_pic4);
		mImageViews[4] = (ImageView) view.findViewById(R.id.iv_pic5);

		mBitmapUtils = BitmapHelper.getBitmapUtils();

		return view;
	}

	@Override
	public void refreshView(AppInfo data) {
		if (data != null) {
			ArrayList<String> list = data.screen;
			for (int i = 0; i < 5; i++) {
				if (i < list.size()) {
					mImageViews[i].setVisibility(View.VISIBLE);
					mBitmapUtils.display(mImageViews[i], HttpHelper.URL
							+ "image?name=" + list.get(i));
				} else {
					mImageViews[i].setVisibility(View.GONE);
				}
			}
		}
	}

}
