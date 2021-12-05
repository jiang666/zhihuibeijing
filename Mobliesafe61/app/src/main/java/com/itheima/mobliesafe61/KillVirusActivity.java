package com.itheima.mobliesafe61;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.view.animation.RotateAnimation;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.itheima.mobliesafe61.bean.AppInfo;
import com.itheima.mobliesafe61.bean.ScanInfo;
import com.itheima.mobliesafe61.db.dao.VirusDao;
import com.itheima.mobliesafe61.utils.InstalledSoftUtils;
import com.itheima.mobliesafe61.utils.MD5Utls;
import com.lidroid.xutils.ViewUtils;
import com.lidroid.xutils.view.annotation.ViewInject;

public class KillVirusActivity extends Activity {

	@ViewInject(R.id.act_image)
	ImageView act_image;
	@ViewInject(R.id.progress_text)
	TextView progress_text;
	@ViewInject(R.id.progress_bar)
	ProgressBar progress_bar;
	@ViewInject(R.id.report)
	LinearLayout report;

	private VirusDao dao = null;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_kill_virus);
		ViewUtils.inject(this);
		dao = new VirusDao();

		startRoating();

		initUi();
	}

	/**
	 * 初始化界面
	 */

	private List<ScanInfo> viruslist = new ArrayList<ScanInfo>();

	private void initUi() {
		new AsyncTask<Void, ScanInfo, Void>() {
			// 任务开始 前
			protected void onPreExecute() {
				viruslist.clear();
				progress_bar.setMax(100);
				progress_bar.setProgress(0);
				progress_text.setText("正在初始化8核引擎...");
			};

			// 任务进行中
			// 任务完成后

			private int total=0;
			@Override
			protected Void doInBackground(Void... params) {
				// system/app data/app
				List<AppInfo> apps = InstalledSoftUtils.getAllSofts(getBaseContext());
				total=apps.size();
				for (int i = 0; i < apps.size(); i++) {
					AppInfo info = apps.get(i);
					try {
						String apkMd5 = MD5Utls.getFileMd5(info.path);
						String desc = dao.getDesc(getBaseContext(), apkMd5);
						System.out.println(info.name + ":" + apkMd5 + " :" + desc);
						// 发送扫描结果到界面
						ScanInfo bean = new ScanInfo();
						bean.name = info.name;
						bean.packagename = info.packageName;
						bean.path = info.path;
						bean.desc = desc;

						if (bean.desc != null) {
							viruslist.add(bean);
						} else {
							// ..
						}
						publishProgress(bean);
						try {
							Thread.sleep(100);
						} catch (InterruptedException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
					} catch (FileNotFoundException e) {
						e.printStackTrace();
					} catch (NoSuchAlgorithmException e) {
					} catch (IOException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
				return null;
			}

			// 任务中更新界面
			int progress = 0;

			protected void onProgressUpdate(ScanInfo[] values) {
				ScanInfo info = values[0];
				++progress;
				// 更新进度
				progress_bar.setProgress(progress);
				progress_bar.setMax(total);
				progress_text.setText("正在扫描:" + info.name);
				View view = View.inflate(getBaseContext(), R.layout.item_scan_info, null);
				TextView text = (TextView) view;
				if (info.desc == null) {
					text.setText(info.name + ":正常");
					text.setTextColor(Color.parseColor("#000000"));
				} else {
					text.setText(info.name +":"+ info.desc);
					text.setTextColor(Color.parseColor("#F20031"));
				}
				report.addView(view, 0);// 0添加在顶部
			};

			protected void onPostExecute(Void result) {
				act_image.clearAnimation();// 停止动画
				if (viruslist.size() == 0) {
					progress_text.setText("查杀结束,没有发现病毒");
					progress_text.setTextColor(Color.parseColor("#97C024"));
				} else {
					progress_text.setText("发现病毒:" + viruslist.size());
					progress_text.setTextColor(Color.parseColor("#F20031"));
				}
			};
		}.execute();
	}

	/**
	 * 开始旋转
	 */
	private void startRoating() {
		// RotateAnimation:旋转动画
		// pivot:轴心
		RotateAnimation rotate = new RotateAnimation(//
				0, 360,// 旋转角度 顺时针
				RotateAnimation.RELATIVE_TO_SELF, 0.5f, RotateAnimation.RELATIVE_TO_SELF, 0.5f);
		// 时长
		rotate.setDuration(1000);
		// 旋转次数
		rotate.setRepeatCount(Integer.MAX_VALUE);//
		// 开启
		act_image.startAnimation(rotate);
	}

}
