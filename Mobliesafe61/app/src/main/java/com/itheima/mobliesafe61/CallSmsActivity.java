package com.itheima.mobliesafe61;

import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AbsListView.OnScrollListener;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.itheima.mobliesafe61.bean.NumberInfo;
import com.itheima.mobliesafe61.db.dao.BlackNumberDao;
import com.itheima.mobliesafe61.service.CallSmsService;
import com.lidroid.xutils.ViewUtils;
import com.lidroid.xutils.view.annotation.ViewInject;
import com.lidroid.xutils.view.annotation.event.OnClick;

public class CallSmsActivity extends Activity {

	@ViewInject(R.id.add)
	Button add;
	@ViewInject(R.id.blacklistview)
	ListView blacklistview;
	@ViewInject(R.id.empty)
	LinearLayout empty;
	@ViewInject(R.id.loading)
	LinearLayout loading;
	private List<NumberInfo> list = new ArrayList<NumberInfo>();

	private BlackNumberDao dao = null;

	private int currpage = 0;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_call_sms);
		ViewUtils.inject(this);
		
		//测试
		startService(new Intent(this,CallSmsService.class));
		// // 静态UI:假数据f
		// for (int i = 0; i < 30; i++) {
		// NumberInfo info = new NumberInfo();
		// info.number = "100101" + i;
		// info.mode = "0";
		// list.add(info);
		// }
		dao = new BlackNumberDao(this);

		getData();

		// Button OnClickListener
		// OnScrollListener:滚动监听器
		blacklistview.setOnScrollListener(new OnScrollListener() {

			// scrollState IDLE空闲 滚动中..FlING
			@Override
			public void onScrollStateChanged(AbsListView view, int scrollState) {
				switch (scrollState) {
				case OnScrollListener.SCROLL_STATE_IDLE:
					// 可见 5 4
					if ((list.size() - 1) == blacklistview.getLastVisiblePosition()) {
						// 底部
						Toast.makeText(getBaseContext(), "底部", 0).show();
						// 加载下一页数据
						getData();
					}// 底部行的下标
					break;
				case OnScrollListener.SCROLL_STATE_FLING:

					break;
				case OnScrollListener.SCROLL_STATE_TOUCH_SCROLL:

					break;
				}
			}

			@Override
			public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
				// TODO Auto-generated method stub

			}
		});

	}

	// findviewbyid setOnclickListener
	@OnClick(R.id.add)
	public void add(View view) {
		// Dialog:弹出方式显示视图对象
		View dialogView = View.inflate(this, R.layout.dialog_add_blacknumber, null);
		final EditText inputnumber = (EditText) dialogView.findViewById(R.id.input_number);
		final CheckBox number = (CheckBox) dialogView.findViewById(R.id.number);
		final CheckBox sms = (CheckBox) dialogView.findViewById(R.id.sms);
		final Button btn_ok = (Button) dialogView.findViewById(R.id.btn_ok);
		final Button btn_cancel = (Button) dialogView.findViewById(R.id.btn_cancel);
		// 创建工具AlertDialog.Builder
		AlertDialog.Builder buidler = new AlertDialog.Builder(this);
		buidler.setView(dialogView);
		final Dialog dialog = buidler.create();
		dialog.setCancelable(true);
		dialog.show();// 显示

		btn_ok.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {

				NumberInfo info = new NumberInfo();
				// 号码与模式
				String numberStr = inputnumber.getText().toString().trim();
				info.number = numberStr;
				boolean mode_call = number.isChecked();
				boolean mode_sms = sms.isChecked();
				if (mode_call & mode_sms) {
					info.mode = "0";
				} else if (mode_call & !mode_sms) {
					info.mode = "1";
				} else if (!mode_call & mode_sms) {
					info.mode = "2";
				}
				dao.add(info);
				list.add(0, info);

				refreshListView();

				dialog.dismiss();
				// 列表刷新

			}
		});
		btn_cancel.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				dialog.dismiss();

			}
		});
	}

	private void getData() {
		loading.setVisibility(View.VISIBLE);
		empty.setVisibility(View.GONE);
		new Thread() {
			public void run() {
				try {
					Thread.sleep(2000);
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				++currpage;// 1
				if (currpage == 1) {
					list.clear();
				}
				List<NumberInfo> temp = dao.findPageByCurrentIndex(10, currpage);
				list.addAll(temp);
				// setAdapter();
				handler.sendEmptyMessage(0);
			};
		}.start();
	}

	private Handler handler = new Handler() {

		public void handleMessage(android.os.Message msg) {
			loading.setVisibility(View.GONE);
			refreshListView();
		};
	};
	MyBaseAdapter adapter;

	private void refreshListView() {
		if (list.size() == 0) {
			empty.setVisibility(View.VISIBLE);
		} else {
			// 内容 ALt+shift+M
			if (adapter == null) {
				adapter = new MyBaseAdapter();
				// BaseAdapter x4
				// |--ArrayAdapter x1
				blacklistview.setAdapter(adapter);
			} else {
				adapter.notifyDataSetChanged();// 整个列表刷新
			}

		}

	}

	private class MyBaseAdapter extends BaseAdapter {

		// 显示列表的行数
		@Override
		public int getCount() {
			return list.size();
		}

		// 返回行视图 显示指定 下标的数据
		// 缓存视图 ：convertView视图返回的带有数据的视图

		class ViewHolder {
			TextView number;
			TextView mode;
			ImageView delete;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {

			// xml
			// inflate--xml-->View
			System.out.println("getView----");
			// 内存 setAdapter 2.滑动
			ViewHolder holder = null;
			if (convertView == null)// 没有返回
			{
				System.out.println("---大量销耗内存");
				convertView = View.inflate(getBaseContext(), R.layout.item_call_sms, null);
				holder = new ViewHolder();
				// 查找 性能 ViewHoder:视图的javaBean
				holder.number = (TextView) convertView.findViewById(R.id.number);
				holder.mode = (TextView) convertView.findViewById(R.id.mode);
				holder.delete = (ImageView) convertView.findViewById(R.id.delete);
				convertView.setTag(holder);// 标记 get set方法
			} else {// 有可重用
				holder = (ViewHolder) convertView.getTag();

			}
			final NumberInfo info = list.get(position);
			holder.number.setText(info.number);
			if ("0".equals(info.mode)) {
				holder.mode.setText("全部拦截");
			} else if ("1".equals(info.mode)) {
				holder.mode.setText("电话");
			} else if ("2".equals(info.mode)) {
				holder.mode.setText("短信");
			}

			holder.delete.setOnClickListener(new View.OnClickListener() {
				@Override
				public void onClick(View v) {

					// 集合删除 号码
					list.remove(info);
					// 数据库删除
					dao.delete(info.id);

					refreshListView();

				}
			});
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
