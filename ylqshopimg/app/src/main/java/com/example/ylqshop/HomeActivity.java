package com.example.ylqshop;

import android.graphics.Bitmap;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;

/**
 * 主页
 */
public class HomeActivity extends BaseActivity {

    private Button bt0, bt1, bt2, bt3;
    public static Bitmap b1, b2, b3, b4;
    public static boolean needApi = false;
    private ImageView iv_back;

    @Override
    protected int getViewId() {
        return R.layout.activity_home;
    }

    @Override
    protected void initView() {
        bt0 = findViewById(R.id.bt0);
        bt1 = findViewById(R.id.bt1);
        bt2 = findViewById(R.id.bt2);
        bt3 = findViewById(R.id.bt3);
        iv_back = (ImageView) findViewById(R.id.iv_back);
        if (Build.VERSION.SDK_INT >= 23 && !permissionCheck(1)) {
            ActivityCompat.requestPermissions(HomeActivity.this, permissionManifestCamera, 0x1);
        }
    }

    @Override
    protected void click() {
        bt0.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (isFastDoubleClick()) {
                    return;
                }
                Bundle bundle = new Bundle();
                bundle.putInt("type", 1);
                bundle.putInt("sfztype", 1);//1正面 2反面
                changeAct(bundle, CameraPrevActivity.class);
            }
        });

        bt1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (isFastDoubleClick()) {
                    return;
                }
                Bundle bundle = new Bundle();
                bundle.putInt("type", 1);
                bundle.putInt("sfztype", 2);//1正面 2反面
                changeAct(bundle, CameraPrevActivity.class);
            }
        });

        bt2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (isFastDoubleClick()) {
                    return;
                }
                Bundle bundle = new Bundle();
                bundle.putInt("type", 2);
                changeAct(bundle, CameraPrevActivity.class);
            }
        });

        bt3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (isFastDoubleClick()) {
                    return;
                }
                Bundle bundle = new Bundle();
                bundle.putInt("type", 3);
                changeAct(bundle, CameraPrevActivity.class);
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        iv_back.setImageBitmap(b1);
        Log.e("=====", "onResume");
    }
}
