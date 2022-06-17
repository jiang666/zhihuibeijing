package com.example.mylibrary.httpUtils.utils;

import android.content.Context;

public class UserUtils {

    public static String KF_IMG_URL = "https://img-blog.csdnimg.cn/201912111047391.png?x-oss-process=image/watermark,type_ZmFuZ3poZW5naGVpdGk,shadow_10,text_aHR0cHM6Ly9ibG9nLmNzZG4ubmV0L3FxXzM3MzIxMDk4,size_16,color_FFFFFF,t_70";
    public static String STRING_RULE = "<font color='#ffffff'>登录即同意《</font>" + "<font color='#ffffff'>中国移动认证服务条款</font>"
            + "<font color='#ffffff'>》和《</font>" + "<font color='#ffffff'>鱼络圈使用协议</font>" + "<font color='#ffffff'>》并<br>使用本机号登录</font>";
    public static String IS_LOGIN = "is_login";//是否登录
    public static String LOGIN_TYPE = "login_type";//登录方式 wx qq
    public static String FROM_ACT = "from_act";//从哪个页面跳转到登录页面的
    public static String FROM_ACT_TO_AUTOSIGNIN = "from_act_to_signin";//从哪个页面跳转到一键登录
    public static String POWERCODE_LOGIN_CAN_INPUT_PHONE = "powercode_login_can_input_code";//验证码登录页面是否可以输入手机号
    public static String SCREENHEIGHT = "screenheight";
    public static String SCREENWIDTH = "screenwidth";
    public static String TOKEN_LOGIN = "token_login";//登录 token
    public static String TOKEN_RY = "token_ry";//融云token
    public static String PHONE_NUM = "phone_num";
    public static String OTHER_IM_NAME = "Other_im_name";
    public static String OTHER_IM_TYPE = "Other_im_type";//正常私聊1 弹窗私聊2
    public static String UID = "u_id";
    public static String DID = "d_id";
    public static String NICE_NAME = "nice_name";
    public static String NICE_IMG_URL = "nice_img_url";
    public static String YU_NUM = "yu_num";//鱼号
    public static String SEX = "sex";//性别
    public static String SELF_DJ_URL = "self_dj_url";//自己用户等级图片
    public static String ADDRESS = "address";//地址
    public static String LIVE_BQS = "live_bqs";//直播所有标签
    public static String ADDRESS_CODE = "address_code";//地址编码
    public static String LO = "lo";//经纬度
    public static String LA = "la";//经纬度
    public static String CITY = "city";//地址编码
    public static String START_LIVE_IMG_BG = "start_live_img_bg";//开播上次上传的图片
    public static String START_LIVE_TYPE = "start_live_type";//频道
    public static String START_LIVE_TAG = "start_live_tag";//标签
    public static String IMG_TYPE = "img_type";//开播预览图片的类型
    public static String IMG_RESOURCE = "img_resource";//开播预览图片
    public static String PERSON_AUTH_NEED_API_DATE = "person_auth_need_api_date";//跳转到人工授权页面，是否需要获取之前提交的数据
    public static String AUTH_END_NEED_BACK = "auth_end_need_back";//授权审核界面是否需要返回按钮
    public static String SELF_SEARCH_HIS = "self_search_his";//主页搜索历史
    public static String SELF_SEARCH_HIS_SHOP = "self_search_his_shop";//店铺搜索历史
    public static String IS_HAS_FIRST_SIGN_UP = "IS_HAS_SIGN_UP_SUCCESS";//是否第一次注册成功过 -> 第一次展示推荐
    public static String AUTH_NAME = "auth_name";//认证的姓名
    public static String AUTH_SFZ_CODE = "auth_sfz_code";//认证的身份证号
    public static String LINK_URL = "linkurl";//网页地址
    public static String SEND_DM = "send_dm";//是否选中了发送弹幕
    public static String SHOW_EXIT_POP = "show_exit_pop";//是否显示退出直播间的弹框
    public static String LINK_URL_TITLE = "linkurl_title";//网页标题
    public static String LINK_NEED_SUGGEST = "link_needsuggest";//网页界面需要反馈
    public static String LINK_LINE = "link_line";//需要在线客服
    public static String HUAWEI_MEIZU_BOTTOM_HEIGHT = "hw_mz_bottom_height";//华为魅族底部状态栏高度  在不用values沉浸情况下，需要减去这个高度
    public static String GG_ID = "gg_id";//工会id
    public static String SpSaveLoadFileName = "SP_SAVE_LOADFILENAME";//下载好的svg保存姓名
    public static String START_TIME = "2018-01";
    public static String HAS_AGREE_XY = "has_agree_xy";
    //看直播
    public static String LOOK_LIVER_IDS = "look_liver_ids";//拉流跳转的id列表
    public static String LOOK_LIVER_URLS = "look_liver_urls";//拉流跳转的url列表
    public static String LOOK_LIVER_INDEX = "look_liver_index";//拉流跳转的id列表位置
    public static String LOOK_LIVER_TITLES = "look_liver_titles";//拉流跳转的title列表位置
    public static String LOOK_LIVER_IS_PAGE = "look_liver_is_page";//是否有下页数据
    public static String LOOK_LIVER_API = "look_liver_api";//api
    public static String LOOK_LIVER_PAGE = "look_liver_page";//page页数
    public static String LOOK_LO = "look_liver_lo";//lo
    public static String LOOK_LA = "look_liver_la";//la
    public static String LOOK_CITY_CODE = "look_liver_city_code";//city_code
    public static String LOOK_SEX = "look_liver_sex";//sex
    public static String LOOK_KEYWORD = "look_liver_keyword";//sex


    //-------------------操作登录信息-------------------
    // 1：一键登录 2：手机验证码 3：微信 4：QQ
    public static void saveLoginInfo(Context context, int type) {
        SPUtil.put(context, LOGIN_TYPE, type + "");
        setUserIsLogin(context);
    }

    //保存用户id
    public static void saveUid(Context context, String id) {
        SPUtil.put(context, UID, id + "");
    }

    //退出登录时候调用
    public static void clearLoginInfo(Context context) {
//        SPUtil.put(context, LOGIN_TYPE, "-1");  //退出登录后 不清除
        SPUtil.put(context, IS_LOGIN, "false");
        SPUtil.put(context, TOKEN_LOGIN, "");
//        SPUtil.put(context, NICE_NAME, "");
//        SPUtil.put(context, NICE_IMG_URL, "");//退出登录后 不清除
        SPUtil.put(context, TOKEN_RY, "");
        SPUtil.put(context, YU_NUM, "");
    }

    //保存每次打开app的定位地址
    public static void saveLo(Context context, String lo, String la, String code, String address, String city) {
        SPUtil.put(context, ADDRESS_CODE, code);
        SPUtil.put(context, LO, lo);
        SPUtil.put(context, LA, la);
        SPUtil.put(context, CITY, city);
        SPUtil.put(context, ADDRESS, address);
    }

    //保存经纬度
    public static void saveLoLa(Context context, String lo, String la, String code) {
        SPUtil.put(context, LO, lo);
        SPUtil.put(context, LA, la);
        SPUtil.put(context, ADDRESS_CODE, code);
    }

    //保存手机号码
    public static void savePhoneNum(Context context, String phoneNum) {
        SPUtil.put(context, PHONE_NUM, phoneNum);
    }

    //昵称
    public static void saveNiceName(Context context, String niceName) {
        SPUtil.put(context, NICE_NAME, niceName);
    }

    //头像url
    public static void saveNiceImgUrl(Context context, String niceImgUrl) {
        SPUtil.put(context, NICE_IMG_URL, niceImgUrl);
    }

    //token
    public static void saveToken(Context context, String token) {
        SPUtil.put(context, TOKEN_LOGIN, token);
    }

    //融云token
    public static void saveRyToken(Context context, String token) {
        SPUtil.put(context, TOKEN_RY, token);
    }

    //鱼号
    public static void saveYuNum(Context context, String yunum) {
        SPUtil.put(context, YU_NUM, yunum);
    }

    //性别
    public static void saveSex(Context context, String sex) {
        SPUtil.put(context, SEX, sex);
    }


    //-------------------判断是否登录-------------------
    //是否登录
    public static boolean isLogin(Context context) {
        String code = (String) SPUtil.get(context, IS_LOGIN, "false");
        if (code.equals("true")) {
            return true;
        }
        return false;
    }

    //设置为登录状态
    public static void setUserIsLogin(Context context) {
        SPUtil.put(context, IS_LOGIN, "true");
    }

    //-------------------判断是否第一次注册成功-------------------
    public static boolean hasFirstSignUpSuccess(Context context) {
        String value = (String) SPUtil.get(context, IS_HAS_FIRST_SIGN_UP, "false");
        if (value.equals("false")) {
            return false;
        }
        return true;
    }

    //跳转到推荐页面再调用 -> 永不清空
    public static void setHasSignUpSuccess(Context context) {
        SPUtil.put(context, IS_HAS_FIRST_SIGN_UP, "true");
    }

    //----------------------------开播工具------------------------------
    public static String TOOLS_GG = "tools_gg";//公告 0:无 1:有
    public static String TOOLS_CAMERA = "tools_camea";//翻转摄像头 0:前 1:后置
    public static String TOOLS_FLASH = "tools_flash";//开启闪光 0:没开 1:开了
    public static String TOOLS_JX = "tools_jx";//镜像 0:关 1:开
    public static String TOOLS_NO_SOUND = "tools_no_sound";//静音 0:不静音 1:静音
    public static String TOOLS_BIG_TEX = "tools_big_tex";//大字体 0:小字体 1:大字体

    //只有关闭直播间时候才需要调用
    public static void resetLiveToolsParams(Context context) {
        SPUtil.put(context, TOOLS_GG, "0");
        SPUtil.put(context, TOOLS_CAMERA, "0");
        SPUtil.put(context, TOOLS_FLASH, "0");
        SPUtil.put(context, TOOLS_JX, "0");
        SPUtil.put(context, TOOLS_NO_SOUND, "0");
        SPUtil.put(context, TOOLS_BIG_TEX, "0");
    }
}
