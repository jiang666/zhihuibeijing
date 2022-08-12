package com.itheima.qq15.view;

import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.support.design.widget.TextInputLayout;
import android.view.KeyEvent;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.itheima.qq15.R;
import com.itheima.qq15.presenter.IRegistPresenter;
import com.itheima.qq15.presenter.impl.RegistPresenter;
import com.itheima.qq15.utils.StringUtils;

import butterknife.ButterKnife;
import butterknife.InjectView;
import butterknife.OnClick;

public class RegistActivity extends BaseActivity implements TextView.OnEditorActionListener, RegistView {

    @InjectView(R.id.et_username)
    EditText mEtUsername;
    @InjectView(R.id.til_username)
    TextInputLayout mTilUsername;
    @InjectView(R.id.et_pwd)
    EditText mEtPwd;
    @InjectView(R.id.til_pwd)
    TextInputLayout mTilPwd;
    @InjectView(R.id.btn_regist)
    Button mBtnRegist;

    private IRegistPresenter mRegistPresenter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            Window window = getWindow();
            window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS
                    | WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION);
            window.getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                    | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                    | View.SYSTEM_UI_FLAG_LAYOUT_STABLE);
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.setStatusBarColor(Color.TRANSPARENT);
//            window.setNavigationBarColor(Color.TRANSPARENT);
        }


        setContentView(R.layout.activity_regist);
        ButterKnife.inject(this);
        mRegistPresenter = new RegistPresenter(this);
        //给UI绑定事件
        mEtPwd.setOnEditorActionListener(this);
    }

    @OnClick(R.id.btn_regist)
    public void onClick() {
        regist();
    }

    private void regist() {
        String username = mEtUsername.getText().toString().trim();
        String pwd = mEtPwd.getText().toString().trim();

        if (!StringUtils.checkUsername(username)){
            mTilUsername.setErrorEnabled(true);
            mTilUsername.setError("用户名不合法");

            mEtUsername.requestFocus(View.FOCUS_RIGHT);

            return;
        }else {
            mTilUsername.setErrorEnabled(false);
        }
        if (!StringUtils.checkPwd(pwd)){
            mTilPwd.setErrorEnabled(true);
            mTilPwd.setError("密码不合法");

            mEtPwd.requestFocus(View.FOCUS_RIGHT);
            return;
        }else{
            mTilPwd.setErrorEnabled(false);
        }
        showDialog("正在注册...");
        mRegistPresenter.regist(username,pwd);
    }
    @Override
    public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
        if (v.getId() == R.id.et_pwd) {
            if (actionId == EditorInfo.IME_ACTION_DONE) {
                regist();
                return true;
            }
        }
        return false;
    }

    @Override
    public void onRegist(String username, String pwd, boolean isSuccess, String msg) {
        hideDialog();
        if (isSuccess){
            /**
             * 将注册成功的数据保存到本地
             *
             * 跳转到登录界面
             */
            saveUser(username, pwd);

            startActivity(LoginActivity.class,true);
        }else {
            /**
             * 弹吐司，告诉用户失败了
             */
            showToast("注册失败："+msg);
        }
    }
}
