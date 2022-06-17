package com.example.ylqshop;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.SystemClock;
import android.support.v4.content.PermissionChecker;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.Toast;
import com.jaeger.library.StatusBarUtil;
import java.util.List;

public abstract class BaseActivity extends AppCompatActivity {

    protected int mNoPermissionIndex = 0;
    protected final int PERMISSION_REQUEST_CAMEAR_CODE1 = 0x1;//上部的身份证
    protected final int PERMISSION_REQUEST_CAMEAR_CODE2 = 0x2;//下部的身份证
    protected final int PERMISSION_REQUEST_CAMEAR_CODE3 = 0x3;//拍照+相册

    //all
    protected final String[] permissionManifestAll = {
            Manifest.permission.CAMERA,
            Manifest.permission.WRITE_EXTERNAL_STORAGE,
            Manifest.permission.READ_EXTERNAL_STORAGE
//            ,Manifest.permission.ACCESS_FINE_LOCATION
    };

    //camera
    protected final String[] permissionManifestCamera = {
            Manifest.permission.CAMERA,
            Manifest.permission.WRITE_EXTERNAL_STORAGE,
            Manifest.permission.READ_EXTERNAL_STORAGE
    };

    protected long lastClickTime;

    protected abstract int getViewId();

    protected abstract void initView();

    protected abstract void click();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        StatusBarUtil.setLightMode(this);
        StatusBarUtil.setTransparent(this);
        super.onCreate(savedInstanceState);
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN);
        setContentView(getViewId());
        initView();
        click();
    }

    //检测是否有权限 1：camera
    protected boolean permissionCheck(int type) {
        int permissionCheck = PackageManager.PERMISSION_GRANTED;
        String permission;
        if (type == 1) {
            for (int i = 0; i < permissionManifestAll.length; i++) {
                permission = permissionManifestAll[i];
                mNoPermissionIndex = i;
                if (PermissionChecker.checkSelfPermission(this, permission) != PackageManager.PERMISSION_GRANTED) {
                    permissionCheck = PackageManager.PERMISSION_DENIED;
                }
            }
        }else if(type == 2){
            for (int i = 0; i < permissionManifestCamera.length; i++) {
                permission = permissionManifestCamera[i];
                mNoPermissionIndex = i;
                if (PermissionChecker.checkSelfPermission(this, permission)
                        != PackageManager.PERMISSION_GRANTED) {
                    permissionCheck = PackageManager.PERMISSION_DENIED;
                }
            }
        }
        if (permissionCheck != PackageManager.PERMISSION_GRANTED) {
            return false;
        } else {
            return true;
        }
    }

    protected boolean isFastDoubleClick() {
        long time = SystemClock.uptimeMillis();
        if (time - lastClickTime < 500) {
            return true;
        }
        lastClickTime = time;
        return false;
    }

    protected boolean isVisBottom(RecyclerView recyclerView) {
        LinearLayoutManager layoutManager = (LinearLayoutManager) recyclerView.getLayoutManager();
        //屏幕中最后一个可见子项的position
        int lastVisibleItemPosition = layoutManager.findLastVisibleItemPosition();
        //当前屏幕所看到的子项个数
        int visibleItemCount = layoutManager.getChildCount();
        //当前RecyclerView的所有子项个数
        int totalItemCount = layoutManager.getItemCount();
        //RecyclerView的滑动状态
        int state = recyclerView.getScrollState();
        if (visibleItemCount > 0 && lastVisibleItemPosition == totalItemCount - 1 && state == recyclerView.SCROLL_STATE_IDLE) {
//            LogUtils.logE("测试刷新: ", "刷新");
            return true;
        } else {
//            LogUtils.logE("测试刷新: ", "不刷新");
            return false;
        }
    }

    //传入3会变成3.0
    public String format2(double date) {
        String money = String.valueOf(date);
        String[] moneyArr = money.split("\\.");
        if(moneyArr.length==1){
            return moneyArr[0] + ".00";
        }
        else if(moneyArr[1].length()==1){
            return money + "0";
        }else if(moneyArr[1].length()==2){
            return money;
        }else{
            return moneyArr[0] + "." +moneyArr[1].substring(0,2);
        }
    }

    //检测是否有权限
    protected boolean permissionCheck() {
        int permissionCheck = PackageManager.PERMISSION_GRANTED;
        String permission;
        for (int i = 0; i < permissionManifestCamera.length; i++) {
            permission = permissionManifestCamera[i];
            mNoPermissionIndex = i;
            if (PermissionChecker.checkSelfPermission(this, permission)
                    != PackageManager.PERMISSION_GRANTED) {
                permissionCheck = PackageManager.PERMISSION_DENIED;
            }
        }
        if (permissionCheck != PackageManager.PERMISSION_GRANTED) {
            return false;
        } else {
            return true;
        }
    }

    //toast
    protected void showToast(String tip) {
        Toast.makeText(this.getApplicationContext(), tip, Toast.LENGTH_LONG).show();
    }

    //changeAct
    protected void changeAct(Bundle bundle, Class<?> toClass) {
        Intent intent = new Intent();
        if (null != bundle) {
            intent.putExtras(bundle);
        }
        intent.setClass(this, toClass);
        this.startActivity(intent);
    }

    //changeAct
    protected void changeAct(Bundle bundle, String actUrl) {
        Intent intent = new Intent(actUrl);
        if (null != bundle) {
            intent.putExtras(bundle);
        }
        this.startActivity(intent);
    }

    //显示软键盘
    protected void showKeyboard(EditText et_chat) {
        InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.showSoftInput(et_chat, InputMethodManager.SHOW_FORCED);
    }

    //隐藏软键盘
    protected void hideKeyboard(EditText et_chat) {
        InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(et_chat.getWindowToken(), 0);
    }

    //多输入控件的隐藏软键盘
    protected  void hideKeyboards(List<View> viewList) {
        if (viewList == null) return;
        InputMethodManager inputMethodManager = (InputMethodManager)getSystemService(Activity.INPUT_METHOD_SERVICE);
        for (View v : viewList) {
            inputMethodManager.hideSoftInputFromWindow(v.getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
        }
    }

    //设置控件的大小
    protected void setViewSize(View view,int width,int height){
        ViewGroup.LayoutParams vp = view.getLayoutParams();
        vp.width = width;
        vp.height = height;
    }
}
