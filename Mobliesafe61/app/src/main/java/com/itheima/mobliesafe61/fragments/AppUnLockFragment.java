package com.itheima.mobliesafe61.fragments;

import java.util.ArrayList;
import java.util.List;

import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.animation.TranslateAnimation;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.itheima.mobliesafe61.R;
import com.itheima.mobliesafe61.bean.AppInfo;
import com.itheima.mobliesafe61.db.dao.AppLockDao;
import com.itheima.mobliesafe61.utils.InstalledSoftUtils;

//未加锁  "Activity"
public class AppUnLockFragment extends Fragment {
	private List<AppInfo> apps = new ArrayList<AppInfo>();
	private AppLockDao dao = null;
	// 返回当前片段的视图 onCreateView 相当于Activity的setContentView
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		// inflate layout--View
		final View view = View.inflate(getActivity(), R.layout.fragment_app_unlock, null);

		dao = new AppLockDao(getActivity());
		startTask(view);

		return view;
	}

	/**
	 * 打开任务
	 * 
	 * @param view
	 */
	private void startTask(final View view) {
		new AsyncTask<Void, Void, Void>() {
			// 任务前
			protected void onPreExecute() {
				view.findViewById(R.id.loading).setVisibility(View.VISIBLE);
			};
			// 任务中[子线程]
			@Override
			protected Void doInBackground(Void... params) {
				// // TODO Auto-generated method stub
				// for (int i = 0; i < 10; i++) {
				// AppInfo info = new AppInfo();
				// info.name = "快播...." + i;
				// info.packageName = "com.itheiam.kuaibo..." + i;
				// apps.add(info);
				// }
				// try {
				// Thread.sleep(1000);
				// } catch (InterruptedException e) {
				// // TODO Auto-generated catch block
				// e.printStackTrace();
				// }
				apps.clear();
				List<AppInfo> list = InstalledSoftUtils.getAllSofts(getActivity());
				for (AppInfo info : list) {
					if (!dao.hasExist(info.packageName)) {
						apps.add(info);
					}
				}
				try {
					Thread.sleep(1000);
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}

				return null;
			}

			// 任务后
			protected void onPostExecute(Void result) {
				view.findViewById(R.id.loading).setVisibility(View.GONE);
				// Thread+Handler
				ListView unlockapp_listview = (ListView) view.findViewById(R.id.unlockapp_listview);
				adapter = new MyAppAdapter();
				unlockapp_listview.setAdapter(adapter);

				TextView title = (TextView) getActivity().findViewById(R.id.title);
				title.setText("未加锁:" + apps.size());
			};
		}.execute();// [调用执行]
	}

	MyAppAdapter adapter;

	private class MyAppAdapter extends BaseAdapter {
		// 返回列表行数
		@Override
		public int getCount() {
			return apps.size();
		}

		// 返回行视图，显示指定下标的数据
		@Override
		public View getView(int position, View convertView, ViewGroup parent) {

			ViewHolder holder = null;
			// 1. 重用缓存视图
			if (convertView == null) {
				holder = new ViewHolder();
				convertView = View.inflate(getActivity(), R.layout.item_app_lock, null);
				holder.appname = (TextView) convertView.findViewById(R.id.appname);
				holder.icon = (ImageView) convertView.findViewById(R.id.icon);
				holder.lock = (ImageView) convertView.findViewById(R.id.lock);
				convertView.setTag(holder);
			} else {
				// 2.findViewByID
				holder = (ViewHolder) convertView.getTag();
			}
			final AppInfo info = apps.get(position);

			holder.appname.setText(info.name);
			holder.icon.setImageDrawable(info.icon);

			startAnimation(convertView, holder, info);
			return convertView;
		}

		@Override
		public Object getItem(int position) {
			// TODO Auto-generated method stub
			return null;
		}

		@Override
		public long getItemId(int position) {
			// TODO Auto-generated method stub
			return 0;
		}

	}

	class ViewHolder {
		TextView appname;
		ImageView lock;
		ImageView icon;
	}

	private Handler handler = new Handler();

	/**
	 * 向右移动动画
	 * 
	 * @param convertView
	 * @param holder
	 * @param info
	 */
	private void startAnimation(View convertView, ViewHolder holder, final AppInfo info) {
		final View currView = convertView;
		OnClickListener listener = new View.OnClickListener() {

			@Override
			public void onClick(View v) {

				// 移动动画
				TranslateAnimation ta = new TranslateAnimation//
				(TranslateAnimation.RELATIVE_TO_SELF, 0f, TranslateAnimation.RELATIVE_TO_SELF, 1.0f,//
						TranslateAnimation.RELATIVE_TO_SELF, 0f, TranslateAnimation.RELATIVE_TO_SELF, 0f);
				// 时长
				ta.setDuration(2000);
				// 添加
				currView.startAnimation(ta);

				// 等待2000
				new Thread() {
					public void run() {
						try {
							Thread.sleep(2000);
							// new Message -->handler
							// sendMessage-->hanlerMessage r.run();
							handler.post(new Runnable() {
								@Override
								public void run() {
									dao.add(info.packageName);
									// 移除数据
									apps.remove(info);
									// 刷新列表
									adapter.notifyDataSetChanged();
									TextView title = (TextView) getActivity().findViewById(R.id.title);
									title.setText("未加锁:" + apps.size());
								}
							});

						} catch (InterruptedException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
					};
				}.start();

			}
		};
		holder.lock.setOnClickListener(listener);
	}
}
