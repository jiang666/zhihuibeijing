package com.itheima.qq15;

import android.app.ActivityManager;
import android.app.Application;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.BitmapFactory;
import android.media.AudioManager;
import android.media.SoundPool;
import android.util.Log;

import com.hyphenate.EMConnectionListener;
import com.hyphenate.EMContactListener;
import com.hyphenate.EMError;
import com.hyphenate.EMMessageListener;
import com.hyphenate.chat.EMClient;
import com.hyphenate.chat.EMMessage;
import com.hyphenate.chat.EMOptions;
import com.hyphenate.chat.EMTextMessageBody;
import com.hyphenate.exceptions.HyphenateException;
import com.itheima.qq15.adapter.MessageListenerAdapter;
import com.itheima.qq15.db.DBUtils;
import com.itheima.qq15.event.OnContactUpdateEvent;
import com.itheima.qq15.utils.ThreadUtils;
import com.itheima.qq15.utils.ToastUtils;
import com.itheima.qq15.view.BaseActivity;
import com.itheima.qq15.view.ChatActivity;
import com.itheima.qq15.view.LoginActivity;

import org.greenrobot.eventbus.EventBus;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import cn.bmob.v3.Bmob;

import static com.hyphenate.chat.a.a.a.a;
import static com.hyphenate.chat.a.a.a.i;

/**
 * 作者： itheima
 * 时间：2016-10-15 10:26
 * 网址：http://www.itheima.com
 */
//用于全局的初始化操作
//生命周期跟进程的生命周期是一致
public class QQApplication extends Application {

    private static final String TAG = "QQApplication";
    private SoundPool mSoundPool;
    private int mDuanSound;
    private int mYuluSound;
    private List<BaseActivity> mBaseActivityList = new ArrayList<>();

    /**
     * 默认情况下，只有当应用第一次启动的时候调用一次。
     */
    @Override
    public void onCreate() {
        super.onCreate();
        initHuanxin();
        initBmob();
        initDB();
        initSoundPool();
    }
    public void addActivity(BaseActivity activity){
        if (!mBaseActivityList.contains(activity)){
            mBaseActivityList.add(activity);
        }
    }
    public void removeActivity(BaseActivity activity){
        mBaseActivityList.remove(activity);
    }

    private void initSoundPool() {
        mSoundPool = new SoundPool(2, AudioManager.STREAM_MUSIC,0);
        mDuanSound = mSoundPool.load(this, R.raw.duan, 1);
        mYuluSound = mSoundPool.load(this, R.raw.yulu, 1);
    }

    private void initDB() {
        DBUtils.initDB(this);
    }

    private void initBmob() {
        Bmob.initialize(this, "31d262596a9e0a6e09dce19016f2e567");
    }

    private void initHuanxin() {
        EMOptions options = new EMOptions();
        // 默认添加好友时，是不需要验证的，改成需要验证
        options.setAcceptInvitationAlways(false);
        /**
         * 下面的代码是为了避免环信被初始化2次
         */
        int pid = android.os.Process.myPid();
        String processAppName = getAppName(pid);
        // 如果APP启用了远程的service，此application:onCreate会被调用2次
        // 为了防止环信SDK被初始化2次，加此判断会保证SDK被初始化1次
        // 默认的APP会在以包名为默认的process name下运行，如果查到的process name不是APP的process name就立即返回
        if (processAppName == null || !processAppName.equalsIgnoreCase(getPackageName())) {
            // 则此application::onCreate 是被service 调用的，直接返回
            return;
        }
        //初始化
        EMClient.getInstance().init(this, options);
        //在做打包混淆时，关闭debug模式，避免消耗不必要的资源
        EMClient.getInstance().setDebugMode(true);
        //添加通讯录监听
        initContactListener();
        //添加消息的监听
        initMessageListener();
        //监听连接状态的改变
        initConnectionListener();
    }

    private void initContactListener() {
        EMClient.getInstance().contactManager().setContactListener(new EMContactListener() {
            @Override
            public void onContactAdded(String s) {
                //好友请求被同意
                //发出通知让ContactFragment更新UI
                EventBus.getDefault().post(new OnContactUpdateEvent(s, true));
            }

            @Override
            public void onContactDeleted(String s) {
                //被删除时回调此方法
                EventBus.getDefault().post(new OnContactUpdateEvent(s, false));
                Log.d(TAG, "onContactDeleted: " + s);
            }

            @Override
            public void onContactInvited(String username, String reason) {
                //收到好友邀请
                Log.d(TAG, "onContactInvited: " + username + "/" + reason);
                //同意或者拒绝
                try {
                    EMClient.getInstance().contactManager().acceptInvitation(username);
                } catch (HyphenateException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onContactAgreed(String s) {
                //增加了联系人时回调此方法
            }

            @Override
            public void onContactRefused(String s) {
                //好友请求被拒绝
            }
        });
    }

    private void initMessageListener() {
        EMClient.getInstance().chatManager().addMessageListener(new MessageListenerAdapter() {
            @Override
            public void onMessageReceived(List<EMMessage> list) {
                super.onMessageReceived(list);
                if (list != null && list.size() > 0) {
                    /**
                     * 1. 判断当前应用是否在后台运行
                     * 2. 如果是在后台运行，则发出通知栏
                     * 3. 如果是在后台发出长声音
                     * 4. 如果在前台发出短声音
                     */
                    if (isRuninBackground()) {
                        sendNotification(list.get(0));
                        //发出长声音
                        //参数2/3：左右喇叭声音的大小
                        mSoundPool.play(mYuluSound,1,1,0,0,1);
                    } else {
                        //发出短声音
                        mSoundPool.play(mDuanSound,1,1,0,0,1);
                    }
                    EventBus.getDefault().post(list.get(0));
                }
            }
        });
    }


    private void initConnectionListener() {
        EMClient.getInstance().addConnectionListener(new EMConnectionListener() {
            @Override
            public void onConnected() {
            }

            @Override
            public void onDisconnected(int i) {
                if (i== EMError.USER_LOGIN_ANOTHER_DEVICE){
                    // 显示帐号在其他设备登录
                    /**
                     *  将当前任务栈中所有的Activity给清空掉
                     *  重新打开登录界面
                     */
                    for (BaseActivity baseActivity : mBaseActivityList) {
                        baseActivity.finish();
                    }

                    Intent intent = new Intent(QQApplication.this, LoginActivity.class);
                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    startActivity(intent);
                    ThreadUtils.runOnMainThread(new Runnable() {
                        @Override
                        public void run() {
                            ToastUtils.showToast(QQApplication.this,"您已在其他设备上登录了，请重新登录。");
                        }
                    });

                }
            }
        });
    }


    private void sendNotification(EMMessage message) {
         EMTextMessageBody messageBody = (EMTextMessageBody) message.getBody();
        NotificationManager notificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
        //延时意图
        /**
         * 参数2：请求码 大于1
         */
        Intent mainIntent = new Intent(this,MainActivity.class);
        mainIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        Intent chatIntent = new Intent(this, ChatActivity.class);
        chatIntent.putExtra("username",message.getFrom());

        Intent[] intents = {mainIntent,chatIntent};
        PendingIntent pendingIntent = PendingIntent.getActivities(this,1,intents,PendingIntent.FLAG_UPDATE_CURRENT) ;
        Notification notification = new Notification.Builder(this)
                .setAutoCancel(true) //当点击后自动删除
                .setSmallIcon(R.mipmap.message) //必须设置
                .setLargeIcon(BitmapFactory.decodeResource(getResources(),R.mipmap.default_avatar))
                .setContentTitle("您有一条新消息")
                .setContentText(messageBody.getMessage())
                .setContentInfo(message.getFrom())
                .setContentIntent(pendingIntent)
                .setPriority(Notification.PRIORITY_MAX)
                .build();
        notificationManager.notify(1,notification);
    }

    private boolean isRuninBackground() {
        ActivityManager activityManager = (ActivityManager) getSystemService(ACTIVITY_SERVICE);
        List<ActivityManager.RunningTaskInfo> runningTasks = activityManager.getRunningTasks(100);
        ActivityManager.RunningTaskInfo runningTaskInfo = runningTasks.get(0);
        if (runningTaskInfo.topActivity.getPackageName().equals(getPackageName())) {
            return false;
        } else {
            return true;
        }
    }

    private String getAppName(int pID) {
        String processName = null;
        ActivityManager am = (ActivityManager) this.getSystemService(ACTIVITY_SERVICE);
        List l = am.getRunningAppProcesses();
        Iterator i = l.iterator();
        PackageManager pm = this.getPackageManager();
        while (i.hasNext()) {
            ActivityManager.RunningAppProcessInfo info = (ActivityManager.RunningAppProcessInfo) (i.next());
            try {
                if (info.pid == pID) {
                    processName = info.processName;
                    return processName;
                }
            } catch (Exception e) {
            }
        }
        return processName;
    }
}
