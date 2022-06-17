package com.example.mylibrary.httpUtils;

import com.alibaba.android.arouter.launcher.ARouter;
import com.example.mylibrary.httpUtils.utils.RouterUtils;
import com.example.mylibrary.httpUtils.utils.UserUtils;

public class ErrorUtils {

    public static String getErrorStr(String code, String errorMsg) {
        if(code.equals("10001")||code.equals("10002")){
            ARouter.getInstance()
                    .build(RouterUtils.USER_SIGN)
                    .withString("needclearall", "true")
                    .navigation();
            return "登录过期,请重新登录";
        }
        if (!errorMsg.equals("")) {
            String s = errorMsg;
            return s;
        } else {
            return getErrorS(code);
        }
    }


    public static String getErrorS(String code) {
        if (code.equals("10001")) {
            return "登录过期,请重新登录";//token过期
        } else if (code.equals("10002")) {
            return "无效token";
        } else if (code.equals("20001")) {
            return "未登录";
        } else if (code.equals("20002")) {
            return "用户信息错误";
        } else if (code.equals("20003")) {
            return "用户不存在";
        } else if (code.equals("20004")) {
            return "手机号码已注册该平台";
        } else if (code.equals("20005")) {
            return "手机号码未注册该平台";
        } else if (code.equals("20006")) {
            return "用户被封禁";
        } else if (code.equals("20007")) {
            return "登陆密码有误";
        } else if (code.equals("20008")) {
            return "请绑定手机号码";
        } else if (code.equals("20009")) {
            return "旧密码输入错误";
        } else if (code.equals("20010")) {
            return "不能与原有手机号码相同";
        } else if (code.equals("30001")) {
            return "注册融云报错";
        } else if (code.equals("40000")) {
            return "服务器发生错误";
        } else if (code.equals("40001")) {
            return "sql错误";
        } else if (code.equals("40002")) {
            return "参数错误";
        } else if (code.equals("40003")) {
            return "rsa解密错误";
        } else if (code.equals("40004")) {
            return "非法操作";
        } else if (code.equals("40005")) {
            return "数据不存在";
        } else if (code.equals("40006")) {
            return "图片解析错误";
        } else if (code.equals("40007")) {
            return "重复提交接口";
        } else if (code.equals("40008")) {
            return "签名错误";
        } else if (code.equals("60000")) {
            return "通用提示代码";
        } else if (code.equals("60001")) {
            return "验证码超次数";
        } else if (code.equals("60002")) {
            return "验证码发送失败-调用第三方失败";
        } else if (code.equals("60003")) {
            return "验证码一分钟后可获取";
        } else if (code.equals("60004")) {
            return "验证码错误";
        } else if (code.equals("60005")) {
            return "钻石或积分不足";
        } else if (code.equals("60006")) {
            return "未绑定手机号码";
        } else if (code.equals("70001")) {
            return "苹果交易transaction_id没有找到";
        } else if (code.equals("70002")) {
            return "苹果交易receipt_data验证不通过";
        } else {
            return "位置错误";
        }
    }
}
