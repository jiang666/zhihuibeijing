package com.itcast.googleplayteach.ui.holder;

import android.text.format.Formatter;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;

import com.itcast.googleplayteach.R;
import com.itcast.googleplayteach.domain.AppInfo;
import com.itcast.googleplayteach.domain.DownloadInfo;
import com.itcast.googleplayteach.http.HttpHelper;
import com.itcast.googleplayteach.manager.DownloadManager;
import com.itcast.googleplayteach.manager.DownloadManager.DownloadObserver;
import com.itcast.googleplayteach.ui.widget.ProgressArc;
import com.itcast.googleplayteach.utils.BitmapHelper;
import com.itcast.googleplayteach.utils.UIUtils;
import com.lidroid.xutils.BitmapUtils;

/**
 * 首页holder
 * 
 * @author Kevin
 * 
 */
public class HomeHolder extends BaseHolder<AppInfo> implements
		DownloadObserver, OnClickListener {

	private TextView tvName;
	private ImageView ivIcon;
	private TextView tvSize;
	private TextView tvDesc;
	private RatingBar rbStar;
	private FrameLayout flDownload;
	private TextView tvDownload;
	private ProgressArc pbProgress;

	private BitmapUtils mBitmapUtils;

	private DownloadManager mDownloadManager;
	private float mProgress;// 当前下载进度
	private int mCurrentState;// 当前下载状态

	private AppInfo mAppInfo;

	@Override
	public View initView() {
		View view = View.inflate(UIUtils.getContext(), R.layout.list_item_home,
				null);
		tvName = (TextView) view.findViewById(R.id.tv_name);
		ivIcon = (ImageView) view.findViewById(R.id.iv_icon);
		tvSize = (TextView) view.findViewById(R.id.tv_size);
		tvDesc = (TextView) view.findViewById(R.id.tv_desc);
		rbStar = (RatingBar) view.findViewById(R.id.rb_star);
		flDownload = (FrameLayout) view.findViewById(R.id.fl_download);
		tvDownload = (TextView) view.findViewById(R.id.tv_download);

		mBitmapUtils = BitmapHelper.getBitmapUtils();
		mBitmapUtils.configDefaultLoadingImage(R.drawable.ic_default);

		//初始化圆形进度条
		pbProgress = new ProgressArc(UIUtils.getContext());
		//设置圆形进度条直径
		pbProgress.setArcDiameter(UIUtils.dip2px(26));
		//设置进度条颜色
		pbProgress.setProgressColor(UIUtils.getColor(R.color.progress));
		//设置进度条宽高布局参数
		FrameLayout.LayoutParams params = new FrameLayout.LayoutParams(
				UIUtils.dip2px(27), UIUtils.dip2px(27));
		flDownload.addView(pbProgress, params);

		flDownload.setOnClickListener(this);

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

			mAppInfo = data;

			mDownloadManager = DownloadManager.getInstance();
			// 监听下载进度
			mDownloadManager.registerObserver(this);

			DownloadInfo downloadInfo = mDownloadManager.getDownloadInfo(data);
			if (downloadInfo == null) {
				// 没有下载过
				mCurrentState = DownloadManager.STATE_NONE;
				mProgress = 0;
			} else {
				// 之前下载过, 以内存中的对象的状态为准
				mCurrentState = downloadInfo.currentState;
				mProgress = downloadInfo.getProgress();
			}

			refreshUI(mProgress, mCurrentState);
		}
	}

	/**
	 * 刷新界面
	 * 
	 * @param progress
	 * @param state
	 */
	private void refreshUI(float progress, int state) {
		mCurrentState = state;
		mProgress = progress;
		switch (state) {
		case DownloadManager.STATE_NONE:
			pbProgress.setBackgroundResource(R.drawable.ic_download);
			pbProgress.setStyle(ProgressArc.PROGRESS_STYLE_NO_PROGRESS);
			tvDownload.setText(UIUtils.getString(R.string.app_state_download));
			break;
		case DownloadManager.STATE_WAITING:
			pbProgress.setBackgroundResource(R.drawable.ic_download);
			pbProgress.setStyle(ProgressArc.PROGRESS_STYLE_WAITING);
			tvDownload.setText(UIUtils.getString(R.string.app_state_waiting));
			break;
		case DownloadManager.STATE_DOWNLOAD:
			pbProgress.setBackgroundResource(R.drawable.ic_pause);
			pbProgress.setStyle(ProgressArc.PROGRESS_STYLE_DOWNLOADING);
			pbProgress.setProgress(progress, true);
			tvDownload.setText((int) (progress * 100) + "%");
			break;
		case DownloadManager.STATE_PAUSE:
			pbProgress.setBackgroundResource(R.drawable.ic_resume);
			pbProgress.setStyle(ProgressArc.PROGRESS_STYLE_NO_PROGRESS);
			flDownload.setVisibility(View.VISIBLE);
			break;
		case DownloadManager.STATE_ERROR:
			pbProgress.setBackgroundResource(R.drawable.ic_redownload);
			pbProgress.setStyle(ProgressArc.PROGRESS_STYLE_NO_PROGRESS);
			tvDownload.setText(UIUtils.getString(R.string.app_state_error));
			break;
		case DownloadManager.STATE_SUCCESS:
			pbProgress.setBackgroundResource(R.drawable.ic_install);
			pbProgress.setStyle(ProgressArc.PROGRESS_STYLE_NO_PROGRESS);
			tvDownload
					.setText(UIUtils.getString(R.string.app_state_downloaded));
			break;

		default:
			break;
		}
	}

	@Override
	public void onDownloadStateChanged(DownloadInfo info) {
		refreshOnMainThread(info);
	}

	@Override
	public void onDownloadProgressChanged(DownloadInfo info) {
		refreshOnMainThread(info);
	}

	// 主线程刷新ui
	private void refreshOnMainThread(final DownloadInfo info) {
		// 判断要刷新的下载对象是否是当前的应用
		if (info.id.equals(mAppInfo.id)) {
			UIUtils.runOnUiThread(new Runnable() {

				@Override
				public void run() {
					refreshUI(info.getProgress(), info.currentState);
				}
			});
		}
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.fl_download:
			// 根据当前状态来决定相关操作
			if (mCurrentState == DownloadManager.STATE_NONE
					|| mCurrentState == DownloadManager.STATE_PAUSE
					|| mCurrentState == DownloadManager.STATE_ERROR) {
				// 开始下载
				mDownloadManager.download(mAppInfo);
			} else if (mCurrentState == DownloadManager.STATE_DOWNLOAD
					|| mCurrentState == DownloadManager.STATE_WAITING) {
				// 暂停下载
				mDownloadManager.pause(mAppInfo);
			} else if (mCurrentState == DownloadManager.STATE_SUCCESS) {
				// 开始安装
				mDownloadManager.install(mAppInfo);
			}
			break;

		default:
			break;
		}
	}
}
