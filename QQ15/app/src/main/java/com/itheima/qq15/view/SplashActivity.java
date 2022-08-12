package com.itheima.qq15.view;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ObjectAnimator;
import android.os.Bundle;
import android.widget.ImageView;

import com.itheima.qq15.MainActivity;
import com.itheima.qq15.R;
import com.itheima.qq15.presenter.ISplashPresenter;
import com.itheima.qq15.presenter.impl.SplashPresenter;

import butterknife.ButterKnife;
import butterknife.InjectView;

public class SplashActivity extends BaseActivity implements SplashView{

    private static final long DURATION = 2000;
    private ISplashPresenter mSplashPresenter;
    @InjectView(R.id.iv_splash)
    ImageView mIvSplash;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);
        ButterKnife.inject(this);

        mSplashPresenter = new SplashPresenter(this);

        /**
         * 1. 判断是否已经登录了
         *
         * 2. 如果登录了，直接进入MainActivity
         *
         * 3. 否则闪屏2秒后（渐变动画），进入LoginActivity
         */
        mSplashPresenter.checkLogined();

    }

    @Override
    public void onCheckedLogin(boolean isLogined) {
        if (isLogined){
            startActivity(MainActivity.class,true);
        }else {
//            否则闪屏2秒后（渐变动画），进入LoginActivity
            ObjectAnimator alpha = ObjectAnimator.ofFloat(mIvSplash, "alpha", 0, 1).setDuration(DURATION);
            alpha.start();
            alpha.addListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    super.onAnimationEnd(animation);
                    startActivity(LoginActivity.class,true);
                }
            });

        }
    }
}
