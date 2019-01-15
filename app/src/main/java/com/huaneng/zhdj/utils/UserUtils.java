package com.huaneng.zhdj.utils;

import android.text.TextUtils;

import com.alibaba.fastjson.JSON;
import com.huaneng.zhdj.App;
import com.huaneng.zhdj.bean.User;

public class UserUtils {

    private static final String SPR_USER_JSON = "user_json";

    public static void save(User user) {
        if (user != null) {
            SharedPreferencesUtils.create(App.myself).put(SPR_USER_JSON, JSON.toJSONString(user));
        }
    }

    public static User getUser() {
        String json = SharedPreferencesUtils.create(App.myself).get(SPR_USER_JSON);
        if (!TextUtils.isEmpty(json)) {
            User user = JSON.parseObject(json, User.class);
            return user;
        }
        return null;
    }

    public static void clearUser() {
        SharedPreferencesUtils.create(App.myself).put(SPR_USER_JSON, null);
    }

}
