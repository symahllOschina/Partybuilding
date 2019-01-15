package com.huaneng.zhdj.utils;

import android.app.Activity;
import android.app.AppOpsManager;
import android.content.Context;
import android.content.pm.PackageInfo;
import android.graphics.Point;
import android.os.Build;
import android.support.annotation.ColorInt;
import android.support.annotation.RequiresApi;
import android.support.design.internal.NavigationMenuPresenter;
import android.support.design.internal.NavigationMenuView;
import android.support.design.widget.NavigationView;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.Display;
import android.view.View;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.huaneng.zhdj.BaseActivity;
import com.huaneng.zhdj.bean.AppVersion;

import java.lang.reflect.Field;
import java.util.Map;

/**
 * Created by Administrator on 2017/11/9.
 */

public class UIUtils {

    public static int getPhoneWidth(Context context) {
        WindowManager windowManager = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
        Display display = windowManager.getDefaultDisplay(); //Activity#getWindowManager()
        Point size = new Point();
        display.getSize(size);
        int width = size.x;
        int height = size.y;
        return width;
    }

    public static void showText(TextView textView, Object value) {
        if (value != null && !TextUtils.isEmpty(value.toString())) {
            textView.setText(value.toString());
        } else {
            textView.setText(null);
        }
    }

    public static void showText(TextView textView, String prefix, Object value) {
        if (value != null && !TextUtils.isEmpty(value.toString())) {
            textView.setText(prefix + value.toString());
        } else {
            textView.setText(prefix);
        }
    }

    public static void showNum(TextView textView, String prefix, Object value) {
        textView.setText(prefix + getNumValue(value));
    }

    public static void showNum(TextView textView, Object value) {
        if (value == null || TextUtils.isEmpty(value.toString()) || "null".equals(value.toString())) {
            value = "0";
        }
        textView.setText(getNumValue(value));
    }

    private static String getNumValue(Object value) {
        if (value == null || TextUtils.isEmpty(value.toString()) || "null".equals(value.toString())) {
            return "0";
        }
        return value.toString();
    }

    public static void setHeight(final View view) {
        view.post(new Runnable() {

            @Override
            public void run() {
                LinearLayout.LayoutParams layoutParams = (LinearLayout.LayoutParams)view.getLayoutParams();
                layoutParams.height = view.getWidth();
                view.setLayoutParams(layoutParams);
            }
        });
    }

    /**
     * 权限检查
     * @param context
     * @param permission
     * @return
     */
    @RequiresApi(api = Build.VERSION_CODES.M)
    public static boolean checkOpsPermission(Context context, String permission) {
        try {
            AppOpsManager appOpsManager = (AppOpsManager) context.getSystemService(Context.APP_OPS_SERVICE);
            String opsName = AppOpsManager.permissionToOp(permission);
            if (opsName == null) {
                return true;
            }
            int opsMode = appOpsManager.checkOpNoThrow(opsName, android.os.Process.myUid(), context.getPackageName());
            return opsMode == AppOpsManager.MODE_ALLOWED;
        } catch (Exception ex) {
            return true;
        }
    }

    /**
     * 是否需要更新
     * @param context
     * @param appVersion
     * @return
     */
    public static boolean isNeedUpdate(Context context, AppVersion appVersion) {
        String oldVersion = getVersionName(context);
        String newVersion = appVersion.value;
        return isNeedUpdate(oldVersion, newVersion);
    }

    /**
     * 对比新旧版本号，确定是否需要更新
     * @param oldVersion 旧版本号
     * @param newVersion 新版本号
     * @return 是否需要更新
     */
    public static boolean isNeedUpdate(String oldVersion, String newVersion) {
        try {
            String[] odlVersionArr = oldVersion.split("\\.");
            String[] newVersionArr = newVersion.split("\\.");
            int size = Math.max(odlVersionArr.length, newVersionArr.length);
            for (int i = 0; i < size; i++) {
                if (i >= odlVersionArr.length) {
                    return true;
                }
                if (i >= newVersionArr.length) {
                    return false;
                }
                if (Integer.valueOf(newVersionArr[i]) > Integer.valueOf(odlVersionArr[i])) {
                    return true;
                }
            }
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
        return false;
    }

    // 获取版本名称
    public static String getVersionName(Context context) {
        try {
            // getPackageName()是你当前类的包名，0代表是获取版本信息
            PackageInfo packInfo = context.getPackageManager().getPackageInfo(context.getPackageName(), 0);
            return packInfo.versionName;
        } catch (Exception e) {
            return "1.0";
        }
    }

    // 获取版本号
    public static int getVersionCode(Context context) {
        try {
            PackageInfo packInfo = context.getPackageManager().getPackageInfo(context.getPackageName(), 0);
            return packInfo.versionCode;
        } catch (Exception e) {
            return 1;
        }
    }

    /**
     * 必填验证
     * @param act
     * @param editTexts
     * @return
     */
    public static boolean validRequired(BaseActivity act, TextView... editTexts) {
        for (TextView editText: editTexts) {
            if (TextUtils.isEmpty(editText.getText().toString().trim())) {
                editText.requestFocus();
                String hint = editText.getHint().toString();
                if (TextUtils.isEmpty(hint)) {
                    hint = "请输入值";
                }
                if (!hint.startsWith("请输入") && !hint.startsWith("输入")) {
                    hint = "请输入" + hint;
                }
                act.toast(hint);
                return false;
            }
        }
        return true;
    }

    /**
     * 必填验证
     * @param act
     * @return
     */
    public static boolean validRequired(BaseActivity act, Map<TextView, String> maps) {
        for (Map.Entry<TextView, String> entry : maps.entrySet()) {
            TextView editText = entry.getKey();
            if (TextUtils.isEmpty(editText.getText().toString().trim())) {
                editText.requestFocus();
                String hint = entry.getValue();
                if (TextUtils.isEmpty(hint)) {
                    hint = editText.getHint().toString();
                }
                if (TextUtils.isEmpty(hint)) {
                    hint = "请输入值";
                }
                act.toast(hint);
                return false;
            }
        }
        return true;
    }

    public static String value(TextView editText) {
        return editText.getText().toString().trim();
    }

}
