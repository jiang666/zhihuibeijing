package com.itcast.googleplayteach.manager;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.concurrent.ConcurrentHashMap;

import android.content.Intent;
import android.net.Uri;

import com.itcast.googleplayteach.domain.AppInfo;
import com.itcast.googleplayteach.domain.DownloadInfo;
import com.itcast.googleplayteach.http.HttpHelper;
import com.itcast.googleplayteach.http.HttpHelper.HttpResult;
import com.itcast.googleplayteach.utils.IOUtils;
import com.itcast.googleplayteach.utils.UIUtils;

/**
 * 下载管理器-单例
 * 
 * @author Kevin
 */
public class DownloadManager {

	public static final int STATE_NONE = 0;// 下载未开始
	public static final int STATE_WAITING = 1;// 等待下载
	public static final int STATE_DOWNLOAD = 2;// 正在下载
	public static final int STATE_PAUSE = 3;// 下载暂停
	public static final int STATE_ERROR = 4;// 下载失败
	public static final int STATE_SUCCESS = 5;// 下载成功

	// 所有观察者的集合
	private ArrayList<DownloadObserver> mObservers = new ArrayList<DownloadObserver>();

	// 下载对象的集合, ConcurrentHashMap是线程安全的HashMap
	private ConcurrentHashMap<String, DownloadInfo> mDownloadInfoMap = new ConcurrentHashMap<String, DownloadInfo>();
	// 下载任务集合, ConcurrentHashMap是线程安全的HashMap
	private ConcurrentHashMap<String, DownloadTask> mDownloadTaskMap = new ConcurrentHashMap<String, DownloadTask>();

	private static DownloadManager sInstance = new DownloadManager();

	private DownloadManager() {
	}

	public static DownloadManager getInstance() {
		return sInstance;
	}

	// 2. 注册观察者
	public synchronized void registerObserver(DownloadObserver observer) {
		if (observer != null && !mObservers.contains(observer)) {
			mObservers.add(observer);
		}
	}

	// 3. 注销观察者
	public synchronized void unregisterObserver(DownloadObserver observer) {
		if (observer != null && mObservers.contains(observer)) {
			mObservers.remove(observer);
		}
	}

	// 4. 通知下载状态变化
	public synchronized void notifyDownloadStateChanged(DownloadInfo info) {
		for (DownloadObserver observer : mObservers) {
			observer.onDownloadStateChanged(info);
		}
	}

	// 5. 通知下载进度变化
	public synchronized void notifyDownloadProgressChanged(DownloadInfo info) {
		for (DownloadObserver observer : mObservers) {
			observer.onDownloadProgressChanged(info);
		}
	}

	/**
	 * 1. 定义下载观察者接口
	 */
	public interface DownloadObserver {
		// 下载状态发生变化
		public void onDownloadStateChanged(DownloadInfo info);

		// 下载进度发生变化
		public void onDownloadProgressChanged(DownloadInfo info);
	}

	/**
	 * 开始下载
	 */
	public synchronized void download(AppInfo appInfo) {
		if (appInfo != null) {
			DownloadInfo downloadInfo = mDownloadInfoMap.get(appInfo.id);
			// 如果downloadInfo不为空,表示之前下载过, 就无需创建新的对象, 要接着原来的下载位置继续下载,也就是断点续传
			if (downloadInfo == null) {// 如果为空,表示第一次下载, 需要创建下载对象, 从头开始下载
				downloadInfo = DownloadInfo.copy(appInfo);
			}

			// 下载状态更为正在等待
			downloadInfo.currentState = STATE_WAITING;
			// 通知状态发生变化,各观察者根据此通知更新主界面
			notifyDownloadStateChanged(downloadInfo);

			// 将下载对象保存在集合中
			mDownloadInfoMap.put(appInfo.id, downloadInfo);

			// 初始化下载任务
			DownloadTask task = new DownloadTask(downloadInfo);
			// 启动下载任务
			ThreadManager.getThreadPool().execute(task);
			// 将下载任务对象维护在集合当中
			mDownloadTaskMap.put(appInfo.id, task);
		}
	}

	/**
	 * 下载任务
	 */
	class DownloadTask implements Runnable {

		private DownloadInfo downloadInfo;
		private HttpResult httpResult;

		public DownloadTask(DownloadInfo downloadInfo) {
			this.downloadInfo = downloadInfo;
		}

		@Override
		public void run() {
			// 更新下载状态
			downloadInfo.currentState = STATE_DOWNLOAD;
			notifyDownloadStateChanged(downloadInfo);

			File file = new File(downloadInfo.path);
			if (!file.exists() || file.length() != downloadInfo.currentPos
					|| downloadInfo.currentPos == 0) {// 文件不存在, 或者文件长度和对象的长度不一致,
														// 或者对象当前下载位置是0
				file.delete();// 移除无效文件
				downloadInfo.currentPos = 0;// 文件当前位置归零
				httpResult = HttpHelper.download(HttpHelper.URL
						+ "download?name=" + downloadInfo.downloadUrl);// 从头开始下载文件
			} else {
				// 需要断点续传
				httpResult = HttpHelper.download(HttpHelper.URL
						+ "download?name=" + downloadInfo.downloadUrl
						+ "&range=" + file.length());
			}

			InputStream in = null;
			FileOutputStream out = null;
			if (httpResult != null
					&& (in = httpResult.getInputStream()) != null) {
				try {
					out = new FileOutputStream(file, true);//在源文件基础上追加

					int len = 0;
					byte[] buffer = new byte[1024];
					while ((len = in.read(buffer)) != -1
							&& downloadInfo.currentState == STATE_DOWNLOAD) {// 只有在下载的状态才读取文件,如果状态变化,就立即停止读写文件
						out.write(buffer, 0, len);
						out.flush();

						downloadInfo.currentPos += len;// 更新当前文件下载位置
						notifyDownloadProgressChanged(downloadInfo);// 通知进度更新
					}
				} catch (Exception e) {
					e.printStackTrace();
				} finally {
					IOUtils.close(in);
					IOUtils.close(out);
				}

				// 下载结束, 判断文件是否完整
				if (file.length() == downloadInfo.size) {
					// 下载完毕
					downloadInfo.currentState = STATE_SUCCESS;
					notifyDownloadStateChanged(downloadInfo);
				} else if (downloadInfo.currentState == STATE_PAUSE) {
					// 中途暂停
					notifyDownloadStateChanged(downloadInfo);
				} else {
					// 下载失败
					downloadInfo.currentState = STATE_ERROR;
					downloadInfo.currentPos = 0;
					notifyDownloadStateChanged(downloadInfo);
					// 删除无效文件
					file.delete();
				}

			} else {
				// 下载失败
				downloadInfo.currentState = STATE_ERROR;
				downloadInfo.currentPos = 0;
				notifyDownloadStateChanged(downloadInfo);
				// 删除无效文件
				file.delete();
			}

			// 不管下载成功,失败还是暂停, 下载任务已经结束,都需要从当前任务集合中移除
			mDownloadTaskMap.remove(downloadInfo.id);
		}

	}

	/**
	 * 下载暂停
	 */
	public synchronized void pause(AppInfo appInfo) {
		if (appInfo != null) {
			DownloadInfo downloadInfo = mDownloadInfoMap.get(appInfo.id);
			if (downloadInfo != null) {
				int state = downloadInfo.currentState;
				// 如果当前状态是等待下载或者正在下载, 需要暂停当前任务
				if (state == STATE_WAITING || state == STATE_DOWNLOAD) {
					// 停止当前的下载任务
					DownloadTask downloadTask = mDownloadTaskMap
							.get(appInfo.id);
					if (downloadTask != null) {
						ThreadManager.getThreadPool().cancel(downloadTask);
					}

					// 更新下载状态为暂停
					downloadInfo.currentState = STATE_PAUSE;
					notifyDownloadStateChanged(downloadInfo);
				}
			}
		}
	}

	/**
	 * 安装apk
	 */
	public synchronized void install(AppInfo appInfo) {
		DownloadInfo downloadInfo = mDownloadInfoMap.get(appInfo.id);
		if (downloadInfo != null) {
			// 跳到系统的安装页面进行安装
			Intent intent = new Intent(Intent.ACTION_VIEW);
			intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
			intent.setDataAndType(Uri.parse("file://" + downloadInfo.path),
					"application/vnd.android.package-archive");
			UIUtils.getContext().startActivity(intent);
		}
	}

	// 根据应用对象,获取对应的下载对象
	public DownloadInfo getDownloadInfo(AppInfo data) {
		if (data != null) {
			return mDownloadInfoMap.get(data.id);
		}

		return null;
	}
}
