package com.huaneng.zhdj.utils;

import android.text.TextUtils;

/**
 * Created by TH on 2018/1/12.
 */

public class Utils {

    /**
     * 如果obj为null或者空，返回0
     * @param obj
     * @return
     */
    public static String zeroIfNull(Object obj) {
        if (obj == null || TextUtils.isEmpty(obj.toString())) {
            return "0";
        }
        return obj.toString();
    }

    /**
     * 如果obj为null或者空，返回""
     * @param obj
     * @return
     */
    public static String emptyIfNull(Object obj) {
        if (obj == null || TextUtils.isEmpty(obj.toString())) {
            return "";
        }
        return obj.toString();
    }
}
