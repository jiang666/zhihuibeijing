package com.itcast.googleplayteach.ui.fragment;

import java.util.ArrayList;
import java.util.Random;

import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.itcast.googleplayteach.http.protocol.HotProtocol;
import com.itcast.googleplayteach.ui.widget.MyFlowLayout;
import com.itcast.googleplayteach.ui.widget.LoadingPage.ResultState;
import com.itcast.googleplayteach.utils.DrawableUtils;
import com.itcast.googleplayteach.utils.UIUtils;

/**
 * 排行
 * 
 * @author Kevin
 * 
 */
public class HotFragment extends BaseFragment {

	private ArrayList<String> mList;

	@SuppressWarnings("deprecation")
	@Override
	public View onCreateSuccessView() {
		int padding = UIUtils.dip2px(10);
		// 为了使布局可以上下滑动,需要用ScrollView包装起来
		ScrollView scrollView = new ScrollView(UIUtils.getContext());
		// 设置ScrollView边距
		scrollView.setPadding(padding, padding, padding, padding);

		// 初始化自定义控件
		MyFlowLayout flow = new MyFlowLayout(UIUtils.getContext());
		// 水平间距
		//flow.setHorizontalSpacing(UIUtils.dip2px(6));
		// 竖直间距
		//flow.setVerticalSpacing(UIUtils.dip2px(10));

		// 根据接口返回的数据个数,动态添加TextView
		for (final String str : mList) {
			TextView view = new TextView(UIUtils.getContext());
			view.setText(str);
			view.setTextColor(Color.WHITE);
			view.setGravity(Gravity.CENTER);
			view.setPadding(padding, padding, padding, padding);
			view.setTextSize(TypedValue.COMPLEX_UNIT_SP, 16);

			// 设置随机文字颜色
			Random random = new Random();
			int r = 30 + random.nextInt(210);
			int g = 30 + random.nextInt(210);
			int b = 30 + random.nextInt(210);

			int color = 0xffcecece;// 按下后偏白的背景色

			// 根据默认颜色和按下颜色, 生成圆角矩形的状态选择器
			Drawable selector = DrawableUtils.getStateListDrawable(
					Color.rgb(r, g, b), color, UIUtils.dip2px(6));

			// 给TextView设置背景
			view.setBackgroundDrawable(selector);

			// 必须设置点击事件, TextView按下后颜色才会变化
			view.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {
					Toast.makeText(UIUtils.getContext(), str,
							Toast.LENGTH_SHORT).show();
				}
			});

			// 给自定义控件添加view对象
			flow.addView(view);
		}

		scrollView.addView(flow);

		return scrollView;
	}

	@Override
	public ResultState onLoad() {
		HotProtocol protocol = new HotProtocol();
		mList = protocol.getData(0);
		return check(mList);
	}
}
