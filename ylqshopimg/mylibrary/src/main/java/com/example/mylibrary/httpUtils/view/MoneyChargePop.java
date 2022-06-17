package com.example.mylibrary.httpUtils.view;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.os.Message;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Html;
import android.text.TextUtils;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.alibaba.android.arouter.launcher.ARouter;
import com.alipay.sdk.app.PayTask;
import com.example.mylibrary.R;
import com.example.mylibrary.httpUtils.ErrorUtils;
import com.example.mylibrary.httpUtils.callback.HttpListener;
import com.example.mylibrary.httpUtils.entity.PayResult;
import com.example.mylibrary.httpUtils.http.Pay.PayLoader;
import com.example.mylibrary.httpUtils.http.Pay.bean.AlipayBean;
import com.example.mylibrary.httpUtils.http.Pay.bean.WxpayBean;
import com.example.mylibrary.httpUtils.http.app.AppLoader;
import com.example.mylibrary.httpUtils.http.app.bean.GetAgreementList;
import com.example.mylibrary.httpUtils.http.app.bean.GetRechargePackage;
import com.example.mylibrary.httpUtils.http.live.bean.GetHostroomUserlist;
import com.example.mylibrary.httpUtils.http.user.UserInfoLoader;
import com.example.mylibrary.httpUtils.http.user.bean.AddFollowBean;
import com.example.mylibrary.httpUtils.ui.WebActivity;
import com.example.mylibrary.httpUtils.utils.DensityUtil;
import com.example.mylibrary.httpUtils.utils.FastClickUtils;
import com.example.mylibrary.httpUtils.utils.GlideUtils;
import com.example.mylibrary.httpUtils.utils.HttpUtils;
import com.example.mylibrary.httpUtils.utils.LogUtils;
import com.example.mylibrary.httpUtils.utils.RouterUtils;
import com.example.mylibrary.httpUtils.utils.SPUtil;
import com.example.mylibrary.httpUtils.utils.UserUtils;
import com.tencent.mm.opensdk.modelpay.PayReq;
import com.tencent.mm.opensdk.openapi.IWXAPI;
import com.tencent.mm.opensdk.openapi.WXAPIFactory;

import java.lang.ref.WeakReference;
import java.util.List;
import java.util.Map;

/**
 * Created by 孟晨 on 2018/8/22.
 */

public class MoneyChargePop {

    public MoneyChargePop(Context baseActivity) {
        this.baseActivity = baseActivity;
    }

    private View popView;
    private PopupWindow popWindow;
    //不足
    private TextView tex;
    //更多充值方式
    private TextView tv_more_charege;
    //充值金额
    private LinearLayout rl_select_money1;
    private TextView tv_yuan_left, tv_zs_left;
    private LinearLayout rl_select_money2;
    private TextView tv_yuan_right, tv_zs_right;
    private String selectMoneyShopId = "";//充值商品id
    private RelativeLayout rl_money_left, rl_money_right;
    private ImageView img_select_send_bg_left,img_select_send_bg_right;
    private TextView tv_send_jf_left,tv_send_jf_right;
    //充值方式
    private int selectType = 1;//1支付宝  2微信
    private RelativeLayout rl_select_zfb;
    private RelativeLayout rl_select_wx;
    private ImageView img_select_zfb;
    private ImageView img_select_wx;
    //确认支付
    private TextView tv_charge;
    //协议
    private RelativeLayout rl_xy;
    private ImageView img_xy;
    private TextView tv_xy;
    private String xyUrl = "";
    //支付
    private IWXAPI api;
    private int SDK_PAY_FLAG_ALI = 0X1;
    private Context baseActivity;
    private String wxAppId = "wxd3c035d347218b30";

    //显示弹窗
    public PopupWindow showPop(int type, ClickCallback clickUserCallback) {
        this.clickUserCallback = clickUserCallback;
        api = WXAPIFactory.createWXAPI(baseActivity, wxAppId, true);
        api.registerApp(wxAppId);
        if (baseActivity != null) {
            popView = LayoutInflater.from(baseActivity)
                    .inflate(R.layout.layout_money_charge, null, false);

            //点击背景消失
            TextView tv_bg = popView.findViewById(R.id.tv_bg);
            tv_bg.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (FastClickUtils.isFastClick()) {
                        return;
                    }
                    dismissPop();
                }
            });

            //鱼钻/积分不足
            String texContent = (type == 1) ? "钻石余额不足" : "积分余额不足";
            tex = popView.findViewById(R.id.tex);
            tex.setText(texContent);

            //更多充值方式
            tv_more_charege = popView.findViewById(R.id.tv_more_charege);
            tv_more_charege.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (FastClickUtils.isFastClick()) {
                        return;
                    }
                    if (MoneyChargePop.this.clickUserCallback != null) {
                        MoneyChargePop.this.clickUserCallback.notifyNotStopLiver();
                    }
                    dismissPop();
                    ARouter.getInstance()
                            .build(RouterUtils.USER_MONEY_CHARGE)
                            .navigation();
                }
            });

            //充值金额
            rl_money_left = popView.findViewById(R.id.rl_money_left);
            rl_select_money1 = popView.findViewById(R.id.rl_select_money1);
            tv_yuan_left = popView.findViewById(R.id.tv_yuan_left);
            tv_zs_left = popView.findViewById(R.id.tv_zs_left);
            rl_select_money2 = popView.findViewById(R.id.rl_select_money2);
            rl_money_right = popView.findViewById(R.id.rl_money_right);
            tv_yuan_right = popView.findViewById(R.id.tv_yuan_right);
            tv_zs_right = popView.findViewById(R.id.tv_zs_right);
            img_select_send_bg_left = popView.findViewById(R.id.img_select_send_bg_left);
            img_select_send_bg_right = popView.findViewById(R.id.img_select_send_bg_right);
            tv_send_jf_left = popView.findViewById(R.id.tv_send_jf_left);
            tv_send_jf_right = popView.findViewById(R.id.tv_send_jf_right);

            //充值方式
            rl_select_zfb = popView.findViewById(R.id.rl_select_zfb);
            img_select_zfb = popView.findViewById(R.id.img_select_zfb);
            rl_select_zfb.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (FastClickUtils.isFastClick()) {
                        return;
                    }
                    selectType = 1;
                    img_select_zfb.setImageResource(R.drawable.img_charge_ok);
                    img_select_wx.setImageResource(R.drawable.img_charge_no);
                }
            });

            rl_select_wx = popView.findViewById(R.id.rl_select_wx);
            img_select_wx = popView.findViewById(R.id.img_select_wx);
            rl_select_wx.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (FastClickUtils.isFastClick()) {
                        return;
                    }
                    selectType = 2;
                    img_select_zfb.setImageResource(R.drawable.img_charge_no);
                    img_select_wx.setImageResource(R.drawable.img_charge_ok);
                }
            });

            //确认支付
            tv_charge = popView.findViewById(R.id.tv_charge);
            tv_charge.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (FastClickUtils.isFastClick()) {
                        return;
                    }
                    //协议未获取到
                    if (xyUrl.equals("")) {
                        Toast.makeText(baseActivity, "正在获取用户充值协议", Toast.LENGTH_SHORT).show();
                        return;
                    }
                    //未同意协议
                    boolean aggreeXy = (boolean) rl_xy.getTag();
                    if (!aggreeXy) {
                        Toast.makeText(baseActivity, "请先阅读并同意用户充值协议", Toast.LENGTH_SHORT).show();
                        return;
                    }
                    //未选择商品
                    if (selectMoneyShopId.equals("")) {
                        Toast.makeText(baseActivity, "请先选择您需要充值的商品", Toast.LENGTH_SHORT).show();
                        return;
                    }
                    //支付宝1  微信2
                    if (selectType == 1) {
                        alipay(selectMoneyShopId);
                    } else {
                        if (!api.isWXAppInstalled()) {
                            Toast.makeText(baseActivity, "您的设备未安装微信客户端", Toast.LENGTH_SHORT).show();
                        } else {
                            wxpay(selectMoneyShopId);
                        }
                    }
                }
            });

            //协议
            rl_xy = popView.findViewById(R.id.rl_xy);
            img_xy = popView.findViewById(R.id.img_xy);
            tv_xy = popView.findViewById(R.id.tv_xy);
            rl_xy.setTag(true);
            img_xy.setImageResource(R.drawable.img_charge_ok);
            tv_xy.setText(Html.fromHtml("我已阅读并同意<font color='#FF5686'>《用户充值协议》</font>"));
            //协议详情
            tv_xy.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (FastClickUtils.isFastClick()) {
                        return;
                    }
                    if (xyUrl.equals("")) {
                        Toast.makeText(baseActivity, "网络异常,暂未获取到协议内容。", Toast.LENGTH_SHORT).show();
                        return;
                    }
                    if (MoneyChargePop.this.clickUserCallback != null) {
                        MoneyChargePop.this.clickUserCallback.notifyNotStopLiver();
                    }
                    Intent intent = new Intent();
                    intent.putExtra(UserUtils.LINK_URL, xyUrl);
                    intent.putExtra(UserUtils.LINK_URL_TITLE, "用户充值协议");
                    intent.setClass(baseActivity, WebActivity.class);
                    baseActivity.startActivity(intent);
                }
            });
            rl_xy.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (FastClickUtils.isFastClick()) {
                        return;
                    }
                    boolean isOk = (boolean) rl_xy.getTag();
                    if (isOk) {
                        img_xy.setImageResource(R.drawable.img_charge_xy_no);
                    } else {
                        img_xy.setImageResource(R.drawable.img_charge_ok);
                    }
                    rl_xy.setTag(!isOk);
                }
            });

            //商品
            get_recharge_package(baseActivity);
            //协议
            get_agreement_list(baseActivity);

            popWindow = new PopupWindow(popView,
                    ViewGroup.LayoutParams.MATCH_PARENT,
                    ViewGroup.LayoutParams.MATCH_PARENT,//(int) (WindowParamUtils.screenHeight(baseActivity) * 0.9
                    true);
            popWindow.setAnimationStyle(R.style.popwin_anim_style);
            //点击外部，popupwindow会消失
            popWindow.setFocusable(false);
            popWindow.setOutsideTouchable(false);
            popWindow.showAtLocation(popView, Gravity.BOTTOM | Gravity.CENTER_HORIZONTAL, 0, 0);
            showPopBlackBg(baseActivity);
        }
        return popWindow;
    }

    //----------------------------------------------背景颜色的改变----------------------------------
    //初始化弹窗的背景色
    public void showPopBlackBg(Context baseActivity) {
        if (baseActivity != null) {
            changePopBlackBg(0.8F);//越大越浅
        }
    }

    //恢复弹窗的背景色
    public void backNormalPopBg() {
        if (baseActivity != null) {
            WindowManager.LayoutParams lp = ((Activity) baseActivity).getWindow().getAttributes();
            lp.alpha = 1f;
            ((Activity) baseActivity).getWindow().setAttributes(lp);
        }
    }

    //弹窗背景颜色
    public void changePopBlackBg(float blackBg) {
        if (baseActivity != null) {
            WindowManager.LayoutParams lp = ((Activity) baseActivity).getWindow().getAttributes();
            lp.alpha = blackBg;
            ((Activity) baseActivity).getWindow().setAttributes(lp);
        }
    }

    public void dismissPop() {
        if (popWindow.isShowing()) {
            popWindow.dismiss();
            popWindow = null;
            backNormalPopBg();
        }
    }

    //商品信息
    private void get_recharge_package(final Context baseActivity) {
        Map<String, String> map = HttpUtils.getPublicApiMap(baseActivity);
        String sign = HttpUtils.getSign(map);
        LogUtils.logE("测试: ", "加密的签名=" + sign);
        HttpListener<GetRechargePackage> httpListener = new HttpListener<GetRechargePackage>() {
            @Override
            public void onError(String errorMsg) {
            }

            @Override
            public void onSuccess(final GetRechargePackage getRechargePackage) {
                if (getRechargePackage.getCode().equals("200")) {
                    if (getRechargePackage.getData().getList().size() > 2) {
                        selectMoneyShopId = getRechargePackage.getData().getList().get(0).getId() + "";
                        //前2种充值方式
                        rl_money_left.setVisibility(View.VISIBLE);
                        rl_money_right.setVisibility(View.VISIBLE);
                        rl_select_money1.setBackground(baseActivity.getResources().getDrawable(R.drawable.img_charge_shop_select));
                        img_select_send_bg_left.setImageResource(R.drawable.img_send_jg_bg_yes);
                        for (int i = 0; i < getRechargePackage.getData().getList().size(); i++) {
                            if (i == 0) {
                                //价格
                                tv_yuan_left.setText(getRechargePackage.getData().getList().get(0).getPrice() + "元");
                                //等价钻石
                                tv_zs_left.setText(getRechargePackage.getData().getList().get(0).getDiamond_count() + "钻石");
                                //赠
                                tv_send_jf_left.setText("赠" + getRechargePackage.getData().getList().get(0).getIntegral_count() + "积分");
                            } else if (i == 1) {
                                //价格
                                tv_yuan_right.setText(getRechargePackage.getData().getList().get(1).getPrice() + "元");
                                //等价钻石
                                tv_zs_right.setText(getRechargePackage.getData().getList().get(1).getDiamond_count() + "钻石");
                                //赠
                                tv_send_jf_right.setText("赠" + getRechargePackage.getData().getList().get(1).getIntegral_count() + "积分");
                            }
                        }
                        rl_select_money1.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                selectMoneyShopId = getRechargePackage.getData().getList().get(0).getId() + "";
                                rl_select_money2.setBackground(baseActivity.getResources().getDrawable(R.drawable.shape_4_corner_bbbbbb));
                                rl_select_money1.setBackground(baseActivity.getResources().getDrawable(R.drawable.img_charge_shop_select));
                                img_select_send_bg_left.setImageResource(R.drawable.img_send_jg_bg_yes);
                                img_select_send_bg_right.setImageResource(R.drawable.img_send_jg_bg_no);
                            }
                        });
                        rl_select_money2.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                selectMoneyShopId = getRechargePackage.getData().getList().get(1).getId() + "";
                                rl_select_money1.setBackground(baseActivity.getResources().getDrawable(R.drawable.shape_4_corner_bbbbbb));
                                rl_select_money2.setBackground(baseActivity.getResources().getDrawable(R.drawable.img_charge_shop_select));
                                img_select_send_bg_left.setImageResource(R.drawable.img_send_jg_bg_no);
                                img_select_send_bg_right.setImageResource(R.drawable.img_send_jg_bg_yes);
                            }
                        });
                    }
                    return;
                }
            }
        };
        AppLoader appLoader = new AppLoader(AppLoader.BASEURL);
        appLoader.get_recharge_package(map.get("app_version"), HttpUtils.app_type, map.get("timestamp"), map.get("randomstr"), sign, httpListener);
    }

    //协议
    public void get_agreement_list(final Context baseActivity) {
        Map<String, String> map = HttpUtils.getPublicApiMap(baseActivity);
        String sign = HttpUtils.getSign(map);
        LogUtils.logE("测试: ", "加密的签名=" + sign);
        HttpListener<GetAgreementList> httpListener = new HttpListener<GetAgreementList>() {
            @Override
            public void onError(String errorMsg) {
            }

            @Override
            public void onSuccess(final GetAgreementList getAgreementList) {
                if (getAgreementList.getCode().equals("200")) {
                    //1注册协议，2用户服务协议，3充值协议，4积分规则，5签约公会协议，6主播直播协议，7主播直播管理规范，
                    //8用户违规管理规范，9关于我们，10联系我们，11鱼络圈隐私政策，12社区公约
                    for (int i = 0; i < getAgreementList.getData().size(); i++) {
                        if (getAgreementList.getData().get(i).getT_id() == 3) {
                            xyUrl = getAgreementList.getData().get(i).getHttp_url();
                            break;
                        }
                    }
                    return;
                }
            }
        };
        com.example.mylibrary.httpUtils.http.app.AppLoader appLoader = new com.example.mylibrary.httpUtils.http.app.AppLoader(com.example.mylibrary.httpUtils.http.app.AppLoader.BASEURL);
        appLoader.get_agreement_list(map.get("app_version"), HttpUtils.app_type, map.get("timestamp"), map.get("randomstr"), sign, httpListener);
    }

    //----------------------------------ali支付----------------------------------
    //al支付
    private void alipay(String id) {
        String token = (String) SPUtil.get(baseActivity, UserUtils.TOKEN_LOGIN, "");
        Map<String, String> map = HttpUtils.getPublicApiMap(baseActivity);
        map.put("token", token);
        map.put("id", id);
        String sign = HttpUtils.getSign(map);
        LogUtils.logE("测试: ", "加密的签名=" + sign);
        HttpListener<AlipayBean> httpListener = new HttpListener<AlipayBean>() {
            @Override
            public void onError(String errorMsg) {
            }

            @Override
            public void onSuccess(AlipayBean alipayBean) {
                if (alipayBean.getCode().equals("200")) {
                    startAliPay(alipayBean.getData().getResult());
                    return;
                }
                Toast.makeText(baseActivity, ErrorUtils.getErrorStr(alipayBean.getError_code(), alipayBean.getMsg()), Toast.LENGTH_SHORT).show();
            }
        };
        PayLoader payLoader = new PayLoader(PayLoader.BASEURL);
        payLoader.alipay(map.get("app_version"), HttpUtils.app_type, map.get("timestamp"), map.get("randomstr"), sign, token, id, httpListener);
    }

//    private Handler mHandler = new Handler() {
//        @Override
//        public void handleMessage(Message msg) {
//            super.handleMessage(msg);
//            if (msg.what == SDK_PAY_FLAG_ALI) {
//                //这里接收支付宝的回调信息
//                //需要注意的是，支付结果一定要调用自己的服务端来确定，不能通过支付宝的回调结果来判断
//                PayResult payResult = new PayResult((Map<String, String>) msg.obj);
//                //对于支付结果，请商户依赖服务端的异步通知结果。同步通知结果，仅作为支付结束的通知。
//                String resultInfo = payResult.getResult();// 同步返回需要验证的信息
//                String resultStatus = payResult.getResultStatus();
//                // 判断resultStatus 为9000则代表支付成功
//                if (TextUtils.equals(resultStatus, "9000")) {
//                    // 该笔订单是否真实支付成功，需要依赖服务端的异步通知。
////                    showToast("支付成功");
//                } else {
//                    // 该笔订单真实的支付结果，需要依赖服务端的异步通知。
////                    showToast("支付失败");
//                }
//                LogUtils.logE("测试支付宝支付: ", "msg=" + msg.toString());
//            }
//        }
//    };

    private void startAliPay(final String orderInfo) {
        final Runnable payRunnable = new Runnable() {

            @Override
            public void run() {
                PayTask alipay = new PayTask((Activity) baseActivity);
                Map<String, String> result = alipay.payV2(orderInfo, true);
//                LogUtils.logE("测试msp", result.toString());
//                Message msg = new Message();
//                msg.what = SDK_PAY_FLAG_ALI;
//                msg.obj = result;
//                mHandler.sendMessage(msg);
            }
        };
        // 必须异步调用
        Thread payThread = new Thread(payRunnable);
        payThread.start();
        //关闭弹窗
        if (MoneyChargePop.this.clickUserCallback != null) {
            MoneyChargePop.this.clickUserCallback.notifyNotStopLiver();
        }
        dismissPop();
    }

    //----------------------------------wx支付----------------------------------
    private void wxpay(String id) {
        String token = (String) SPUtil.get(baseActivity, UserUtils.TOKEN_LOGIN, "");
        Map<String, String> map = HttpUtils.getPublicApiMap(baseActivity);
        map.put("token", token);
        map.put("id", id);
        String sign = HttpUtils.getSign(map);
        LogUtils.logE("测试: ", "加密的签名=" + sign);
        HttpListener<WxpayBean> httpListener = new HttpListener<WxpayBean>() {
            @Override
            public void onError(String errorMsg) {
            }

            @Override
            public void onSuccess(WxpayBean wxpayBean) {
                if (wxpayBean.getCode().equals("200")) {
                    startWxPay(wxpayBean);
                    return;
                }
                Toast.makeText(baseActivity, ErrorUtils.getErrorStr(wxpayBean.getError_code(), wxpayBean.getMsg()), Toast.LENGTH_SHORT).show();
            }
        };
        PayLoader payLoader = new PayLoader(PayLoader.BASEURL);
        payLoader.wxpay(map.get("app_version"), HttpUtils.app_type, map.get("timestamp"), map.get("randomstr"), sign, token, id, httpListener);
    }

    public void startWxPay(final WxpayBean wxpayBean) {
        IWXAPI api = WXAPIFactory.createWXAPI(baseActivity, wxAppId);
        PayReq req = new PayReq();
        req.appId = wxpayBean.getData().getAppid();
        req.partnerId = wxpayBean.getData().getPartnerid();
        req.prepayId = wxpayBean.getData().getPrepayid();
        req.nonceStr = wxpayBean.getData().getNoncestr();
        req.timeStamp = wxpayBean.getData().getTimestamp() + "";
        req.packageValue = wxpayBean.getData().getPackageX();
        req.sign = wxpayBean.getData().getSign();
        api.sendReq(req);
        //关闭弹窗
        if (MoneyChargePop.this.clickUserCallback != null) {
            MoneyChargePop.this.clickUserCallback.notifyNotStopLiver();
        }
        dismissPop();
    }

    //----------------------------接口----------------------------
    public interface ClickCallback {
        void notifyNotStopLiver();
    }

    private ClickCallback clickUserCallback;
}

