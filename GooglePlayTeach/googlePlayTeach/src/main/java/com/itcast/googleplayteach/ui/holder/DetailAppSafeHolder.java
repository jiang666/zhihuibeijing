package com.itcast.googleplayteach.ui.holder;

import java.util.ArrayList;

import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup.LayoutParams;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.itcast.googleplayteach.R;
import com.itcast.googleplayteach.domain.AppInfo;
import com.itcast.googleplayteach.domain.AppInfo.SafeInfo;
import com.itcast.googleplayteach.http.HttpHelper;
import com.itcast.googleplayteach.utils.BitmapHelper;
import com.itcast.googleplayteach.utils.UIUtils;
import com.lidroid.xutils.BitmapUtils;
import com.nineoldandroids.animation.Animator;
import com.nineoldandroids.animation.Animator.AnimatorListener;
import com.nineoldandroids.animation.ValueAnimator;
import com.nineoldandroids.animation.ValueAnimator.AnimatorUpdateListener;

/**
 * 详情页-安全标识
 * 
 * @author Kevin
 */
public class DetailAppSafeHolder extends BaseHolder<AppInfo> {

	private ImageView[] ivSafes;// 安全标识的控件数组
	private LinearLayout[] llDes;// 安全描述控件数组
	private ImageView[] ivDes;// 安全描述图片控件数组
	private TextView[] tvDes;// 安全描述文字控件数组
	private LinearLayout llDesRoot;// 安全描述根布局

	private BitmapUtils mBitmapUtils;

	private int mDesRootHeight;// 安全描述整体布局高度
	private LayoutParams mParams;// 安全描述整体控件布局参数

	private boolean isExpanded = false;// 标记当前安全描述打开还是关闭的状态
	private ImageView ivArrow;// 安全标识小箭头

	@Override
	public View initView() {
		View view = View.inflate(UIUtils.getContext(),
				R.layout.layout_detail_safeinfo, null);

		ivSafes = new ImageView[4];
		ivSafes[0] = (ImageView) view.findViewById(R.id.iv_safe1);
		ivSafes[1] = (ImageView) view.findViewById(R.id.iv_safe2);
		ivSafes[2] = (ImageView) view.findViewById(R.id.iv_safe3);
		ivSafes[3] = (ImageView) view.findViewById(R.id.iv_safe4);

		llDes = new LinearLayout[4];
		llDes[0] = (LinearLayout) view.findViewById(R.id.ll_des1);
		llDes[1] = (LinearLayout) view.findViewById(R.id.ll_des2);
		llDes[2] = (LinearLayout) view.findViewById(R.id.ll_des3);
		llDes[3] = (LinearLayout) view.findViewById(R.id.ll_des4);

		ivDes = new ImageView[4];
		ivDes[0] = (ImageView) view.findViewById(R.id.iv_des1);
		ivDes[1] = (ImageView) view.findViewById(R.id.iv_des2);
		ivDes[2] = (ImageView) view.findViewById(R.id.iv_des3);
		ivDes[3] = (ImageView) view.findViewById(R.id.iv_des4);

		tvDes = new TextView[4];
		tvDes[0] = (TextView) view.findViewById(R.id.tv_des1);
		tvDes[1] = (TextView) view.findViewById(R.id.tv_des2);
		tvDes[2] = (TextView) view.findViewById(R.id.tv_des3);
		tvDes[3] = (TextView) view.findViewById(R.id.tv_des4);

		ivArrow = (ImageView) view.findViewById(R.id.iv_arrow);

		// 设置安全描述整体布局高度为0, 达到隐藏效果
		llDesRoot = (LinearLayout) view.findViewById(R.id.ll_des_root);
		mParams = llDesRoot.getLayoutParams();
		mParams.height = 0;
		llDesRoot.setLayoutParams(mParams);

		// 设置安全标识整体控件的点击事件
		view.findViewById(R.id.rl_des_root).setOnClickListener(
				new OnClickListener() {

					@Override
					public void onClick(View v) {
						// 展开或者收起安全描述信息
						toggle();
					}
				});

		mBitmapUtils = BitmapHelper.getBitmapUtils();

		return view;
	}

	@Override
	public void refreshView(AppInfo data) {
		if (data != null) {
			ArrayList<SafeInfo> safe = data.safe;
			if (safe != null) {
				for (int i = 0; i < 4; i++) {
					if (i < safe.size()) {
						SafeInfo safeInfo = safe.get(i);

						ivSafes[i].setVisibility(View.VISIBLE);
						llDes[i].setVisibility(View.VISIBLE);

						tvDes[i].setText(safeInfo.safeDes);
						mBitmapUtils.display(ivSafes[i], HttpHelper.URL
								+ "image?name=" + safeInfo.safeUrl);
						mBitmapUtils.display(ivDes[i], HttpHelper.URL
								+ "image?name=" + safeInfo.safeDesUrl);
					} else {
						ivSafes[i].setVisibility(View.GONE);
						llDes[i].setVisibility(View.GONE);
					}
				}
			}

			// 计算安全描述布局的整体高度
			llDesRoot.measure(0, 0);
			mDesRootHeight = llDesRoot.getMeasuredHeight();
		}
	}

	/**
	 * 展开或者收起安全描述信息
	 */
	protected void toggle() {
		// 需要引入nineoldandroids.jar,可以兼容api11以下的版本
		ValueAnimator animator = null;
		if (isExpanded) {
			// 收起描述信息
			isExpanded = false;
			// 初始化按指定值变化的动画器, 布局高度从mDesRootHeight变化到0,此方法调用,并开启动画之后,
			// 会将最新的高度值不断回调在onAnimationUpdate方法中,在onAnimationUpdate中更新布局高度
			animator = ValueAnimator.ofInt(mDesRootHeight, 0);
		} else {
			// 展开描述信息
			isExpanded = true;
			// 初始化按指定值变化的动画器, 布局高度从0变化到mDesRootHeight
			animator = ValueAnimator.ofInt(0, mDesRootHeight);
		}

		// 设置动画更新的监听
		animator.addUpdateListener(new AnimatorUpdateListener() {

			// 在动画开始以后,每次动画有了最新的状态都会回调此方法
			@Override
			public void onAnimationUpdate(ValueAnimator valueAnimator) {
				// 获取当前最新的高度信息
				Integer height = (Integer) valueAnimator.getAnimatedValue();
				mParams.height = height;
				// 更新安全描述的高度
				llDesRoot.setLayoutParams(mParams);
			}
		});

		// 设置动画监听
		animator.addListener(new AnimatorListener() {

			@Override
			public void onAnimationStart(Animator arg0) {
				// 动画开始
			}

			@Override
			public void onAnimationRepeat(Animator arg0) {
				// 动画重复
			}

			@Override
			public void onAnimationEnd(Animator arg0) {
				// 动画结束
				// 更新安全标识小箭头方向
				if (isExpanded) {
					ivArrow.setImageResource(R.drawable.arrow_up);
				} else {
					ivArrow.setImageResource(R.drawable.arrow_down);
				}
			}

			@Override
			public void onAnimationCancel(Animator arg0) {
				// 动画取消
			}
		});

		// 设置动画时间
		animator.setDuration(200);
		// 开启动画
		animator.start();
	}

}
