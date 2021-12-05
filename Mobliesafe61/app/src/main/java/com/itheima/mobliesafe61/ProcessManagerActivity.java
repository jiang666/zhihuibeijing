package com.itheima.mobliesafe61;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.app.ActivityManager;
import android.app.ActivityManager.RunningAppProcessInfo;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.os.Bundle;
import android.os.Debug;
import android.os.Handler;
import android.text.format.Formatter;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.itheima.mobliesafe61.bean.ProcessInfo;
import com.itheima.mobliesafe61.service.AutoKillProcess;
import com.itheima.mobliesafe61.service.KeppAliveService;
import com.itheima.mobliesafe61.service.WidgetUpdateService;
import com.lidroid.xutils.ViewUtils;
import com.lidroid.xutils.view.annotation.ViewInject;
import com.lidroid.xutils.view.annotation.event.OnClick;

public class ProcessManagerActivity extends Activity {

	List<ProcessInfo> sysProcess = new ArrayList<ProcessInfo>();
	List<ProcessInfo> userProcess = new ArrayList<ProcessInfo>();

	@ViewInject(R.id.processlistview)
	ListView processlistview;
	@ViewInject(R.id.processcount)
	TextView processcount;
	@ViewInject(R.id.meminfo)
	TextView meminfo;

	@OnClick(R.id.setting)
	public void setting(View view) {

		// ComponentName 描述标签 对应的组件
		//name 特指 四大组件 类名包装对象
		ComponentName activity = new ComponentName(getPackageName(), ProcessManagerSetActivity.class.getCanonicalName());
		Intent intent = new Intent();
		intent.setComponent(activity);
		startActivity(intent);
		// startActivity(new Intent(this, ProcessManagerSetActivity.class));
	}

	@OnClick(R.id.cleanall)
	public void cleanall(View view) {

		// 哪些被我们选中
		List<ProcessInfo> selectList = new ArrayList<ProcessInfo>();

		for (ProcessInfo info : userProcess) {
			if (!getPackageName().equals(info.packageName)) {

				if (info.isCheck) {
					selectList.add(info);
				}
			}
		}

		for (ProcessInfo info : sysProcess) {
			if (info.isCheck) {
				selectList.add(info);
			}
		}

		// 清理
		int killCount = 0;

		long freeMemSize = 0;

		ActivityManager am = (ActivityManager) getSystemService(Context.ACTIVITY_SERVICE);
		for (ProcessInfo info : selectList) {
			// 终止进程
			am.killBackgroundProcesses(info.packageName);
			++killCount;
			freeMemSize += info.memSize;

			// 刷新界面
			if (info.isSys) {
				sysProcess.remove(info);
			} else {
				userProcess.remove(info);
			}
		}

		// 界面显示
		Toast.makeText(getBaseContext(), "杀死了" + killCount + "进程释放了" + formate(freeMemSize) + "内存", 0).show();
		if (adapter != null) {
			adapter.notifyDataSetChanged();
		}

		availmemSize += freeMemSize;
		// 显示内存状态
		processcount.setText("进程总数" + (sysProcess.size() + userProcess.size()));
		meminfo.setText("可用/总内存数:" + formate(availmemSize) + "/" + formate(getTotalMemByProc()));
	}

	@OnClick(R.id.selectall)
	public void selectAll(View view) {
		// 两个集合都打上勾
		for (ProcessInfo info : sysProcess) {
			info.isCheck = true;
		}
		for (ProcessInfo info : userProcess) {
			if (!getPackageName().equals(info.packageName)) {
				info.isCheck = true;
			}
		}

		if (adapter != null) {
			adapter.notifyDataSetChanged();// 要求listView 同步显示
		}
	}

	@OnClick(R.id.reverseall)
	public void reverseall(View view) {
		// 两个集合都打上勾
		for (ProcessInfo info : sysProcess) {

			info.isCheck = !info.isCheck;
		}
		for (ProcessInfo info : userProcess) {
			if (!getPackageName().equals(info.packageName)) {
				info.isCheck = !info.isCheck;
			}

		}
		if (adapter != null) {
			adapter.notifyDataSetChanged();// 要求listView 同步显示
		}
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_process_manager);

		ViewUtils.inject(this);

		// 进程管理者:运行程序 的个数 使用内存的情况 。。。
		final ActivityManager am = (ActivityManager) getSystemService(Context.ACTIVITY_SERVICE);

		// 内存信息
		getMemInfo(am);

		startService(new Intent(this, KeppAliveService.class));
		startService(new Intent(this, AutoKillProcess.class));
		

		new Thread() {
			public void run() {
				// 包管理者
				PackageManager pm = getPackageManager();
				// 获取进程列表
				List<RunningAppProcessInfo> list = am.getRunningAppProcesses();
				// 遍历 RunningAppProcessInfo:1.进程编号 2.进程名(通常是指包名)
				ProcessInfo bean = null;
				for (RunningAppProcessInfo item : list) {
//					item.uid;
					// 实体对象
					bean = new ProcessInfo();
					if (item.processName.startsWith("system")) {

					} else {

						// 包名
						bean.packageName = item.processName;
						// ApplicationInfo 功能清单 一个标签 的信息 <application>
						try {
							ApplicationInfo applicationInfo = pm.getApplicationInfo(bean.packageName, 0);
							// Applicaton标签信息
							// 图标
							bean.icon = applicationInfo.loadIcon(pm);
							bean.name = applicationInfo.loadLabel(pm).toString();
							// 获取单个进程的内存使用情况
							// bean.memSize=am.getProcessMemoryInfo(pids);//进程编号
							Debug.MemoryInfo[] singleMemInfo = am.getProcessMemoryInfo(new int[] { item.pid });
							bean.memSize = singleMemInfo[0].getTotalPrivateDirty() * 1024;
							if ((applicationInfo.flags & ApplicationInfo.FLAG_SYSTEM) == ApplicationInfo.FLAG_SYSTEM) {
								bean.isSys = true;
								sysProcess.add(bean);
							} else {
								bean.isSys = false;
								userProcess.add(bean);
							}
							// 系统/用户 flags &类型==类型
						} catch (NameNotFoundException e) {
							// 采用金山的忽略 包名与进程不相等的进程
							// <application
							// android:process="com.itheima.mobliesafe61.xx000">
							// // NameNotFoundException: 进程名!=包名 PackageManager
							// e.printStackTrace();
							// bean.icon=getResources().getDrawable(R.drawable.ic_launcher);
							// bean.name=item.processName;
							// bean.isSys = false;
							// userProcess.add(bean);
						}
					}

				}
				handler.sendEmptyMessage(0);
			};
		}.start();
		// // 假数据
		// for (int j = 0; j < 5; j++) {
		// ProcessInfo info = new ProcessInfo();
		// info.name = "快播...." + j;
		// info.packageName = "com.kuaibo..." + j;
		// info.memSize = 10000l;
		// info.isSys = false;
		// userProcess.add(info);
		// }
		// for (int i = 0; i < 25; i++) {
		// ProcessInfo info = new ProcessInfo();
		// info.name = "电话...." + i;
		// info.packageName = "com.android...." + i;
		// info.memSize = 10000l;
		// info.isSys = true;
		// sysProcess.add(info);
		// }

	}

	private Handler handler = new Handler() {
		public void handleMessage(android.os.Message msg) {
			ActivityManager am = (ActivityManager) getSystemService(Context.ACTIVITY_SERVICE);
			// 进程个数
			getProcessSize(am); // 19
			// 内容
			adapter = new MyProcessAdapter();
			//
			processlistview.setAdapter(adapter);

			// 添加事件

			processlistview.setOnItemClickListener(new ListView.OnItemClickListener() {

				// View view点击对象应的行视图 标记了viewHolder
				@Override
				public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

					ProcessInfo info = getProcessInfoByPosition(position);

					if (info != null)// 程序信息视图
					{
						// 当前程序不显示
						if (!getPackageName().equals(info.packageName)) {
							ViewHolder holder = (ViewHolder) view.getTag();
							info.isCheck = !info.isCheck;
							// 视图打勾--》取消 取消 -->勾
							holder.ischeck.setChecked(info.isCheck);
						}
					}

				}
			});
		};
	};

	class ViewHolder // 视图集合
	{
		TextView title;
		TextView processname;// activity_process_manager_processname
		TextView memsize;//
		CheckBox ischeck;//
		ImageView icon;
	}

	private ProcessInfo getProcessInfoByPosition(int position) {
		ProcessInfo processInfo = null;
		// 数据？？？
		if (position > 0 && position < (userProcess.size() + 1)) {
			processInfo = userProcess.get(position - 1);
		} else if (position > (userProcess.size() + 1)) {
			processInfo = sysProcess.get(position - 2 - userProcess.size());
		}
		return processInfo;
	}

	/**
	 * 获取内存信息
	 * 
	 * @param am
	 */
	long availmemSize;

	private void getMemInfo(ActivityManager am) {
		// MemoryInfo:内存信息的封装对象
		ActivityManager.MemoryInfo mInfo = new ActivityManager.MemoryInfo();
		am.getMemoryInfo(mInfo);// 获取内存信息
		availmemSize = mInfo.availMem;
		// long totalmemSize = mInfo.totalMem;// API 版本 16 K M G
		long totalmemSize = getTotalMemByProc();
		System.out.println(formate(availmemSize));
		System.out.println(formate(totalmemSize));
		meminfo.setText("可用/总内存:" + formate(availmemSize) + "/" + formate(totalmemSize));
	}

	private long getTotalMemByProc() {
		long totalmemSize = 0;
		try {
			File meminfoFile = new File("proc/meminfo");
			// readline读取行
			BufferedReader read = new BufferedReader(new FileReader(meminfoFile));
			// MemTotal: 599744 kB //proc/meminfo
			String line = read.readLine();
			char[] chars = line.toCharArray();
			// 拼接字符串工具
			StringBuffer sb = new StringBuffer();
			for (char item : chars) {
				if (item >= '0' && item <= '9') {
					sb.append(item);
				}
			}
			String result = sb.toString();// 599744KB
			totalmemSize = Long.parseLong(result) * 1024;
			// 关闭流
			read.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return totalmemSize;
	}

	/**
	 * 进程个数
	 * 
	 * @param am
	 */
	private void getProcessSize(ActivityManager am) {
		// 查询所有进程
		// RunningAppProcessInfo 进程信息的封装对象 1.进程名 2.pid 进程编号
		// List<RunningAppProcessInfo> list = am.getRunningAppProcesses();
		// int processCount = list.size();
		processcount.setText("进程总数:" + (userProcess.size() + sysProcess.size()));
	}

	private String formate(long size) {
		return Formatter.formatFileSize(getBaseContext(), size);
	}

	@Override
	protected void onStart() {
		// TODO Auto-generated method stub
		super.onStart();
		if (adapter != null) {
			adapter.notifyDataSetChanged();
		}
	}

	private MyProcessAdapter adapter = null;

	private class MyProcessAdapter extends BaseAdapter {
		// 返回列表的行数
		@Override
		public int getCount() {
			// SharedPreferences sp=getSharedPreferences(文件名, 模式);
			SharedPreferences sp = getSharedPreferences("config", Context.MODE_PRIVATE);
			boolean falg = sp.getBoolean("show_sys_process", true);
			if (falg) {
				// 用户进程+系统进程
				return userProcess.size() + sysProcess.size() + 2;
			} else {
				return userProcess.size() + 1;
			}
		}

		// 返回行视图的种类 0 标题 1 进程信息
		@Override
		public int getViewTypeCount() {
			return 2;
		}

		// 返回指定 下标的视图类型
		@Override
		public int getItemViewType(int position) {

			if (position == 0) {
				return 0;// 标题
			}
			if (position == (userProcess.size() + 1)) {
				return 0;// 标题
			}

			return 1;// 进程信息

		}

		// 返回行视图 显示指定 下标的数据
		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			int type = getItemViewType(position);
			if (type == 0)// 标题
			{
				convertView = setTitle(position, convertView);
				return convertView;
			} else if (type == 1) {// 进程信息
				convertView = setProcessInfoDetail(position, convertView);
				return convertView;
			}
			return null;
		}

		/**
		 * 设置进程信息
		 * 
		 * @param position
		 * @param convertView
		 * @return
		 */
		private View setProcessInfoDetail(int position, View convertView) {
			ViewHolder holder = null;
			if (convertView == null) {
				holder = new ViewHolder();
				convertView = View.inflate(getBaseContext(), R.layout.item_process_detail, null);
				holder.icon = (ImageView) convertView.findViewById(R.id.icon);
				holder.processname = (TextView) convertView.findViewById(R.id.processname);
				holder.memsize = (TextView) convertView.findViewById(R.id.memsize);
				holder.ischeck = (CheckBox) convertView.findViewById(R.id.ischeck);
				convertView.setTag(holder);
			} else {
				holder = (ViewHolder) convertView.getTag();
			}
			ProcessInfo processInfo = getProcessInfoByPosition(position);
			holder.processname.setText(processInfo.name);
			holder.icon.setImageDrawable(processInfo.icon);
			holder.memsize.setText(formate(processInfo.memSize));

			// 选 中CheckBox 显示
			holder.ischeck.setChecked(processInfo.isCheck);

			// 当前程序不显示
			if (getPackageName().equals(processInfo.packageName)) {
				// 61黑马安全卫士
				holder.ischeck.setVisibility(View.GONE);
			} else {
				holder.ischeck.setVisibility(View.VISIBLE);

			}
			return convertView;
		}

		private View setTitle(int position, View convertView) {
			ViewHolder holder = null;
			if (convertView == null) {
				holder = new ViewHolder();
				// 1.重用视图回收器返回的缓存视图
				convertView = View.inflate(getBaseContext(), R.layout.item_app_title, null);
				// 2.findViewByID
				holder.title = (TextView) convertView.findViewById(R.id.title);
				convertView.setTag(holder);// 标记
			} else {
				holder = (ViewHolder) convertView.getTag();
			}

			// 设置值
			if (position == 0) {
				holder.title.setText("用户程序:" + userProcess.size());
			} else {
				holder.title.setText("系统 程序:" + sysProcess.size());
			}
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
}
