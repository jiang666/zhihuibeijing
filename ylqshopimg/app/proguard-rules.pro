# Add project specific ProGuard rules here.
# You can control the set of applied configuration files using the
# proguardFiles setting in build.gradle.
#
# For more details, see
#   http://developer.android.com/guide/developing/tools/proguard.html

# If your project uses WebView with JS, uncomment the following
# and specify the fully qualified class name to the JavaScript interface
# class:
#-keepclassmembers class fqcn.of.javascript.interface.for.webview {
#   public *;
#}

# Uncomment this to preserve the line number information for
# debugging stack traces.
#-keepattributes SourceFile,LineNumberTable

# If you keep the line number information, uncomment this to
# hide the original source file name.
#-renamesourcefileattribute SourceFile

#日历控件
-keepclasseswithmembers class * {
    public <init>(android.content.Context);
}

-ignorewarnings
-keep class * {
    public private *;
}

-keep class com.squareup.wire.** { *; }
-keep class com.opensource.svgaplayer.proto.** { *; }
#保护自定义moudle
-keep public class * extends com.bumptech.glide.module.AppGlideModule
-keep class com.bumptech.glide.GeneratedAppGlideModuleImpl
-keep public class * implements com.bumptech.glide.module.GlideModule
    -keep public class * extends com.bumptech.glide.AppGlideModule
    -keep public enum com.bumptech.glide.load.resource.bitmap.ImageHeaderParser$** {
        **[] $VALUES;
        public *;
    }

    -keep class com.alibaba.livecloud.** { *;}
    -keep class com.alivc.** { *;}
#支付宝
-keep class com.alipay.android.app.IAlixPay{*;}
-keep class com.alipay.mobile.**{*;}
-keep class com.alipay.android.app.IAlixPay$Stub{*;}
-keep class com.alipay.android.app.IRemoteServiceCallback{*;}
-keep class com.alipay.android.app.IRemoteServiceCallback$Stub{*;}
-keep class com.alipay.sdk.app.PayTask{ public *;}
-keep class com.alipay.sdk.app.AuthTask{ public *;}
-dontwarn com.tencent.bugly.**
-keep public class com.tencent.bugly.**{*;}
#不要混淆MyBean的所有属性与方法
-keepclasseswithmembers class MyBean {
    <fields>;
    <methods>;
}

#不要混淆MySuperBean所有子类的属性与方法
-keepclasseswithmembers class * extends MySuperBean{
    <fields>;
    <methods>;
}
#保持 Parcelable 不被混淆（aidl文件不能去混淆）
-keepclassmembers class * implements android.os.Parcelable {
  public static final android.os.Parcelable$Creator CREATOR;
}
#保持R文件中的静态变量不被混淆，否则，你的反射是获取不到资源id的
-keepclassmembers class **.R$* {
    public static <fields>;
}
#在view中保留get和set方法以便动画可以正常运行
-keepclassmembers public class * extends android.view.View {
   void set*(***);
   *** get*();
}
#以便正常反射在xml中设置的onClickWe
-keepclassmembers class * extends android.app.Activity {
   public void *(android.view.View);
}

-keep class com.yulqlive.app.http.** {*;}
-keep class com.example.mylibrary.** {*;}
-keep class com.aliyun.vodplayerview.** {*;}
-keep class com.alivc.live.** {*;}
#ali播放
-keep class com.aliyun.alivclive.** {*;}
-keep class com.aliyun.quhelp.** {*;}
-keep class com.aliyun.resample.** {*;}
-keep class com.aliyun.quview.** {*;}
-keep class com.alibaba.sdk.** {*;}
-keep class com.alibaba.demo.** {*;}
-keep class net.yc.** {*;}
-keep class net.lling.** {*;}
-keep class com.aliyun.** {*;}
-keep class com.aliyun.pusher.** {*;}
-keep class com.aliyun.vodplayerview.** {*;}
-keep class com.alivc.** {*;}
-keep class com.alivc.live.** {*;}
-keep class com.flyco.** {*;}

#crashreporter
-keep class com.alibaba.motu.crashreporter.MotuCrashReporter{*;}
-keep class com.alibaba.motu.crashreporter.ReporterConfigure{*;}
-keep class com.alibaba.motu.crashreporter.IUTCrashCaughtListener{*;}
-keep class com.ut.mini.crashhandler.IUTCrashCaughtListener{*;}
-keep class com.alibaba.motu.crashreporter.utrestapi.UTRestReq{*;}
-keep class com.alibaba.motu.crashreporter.handler.nativeCrashHandler.NativeCrashHandler{*;}
-keep class com.alibaba.motu.crashreporter.handler.nativeCrashHandler.NativeExceptionHandler{*;}
-keep interface com.alibaba.motu.crashreporter.handler.nativeCrashHandler.NativeExceptionHandler{*;}
#crashreporter3.0以后 一定要加这个
-keep class com.uc.crashsdk.JNIBridge{*;}
#微信登录
-keep class com.tencent.mm.opensdk.** {
    *;
}
-keep class com.tencent.wxop.** {
    *;
}
-keep class com.tencent.mm.sdk.** {
    *;
}
#通用
-keepnames class * implements java.io.Serializable #需要序列化和反序列化的类不能被混淆（注：Java反射用到的类也不能被混淆）
-keepattributes Signature  #过滤泛型（不写可能会出现类型转换错误，一般情况把这个加上就是了）
-keepattributes *Annotation*  #假如项目中有用到注解，应加入这行配置
-keep class **.Webview2JsInterface { *; }  #保护WebView对HTML页面的API不被混淆
-keepclassmembers class * extends android.webkit.WebViewClient {  #如果你的项目中用到了webview的复杂操作 ，最好加入
     public void *(android.webkit.WebView,java.lang.String,android.graphics.Bitmap);
     public boolean *(android.webkit.WebView,java.lang.String);
}
-keepclassmembers class * extends android.webkit.WebChromeClient {  #如果你的项目中用到了webview的复杂操作 ，最好加入
     public void *(android.webkit.WebView,java.lang.String);
}
#不需要提醒这个肯定安全（文档就是这么说的）
-dontwarn android.support.**
# Understand the @Keep support annotation.
-keep class android.support.annotation.Keep
-keep @android.support.annotation.Keep class * {*;}
-keepclasseswithmembers class * {
    @android.support.annotation.Keep <methods>;
}
-keepclasseswithmembers class * {
    @android.support.annotation.Keep <fields>;
}
-keepclasseswithmembers class * {
    @android.support.annotation.Keep <init>(...);
}
#eventbus
-keepattributes *Annotation*
-keepclassmembers class ** {
    @org.greenrobot.eventbus.Subscribe <methods>;
}
-keep enum org.greenrobot.eventbus.ThreadMode { *; }
# Only required if you use AsyncExecutor
-keepclassmembers class * extends org.greenrobot.eventbus.util.ThrowableFailureEvent {
    <init>(java.lang.Throwable);
}
##Glide
-dontwarn com.bumptech.glide.**
-keep class com.bumptech.glide.**{*;}
-keep public class * implements com.bumptech.glide.module.GlideModule
-keep public class * extends com.bumptech.glide.AppGlideModule
-keep public enum com.bumptech.glide.load.resource.bitmap.ImageHeaderParser$** {
  **[] $VALUES;
  public *;
}
#-keep public class * implements com.bumptech.glide.module.GlideModule
#-keep public enum com.bumptech.glide.load.resource.bitmap.ImageHeaderParser$** {
#  **[] $VALUES;
#  public *;
#}

#高斯模糊
-keep public class * extends com.bumptech.glide.module.AppGlideModule
-keep public enum com.bumptech.glide.load.ImageHeaderParser$** {
  **[] $VALUES;
  public *;
}
#阿里一键登录
-keep class cn.com.chinatelecom.gateway.lib.** {*;}
-keep class com.unicom.xiaowo.login.** {*;}
-keep class com.cmic.sso.sdk.** {*;}
-keep class com.mobile.auth.gatewayauth.** {*;}
-keep class android.support.v4.** { *;}
-keep class org.json.**{*;}
-keep class com.alibaba.fastjson.** {*;}
#arouter
-keep public class com.alibaba.android.arouter.routes.**{*;}
#alivc
#-keep class com.alivc.component.**{*;}
#-libraryjars ../app/src/main/jniLibs/arm64-v8a/libAliFaceAREngine.so
#-libraryjars ../app/src/main/jniLibs/arm64-v8a/libaliresample.so
#-libraryjars ../app/src/main/jniLibs/arm64-v8a/libFaceAREngine.so
#-libraryjars ../app/src/main/jniLibs/arm64-v8a/libfdk-aac.so
#-libraryjars ../app/src/main/jniLibs/arm64-v8a/liblive-openh264.so
#-libraryjars ../app/src/main/jniLibs/arm64-v8a/libQuCore.so
#-libraryjars ../app/src/main/jniLibs/arm64-v8a/libQuCore-ThirdParty.so
#
#-libraryjars ../app/src/main/jniLibs/armeabi-v7a/libAliFaceAREngine.so
#-libraryjars ../app/src/main/jniLibs/armeabi-v7a/libaliresample.so
#-libraryjars ../app/src/main/jniLibs/armeabi-v7a/libFaceAREngine.so
#-libraryjars ../app/src/main/jniLibs/armeabi-v7a/libfdk-aac.so
#-libraryjars ../app/src/main/jniLibs/armeabi-v7a/liblive-openh264.so
#-libraryjars ../app/src/main/jniLibs/armeabi-v7a/libQuCore.so
#-libraryjars ../app/src/main/jniLibs/armeabi-v7a/libQuCore-ThirdParty.so

-keepclasseswithmembernames class * {
native <methods>;
}
#3D 地图
-keep class com.amap.api.mapcore.**{*;}
-keep class com.amap.api.maps.**{*;}
-keep class com.autonavi.amap.mapcore.*{*;}
#定位
-keep class com.amap.api.location.**{*;}
-keep class com.loc.**{*;}
-keep class com.amap.api.fence.**{*;}
-keep class com.autonavi.aps.amapapi.model.**{*;}
# 搜索
-keep class com.amap.api.services.**{*;}
# 2D地图
-keep  class com.amap.api.maps2d.**{*;}
-keep class com.amap.api.mapcore2d.**{*;}
# 导航
-keep class com.amap.api.navi.**{*;}
-keep class com.autonavi.**{*;}



#指定代码的压缩级别
-optimizationpasses 5
#包明不混合大小写
-dontusemixedcaseclassnames
#不去忽略非公共的库类
-dontskipnonpubliclibraryclasses
 #优化  不优化输入的类文件
-dontoptimize
 #预校验
-dontpreverify
 #混淆时是否记录日志
-verbose
 # 混淆时所采用的算法
-optimizations !code/simplification/arithmetic,!field/*,!class/merging/*
#保护注解
-keepattributes *Annotation*
#如果有引用v4包可以添加下面这行
-keep public class * extends android.support.v4.app.Fragment
########记录生成的日志数据，gradle build时 在本项目根目录输出-end######
#如果引用了v4或者v7包
-dontwarn android.support.**
#保持 native 方法不被混淆
#-keepclasseswithmembernames class * {
#    native <methods>;
#}

-keepclassmembers class ** {
 public void onEvent*(**);
}

-keep class * {
    native <methods>;
}
-keep class com.alivc.component.**{*;}

#保持自定义控件类不被混淆
-keepclasseswithmembers class * {
    public <init>(android.content.Context, android.util.AttributeSet);
}
-keepclasseswithmembers class * {
    public <init>(android.content.Context, android.util.AttributeSet, int);
}

#保持自定义控件类不被混淆
-keepclassmembers class * extends android.app.Activity {
   public void *(android.view.View);
}

-keep public class * extends android.view.View {
    public <init>(android.content.Context);
    public <init>(android.content.Context, android.util.AttributeSet);
    public <init>(android.content.Context, android.util.AttributeSet, int);
    public void set*(...);
}

#保持 Parcelable 不被混淆
-keep class * implements android.os.Parcelable {
  public static final android.os.Parcelable$Creator *;
}

#保持 Serializable 不被混淆
-keepnames class * implements java.io.Serializable

#保持 Serializable 不被混淆并且enum 类也不被混淆
-keepclassmembers class * implements java.io.Serializable {
    static final long serialVersionUID;
    private static final java.io.ObjectStreamField[] serialPersistentFields;
    !static !transient <fields>;
    !private <fields>;
    !private <methods>;
    private void writeObject(java.io.ObjectOutputStream);
    private void readObject(java.io.ObjectInputStream);
    java.lang.Object writeReplace();
    java.lang.Object readResolve();
}

#保持枚举 enum 类不被混淆
-keepclassmembers enum * {
  public static **[] values();
  public static ** valueOf(java.lang.String);
}

-keepclassmembers class * {
    public void *ButtonClicked(android.view.View);
}

#不混淆资源类
-keepclassmembers class **.R$* {
    public static <fields>;
}

#避免混淆泛型 如果混淆报错建议关掉
-keepattributes Signature

#移除Log类打印各个等级日志的代码，打正式包的时候可以做为禁log使用，这里可以作为禁止log打印的功能使用，另外的一种实现方案是通过BuildConfig.DEBUG的变量来控制
#-assumenosideeffects class android.util.Log {
#    public static *** v(...);
#    public static *** i(...);
#    public static *** d(...);
#    public static *** w(...);
#    public static *** e(...);
#}

#-------------------------------------------定制化区域----------------------------------------------
#---------------------------------1.实体类---------------------------------
-keep class com.yulqlive.app.bean.** { *; }

#########################################################第三方的配置开始#######################
############shareSDK混淆配置################
-keep class cn.sharesdk.**{*;}
-keep class com.sina.**{*;}
-keep class **.R$* {*;}
-keep class **.R{*;}
-keep class com.mob.**{*;}
#####EventBus混淆配置
-keepattributes *Annotation*
-keepclassmembers class ** {
    @org.greenrobot.eventbus.Subscribe <methods>;
}
-keep enum org.greenrobot.eventbus.ThreadMode { *; }

# Only required if you use AsyncExecutor
-keepclassmembers class * extends org.greenrobot.eventbus.util.ThrowableFailureEvent {
    <init>(java.lang.Throwable);
}

###########极光混淆配置
-dontoptimize
-dontpreverify

-dontwarn cn.jpush.**
-keep class cn.jpush.** { *; }
-keep class * extends cn.jpush.android.helpers.JPushMessageReceiver { *; }

-dontwarn cn.jiguang.**
-keep class cn.jiguang.** { *; }


###########okhttputils 和okhttp相关的 的混淆####################
#okhttputils
-dontwarn com.zhy.http.**
-keep class com.zhy.http.**{*;}

#okhttp
-dontwarn okhttp3.**
-keep class okhttp3.**{*;}
-dontwarn com.squareup.okhttp.**
-keep class com.squareup.okhttp.{*;}
-dontwarn com.squareup.okhttp3.**
-keep class com.squareup.okhttp3.** { *;}

#----------- gson ----------------
-keep class com.google.gson.** {*;}
-keep class com.google.**{*;}
-keep class sun.misc.Unsafe { *; }
-keep class com.google.gson.stream.** { *; }
-keep class com.google.gson.examples.android.model.** { *; }
-keep class com.qiancheng.carsmangersystem.**{*;}
# Application classes that will be serialized/deserialized over Gson 下面替换成自己的实体类
-keep class com.xxx.xxx.bean.** { *; }

#----------- rxjava rxandroid----------------
-dontwarn sun.misc.**
-keepclassmembers class rx.internal.util.unsafe.*ArrayQueue*Field* {
    long producerIndex;
    long consumerIndex;
}
-keepclassmembers class rx.internal.util.unsafe.BaseLinkedQueueProducerNodeRef {
    rx.internal.util.atomic.LinkedQueueNode producerNode;
}
-keepclassmembers class rx.internal.util.unsafe.BaseLinkedQueueConsumerNodeRef {
    rx.internal.util.atomic.LinkedQueueNode consumerNode;
}
-dontnote rx.internal.util.PlatformDependent

#okio
-dontwarn okio.**
-keep class okio.**{*;}

-dontwarn com.squareup.okhttp.**
-dontwarn javax.annotation.**
-dontwarn com.android.volley.toolbox.**
-dontwarn com.facebook.infer.**
-keep class com.squareup.okhttp.** { *;}
-keep interface com.squareup.okhttp.** { *; }

######################### Retrofit混淆配置##############################
-dontnote retrofit2.Platform
-dontnote retrofit2.Platform$IOS$MainThreadExecutor
-dontwarn retrofit2.Platform$Java8
-keepattributes Signature
-keepattributes Exceptions

-keep class retrofit2.** { *; }
-dontwarn retrofit2.**
-keepattributes Signature
-keepattributes Exceptions
-dontwarn okio.**
-dontwarn javax.annotation.**

#########################友盟的混淆配置################

-keep class com.umeng.** {*;}
-keepclassmembers class * {
   public <init> (org.json.JSONObject);
}

-keepclassmembers class * {
   public <init>(org.json.JSONObject);
}
-keep public class [com.uutus.huaxia.geography].R$*{
public static final int *;
}
-keepclassmembers enum * {
    public static **[] values();
    public static ** valueOf(java.lang.String);
}
-keep public class [com.uutus.huaxia.geography].R$*{
public static final int *;
}

###############加密混淆###########
########腾讯X5内核浏览器中的的代码不被混淆#####
-keep class com.tencent.** {*;}
########RSA中的代码不被混淆
-keep class Decoder.** {*;}
-keep class org.bouncycastle.** {*;}


##########  保证类库Image 中导入的jar包不被混淆
-keep class com.davemorrissey.** {*;}
-keep class com.filippudak.** {*;}
-keep class am.util.** {*;}
-keep class com.blankj.** {*;}

#########################################################第三方的配置结束#######################

#==================gson && protobuf==========================
-dontwarn com.google.**
-keep class com.google.gson.** {*;}
-keep class com.google.protobuf.** {*;}

#ProGuard 混淆

# keep住源文件以及行号
-keepattributes SourceFile,LineNumberTable


-keepattributes Signature
-dontwarn com.jcraft.jzlib.**
-keep class com.jcraft.jzlib.**  { *;}

-dontwarn sun.misc.**
-keep class sun.misc.** { *;}

-dontwarn com.alibaba.fastjson.**
-keep class com.alibaba.fastjson.** { *;}

-dontwarn sun.security.**
-keep class sun.security.** { *; }

-dontwarn com.google.**
-keep class com.google.** { *;}

-dontwarn com.avos.**
-keep class com.avos.** { *;}

-keep public class android.net.http.SslError
-keep public class android.webkit.WebViewClient

-dontwarn android.webkit.WebView
-dontwarn android.net.http.SslEr
-dontwarn android.webkit.WebViewClient
-dontwarn android.support.**

#融云
-keep class cn.rongcloud.** {*;}
-keepclassmembers class fqcn.of.javascript.interface.for.webview {
     public *;
}

-keepattributes Exceptions,InnerClasses

-keep class io.rong.** {*;}

-keep class * implements io.rong.imlib.model.MessageContent{*;}

-keepattributes Signature

-keepattributes *Annotation*

-keep class sun.misc.Unsafe { *; }

-keep class com.google.gson.examples.android.model.** { *; }

-keepclassmembers class * extends com.sea_monster.dao.AbstractDao {
     public static java.lang.String TABLENAME;
}
-keep class **$Properties
-dontwarn org.eclipse.jdt.annotation.**

-keep class com.ultrapower.** {*;}

-dontwarn org.apache.**
-keep class org.apache.** { *;}

-dontwarn org.jivesoftware.smack.**
-keep class org.jivesoftware.smack.** { *;}

-dontwarn com.loopj.**
-keep class com.loopj.** { *;}


-dontwarn org.xbill.**
-keep class org.xbill.** { *;}

-keepattributes *Annotation*


# Gson
-keep class com.example.testing.retrofitdemo.bean.**{*;} # 自定义数据模型的bean目录
#gson
#如果用用到Gson解析包的，直接添加下面这几行就能成功混淆，不然会报错。

##---------------Begin: proguard configuration for Gson  ----------
# Gson uses generic type information stored in a class file when working with fields. Proguard
# removes such information by default, so configure it to keep all of it.
-keepattributes Signature

# For using GSON @Expose annotation
-keepattributes *Annotation*

# Gson specific classes
-dontwarn sun.misc.**
#-keep class com.google.gson.stream.** { *; }

# Prevent proguard from stripping interface information from TypeAdapterFactory,
# JsonSerializer, JsonDeserializer instances (so they can be used in @JsonAdapter)
-keep class * implements com.google.gson.TypeAdapterFactory
-keep class * implements com.google.gson.JsonSerializer
-keep class * implements com.google.gson.JsonDeserializer
# Application classes that will be serialized/deserialized over Gson


##---------------End: proguard configuration for Gson  ----------

#基础混淆添加配置
-keepclassmembers class **.R$* {
    public static <fields>;
    public static final int *;
}
-keepclassmembers class * extends android.app.Activity { # 保持自定义控件类不被混淆
    public void *(android.view.View);
}

#保留我们使用的四大组件，自定义的Application等等这些类不被混淆,因为这些子类都有可能被外部调用
-keep public class * extends android.app.Activity
-keep public class * extends android.app.Application
-keep public class * extends android.app.Service
-keep public class * extends android.content.BroadcastReceiver
-keep public class * extends android.content.ContentProvider
-keep public class * extends android.app.backup.BackupAgentHelper
-keep public class * extends android.preference.Preference
-keep public class * extends android.view.View
-keep public class com.android.vending.licensing.ILicensingService
# 保留support下的所有类及其内部类
-keep class android.support.** {*;}

# 保留继承的
-keep public class * extends android.support.v4.**
-keep public class * extends android.support.v7.**
-keep public class * extends android.support.annotation.**
-keep public class * extends android.app.Fragment

# 对于带有回调函数的onXXEvent、**On*Listener的，不能被混淆
-keepclassmembers class * {
    void *(**On*Event);
    void *(**On*Listener);
}

# webView处理，项目中没有使用到webView忽略即可
-keepclassmembers class fqcn.of.javascript.interface.for.webview {
    public *;
}
-keepclassmembers class * extends android.webkit.webViewClient {
    public void *(android.webkit.WebView, java.lang.String, android.graphics.Bitmap);
    public boolean *(android.webkit.WebView, java.lang.String);
}
-keepclassmembers class * extends android.webkit.webViewClient {
    public void *(android.webkit.webView, jav.lang.String);
}

-keep class android.support.**{*;}

-keepclassmembers class ** {
    @com.yanzhenjie.permission.PermissionYes <methods>;
}
-keepclassmembers class ** {
    @com.yanzhenjie.permission.PermissionNo <methods>;
}

-keepclassmembers class * {
   public <init> (org.json.JSONObject);
}

