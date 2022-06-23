package com.chm.bledemo.activity;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.Environment;
import android.support.annotation.Nullable;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.chm.bledemo.R;
import com.chm.bledemo.bleutils.BleController;
import com.chm.bledemo.bleutils.callback.OnReceiverCallback;
import com.chm.bledemo.bleutils.callback.OnWriteCallback;
import com.chm.bledemo.utils.HexUtil;
import com.eraare.epd.EPD;

import java.io.File;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

import static com.eraare.epd.EPD.getInstance;

/**
 * 类名: TestActivity
 * 作者: 陈海明
 * 时间: 2017-08-14 11:30
 * 描述: NULL
 */
public class TestActivity extends BaseActivity {

    private static final String TAG = "TestActivity";

    @BindView(R.id.toolbar)
    Toolbar toolbar;
    @BindView(R.id.btn_send)
    Button btnSend;
    @BindView(R.id.btn_senddiy)
    Button btnSenddiy;
    @BindView(R.id.tv_msg)
    TextView tvMsg;
    @BindView(R.id.ed_msg)
    EditText edMsg;

    //震动指令
    //private String mShockinstructions = "AA5502010202020232003c";
    //默认写入图片
    private String mShockinstructions = "ffffffffffffffffffffffffffffffffffff3fe0bfffbfffbfffbfffbfffbfffbfffbfffbfff0000";

    private String mShock = "65560002800180";
    private BleController mBleController;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_test);
        ButterKnife.bind(this);

        setToolbar();

        //获得实例
        mBleController = BleController.getInstance();

        // TODO 接收数据的监听
        mBleController.RegistReciveListener(TAG, new OnReceiverCallback() {
            @Override
            public void onReceiver(byte[] value) {
                tvMsg.setText(HexUtil.bytesToHexString(value));
            }
        });
        /* 获取单例 */ EPD epd = getInstance();
        /* 接口调用 */ String cmd = getInstance().command("black");
        String sdPath = Environment.getExternalStorageDirectory().getAbsolutePath();
        String filepath = sdPath + File.separator +"photos"+File.separator+"22222.jpg";
        File file = new File(filepath);
        Bitmap bm = null;
        if (file.exists()) {
            bm = BitmapFactory.decodeFile(filepath);
            if(bm != null){
                String vvvv = EPD.getInstance().handle(bm);
            }
        }
        Bitmap bitmap= BitmapFactory.decodeResource(this.getResources(), R.drawable.ic_bluetooth);
        String hhhh = EPD.getInstance().handle(bitmap);
        //String cmdss = getInstance().command("white");
        Log.e("=======","cmd  " + cmd +  "  " + hhhh );
    }

    /**
     * 设置标题栏
     */
    private void setToolbar() {
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(false);//设计隐藏标题
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);//设置显示返回键
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }

    //点击事件
    @OnClick({R.id.tv, R.id.btn_send, R.id.btn_senddiy, R.id.tv_msg,R.id.btn_sendpro})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.btn_senddiy:
                if (TextUtils.isEmpty(edMsg.getText().toString())){
                    Toast.makeText(TestActivity.this, "请输入消息!", Toast.LENGTH_SHORT).show();
                    return;
                }
                Write(edMsg.getText().toString());
                    break;
            case R.id.btn_send:
                Write(mShockinstructions);
                break;
            case R.id.btn_sendpro:
                Write(mShock);
                break;

        }
    }

    public void Write(String value){
        mBleController.WriteBuffer(value, new OnWriteCallback() {
            @Override
            public void onSuccess() {
                Toast.makeText(TestActivity.this, "ok", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onFailed(int state) {
                Toast.makeText(TestActivity.this, "fail", Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        //移除接收数据的监听
        mBleController.UnregistReciveListener(TAG);
        // TODO 断开连接
        //mBleController.CloseBleConn();
    }

}
