package com.itheima.mobliesafe61;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import opensource.jpinyin.PinyinFormat;
import opensource.jpinyin.PinyinHelper;
import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.text.format.Formatter;
import android.view.Gravity;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.animation.AlphaAnimation;
import android.view.animation.AnimationSet;
import android.view.animation.ScaleAnimation;
import android.widget.AbsListView;
import android.widget.AbsListView.OnScrollListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.TextView;
import android.widget.Toast;

import com.itheima.mobliesafe61.bean.AppInfo;
import com.lidroid.xutils.ViewUtils;
import com.lidroid.xutils.view.annotation.ViewInject;
import com.stericson.RootTools.RootTools;

public class AppManagerActivity extends Activity {

	@ViewInject(R.id.innerspace)
	TextView innerspace;
	@ViewInject(R.id.sdspace)
	TextView sdspace;
	@ViewInject(R.id.latter)
	TextView latter;
	@ViewInject(R.id.applistview)
	ListView applistview;
	@ViewInject(R.id.loading)
	LinearLayout loading;

	private List<AppInfo> userApps = new ArrayList<AppInfo>();
	private List<AppInfo> sysApps = new ArrayList<AppInfo>();

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_app_manager);
		ViewUtils.inject(this);
		showSpaceInfo();

		registerUninstallSucessReceiver();

		// for (int i = 0; i < 5; i++) {
		// AppInfo info = new AppInfo();
		// info.isUser = true;
		// info.name = "??????" + i;
		// info.packageName = "com.itheima.kuaibo" + i;
		// info.size = 1024;
		// userApps.add(info);
		// }
		// for (int i = 0; i < 10; i++) {
		// AppInfo info = new AppInfo();
		// info.isUser = false;
		// info.name = "?????????" + i;
		// info.packageName = "com.itheima.browser" + i;
		// info.size = 1024;
		// sysApps.add(info);
		// }

		getData();

	}

	private void getData() {
		if (window != null) {
			window.dismiss();
		}
		loading.setVisibility(View.VISIBLE);
		new Thread() {
			public void run() {
				// ????????????
				sysApps.clear();
				userApps.clear();
				// ??????PackageManager
				PackageManager pm = getPackageManager();
				// ????????????????????????????????? PackageInfo
				// List<PackageInfo> infos=pm.getInstalledPackages(????????????);
				// GET_ACTIVITY
				List<PackageInfo> infos = pm.getInstalledPackages(0);// ?????????application???????????????
				for (PackageInfo info : infos) {
					AppInfo bean = new AppInfo();
					// 1.??????
					bean.icon = info.applicationInfo.loadIcon(getPackageManager());
					// 2.?????????/??????
					bean.name = info.applicationInfo.loadLabel(getPackageManager()).toString();
					// 3.??????
					bean.packageName = info.packageName;
					// 4.??????--?????????
					bean.path = info.applicationInfo.sourceDir;// ??????
					bean.size = new File(bean.path).length();// 30M
					System.out.println(bean);
					// info.applicationInfo.flags
					// 5.??????/??????
					if ((ApplicationInfo.FLAG_EXTERNAL_STORAGE & info.applicationInfo.flags) == ApplicationInfo.FLAG_EXTERNAL_STORAGE) {
						bean.isInSd = true;
					} else {
						bean.isInSd = false;// ????????????
					}
					// 5.??????/??????
					if ((ApplicationInfo.FLAG_SYSTEM & info.applicationInfo.flags) == ApplicationInfo.FLAG_SYSTEM) {
						bean.isUser = false;
						sysApps.add(bean);
					} else {
						bean.isUser = true;// ??????
						userApps.add(bean);
					}

				}

				// ??????
				Comparator<AppInfo> cp = new Comparator<AppInfo>() {

					@Override
					public int compare(AppInfo lhs, AppInfo rhs) {
						String p1 = getPinYin(lhs.name);
						String p2 = getPinYin(rhs.name);
						return p1.compareTo(p2);
					}
				};
				Collections.sort(userApps, cp);
				Collections.sort(sysApps, cp);
				// showListView();
				hanlder.sendEmptyMessage(0);// -->handleMessage

			};
		}.start();
	}

	// ?????? ??????????????????
	private void registerUninstallSucessReceiver() {
		BroadcastReceiver receiver = new BroadcastReceiver() {
			@Override
			public void onReceive(Context context, Intent intent) {
				// TODO Auto-generated method stub
				Toast.makeText(getBaseContext(), "????????????61", 0).show();
				//
				getData();// ????????????????????????
				showSpaceInfo();// ??????????????????

			}
		};
		IntentFilter filter = new IntentFilter();
		filter.addAction(Intent.ACTION_PACKAGE_REMOVED);// ????????????
		filter.addDataScheme("package");
		registerReceiver(receiver, filter);
	}

	// ????????????
	private String getPinYin(String msg) {
		String result = PinyinHelper.convertToPinyinString(msg, "", PinyinFormat.WITHOUT_TONE).toUpperCase();
		return result;
	}

	private Handler hanlder = new Handler() {
		public void handleMessage(android.os.Message msg) {
			loading.setVisibility(View.GONE);
			showListView();
		};
	};

	// ?????? ????????????????????????
	private AppInfo getAppInfoByPosition(int position) {
		AppInfo info = null;
		// ???????????? userApps
		if (position > 0 && position < (1 + userApps.size())) {
			info = userApps.get(position - 1);
		} else if (position > (1 + userApps.size()))// //???????????? sysApps
		{
			info = sysApps.get(position - 2 - userApps.size());
		}
		return info;
	}

	MyAppInfoAdapter adapter = null;

	private void showListView() {
		if (adapter != null) {
			adapter.notifyDataSetChanged();
			return;
		}

		// ?????????
		adapter = new MyAppInfoAdapter();
		// ?????????ListView
		applistview.setAdapter(adapter);
		applistview.setOnScrollListener(new OnScrollListener() {
			// scrollState 1.IDLE 2.fling 3.touch
			@Override
			public void onScrollStateChanged(AbsListView view, int scrollState) {
				switch (scrollState) {
				case OnScrollListener.SCROLL_STATE_FLING:
				case OnScrollListener.SCROLL_STATE_TOUCH_SCROLL:
					if (window != null) {
						window.dismiss();
					}
					latter.setVisibility(View.VISIBLE);
					break;
				case OnScrollListener.SCROLL_STATE_IDLE:
					latter.setVisibility(View.GONE);
					break;
				}
			}

			@Override
			public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
				// userApps sysAPps
				AppInfo info = getAppInfoByPosition(firstVisibleItem);
				if (info != null) {
					String pinYin = getPinYin(info.name).substring(0, 1);
					latter.setText(pinYin);
				}
			}
		});

		// ???????????????
		applistview.setOnItemClickListener(new OnItemClickListener() {

			// AdapterView<?> parent ListView
			// View view ?????????????????????
			// int position ??????
			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
				showMenu(view, position);
			}

		});
	}

	private OnClickListener listener = new OnClickListener() {

		// ????????????
		@Override
		public void onClick(View v) {
			switch (v.getId()) {
			case R.id.uninstall:

				uninstallPackage();

				break;
			case R.id.start:
				startMain();
				break;
			case R.id.share:
				shareTo();
				break;
			case R.id.setting:
				viewDetails();

				break;
			}

		}

		// ??????????????????
		private void uninstallPackage() {
			if (clickAppInfo != null && clickAppInfo.isUser) {
				// <intent-filter>
				// <action android:name="android.intent.action.VIEW" />
				// <action android:name="android.intent.action.DELETE" />
				// <category android:name="android.intent.category.DEFAULT" />
				// <data android:scheme="package" />
				// </intent-filter>
				Intent intent = new Intent();
				intent.setAction("android.intent.action.DELETE");
				intent.addCategory("android.intent.category.DEFAULT");
				intent.setData(Uri.parse("package:" + clickAppInfo.packageName));
				startActivity(intent);
			} else if (clickAppInfo != null && !clickAppInfo.isUser) { // ??????
				try {
					// RootTools:"??????"
					// C:\Users\itheima>adb remount ??? system/??????
					// remount succeeded
					// C:\Users\itheima>adb shell ??????shell??????
					// root@vbox86p:/ # cd system/app
					// root@vbox86p:/system/app # rm -r Calendar.apk ????????????
					if (!RootTools.isRootAvailable()) {
						Toast.makeText(getBaseContext(), "????????????61????????????!", 0).show();
						return;
					}
					if (!RootTools.isAccessGiven()) {
						Toast.makeText(getBaseContext(), "????????????61????????????!????????? ?????????", 0).show();
						return;
					}

					//1?????????
					//2.mount
					RootTools.sendShell("mount -o remount  ,rw /system", 3000);
					RootTools.sendShell("rm -r " + clickAppInfo.path, 3000);

				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		}

		// ??????
		private void viewDetails() {
			// <intent-filter> InstalledAppDetails
			// <action
			// android:name="android.settings.APPLICATION_DETAILS_SETTINGS" />
			// <category android:name="android.intent.category.DEFAULT" />
			// <data android:scheme="package" /> scheme??????
			// </intent-filter>
			Intent intent = new Intent();
			intent.setAction("android.settings.APPLICATION_DETAILS_SETTINGS");
			intent.addCategory("android.intent.category.DEFAULT");
			intent.setData(Uri.parse("package:" + clickAppInfo.packageName));
			startActivity(intent);
		}

		private void startMain() {
			// Intent Action MAIN
			Intent intent = getPackageManager().getLaunchIntentForPackage(clickAppInfo.packageName);

			if (intent != null) {
				startActivity(intent);
			} else {
				Toast.makeText(getBaseContext(), "????????????", 0).show();
			}
		}

		private void shareTo() {
			String msg = "??????360???????????????????????????????????????????????????(???????????????)???????????? http://openbox.mobilem.360.cn/qcms/view/t/detail?sid=1933170 ????????????";
			Intent intent = new Intent();
			intent.setAction("android.intent.action.SEND");
			intent.addCategory("android.intent.category.DEFAULT");
			intent.setType("text/plain");
			intent.putExtra("sms_body", msg);
			startActivity(intent);
		}
	};

	PopupWindow window;

	AppInfo clickAppInfo = null;
	View popView;
	private void showMenu(View view, int position) {
		AppInfo info = getAppInfoByPosition(position);
		if (info != null) {
			clickAppInfo = info;
			if (window == null) {
				 popView = View.inflate(getBaseContext(), R.layout.pop_view, null);

				TextView uninstall = (TextView) popView.findViewById(R.id.uninstall);
				TextView start = (TextView) popView.findViewById(R.id.start);
				TextView share = (TextView) popView.findViewById(R.id.share);
				TextView setting = (TextView) popView.findViewById(R.id.setting);

				uninstall.setOnClickListener(listener);
				start.setOnClickListener(listener);
				share.setOnClickListener(listener);
				setting.setOnClickListener(listener);
				// ??????PopupWindow
				// PopupWindow window=new PopupWindow(?????? ??? ???);
				// layout_wdith layout_height
				window = new PopupWindow(popView,//
						LinearLayout.LayoutParams.WRAP_CONTENT, //
						LinearLayout.LayoutParams.WRAP_CONTENT);
				// window?????????????????? ??? ??????
				window.setOutsideTouchable(true);
				// ?????????
				window.setFocusable(true);
				window.setBackgroundDrawable(new ColorDrawable(android.R.color.transparent));
			} else {
				window.dismiss();
			}

			// 1.?????????????????? AlphaAnimation 0.5 1.0 2.ScaleAnimation????????????
			// AnimationSet ???????????? ???????????????????????????????????????
			AnimationSet set = new AnimationSet(true);
//			AlphaAnimation alpha=new AlphaAnimation(??????, ??????)
			AlphaAnimation alpha=new AlphaAnimation(0.5f, 1.0f);
			alpha.setDuration(1000);
			set.addAnimation(alpha);
			//RELATIVE_TO_SELF View ???????????? 1.0 ??? 1.0  0.5
			
			ScaleAnimation scale=new ScaleAnimation(
					0.5f, 1.0f, 0.5f, 1.0f,  //????????????
					ScaleAnimation.RELATIVE_TO_SELF, 0f, ScaleAnimation.RELATIVE_TO_SELF, 0.5f) ; //??????
			scale.setDuration(1000);
			set.addAnimation(scale);
			popView.startAnimation(set);
			
			

			int[] location = new int[2];
			// ????????????
			view.getLocationInWindow(location);
			// ?????????????????? parent??????????????????
			window.showAtLocation(view, Gravity.TOP | Gravity.LEFT, 60, location[1] + 10);

		}
	}

	private class MyAppInfoAdapter extends BaseAdapter {
		// ??????????????????
		@Override
		public int getCount() {
			return userApps.size() + sysApps.size() + 2;
		}

		// ?????????????????????
		@Override
		public int getViewTypeCount() {
			return 2; // 0 ????????? 1 ????????????
		}

		// ?????? ?????? ??????????????????
		@Override
		public int getItemViewType(int position) {// -
			if (position == 0)// ??????????????????
			{
				return 0;// ??????0
			}

			if (position == (1 + userApps.size()))// ?????????????????? 1+userApps.size();
			{
				return 0;// ??????0
			}

			return 1;// ??????????????????
		}

		class ViewHolder {
			TextView title;
			TextView appname;
			TextView applocation;
			TextView size;
			ImageView icon;

		}

		// 1.??? ???????????? ???????????? ???????????????
		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			// ?????????????????????????????????
			int type = getItemViewType(position);
			if (type == 0) {// ??????
				convertView = setTitle(position, convertView);
				return convertView;// ??????????????????????????????
			} else if (type == 1) {// ?????? ??????
				convertView = setAppDetail(position, convertView);
				return convertView;
			}
			return null;
		}

		// ????????????????????????
		private View setAppDetail(int position, View convertView) {
			// ????????????
			ViewHolder holder = null;
			if (convertView == null) {
				holder = new ViewHolder();
				convertView = View.inflate(getBaseContext(), R.layout.item_app_detail, null);
				holder.icon = (ImageView) convertView.findViewById(R.id.icon);
				holder.appname = (TextView) convertView.findViewById(R.id.appname);
				holder.applocation = (TextView) convertView.findViewById(R.id.applocation);
				holder.size = (TextView) convertView.findViewById(R.id.size);
				convertView.setTag(holder);// ??????
			} else {
				holder = (ViewHolder) convertView.getTag();
			}

			AppInfo info = getAppInfoByPosition(position);
			holder.icon.setImageDrawable(info.icon);
			holder.appname.setText(info.name);
			holder.applocation.setText("????????????");
			holder.size.setText(formate(info.size));
			return convertView;
		}

		// ????????????
		private View setTitle(int position, View convertView) {
			// ????????????
			ViewHolder holder = null;
			if (convertView == null) {
				holder = new ViewHolder();
				convertView = View.inflate(getBaseContext(), R.layout.item_app_title, null);
				holder.title = (TextView) convertView.findViewById(R.id.title);
				convertView.setTag(holder);// ??????
			} else {
				holder = (ViewHolder) convertView.getTag();
			}
			if (position == 0) {
				holder.title.setText("????????????:" + userApps.size());
			} else {
				holder.title.setText("????????????:" + sysApps.size());
			}
			return convertView;
		}

		@Override
		public Object getItem(int position) {
			return null;
		}

		@Override
		public long getItemId(int position) {
			return 0;
		}

	}

	/**
	 * ??????????????????
	 */
	private void showSpaceInfo() {
		// /data ??????????????????
		// /mnt/sdcard sd???????????? ?????? MOUNT WRITE_EXTERNAL_STORAGE getFreeSpace????????????
		long innerSize = Environment.getDataDirectory().getFreeSpace();
		long sdSize = Environment.getExternalStorageDirectory().getFreeSpace();
		System.out.println(innerSize);
		System.out.println(sdSize);

		innerspace.setText("??????????????????:" + formate(innerSize));
		sdspace.setText("SD????????????:" + formate(sdSize));
	}

	private String formate(long size) {
		return Formatter.formatFileSize(this, size);// K M G
	}
}
