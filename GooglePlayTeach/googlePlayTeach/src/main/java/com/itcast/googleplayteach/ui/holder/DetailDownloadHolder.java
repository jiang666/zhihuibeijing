package com.itcast.googleplayteach.ui.holder;

import android.graphics.Color;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.FrameLayout;

import com.itcast.googleplayteach.R;
import com.itcast.googleplayteach.domain.AppInfo;
import com.itcast.googleplayteach.domain.DownloadInfo;
import com.itcast.googleplayteach.manager.DownloadManager;
import com.itcast.googleplayteach.manager.DownloadManager.DownloadObserver;
import com.itcast.googleplayteach.ui.widget.ProgressHorizontal;
import com.itcast.googleplayteach.utils.UIUtils;

/**
 * 详情页-下载
 * 
 */
public class DetailDownloadHolder extends BaseHolder<AppInfo> implements
		DownloadObserver, OnClickListener {

	private DownloadManager mDownloadManager;

	private Button btnDownload;
	private FrameLayout flDownload;
	private ProgressHorizontal pbProgress;

	private AppInfo mAppInfo;

	private float mProgress;// 当前下载进度
	private int mCurrentState;// 当前下载状态

	@Override
	public View initView() {
		View view = View.inflate(UIUtils.getContext(),
				R.layout.layout_detail_download, null);
		btnDownload = (Button) view.findViewById(R.id.btn_download);
		flDownload = (FrameLayout) view.findViewById(R.id.fl_download);
		btnDownload.setOnClickListener(this);
		flDownload.setOnClickListener(this);

		// 添加进度条布局
		pbProgress = new ProgressHorizontal(UIUtils.getContext());
		pbProgress.setProgressTextColor(Color.WHITE);// 文字颜色为白色
		pbProgress.setProgressTextSize(UIUtils.dip2px(18));// 文字大小
		pbProgress.setProgressResource(R.drawable.progress_normal);// 进度条颜色
		pbProgress.setProgressBackgroundResource(R.drawable.progress_bg);// 进度条背景色
		// 初始化布局参数
		FrameLayout.LayoutParams params = new FrameLayout.LayoutParams(
				FrameLayout.LayoutParams.MATCH_PARENT,
				FrameLayout.LayoutParams.MATCH_PARENT);

		flDownload.addView(pbProgress, params);
		return view;
	}

	@Override
	public void refreshView(AppInfo data) {
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

		//根据对象状态刷新界面
		refreshUI(mProgress, mCurrentState);
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
			flDownload.setVisibility(View.GONE);
			btnDownload.setText(UIUtils.getString(R.string.app_state_download));
			break;
		case DownloadManager.STATE_WAITING:
			flDownload.setVisibility(View.GONE);
			btnDownload.setText(UIUtils.getString(R.string.app_state_waiting));
			break;
		case DownloadManager.STATE_DOWNLOAD:
			flDownload.setVisibility(View.VISIBLE);
			pbProgress.setProgress(progress);
			pbProgress.setCenterText("");
			break;
		case DownloadManager.STATE_PAUSE:
			flDownload.setVisibility(View.VISIBLE);
			pbProgress.setCenterText(UIUtils
					.getString(R.string.app_state_paused));
			break;
		case DownloadManager.STATE_ERROR:
			flDownload.setVisibility(View.GONE);
			btnDownload.setText(UIUtils.getString(R.string.app_state_error));
			break;
		case DownloadManager.STATE_SUCCESS:
			flDownload.setVisibility(View.GONE);
			btnDownload.setText(UIUtils
					.getString(R.string.app_state_downloaded));
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
		case R.id.btn_download:
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
