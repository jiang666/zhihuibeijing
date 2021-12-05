package com.itcast.googleplayteach.ui.holder;

import java.util.ArrayList;

import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.ImageView;
import android.widget.ImageView.ScaleType;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

import com.itcast.googleplayteach.R;
import com.itcast.googleplayteach.http.HttpHelper;
import com.itcast.googleplayteach.utils.BitmapHelper;
import com.itcast.googleplayteach.utils.UIUtils;
import com.lidroid.xutils.BitmapUtils;

public class HomeHeaderHolder extends BaseHolder<ArrayList<String>> {

	private ArrayList<String> mList;

	private ViewPager mViewPager;
	private LinearLayout mIndicator;

	private int mPreviousPos;// 上一个被选中圆点的位置

	@Override
	public View initView() {
		// 头布局的根布局
		RelativeLayout header = new RelativeLayout(UIUtils.getContext());

		// 根布局布局参数
		AbsListView.LayoutParams params = new AbsListView.LayoutParams(
				AbsListView.LayoutParams.MATCH_PARENT,
				UIUtils.getDimen(R.dimen.list_header_height));
		header.setLayoutParams(params);

		// 初始化ViewPager
		mViewPager = new ViewPager(UIUtils.getContext());
		mViewPager.setLayoutParams(new RelativeLayout.LayoutParams(
				RelativeLayout.LayoutParams.MATCH_PARENT,
				RelativeLayout.LayoutParams.MATCH_PARENT));

		// 将ViewPager添加给根布局
		header.addView(mViewPager);

		// 页码指示器
		mIndicator = new LinearLayout(UIUtils.getContext());
		// 设置边距
		int padding = UIUtils.dip2px(5);
		mIndicator.setPadding(padding, padding, padding, padding);

		// 初始化页码指示器布局参数
		RelativeLayout.LayoutParams iParams = new RelativeLayout.LayoutParams(
				RelativeLayout.LayoutParams.WRAP_CONTENT,
				RelativeLayout.LayoutParams.WRAP_CONTENT);
		// 设置当前线性布局相对于父控件的位置
		iParams.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM);
		iParams.addRule(RelativeLayout.ALIGN_PARENT_RIGHT);

		mIndicator.setLayoutParams(iParams);

		// 将页码指示器添加给根布局
		header.addView(mIndicator);

		mViewPager.setOnPageChangeListener(new OnPageChangeListener() {

			@Override
			public void onPageSelected(int arg0) {
				int pos = arg0 % mList.size();

				// 将当前圆点设置为选中样式
				ImageView view = (ImageView) mIndicator.getChildAt(pos);
				view.setImageResource(R.drawable.indicator_selected);

				if (pos != mPreviousPos) {
					// 将上一个圆点设置为默认样式
					ImageView prView = (ImageView) mIndicator
							.getChildAt(mPreviousPos);
					prView.setImageResource(R.drawable.indicator_normal);
				}

				mPreviousPos = pos;
			}

			@Override
			public void onPageScrolled(int arg0, float arg1, int arg2) {

			}

			@Override
			public void onPageScrollStateChanged(int arg0) {

			}
		});

		return header;
	}

	@Override
	public void refreshView(ArrayList<String> data) {
		mList = data;
		mViewPager.setAdapter(new MyPagerAdapter());

		mIndicator.removeAllViews();// 保险期间,先清除所有子view

		LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
				LinearLayout.LayoutParams.WRAP_CONTENT,
				LinearLayout.LayoutParams.WRAP_CONTENT);
		for (int i = 0; i < mList.size(); i++) {
			ImageView view = new ImageView(UIUtils.getContext());
			if (i == 0) {
				view.setImageResource(R.drawable.indicator_selected);
			} else {
				view.setImageResource(R.drawable.indicator_normal);
				params.leftMargin = UIUtils.dip2px(3);// 设置圆点间距
			}

			mIndicator.addView(view, params);
		}

		// 设置viewpager滑动的初始位置
		mViewPager.setCurrentItem(mList.size() * 1000);

		// 开启自动轮播效果
		new RunnableTask().start();
	}

	class RunnableTask implements Runnable {

		public void start() {
			// 移除之前遗留的任务(handler只有一个,但HomeFragment有可能多次被创建,
			// 从而导致消息被重复发送,所以需要先把之前的消息移除掉)
			UIUtils.getHandler().removeCallbacksAndMessages(null);
			// 发送延时2秒的任务
			UIUtils.getHandler().postDelayed(this, 2000);
		}

		@Override
		public void run() {
			// 跳到viewpager下一个页面
			int currentItem = mViewPager.getCurrentItem();
			currentItem++;
			mViewPager.setCurrentItem(currentItem);

			// 继续发送延时两秒的任务, 形成闭环, 达到循环执行的效果
			UIUtils.getHandler().postDelayed(this, 2000);
		}

	}

	class MyPagerAdapter extends PagerAdapter {

		private BitmapUtils mBitmapUtils;

		public MyPagerAdapter() {
			mBitmapUtils = BitmapHelper.getBitmapUtils();
			mBitmapUtils.configDefaultLoadingImage(R.drawable.subject_default);
		}

		@Override
		public int getCount() {
			return Integer.MAX_VALUE;
		}

		@Override
		public boolean isViewFromObject(View arg0, Object arg1) {
			return arg0 == arg1;
		}

		@Override
		public Object instantiateItem(ViewGroup container, int position) {
			int pos = position % mList.size();

			ImageView view = new ImageView(UIUtils.getContext());
			view.setScaleType(ScaleType.FIT_XY);
			mBitmapUtils.display(view,
					HttpHelper.URL + "image?name=" + mList.get(pos));

			container.addView(view);
			return view;
		}

		@Override
		public void destroyItem(ViewGroup container, int position, Object object) {
			container.removeView((View) object);
		}

	}

}
