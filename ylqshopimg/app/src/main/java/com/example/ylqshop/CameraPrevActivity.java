package com.example.ylqshop;

import android.Manifest;
import android.annotation.TargetApi;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.ImageFormat;
import android.graphics.Matrix;
import android.graphics.RectF;
import android.graphics.SurfaceTexture;
import android.hardware.camera2.CameraAccessException;
import android.hardware.camera2.CameraCaptureSession;
import android.hardware.camera2.CameraCharacteristics;
import android.hardware.camera2.CameraDevice;
import android.hardware.camera2.CameraManager;
import android.hardware.camera2.CaptureRequest;
import android.hardware.camera2.CaptureResult;
import android.hardware.camera2.TotalCaptureResult;
import android.hardware.camera2.params.MeteringRectangle;
import android.hardware.camera2.params.StreamConfigurationMap;
import android.media.ExifInterface;
import android.media.Image;
import android.media.ImageReader;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.NonNull;
import android.support.annotation.RequiresApi;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.FileProvider;
import android.util.Log;
import android.util.Size;
import android.util.SparseIntArray;
import android.view.KeyEvent;
import android.view.Surface;
import android.view.TextureView;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.TextView;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import rx.Subscription;

public class CameraPrevActivity extends BaseCameraActivity {

    private static final SparseIntArray ORIENTATION = new SparseIntArray();

    static {
        ORIENTATION.append(Surface.ROTATION_0, 90);
        ORIENTATION.append(Surface.ROTATION_90, 0);
        ORIENTATION.append(Surface.ROTATION_180, 270);
        ORIENTATION.append(Surface.ROTATION_270, 180);
    }

    private static final String TAG = "测试相机预览拍照";
    private static final String TAG_PREVIEW = "预览";
    private int type = 1;//1.身份证 2.营业执照 3.银行卡 4.营业执照预览界面
    private int sfztype = 1;//1.身份证正面 2.身份证反面
    private String mCameraId;//相机方向
    private Size mPreviewSize;//预览大小
    private CameraManager manager;
    private CameraDevice mCameraDevice;
    private ImageReader mImageReader;
    private CameraCaptureSession mCaptureSession;
    private CaptureRequest mPreviewRequest;
    private CaptureRequest.Builder mPreviewRequestBuilder;
    private Surface mPreviewSurface;
    private int screenWidth;
    private Subscription upload_biz_license_idcard;
    //控件
    private RelativeLayout rl_back;
    private AutoFitTextureView textureView;
    private TextView tv_pick_img;//相册
    private TextView tv_title;
    private TextView tv_des_bankcar;
    private ImageView takePicture;
    private TextView tv_des;//身份证控件下方的描述

    @Override
    protected int getViewId() {
        return R.layout.activity_camera_prev;
    }

    @Override
    protected void initView() {
        WindowManager wm = (WindowManager) this.getSystemService(Context.WINDOW_SERVICE);
        screenWidth = wm.getDefaultDisplay().getWidth();
        if (getIntent() != null) {
            Bundle bundle = getIntent().getExtras();
            if (bundle.containsKey("type")) {
                type = bundle.getInt("type");
            }
            if (bundle.containsKey("sfztype")) {
                sfztype = bundle.getInt("sfztype");
            }
        }
        tv_des_bankcar = findViewById(R.id.tv_des_bankcar);
        rl_back = findViewById(R.id.rl_back);
        takePicture = findViewById(R.id.takePicture);
        tv_pick_img = findViewById(R.id.tv_pick_img);
        tv_title = findViewById(R.id.tv_title);
        textureView = findViewById(R.id.textureView);
        findViewById(R.id.rl_layout).bringToFront();//最上层
        initCamera();
        //身份证
        if (type == 1) {
            tv_title.setText("上传身份证");
            findViewById((sfztype == 1) ? R.id.layout_sfz_up : R.id.layout_sfz_down).setVisibility(View.VISIBLE);
            //提示1
            tv_des = findViewById(R.id.tv_des);
            tv_des.setVisibility(View.VISIBLE);
            tv_des.setText((sfztype == 1) ? "拍照实例：身份证正面" : "拍照实例：身份证反面");
        }
        //营业执照
        else if (type == 2) {
            tv_title.setText("上传营业执照");
            findViewById(R.id.layout_yyzz).setVisibility(View.VISIBLE);
        }
        //银行卡
        else if (type == 3) {
            tv_title.setText("上传银行卡");
            tv_des_bankcar.setText("请将银行卡正面置于框内");
            tv_des_bankcar.setVisibility(View.VISIBLE);
            findViewById(R.id.layout_bankcar).setVisibility(View.VISIBLE);
        }
    }

    @Override
    protected void click() {
        //返回
        rl_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (isFastDoubleClick()) {
                    return;
                }
                finish();
            }
        });

        //拍照
        takePicture.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isFastDoubleClick()) {
                    return;
                }
                capture();
            }
        });

        //相册
        tv_pick_img.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (isFastDoubleClick()) {
                    return;
                }
                PhotoUtils.openPic(CameraPrevActivity.this, PhotoUtils.TakePic.CODE_GALLERY_REQUEST);
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            switch (requestCode) {
                //访问相册完成回调
                case PhotoUtils.TakePic.CODE_GALLERY_REQUEST:
                    if (PhotoUtils.hasSdcard()) {
                        Uri newUri = Uri.parse(PhotoUtils.getPath(this, data.getData()));
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                            newUri = FileProvider.getUriForFile(this, "com.example.ylqshop.fileprovider", new File(newUri.getPath()));
                        }
                        Bitmap bitmap = PhotoUtils.getBitmapFromUri(newUri, this);
                        int degree = readPictureDegree(newUri.getPath());
                        bitmap = rotaingImageView(degree, bitmap);
                        //身份证
                        if (type == 1) {
                            if (sfztype == 1) {
                                HomeActivity.b1 = bitmap;
                            } else {
                                HomeActivity.b2 = bitmap;
                            }
                            HomeActivity.needApi = true;
                            finish();
                        }
                        //营业执照初次上传
                        else if (type == 2) {
                            HomeActivity.b3 = bitmap;
                            HomeActivity.needApi = true;
                            finish();
                        }
                        //银行卡
                        else if (type == 3) {
                            upload_biz_license_idcard(bitmap, false);
                        }
                        //营业执照预览界面重新上传
                        else if (type == 4) {
                            HomeActivity.b4 = bitmap;
                            HomeActivity.needApi = true;
                            finish();
                        }
                    } else {
                        showToast("设备没有SD卡！");
                    }
                    break;
                default:
            }
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    protected void onDestroy() {
        closeCamera();
        if (upload_biz_license_idcard != null) {
            upload_biz_license_idcard.unsubscribe();
        }
        super.onDestroy();
    }

    //--------------------------工具类--------------------------
    //获取图片的旋转角度
    public int readPictureDegree(String path) {
        int degree = 0;
        try {
            ExifInterface exifInterface = new ExifInterface(path);
            int orientation = exifInterface.getAttributeInt(ExifInterface.TAG_ORIENTATION, ExifInterface.ORIENTATION_NORMAL);
            switch (orientation) {
                case ExifInterface.ORIENTATION_ROTATE_90:
                    degree = 90;
                    break;
                case ExifInterface.ORIENTATION_ROTATE_180:
                    degree = 180;
                    break;
                case ExifInterface.ORIENTATION_ROTATE_270:
                    degree = 270;
                    break;
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return degree;
    }

    //旋转图片
    public Bitmap rotaingImageView(int angle, Bitmap bitmap) {
        //旋转图片 动作
        Matrix matrix = new Matrix();
        matrix.postRotate(angle);
        // 创建新的图片
        Bitmap resizedBitmap = Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(), bitmap.getHeight(), matrix, true);
        return resizedBitmap;
    }

    //--------------------------拍照初始化--------------------------
    private void initCamera() {
        textureView.setSurfaceTextureListener(new TextureView.SurfaceTextureListener() {
            @RequiresApi(api = Build.VERSION_CODES.M)
            @Override
            public void onSurfaceTextureAvailable(SurfaceTexture surface, int width, int height) {
                //fixme 1.拿到相机参数
                manager = (CameraManager) getSystemService(Context.CAMERA_SERVICE);// 获取摄像头的管理者CameraManager
                try {
                    for (String cameraId : manager.getCameraIdList()) {// 遍历所有摄像头
                        CameraCharacteristics characteristics = manager.getCameraCharacteristics(cameraId);
                        if (characteristics.get(CameraCharacteristics.LENS_FACING) == CameraCharacteristics.LENS_FACING_FRONT)
                            continue;// 默认打开后置摄像头 - 忽略前置摄像头
                        // 获取StreamConfigurationMap，它是管理摄像头支持的所有输出格式和尺寸
                        StreamConfigurationMap map = characteristics.get(CameraCharacteristics.SCALER_STREAM_CONFIGURATION_MAP);
                        mPreviewSize = getOptimalSize(map.getOutputSizes(SurfaceTexture.class), width, height);
                        int orientation = getResources().getConfiguration().orientation;
                        if (orientation == Configuration.ORIENTATION_LANDSCAPE) {
                            textureView.setAspectRatio(mPreviewSize.getWidth(), mPreviewSize.getHeight());
                        } else {
                            textureView.setAspectRatio(mPreviewSize.getHeight(), mPreviewSize.getWidth());
                        }
                        mCameraId = cameraId;
                        break;
                    }
                } catch (CameraAccessException e) {
                    e.printStackTrace();
                }

                //fixme 2.配置当前屏幕方向
                configureTransform(width, height);

                //fixme 3.开启预览
                try {
                    if (ActivityCompat.checkSelfPermission(CameraPrevActivity.this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
                        return;
                    }
                    //参数：1.摄像头方向    2.相机的状态回调接口    3.确定Callback在哪个线程执行，为null的话就在当前线程执行
                    manager.openCamera(mCameraId, new CameraDevice.StateCallback() {
                        @Override
                        public void onOpened(CameraDevice camera) {
                            mCameraDevice = camera;
                            startPreview();//开启预览
                        }

                        @Override
                        public void onDisconnected(@NonNull CameraDevice camera) {
                            Log.e(TAG, "相机设备没有连接成功");
                        }

                        @Override
                        public void onError(@NonNull CameraDevice camera, int error) {
                            Log.e(TAG, "相机设备，预览失败，error=" + error);
                        }
                    }, null);
                } catch (CameraAccessException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onSurfaceTextureSizeChanged(SurfaceTexture surface, int width, int height) {
                configureTransform(width, height);
            }

            @Override
            public boolean onSurfaceTextureDestroyed(SurfaceTexture surface) {
                return false;
            }

            @Override
            public void onSurfaceTextureUpdated(SurfaceTexture surface) {

            }
        });
    }

    //预览方向变化
    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    private void configureTransform(int viewWidth, int viewHeight) {
        if (null == textureView || null == mPreviewSize) {
            return;
        }
        int rotation = getWindowManager().getDefaultDisplay().getRotation();
        Matrix matrix = new Matrix();
        RectF viewRect = new RectF(0, 0, viewWidth, viewHeight);
        RectF bufferRect = new RectF(0, 0, mPreviewSize.getHeight(), mPreviewSize.getWidth());
        float centerX = viewRect.centerX();
        float centerY = viewRect.centerY();
        if (Surface.ROTATION_90 == rotation || Surface.ROTATION_270 == rotation) {
            bufferRect.offset(centerX - bufferRect.centerX(), centerY - bufferRect.centerY());
            matrix.setRectToRect(viewRect, bufferRect, Matrix.ScaleToFit.FILL);
            float scale = Math.max(
                    (float) viewHeight / mPreviewSize.getHeight(),
                    (float) viewWidth / mPreviewSize.getWidth());
            matrix.postScale(scale, scale, centerX, centerY);
            matrix.postRotate(90 * (rotation - 2), centerX, centerY);
        } else if (Surface.ROTATION_180 == rotation) {
            matrix.postRotate(180, centerX, centerY);
        }
        textureView.setTransform(matrix);
    }

    private void closeCamera() {
        if (null != mCaptureSession) {
            mCaptureSession.close();
            mCaptureSession = null;
        }
        if (null != mCameraDevice) {
            mCameraDevice.close();
            mCameraDevice = null;
        }
        if (null != mImageReader) {
            mImageReader.close();
            mImageReader = null;
        }
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    private void repeatPreview() {
        mPreviewRequestBuilder.setTag(TAG_PREVIEW);
        mPreviewRequest = mPreviewRequestBuilder.build();
        //设置反复捕获数据的请求，这样预览界面就会一直有数据显示
        try {
            mCaptureSession.setRepeatingRequest(mPreviewRequest, new CameraCaptureSession.CaptureCallback() {
                @Override
                public void onCaptureCompleted(@NonNull CameraCaptureSession session, @NonNull CaptureRequest request, @NonNull TotalCaptureResult result) {

                }

                @Override
                public void onCaptureProgressed(@NonNull CameraCaptureSession session, @NonNull CaptureRequest request, @NonNull CaptureResult partialResult) {

                }
            }, null);
        } catch (CameraAccessException e) {
            e.printStackTrace();
        }
    }

    //选择sizeMap中大于并且最接近width和height的size
    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    private Size getOptimalSize(Size[] sizeMap, int width, int height) {
        List<Size> sizeList = new ArrayList<>();
        for (Size option : sizeMap) {
            if (width > height) {
                if (option.getWidth() > width && option.getHeight() > height) {
                    sizeList.add(option);
                }
            } else {
                if (option.getWidth() > height && option.getHeight() > width) {
                    sizeList.add(option);
                }
            }
        }
        if (sizeList.size() > 0) {
            return Collections.min(sizeList, new Comparator<Size>() {
                @Override
                public int compare(Size lhs, Size rhs) {
                    return Long.signum(lhs.getWidth() * lhs.getHeight() - rhs.getWidth() * rhs.getHeight());
                }
            });
        }
        return sizeMap[0];
    }


    //创建预览请求的Builder（TEMPLATE_PREVIEW表示预览请求）
    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    private void getPreviewRequestBuilder() {
        try {
            mPreviewRequestBuilder = mCameraDevice.createCaptureRequest(CameraDevice.TEMPLATE_PREVIEW);
        } catch (CameraAccessException e) {
            e.printStackTrace();
        }
        //设置预览的显示界面
        mPreviewRequestBuilder.addTarget(mPreviewSurface);
        MeteringRectangle[] meteringRectangles = mPreviewRequestBuilder.get(CaptureRequest.CONTROL_AF_REGIONS);
        if (meteringRectangles != null && meteringRectangles.length > 0) {
            Log.d(TAG, "PreviewRequestBuilder: AF_REGIONS=" + meteringRectangles[0].getRect().toString());
        }
        mPreviewRequestBuilder.set(CaptureRequest.CONTROL_MODE, CaptureRequest.CONTROL_MODE_AUTO);
        mPreviewRequestBuilder.set(CaptureRequest.CONTROL_AF_TRIGGER, CaptureRequest.CONTROL_AF_TRIGGER_IDLE);
    }

    //拍照
    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    private void capture() {
        try {
            //首先我们创建请求拍照的CaptureRequest
            final CaptureRequest.Builder mCaptureBuilder = mCameraDevice.createCaptureRequest(CameraDevice.TEMPLATE_STILL_CAPTURE);
            //获取屏幕方向
            int rotation = getWindowManager().getDefaultDisplay().getRotation();

            mCaptureBuilder.addTarget(mPreviewSurface);
            mCaptureBuilder.addTarget(mImageReader.getSurface());

            //设置拍照方向
            mCaptureBuilder.set(CaptureRequest.JPEG_ORIENTATION, ORIENTATION.get(rotation));

            //停止预览
            mCaptureSession.stopRepeating();

            //开始拍照，然后回调上面的接口重启预览，因为mCaptureBuilder设置ImageReader作为target，所以会自动回调ImageReader的onImageAvailable()方法保存图片
            CameraCaptureSession.CaptureCallback captureCallback = new CameraCaptureSession.CaptureCallback() {

                @Override
                public void onCaptureCompleted(@NonNull CameraCaptureSession session, @NonNull CaptureRequest request, @NonNull TotalCaptureResult result) {
                    repeatPreview();
                }
            };

            mCaptureSession.capture(mCaptureBuilder.build(), captureCallback, null);
        } catch (CameraAccessException e) {
            e.printStackTrace();
        }
    }

    //开启相机预览  fixme 拿到相机图片方法
    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    private void startPreview() {
        Log.e("=========","  startPreview ");
        //参数：尺寸+格式+每次最多获取几帧数据
        mImageReader = ImageReader.newInstance(mPreviewSize.getWidth(), mPreviewSize.getHeight(), ImageFormat.JPEG, 1);
        //有图像流数据可用时会回调onImageAvailable方法，它的参数就是预览帧数据
        mImageReader.setOnImageAvailableListener(new ImageReader.OnImageAvailableListener() {
            @Override
            public void onImageAvailable(ImageReader reader) {
                Log.e("=========", "拍照完成，开始处理处理图片信息");
                Image image = reader.acquireLatestImage();
                new Thread(new ImageSaver(image, handler)).start(); // 开启线程异步保存图片
            }
        }, null);
        SurfaceTexture mSurfaceTexture = textureView.getSurfaceTexture();
        mSurfaceTexture.setDefaultBufferSize(mPreviewSize.getWidth(), mPreviewSize.getHeight());//设置TextureView的缓冲区大小
        //获取Surface显示预览数据
        mPreviewSurface = new Surface(mSurfaceTexture);
        try {
            getPreviewRequestBuilder();
            //创建相机捕获会话，第一个参数是捕获数据的输出Surface列表，第二个参数是CameraCaptureSession的状态回调接口，当它创建好后会回调onConfigured方法，第三个参数用来确定Callback在哪个线程执行，为null的话就在当前线程执行
            mCameraDevice.createCaptureSession(Arrays.asList(mPreviewSurface, mImageReader.getSurface()), new CameraCaptureSession.StateCallback() {
                @Override
                public void onConfigured(CameraCaptureSession session) {
                    mCaptureSession = session;
                    repeatPreview();
                }

                @Override
                public void onConfigureFailed(CameraCaptureSession session) {

                }
            }, null);
        } catch (CameraAccessException e) {
            e.printStackTrace();
        }
    }

    //--------------------------拍照相关--------------------------
    public static class ImageSaver implements Runnable {
        private Image mImage;
        private Handler handler;

        public ImageSaver(Image image, Handler handler) {
            this.mImage = image;
            this.handler = handler;
        }

        @Override
        public void run() {
            Log.e("=========", "ImageSaver");
            ByteBuffer buffer = mImage.getPlanes()[0].getBuffer();
            byte[] data = new byte[buffer.remaining()];
            buffer.get(data);
            File imageFile = new File(Environment.getExternalStorageDirectory() + "/DCIM/myPicture.jpg");
            FileOutputStream fos = null;
            try {
                fos = new FileOutputStream(imageFile);
                fos.write(data, 0, data.length);
            } catch (IOException e) {
                e.printStackTrace();
            } finally {
                if (fos != null) {
                    try {
                        fos.close();
                        mImage.close();
                        Message mas = new Message();
                        mas.obj = imageFile.getAbsolutePath();
                        handler.sendMessage(mas);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
        }
    }

    private Handler handler = new Handler() {
        @Override
        public void dispatchMessage(Message msg) {
            super.dispatchMessage(msg);
            dealMsg(msg);
        }
    };

    //处理handler消息
    private void dealMsg(Message msg) {
        /*final Bitmap bitmap = BitmapFactory.decodeFile((String) msg.obj);
            img.setImageBitmap(bitmap);*/
        int degree = readPictureDegree((String) msg.obj);
        Bitmap bitmap = BitmapFactory.decodeFile((String) msg.obj);
        bitmap = rotaingImageView(degree, bitmap);
        //身份证
        if (type == 1) {
            if (sfztype == 1) {
                HomeActivity.b1 = scaleBitmap(bitmap, 1);
//                    ((ImageView) findViewById(R.id.img_true_bottom)).setImageBitmap(NameAuthActivity.bitmap1);
            } else {
                HomeActivity.b2 = scaleBitmap(bitmap, 1);
//                    ((ImageView) findViewById(R.id.img_true_bottom)).setImageBitmap(NameAuthActivity.bitmap2);
            }
            HomeActivity.needApi = true;
            finish();
        }
        //营业执照
        else if (type == 2) {
            HomeActivity.b3 = scaleBitmap(bitmap, 2);
            HomeActivity.needApi = true;
//                ((ImageView) findViewById(R.id.img_true_bottom)).setImageBitmap(ShopAuthModeActivity.bitmap);
            finish();
        }
        //3.银行卡
        else if (type == 3) {
            upload_biz_license_idcard(bitmap, true);
        }
    }

    //指定区域裁剪
    private Bitmap scaleBitmap(Bitmap bitmap, int type) {
        int newHeight = 0;
        int martop = 0;
        //身份证+银行卡
        if (type == 1) {
            newHeight = DensityUtil.dip2px(CameraPrevActivity.this, 330f);  //fixme 布局高度212   距离顶部高度124
            martop = DensityUtil.dip2px(CameraPrevActivity.this, 74f);    //fixme 推荐顶部偏移高度为   距离顶部高度-50dp
        }
        //营业执照
        else {
            newHeight = DensityUtil.dip2px(CameraPrevActivity.this, 540f);//fixme 布局高度421   距离顶部高度100
            martop = DensityUtil.dip2px(CameraPrevActivity.this, 50f);    //fixme 推荐顶部偏移高度为   距离顶部高度-50dp
        }
        Log.e("scaleBitmap: ", "bitmap.getWidth()=" + bitmap.getWidth());
        Log.e("scaleBitmap: ", "bitmap.getHeight()=" + bitmap.getHeight());
        Log.e("scaleBitmap: ", "textureView.getPreViewWidth()=" + textureView.getPreViewWidth());
        Log.e("scaleBitmap: ", "textureView.getPreViewHeight()=" + textureView.getPreViewHeight());
        Log.e("scaleBitmap: ", "martop=" + martop);
        Log.e("scaleBitmap: ", "newHeight=" + newHeight);
        return Bitmap.createBitmap(bitmap,
                (bitmap.getWidth() - screenWidth) / 2,//以bitmap的实际宽度去算偏移值
                martop,//顶部偏移一点截取，免得高度区域过大
                screenWidth,//screenWidth+ outScreenWidth
                newHeight,
                null, false);
    }

    //--------------------------银行卡识别的特殊操作--------------------------
    private void upload_biz_license_idcard(Bitmap b, boolean isTakePic) {
        Bitmap currentBitmap = null;
        if (isTakePic) {
            currentBitmap = scaleBitmap(b, 1);
//          ((ImageView) findViewById(R.id.img_true_bottom)).setImageBitmap(currentBitmap);
        } else {
            currentBitmap = b;
        }
        HomeActivity.b4 = currentBitmap;
        HomeActivity.needApi = true;
        finish();
    }
}
