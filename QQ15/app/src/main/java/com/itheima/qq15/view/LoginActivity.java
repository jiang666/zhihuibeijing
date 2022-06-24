package com.itheima.qq15.view;

import android.Manifest;
import android.content.Intent;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.TextInputLayout;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.PermissionChecker;
import android.view.KeyEvent;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.itheima.qq15.MainActivity;
import com.itheima.qq15.R;
import com.itheima.qq15.presenter.LoginPresenter;
import com.itheima.qq15.presenter.impl.LoginPresenterImpl;
import com.itheima.qq15.utils.StringUtils;

import butterknife.ButterKnife;
import butterknife.InjectView;
import butterknife.OnClick;

public class LoginActivity extends BaseActivity implements TextView.OnEditorActionListener ,LoginView{

    private static final int REQUEST_SDCARD = 1;

    @InjectView(R.id.et_username)
    EditText mEtUsername;
    @InjectView(R.id.til_username)
    TextInputLayout mTilUsername;
    @InjectView(R.id.et_pwd)
    EditText mEtPwd;
    @InjectView(R.id.til_pwd)
    TextInputLayout mTilPwd;
    @InjectView(R.id.btn_login)
    Button mBtnLogin;
    @InjectView(R.id.tv_newuser)
    TextView mTvNewuser;

    private LoginPresenter mLoginPresenter;

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
        setContentView(R.layout.activity_login);
        ButterKnife.inject(this);
        /**
         * 数据的回显
         */
        mEtUsername.setText(getUserName());
        mEtPwd.setText(getPwd());
        mEtPwd.setOnEditorActionListener(this);

        mLoginPresenter = new LoginPresenterImpl(this);
    }

    /**
     * 当再次startActivity的时候，接收新的Intent对象
     * 调用的前提是该启动模式是singleTask，或者singleTop但是他得在最上面才有效
     * @param intent
     */
    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        mEtUsername.setText(getUserName());
        mEtPwd.setText(getPwd());
    }

    @OnClick({R.id.btn_login, R.id.tv_newuser})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.btn_login:
                login();
                break;
            case R.id.tv_newuser:
            startActivity(RegistActivity.class,false);

                break;
        }
    }

    @Override
    public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
        if (v.getId()==R.id.et_pwd){
            if (actionId== EditorInfo.IME_ACTION_DONE){
                login();
            }
        }
        return false;
    }

    private void login() {
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
        /**
         * 1. 动态申请权限
         */
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE)!= PermissionChecker.PERMISSION_GRANTED){
            ActivityCompat.requestPermissions(this,new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},REQUEST_SDCARD);
            return;
        }

        showDialog("正在玩命登录中...");

        mLoginPresenter.login(username,pwd);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode==REQUEST_SDCARD){
            if (grantResults[0]==PermissionChecker.PERMISSION_GRANTED){
                //被授权了
                login();
            }else{
                showToast("没有给予该应用权限，不让你用了");
            }
        }
    }

    @Override
    public void onLogin(String username, String pwd, boolean success, String msg) {
        hideDialog();
        if (success){
            /**
             * 1.保存用户
             * 2. 跳转到主界面
             */
            saveUser(username, pwd);
            startActivity(MainActivity.class,true);
        }else {
            /**
             * 1.Toast
             */
            showToast("登录失败了："+msg);
        }
    }
}
