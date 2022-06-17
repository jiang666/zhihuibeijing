package com.example.mylibrary.httpUtils.utils;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Environment;
import android.support.annotation.Nullable;
import android.util.Log;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.target.SimpleTarget;
import com.bumptech.glide.request.transition.Transition;
import com.tencent.connect.share.QQShare;
import com.tencent.connect.share.QzoneShare;
import com.tencent.mm.opensdk.modelmsg.SendMessageToWX;
import com.tencent.mm.opensdk.modelmsg.WXMediaMessage;
import com.tencent.mm.opensdk.modelmsg.WXWebpageObject;
import com.tencent.mm.opensdk.openapi.IWXAPI;
import com.tencent.mm.opensdk.openapi.WXAPIFactory;
import com.tencent.tauth.IUiListener;
import com.tencent.tauth.Tencent;
import com.tencent.tauth.UiError;

import java.io.BufferedOutputStream;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;

public class ShareUtils {

    //微信
    public static final String WX_APPID = "wxd3c035d347218b30";
    public static final String WX_APPKEY = "82a02ee975ef0e952499aef9d25146af";

    //qq
    public static final String QQ_APPID = "101808265";
    public static final String QQ_APPKEY = "b664e53fb325b05f7db8920f277e249d";

    //0微信 1微信朋友圈 2qq 3QQ空间       动态图片(有则用,无则应用图标)   动态内容
    public static void shareTread(String title, String url, int type, Context context, String treadImgUrl, String appIconUrl) {
        //2qq 3QQ空间
        if (type == 2 || type == 3) {
            Tencent mTencent = Tencent.createInstance(QQ_APPID, context.getApplicationContext());
            //2qq
            if (type == 2) {
                final Bundle params = new Bundle();
                params.putInt(QQShare.SHARE_TO_QQ_KEY_TYPE, QQShare.SHARE_TO_QQ_TYPE_DEFAULT);//分享的类型
                params.putString(QQShare.SHARE_TO_QQ_TARGET_URL, url);//内容地址
                //纯文字
                if (treadImgUrl == null) {
                    params.putString(QQShare.SHARE_TO_QQ_TITLE, title);//分享标题
                    params.putString(QQShare.SHARE_TO_QQ_SUMMARY, title);//要分享的内容摘要
                    params.putString(QQShare.SHARE_TO_QQ_IMAGE_URL, appIconUrl);//分享的图片URL
                }
                //有图片
                else {
                    //有文字
                    if (title != null) {
                        params.putString(QQShare.SHARE_TO_QQ_TITLE, title);//分享标题
                        params.putString(QQShare.SHARE_TO_QQ_SUMMARY, title);//要分享的内容摘要
                        params.putString(QQShare.SHARE_TO_QQ_IMAGE_URL, treadImgUrl);//分享的图片URL
                    }
                    //无文字
                    else {
                        params.putString(QQShare.SHARE_TO_QQ_TITLE, "分享图片");//分享标题
                        params.putString(QQShare.SHARE_TO_QQ_SUMMARY, "分享图片");//要分享的内容摘要
                        params.putString(QQShare.SHARE_TO_QQ_IMAGE_URL, treadImgUrl);//分享的图片URL
                    }
                }
                params.putString(QQShare.SHARE_TO_QQ_APP_NAME, "鱼络圈");//应用名称
                mTencent.shareToQQ((Activity) context, params, new ShareUiListener());
            }
            //3QQ空间
            else {
                Bundle params = new Bundle();
                params.putInt(QzoneShare.SHARE_TO_QZONE_KEY_TYPE, QzoneShare.SHARE_TO_QZONE_TYPE_NO_TYPE);
                params.putString(QzoneShare.SHARE_TO_QQ_TARGET_URL, url);//分享的链接
                //分享的图片, 以ArrayList<String>的类型传入，以便支持多张图片（注：图片最多支持9张图片，多余的图片会被丢弃）
                ArrayList<String> imageUrls = new ArrayList<String>();
                //纯文字
                if (treadImgUrl == null) {
                    params.putString(QQShare.SHARE_TO_QQ_TITLE, title);//分享标题
                    params.putString(QQShare.SHARE_TO_QQ_SUMMARY, title);//要分享的内容摘要
                    imageUrls.add(appIconUrl);
                    params.putStringArrayList(QzoneShare.SHARE_TO_QQ_IMAGE_URL, imageUrls);//分享的图片URL
                }
                //有图片
                else {
                    //有文字
                    if (title != null) {
                        params.putString(QQShare.SHARE_TO_QQ_TITLE, title);//分享标题
                        params.putString(QQShare.SHARE_TO_QQ_SUMMARY, title);//要分享的内容摘要
                        imageUrls.add(treadImgUrl);
                        params.putStringArrayList(QzoneShare.SHARE_TO_QQ_IMAGE_URL, imageUrls);//分享的图片URL
                    }
                    //无文字
                    else {
                        params.putString(QQShare.SHARE_TO_QQ_TITLE, "分享图片");//分享标题
                        params.putString(QQShare.SHARE_TO_QQ_SUMMARY, "分享图片");//要分享的内容摘要
                        imageUrls.add(treadImgUrl);
                        params.putStringArrayList(QzoneShare.SHARE_TO_QQ_IMAGE_URL, imageUrls);//分享的图片URL
                    }
                }
                mTencent.shareToQzone((Activity) context, params, new ShareUiListener());
            }
        }
        //0微信 1微信朋友圈
        else if (type == 0 || type == 1) {
            IWXAPI mWxApi = WXAPIFactory.createWXAPI(context, WX_APPID, true);
            mWxApi.registerApp(WX_APPID);
            // 检查手机或者模拟器是否安装了微信
            if (!mWxApi.isWXAppInstalled()) {
                Toast.makeText(context, "您还没有安装微信", Toast.LENGTH_SHORT).show();
                return;
            }
            WXWebpageObject webpageObject = new WXWebpageObject();
            webpageObject.webpageUrl = url;// 填写网页的url
            WXMediaMessage msg = new WXMediaMessage(webpageObject);
            //纯文字
            if (treadImgUrl == null) {
                msg.title = title;
                msg.description = title;
                shareAfterBitmap(context, msg, appIconUrl, type, mWxApi);
            }
            //有图片
            else {
                //有文字
                if (title != null) {
                    msg.title = title;
                    msg.description = title;
                    shareAfterBitmap(context, msg, treadImgUrl, type, mWxApi);
                }
                //无文字
                else {
                    msg.title = "分享图片";
                    msg.description = "分享图片";
                    shareAfterBitmap(context, msg, treadImgUrl, type, mWxApi);
                }
            }
        }
    }

    //0微信 1微信朋友圈 2qq 3QQ空间       动态图片(有则用,无则应用图标)   动态内容
    public static void shareTread(String title, String des, String url, int type, Context context, String treadImgUrl, String appIconUrl,ShareCallback shareCall) {
        shareCallback = shareCall;
        //2qq 3QQ空间
        if (type == 2 || type == 3) {
            Tencent mTencent = Tencent.createInstance(QQ_APPID, context.getApplicationContext());
            //2qq
            if (type == 2) {
                final Bundle params = new Bundle();
                params.putInt(QQShare.SHARE_TO_QQ_KEY_TYPE, QQShare.SHARE_TO_QQ_TYPE_DEFAULT);//分享的类型
                params.putString(QQShare.SHARE_TO_QQ_TARGET_URL, url);//内容地址
                //纯文字
                if (treadImgUrl == null) {
                    params.putString(QQShare.SHARE_TO_QQ_TITLE, title);//分享标题
                    params.putString(QQShare.SHARE_TO_QQ_SUMMARY, des);//要分享的内容摘要
                    params.putString(QQShare.SHARE_TO_QQ_IMAGE_URL, appIconUrl);//分享的图片URL
                }
                //有图片
                else {
                    //有文字
                    if (title != null) {
                        params.putString(QQShare.SHARE_TO_QQ_TITLE, title);//分享标题
                        params.putString(QQShare.SHARE_TO_QQ_SUMMARY, des);//要分享的内容摘要
                        params.putString(QQShare.SHARE_TO_QQ_IMAGE_URL, treadImgUrl);//分享的图片URL
                    }
                    //无文字
                    else {
                        params.putString(QQShare.SHARE_TO_QQ_TITLE, "分享图片");//分享标题
                        params.putString(QQShare.SHARE_TO_QQ_SUMMARY, "分享图片");//要分享的内容摘要
                        params.putString(QQShare.SHARE_TO_QQ_IMAGE_URL, treadImgUrl);//分享的图片URL
                    }
                }
                params.putString(QQShare.SHARE_TO_QQ_APP_NAME, "鱼络圈");//应用名称
                mTencent.shareToQQ((Activity) context, params, new ShareUiListener());
            }
            //3QQ空间
            else {
                Bundle params = new Bundle();
                params.putInt(QzoneShare.SHARE_TO_QZONE_KEY_TYPE, QzoneShare.SHARE_TO_QZONE_TYPE_NO_TYPE);
                params.putString(QzoneShare.SHARE_TO_QQ_TARGET_URL, url);//分享的链接
                //分享的图片, 以ArrayList<String>的类型传入，以便支持多张图片（注：图片最多支持9张图片，多余的图片会被丢弃）
                ArrayList<String> imageUrls = new ArrayList<String>();
                //纯文字
                if (treadImgUrl == null) {
                    params.putString(QQShare.SHARE_TO_QQ_TITLE, title);//分享标题
                    params.putString(QQShare.SHARE_TO_QQ_SUMMARY, des);//要分享的内容摘要
                    imageUrls.add(appIconUrl);
                    params.putStringArrayList(QzoneShare.SHARE_TO_QQ_IMAGE_URL, imageUrls);//分享的图片URL
                }
                //有图片
                else {
                    //有文字
                    if (title != null) {
                        params.putString(QQShare.SHARE_TO_QQ_TITLE, title);//分享标题
                        params.putString(QQShare.SHARE_TO_QQ_SUMMARY, des);//要分享的内容摘要
                        imageUrls.add(treadImgUrl);
                        params.putStringArrayList(QzoneShare.SHARE_TO_QQ_IMAGE_URL, imageUrls);//分享的图片URL
                    }
                    //无文字
                    else {
                        params.putString(QQShare.SHARE_TO_QQ_TITLE, "分享图片");//分享标题
                        params.putString(QQShare.SHARE_TO_QQ_SUMMARY, "分享图片");//要分享的内容摘要
                        imageUrls.add(treadImgUrl);
                        params.putStringArrayList(QzoneShare.SHARE_TO_QQ_IMAGE_URL, imageUrls);//分享的图片URL
                    }
                }
                mTencent.shareToQzone((Activity) context, params, new ShareUiListener());
            }
        }
        //0微信 1微信朋友圈
        else if (type == 0 || type == 1) {
            IWXAPI mWxApi = WXAPIFactory.createWXAPI(context, WX_APPID, true);
            mWxApi.registerApp(WX_APPID);
            // 检查手机或者模拟器是否安装了微信
            if (!mWxApi.isWXAppInstalled()) {
                Toast.makeText(context, "您还没有安装微信", Toast.LENGTH_SHORT).show();
                return;
            }
            WXWebpageObject webpageObject = new WXWebpageObject();
            webpageObject.webpageUrl = url;// 填写网页的url
            WXMediaMessage msg = new WXMediaMessage(webpageObject);
            //纯文字
            if (treadImgUrl == null) {
                msg.title = title;
                msg.description = des;
                shareAfterBitmap(context, msg, appIconUrl, type, mWxApi);
            }
            //有图片
            else {
                //有文字
                if (title != null) {
                    msg.title = title;
                    msg.description = des;
                    shareAfterBitmap(context, msg, treadImgUrl, type, mWxApi);
                }
                //无文字
                else {
                    msg.title = "分享图片";
                    msg.description = "分享图片";
                    shareAfterBitmap(context, msg, treadImgUrl, type, mWxApi);
                }
            }
        }
    }

    public static interface ShareCallback{
        void shareSuccess();
    }

    public static ShareCallback shareCallback;

    public static void shareAfterBitmap(final Context context, final WXMediaMessage msg, String imgUrl, final int type, final IWXAPI mWxApi) {
        Glide.with(context.getApplicationContext())
                .asBitmap()
                .load(imgUrl)
                .into(new SimpleTarget<Bitmap>() {
                    @Override
                    public void onResourceReady(Bitmap bitmap, Transition<? super Bitmap> transition) {
                        bitmap = compressImage(bitmap);
                        msg.setThumbImage(bitmap);
                        SendMessageToWX.Req req = new SendMessageToWX.Req();
                        req.transaction = "webpage";
                        req.message = msg;
                        if (type == 0) {
                            req.scene = SendMessageToWX.Req.WXSceneSession;
                        } else {
                            req.scene = SendMessageToWX.Req.WXSceneTimeline;
                        }
                        mWxApi.sendReq(req);
                    }

                    @Override
                    public void onLoadFailed(@Nullable Drawable errorDrawable) {
                        super.onLoadFailed(errorDrawable);
                        Toast.makeText(context.getApplicationContext(), "分享失败,请重试", Toast.LENGTH_SHORT).show();
                    }
                });
    }

    //压缩图片到32Kb一下
    public static Bitmap compressImage(Bitmap bitmap) {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        //质量压缩方法，这里100表示不压缩，把压缩后的数据存放到baos中
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos);
        int options = 100;
        //循环判断如果压缩后图片是否大于50kb,大于继续压缩
        while (baos.toByteArray().length / 1024 > 32) {
            //清空baos
            baos.reset();
            bitmap.compress(Bitmap.CompressFormat.JPEG, options, baos);
            options -= 10;//每次都减少10
        }
        //把压缩后的数据baos存放到ByteArrayInputStream中
        ByteArrayInputStream isBm = new ByteArrayInputStream(baos.toByteArray());
        //把ByteArrayInputStream数据生成图片
        Bitmap newBitmap = BitmapFactory.decodeStream(isBm, null, null);
        return newBitmap;
    }

    private static class ShareUiListener implements IUiListener {

        @Override
        public void onComplete(Object response) {
            LogUtils.logE("测试QQ分享", "onComplete: ");
            //分享成功
            if(shareCallback!=null){
                LogUtils.logE("测试QQ分享", "shareCallback.shareSuccess()");
                shareCallback.shareSuccess();
            }
        }

        @Override
        public void onError(UiError uiError) {
            LogUtils.logE("测试QQ分享", "分享失败: ");
        }

        @Override
        public void onCancel() {
            LogUtils.logE("测试QQ分享", "分享取消: ");
        }
    }
}
