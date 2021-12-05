package com.itcast.googleplayteach.ui.holder;

import android.view.View;
import android.view.View.MeasureSpec;
import android.view.View.OnClickListener;
import android.view.ViewGroup.LayoutParams;
import android.widget.ImageView;
import android.widget.ScrollView;
import android.widget.TextView;

import com.itcast.googleplayteach.R;
import com.itcast.googleplayteach.domain.AppInfo;
import com.itcast.googleplayteach.utils.UIUtils;
import com.nineoldandroids.animation.Animator;
import com.nineoldandroids.animation.Animator.AnimatorListener;
import com.nineoldandroids.animation.ValueAnimator;
import com.nineoldandroids.animation.ValueAnimator.AnimatorUpdateListener;

/**
 * 详情页-应用描述
 * 
 * @author Kevin
 */
public class DetailAppDesHolder extends BaseHolder<AppInfo> {

	private TextView tvDes;
	private TextView tvAuthor;
	private ImageView ivArrow;

	private LayoutParams mParams;
	private boolean isOpen;

	@Override
	public View initView() {
		View view = View.inflate(UIUtils.getContext(),
				R.layout.layout_detail_desinfo, null);
		tvDes = (TextView) view.findViewById(R.id.tv_detail_des);
		tvAuthor = (TextView) view.findViewById(R.id.tv_detail_author);
		ivArrow = (ImageView) view.findViewById(R.id.iv_arrow);
		view.findViewById(R.id.rl_detail_toggle).setOnClickListener(
				new OnClickListener() {

					@Override
					public void onClick(View v) {
						toggle();
					}
				});

		return view;
	}

	@Override
	public void refreshView(AppInfo data) {
		if (data != null) {
			tvDes.setText(data.des);
			tvAuthor.setText(data.author);

			// 获取截断后的高度
			int shortHeight = getShortHeight();

			// 将TextView高度设置为截断7行之后的高度
			mParams = tvDes.getLayoutParams();
			mParams.height = shortHeight;
			tvDes.setLayoutParams(mParams);
		}
	}

	/**
	 * 展开或收起描述信息
	 */
	protected void toggle() {
		int shortHeight = getShortHeight();
		int longHeight = getLongHeight();

		ValueAnimator animator = null;
		if (!isOpen) {
			isOpen = true;
			if (shortHeight < longHeight) {
				animator = ValueAnimator.ofInt(shortHeight, longHeight);
			}
		} else {
			isOpen = false;
			if (shortHeight < longHeight) {
				animator = ValueAnimator.ofInt(longHeight, shortHeight);
			}
		}

		if (animator != null) {
			animator.addUpdateListener(new AnimatorUpdateListener() {

				@Override
				public void onAnimationUpdate(ValueAnimator arg0) {
					int height = (Integer) arg0.getAnimatedValue();
					mParams.height = height;
					tvDes.setLayoutParams(mParams);
				}
			});

			animator.addListener(new AnimatorListener() {

				@Override
				public void onAnimationStart(Animator arg0) {

				}

				@Override
				public void onAnimationRepeat(Animator arg0) {

				}

				@Override
				public void onAnimationEnd(Animator arg0) {
					if (isOpen) {
						ivArrow.setImageResource(R.drawable.arrow_up);
					} else {
						ivArrow.setImageResource(R.drawable.arrow_down);
					}

					// 页面滑动到底端
					final ScrollView scrollView = getScrollView();
					scrollView.post(new Runnable() {

						@Override
						public void run() {
							scrollView.fullScroll(ScrollView.FOCUS_DOWN);
						}
					});
				}

				@Override
				public void onAnimationCancel(Animator arg0) {

				}
			});

			animator.setDuration(200);
			animator.start();
		}
	}

	/**
	 * 获取截断7行之后的高度
	 * 
	 * @return
	 */
	private int getShortHeight() {
		int measuredWidth = tvDes.getMeasuredWidth();

		// 结合模式和具体值,定义一个宽度和高度的参数
		int widthMeasureSpec = MeasureSpec.makeMeasureSpec(measuredWidth,
				MeasureSpec.EXACTLY);// 宽度填充屏幕,已经确定, 所以是EXACTLY
		int heightMeasureSpec = MeasureSpec.makeMeasureSpec(2000,
				MeasureSpec.AT_MOST);// 高度不确定, 模式是包裹内容, 有多高展示多高, 所以是AT_MOST.
										// 这里的2000是高度最大值, 也可以设置为屏幕高度

		// 模拟一个TextView
		TextView view = new TextView(UIUtils.getContext());
		view.setMaxLines(7);// 最多展示7行
		view.setText(getData().des);
		// tvDes得到的规则要作用在模拟的textView上,保持其高度一致
		view.measure(widthMeasureSpec, heightMeasureSpec);

		// 返回测量的高度
		return view.getMeasuredHeight();
	}

	/**
	 * 获取完整展示时的高度
	 * 
	 * @return
	 */
	private int getLongHeight() {
		int measuredWidth = tvDes.getMeasuredWidth();

		// 结合模式和具体值,定义一个宽度和高度的参数
		int widthMeasureSpec = MeasureSpec.makeMeasureSpec(measuredWidth,
				MeasureSpec.EXACTLY);// 宽度填充屏幕,已经确定, 所以是EXACTLY
		int heightMeasureSpec = MeasureSpec.makeMeasureSpec(2000,
				MeasureSpec.AT_MOST);// 高度不确定, 模式是包裹内容, 有多高展示多高, 所以是AT_MOST.
										// 这里的2000是高度最大值, 也可以设置为屏幕高度

		// 模拟一个TextView
		TextView view = new TextView(UIUtils.getContext());
		view.setText(getData().des);
		// tvDes得到的规则要作用在模拟的textView上,保持其高度一致
		view.measure(widthMeasureSpec, heightMeasureSpec);

		// 返回测量的高度
		return view.getMeasuredHeight();
	}

	/**
	 * 获取布局中的ScrollView对象 注意: 必须保证布局中有ScrollView, 否则会陷入死循环
	 * 
	 * @return
	 */
	private ScrollView getScrollView() {
		View parent = (View) tvDes.getParent();
		// 通过while循环,一层一层往上找, 直到找到ScrollView后结束
		while (!(parent instanceof ScrollView)) {
			parent = (View) parent.getParent();
		}

		return (ScrollView) parent;
	}

}
