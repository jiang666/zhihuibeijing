package com.itheima.mobliesafe61.fragments;

import java.util.ArrayList;
import java.util.List;

import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.view.animation.TranslateAnimation;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.itheima.mobliesafe61.R;
import com.itheima.mobliesafe61.bean.AppInfo;
import com.itheima.mobliesafe61.db.dao.AppLockDao;
import com.itheima.mobliesafe61.fragments.AppUnLockFragment.ViewHolder;
import com.itheima.mobliesafe61.utils.InstalledSoftUtils;

//加锁
public class AppLockFragment extends Fragment {
	private List<AppInfo> apps = new ArrayList<AppInfo>();

	private AppLockDao dao = null;

	// 返回当前片段的视图 onCreateView 相当于Activity的setContentView
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		// inflate layout--View
		final View view = View.inflate(getActivity(), R.layout.fragment_app_lock, null);
		dao = new AppLockDao(getActivity());
		final ListView lockapp_listview = (ListView) view.findViewById(R.id.lockapp_listview);
		// for (int i = 0; i < 10; i++) {
		// AppInfo info = new AppInfo();
		// info.name = "电影 加锁...." + i;
		// info.packageName = "com.itheiam.kuaibo..." + i;
		// apps.add(info);
		// }
		//
		// adapter = new MyAppAdapter();
		// lockapp_listview.setAdapter(adapter);
		// 显示数据表存在的应用信息
		new AsyncTask<Void, Void, Void>() {
			// 任务开发前
			protected void onPreExecute() {
				view.findViewById(R.id.loading).setVisibility(View.VISIBLE);
			};

			// 任务中
			@Override
			protected Void doInBackground(Void... params) {
				apps.clear();
				// 所有的安装软件信息
				List<AppInfo> list = InstalledSoftUtils.getAllSofts(getActivity());
				//
				for (AppInfo info : list) {
					if (dao.hasExist(info.packageName)) {
						apps.add(info);
					}
				}
				return null;
			}

			//任务后 ：取得数据显示
			protected void onPostExecute(Void result) {
				adapter = new MyAppAdapter();
				lockapp_listview.setAdapter(adapter);
				view.findViewById(R.id.loading).setVisibility(View.GONE);
				TextView title=(TextView) getActivity().findViewById(R.id.title);
				title.setText("已加锁:"+apps.size());
			};
		}.execute();// start

		return view;
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
			AppInfo info = apps.get(position);

			holder.icon.setImageDrawable(info.icon);
			holder.lock.setImageResource(R.drawable.unlock);
			holder.appname.setText(info.name);
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
	 * 开始向左移动动画
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
				(TranslateAnimation.RELATIVE_TO_SELF, 0f, TranslateAnimation.RELATIVE_TO_SELF, -1.0f,//
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
									dao.delete(info.packageName);
									// 移除数据
									apps.remove(info);
									// 刷新列表
									adapter.notifyDataSetChanged();
									
									TextView title=(TextView) getActivity().findViewById(R.id.title);
									title.setText("已加锁:"+apps.size());
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
