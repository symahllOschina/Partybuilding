package com.huaneng.zhdj.utils;

import android.app.NotificationManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;

import com.huaneng.zhdj.MainActivity;
import com.huaneng.zhdj.SlidingMenuActivity;
import com.huaneng.zhdj.bean.JPushMsg;
import com.huaneng.zhdj.user.MessageActivity;

import org.greenrobot.eventbus.EventBus;

import cn.jpush.android.api.JPushInterface;

/**
 * Created by TH on 2017/11/28.
 */

public class JPushReceiver extends BroadcastReceiver {

    private static final String TAG = "【JPushReceiver】";

    private NotificationManager nm;

    private Context context;

    @Override
    public void onReceive(Context context, Intent intent) {
        this.context = context;
        if (null == nm) {
            nm = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        }
        Bundle bundle = intent.getExtras();
        Log.d(TAG, "onReceive - " + intent.getAction() + ", extras: " + bundle.getString(JPushInterface.EXTRA_EXTRA));
        if (JPushInterface.ACTION_REGISTRATION_ID.equals(intent.getAction())) {
            String regId = bundle.getString(JPushInterface.EXTRA_REGISTRATION_ID);
            Log.d(TAG, "[JPush用户注册成功] 接收Registration Id : " + regId);
        } else if (JPushInterface.ACTION_MESSAGE_RECEIVED.equals(intent.getAction())) {
//            parseMsg(bundle); 同时收到JPushInterface.ACTION_MESSAGE_RECEIVED、JPushInterface.ACTION_NOTIFICATION_RECEIVED的通知，导致通知2次
        } else if (JPushInterface.ACTION_NOTIFICATION_RECEIVED.equals(intent.getAction())) {
            Log.d(TAG, "收到了通知");
            parseMsg(bundle);
            receivingNotification(context, bundle);
        } else if (JPushInterface.ACTION_NOTIFICATION_OPENED.equals(intent.getAction())) {
            Log.d(TAG, "用户点击打开了通知");
            show(bundle);
        } else {
            Log.d(TAG, "Unhandled intent - " + intent.getAction());
        }
    }

    private void show(Bundle bundle) {
        String msg = bundle.getString(JPushInterface.EXTRA_EXTRA);
        showMainActivity();
    }

    private void showMainActivity() {
        Intent intent = new Intent(context, MessageActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        context.startActivity(intent);
    }

    // 自定义消息不会展示在通知栏，完全要开发者写代码去处理
    private void parseMsg(Bundle bundle) {
//        String message = bundle.getString(JPushInterface.EXTRA_MESSAGE);
        String alert = bundle.getString(JPushInterface.EXTRA_ALERT);
        String msg = bundle.getString(JPushInterface.EXTRA_EXTRA);
        Log.d(TAG, "收到了自定义消息。消息内容是：" + msg);
        if (TextUtils.isEmpty(msg)) {
            return;
        }
//        JPushMsg jPushMsg = JSONUtils.parseObject(msg, JPushMsg.class);
//        jPushMsg.alert = alert;
    }

    private void receivingNotification(Context context, Bundle bundle) {
        String title = bundle.getString(JPushInterface.EXTRA_NOTIFICATION_TITLE);
        Log.d(TAG, " title : " + title);
        String message = bundle.getString(JPushInterface.EXTRA_ALERT);
        Log.d(TAG, "message : " + message);
        String extras = bundle.getString(JPushInterface.EXTRA_EXTRA);
        Log.d(TAG, "extras : " + extras);
    }
}
