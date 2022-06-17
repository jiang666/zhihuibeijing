package com.example.mylibrary.httpUtils.utils;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.support.annotation.Nullable;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.load.resource.bitmap.RoundedCorners;
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.RequestOptions;
import com.bumptech.glide.request.target.Target;
import com.example.mylibrary.R;

import java.io.File;
import java.net.MalformedURLException;
import java.net.URL;


/**
 * Created by 孟晨 on 2018/4/8.
 */

public class GlideUtils {

    //圆形预览头像
    public static void glideCirclePerson(Context context, String imgurl, final ImageView img) {
        img.setScaleType(ImageView.ScaleType.CENTER_CROP);
        imgurl = imgurl.replaceAll("amp;", "");//被转义了
        Glide.with(context.getApplicationContext())
                .load(imgurl)
//                .transition(DrawableTransitionOptions.withCrossFade(500)) // 渐变
//                .placeholder(R.drawable.img_big_change_bg)
//                .error(R.mipmap.ic_launcher)
//                .fitCenter()
                .into(img);

//        Glide.with(context).load(imgurl).listener(new RequestListener<Drawable>() {
//            @Override
//            public boolean onLoadFailed(@Nullable GlideException e, Object model, Target<Drawable> target, boolean isFirstResource) {
//                return false;
//            }
//
//            @Override
//            public boolean onResourceReady(Drawable drawable, Object model, Target<Drawable> target, DataSource dataSource, boolean isFirstResource) {
//                img.setBackground(drawable);
//                return false;
//            }
//        }).submit();
    }

    //加载大的图片   https://www.jianshu.com/p/a42ecf4af737
    public static void glideBigImgUrl(Context context, String imgurl, final ImageView img) {
//        RequestOptions options = new RequestOptions();
//        options.centerCrop();
//        options.error(R.drawable.img_big_change_bg);
//        options.diskCacheStrategy(DiskCacheStrategy.ALL);
//        Glide.with(context)
//                .load(imgurl)
//                .thumbnail(0.1f)
//                .apply(options)
//                .into(img);

        img.setScaleType(ImageView.ScaleType.CENTER_CROP);
        imgurl = imgurl.replaceAll("amp;", "");//被转义了
        try {
            Glide.with(context.getApplicationContext())
                    .load(new URL(imgurl))
//                    .placeholder(R.drawable.img_big_change_bg)
                    .into(img);
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }
    }

    //无默认头像
    public static void glideDjImg(Context context, String imgurl, ImageView img) {
        Glide.with(context.getApplicationContext())
                .load(imgurl)
//                .transition(DrawableTransitionOptions.withCrossFade(500)) // 渐变
                .fitCenter()
                .into(img);
    }

    //推荐的banner
    public static void glideSuggestBanner(Context context, String imgurl, ImageView img) {
        Glide.with(context.getApplicationContext())
                .load(imgurl)
                .transition(DrawableTransitionOptions.withCrossFade(500)) // 渐变
//                .placeholder(R.drawable.)
//                .error(R.mipmap.ic_launcher)
                .fitCenter()
                .into(img);
    }

    //banner
    public static void glideBannerBg(Context context, String imgurl, ImageView img) {
        Glide.with(context.getApplicationContext())
                .load(imgurl)
//                .transition(DrawableTransitionOptions.withCrossFade(500)) // 渐变
//                .placeholder(R.drawable.img_big_change_bg)
//                .error(R.drawable.img_big_change_bg)
//                .fitCenter()
                .into(img);
    }

    //正面身份证
    public static void glideSfz(Context context, String imgurl, ImageView img) {
        Glide.with(context.getApplicationContext())
                .load(imgurl)
//                .transition(DrawableTransitionOptions.withCrossFade(500)) // 渐变
//                .placeholder(R.drawable.)
//                .error(R.mipmap.ic_launcher)
                .fitCenter()
                .into(img);
    }

    //主播封面
    public static void glideLiverBg(Context context, String imgurl, ImageView img) {
        Glide.with(context.getApplicationContext())
                .load(imgurl)
                .transition(DrawableTransitionOptions.withCrossFade(500)) // 渐变
//                .placeholder(R.drawable.img_big_change_bg)
                .error(R.drawable.img_big_change_bg)
                .fitCenter()
                .into(img);
    }

    //主播封面
    public static void glideGoogShopBg(Context context, String imgurl, ImageView img) {
        Glide.with(context.getApplicationContext())
                .load(imgurl)
                .transition(DrawableTransitionOptions.withCrossFade(500)) // 渐变
//                .placeholder(R.drawable.img_big_change_bg)
                .error(R.drawable.img_big_change_bg)
                .fitCenter()
                .into(img);
    }

    //建议页面的主播头像加载
    public static void glideSuggestLiver(Context context, Object imgurl, ImageView img) {
        Glide.with(context.getApplicationContext())
                .load(imgurl)
//                .transition(DrawableTransitionOptions.withCrossFade(500)) // 渐变
//                .placeholder(R.drawable.img_big_change_bg)
                .error(R.drawable.img_big_change_bg)
                .fitCenter()
                .into(img);
    }

    //圆角为4的动态发布页面的美女图片
    public static void glideTreadImgCornerImg(Context context, String imgUrl, ImageView img) {
        imgUrl = imgUrl.replaceAll("amp;", "");//被转义了
        Glide.with(context.getApplicationContext()).load(imgUrl)
//                .transition(DrawableTransitionOptions.withCrossFade(500)) // 渐变
                .fitCenter()
                .into(img);
    }

    //圆角为4的动态发布页面的美女图片
    public static void glideTreadImgCornerImgFile(Context context, File imgUrl, ImageView img) {
        Glide.with(context.getApplicationContext()).load(imgUrl)
//                .transition(DrawableTransitionOptions.withCrossFade(500)) // 渐变
                .fitCenter()
                .into(img);
    }

    //无错误预览
    public static void glideNoerrorPerImg(Context context, Object imgUrl, ImageView img) {
        Glide.with(context.getApplicationContext()).load(imgUrl)
                .transition(DrawableTransitionOptions.withCrossFade(500)) // 渐变
                .fitCenter()
                .into(img);
    }

    //推流拉流有pop的shape颜色失效的img背景图
    public static void glideVideoPopBgCornor(Context context, final ImageView img, int width) {
        RoundedCorners roundedCorners = new RoundedCorners(DensityUtil.dip2px(context, 15));
        RequestOptions options = RequestOptions.bitmapTransform(roundedCorners).override(width, DensityUtil.dip2px(context, 355));
        Glide.with(context).load(R.drawable.img_white).apply(options).listener(new RequestListener<Drawable>() {
            @Override
            public boolean onLoadFailed(@Nullable GlideException e, Object model, Target<Drawable> target, boolean isFirstResource) {
                return false;
            }

            @Override
            public boolean onResourceReady(Drawable drawable, Object model, Target<Drawable> target, DataSource dataSource, boolean isFirstResource) {
                img.setBackground(drawable);
                return false;
            }
        }).submit();
    }
}
