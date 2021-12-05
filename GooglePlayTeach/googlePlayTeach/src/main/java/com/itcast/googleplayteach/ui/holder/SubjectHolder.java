package com.itcast.googleplayteach.ui.holder;

import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.itcast.googleplayteach.R;
import com.itcast.googleplayteach.domain.SubjectInfo;
import com.itcast.googleplayteach.http.HttpHelper;
import com.itcast.googleplayteach.utils.BitmapHelper;
import com.itcast.googleplayteach.utils.UIUtils;
import com.lidroid.xutils.BitmapUtils;

public class SubjectHolder extends BaseHolder<SubjectInfo> {

	private ImageView ivPic;
	private TextView tvDes;
	private BitmapUtils mBitmapUtils;

	@Override
	public View initView() {
		View view = View.inflate(UIUtils.getContext(),
				R.layout.list_item_subject, null);
		ivPic = (ImageView) view.findViewById(R.id.iv_pic);
		tvDes = (TextView) view.findViewById(R.id.tv_des);

		mBitmapUtils = BitmapHelper.getBitmapUtils();
		mBitmapUtils.configDefaultLoadingImage(R.drawable.subject_default);
		return view;
	}

	@Override
	public void refreshView(SubjectInfo data) {
		if (data != null) {
			tvDes.setText(data.des);
			mBitmapUtils.display(ivPic, HttpHelper.URL + "image?name="
					+ data.url);
		}
	}

}
