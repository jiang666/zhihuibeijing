package com.chm.bledemo.activity;

import android.bluetooth.BluetoothGatt;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.chm.bledemo.R;
import com.clj.fastble.BleManager;
import com.clj.fastble.callback.BleGattCallback;
import com.clj.fastble.callback.BleNotifyCallback;
import com.clj.fastble.callback.BleWriteCallback;
import com.clj.fastble.data.BleDevice;
import com.clj.fastble.exception.BleException;
import com.clj.fastble.utils.HexUtil;
import com.eraare.epd.EPD;
import com.orhanobut.logger.Logger;

import java.util.List;

public class CardActivity extends AppCompatActivity {
    public static final String DEFAULT_MAC = "FF:FF:FF:FF:FF:FF";
    public static final String UUID_CHARACTERISTIC_NOTIFY = "49535343-1E4D-4BD9-BA61-23C647249616";

    private Button mSendView;

    private BleDevice theBleDevice;
    private boolean isClickone = true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_card);
        BleManager.getInstance().init(getApplication());
        mSendView = findViewById(R.id.btn_send_card);
        mSendView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Bitmap bitmap;
                if(isClickone){
                    isClickone = false;
                    bitmap= BitmapFactory.decodeResource(getResources(), R.drawable.test);
                }else {
                    isClickone = true;
                    bitmap= BitmapFactory.decodeResource(getResources(), R.drawable.test1);
                }

                String data = EPD.getInstance().handle(bitmap);
                xShow(data);
            }
        });
    }

    private void xShow(final String data) {
        if (TextUtils.isEmpty(DEFAULT_MAC)) {
            Toast.makeText(getApplicationContext(), "请绑定设备", Toast.LENGTH_SHORT).show();
            return;
        }

        BleManager.getInstance()
                .enableLog(true)
                .setReConnectCount(3, 5000)
                .setSplitWriteNum(16)
                .setConnectOverTime(10000)
                .setOperateTimeout(5000);
        /* 1. 连接设备 */
        if (BleManager.getInstance().isConnected(DEFAULT_MAC)) {
            List<BleDevice> bleDevices = BleManager.getInstance().getAllConnectedDevice();
            for (BleDevice bleDevice : bleDevices) {
                if (TextUtils.equals(bleDevice.getMac(), DEFAULT_MAC)) {
                    theBleDevice = bleDevice;
                }
            }
        }
        if (theBleDevice != null) {
            ble_notify(data);
        } else {
            BleManager.getInstance().connect(DEFAULT_MAC, new BleGattCallback() {
                @Override
                public void onStartConnect() {
                    Logger.d("RefreshService:onStartConnect()");
                }

                @Override
                public void onConnectFail(BleDevice bleDevice, BleException exception) {
                    Logger.d("RefreshService:onConnectFail()");
                    theBleDevice = null;
                }

                @Override
                public void onConnectSuccess(BleDevice bleDevice, BluetoothGatt gatt, int status) {
                    Logger.d("RefreshService:onConnectSuccess()");
                    theBleDevice = bleDevice;
                    ble_notify(data);
                }

                @Override
                public void onDisConnected(boolean isActiveDisConnected, BleDevice bleDevice, BluetoothGatt gatt, int status) {
                    Logger.d("RefreshService:onDisConnected()");
                    theBleDevice = null;
                }
            });
        }
    }

    private void ble_notify(final String data) {
        BleManager.getInstance().stopNotify(theBleDevice, EPD.UUID_SERVICE_IMAGE, UUID_CHARACTERISTIC_NOTIFY);
        BleManager.getInstance().notify(
                theBleDevice,
                EPD.UUID_SERVICE_IMAGE,
                UUID_CHARACTERISTIC_NOTIFY,
                new BleNotifyCallback() {
                    @Override
                    public void onNotifySuccess() {
                        Logger.d("RefreshService:onNotifySuccess()");
                        String head = EPD.getInstance().command("head", "00");
                        ble_write_image(head);
                        delay(15);
                        ble_write_image(data);
                    }

                    @Override
                    public void onNotifyFailure(BleException exception) {
                        Logger.d("RefreshService:onNotifyFailure()");
                    }

                    @Override
                    public void onCharacteristicChanged(byte[] data) {
                        Logger.d("RefreshService:onCharacteristicChanged()");
                        String res = new String(data);
                        Logger.d(res);
                        if (TextUtils.equals("update", res)) {
                            BleManager.getInstance().stopNotify(theBleDevice, EPD.UUID_SERVICE_IMAGE, EPD.UUID_CHARACTERISTIC_IMAGE);
                            BleManager.getInstance().disconnect(theBleDevice);
                        }
                    }
                });
    }

    private void ble_write_image(String data) {
        ble_write(theBleDevice, EPD.UUID_SERVICE_IMAGE, EPD.UUID_CHARACTERISTIC_IMAGE, HexUtil.hexStringToBytes(data));
    }

    private void ble_write_command(String data, boolean isHex) {
        if (isHex) {
            ble_write(theBleDevice, EPD.UUID_SERVICE_NORMAL, EPD.UUID_CHARACTERISTIC_NORMAL, HexUtil.hexStringToBytes(data));
        } else {
            ble_write(theBleDevice, EPD.UUID_SERVICE_NORMAL, EPD.UUID_CHARACTERISTIC_NORMAL, data.getBytes());
        }
    }

    private void ble_write(BleDevice bleDevice, String uuidService, String uuidCharacteristic, byte[] data) {
        BleManager.getInstance().write(
                bleDevice,
                uuidService,
                uuidCharacteristic,
                data,
                new BleWriteCallback() {
                    @Override
                    public void onWriteSuccess(int current, int total, byte[] justWrite) {
                        Logger.d("RefreshService:onWriteSuccess()");
                    }

                    @Override
                    public void onWriteFailure(BleException exception) {
                        Logger.d("RefreshService:onWriteFailure()");
                    }
                });
    }

    private void delay(long duration) {
        try {
            Thread.sleep(duration);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}
