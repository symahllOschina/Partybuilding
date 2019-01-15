package com.huaneng.zhdj.network;

import android.text.TextUtils;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.serializer.SerializerFeature;

import java.util.List;

/**
 * Created by TH on 2017/11/8.
 */

public class Response {

    public int status;//0-失败 1-成功
    public int code;
    public int num;
    public String message;
    public Object data;

    public boolean suceess() {
        return status == 1;
    }

    public <T> T getEntity(Class<T> t) {
        if (validData()) {
            return JSON.parseObject(getDateString(), t);
        }
        return null;
    }

    public String getDateString() {
        return JSON.toJSONString(data, SerializerFeature.WriteNullStringAsEmpty, SerializerFeature.WriteNullNumberAsZero, SerializerFeature.WriteNullBooleanAsFalse);
    }

    public <T> List<T> getEntityList(Class<T> t) {
        if (validData()) {
            return JSON.parseArray(JSONArray.toJSONString(data, SerializerFeature.WriteNullStringAsEmpty, SerializerFeature.WriteNullNumberAsZero, SerializerFeature.WriteNullBooleanAsFalse), t);
        }
        return null;
    }

    public <T> T getField(String key) {
        if (validData()) {
            JSONObject jsonObject = JSON.parseObject(getDateString());
            if (jsonObject.containsKey(key)) {
                return (T)jsonObject.get(key);
            }
        }
        return null;
    }

    private boolean validData() {
        if (data == null || TextUtils.isEmpty(data.toString())) {
            return false;
        }
        return true;
    }
}
