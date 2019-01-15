package com.huaneng.zhdj.bean;

import android.text.TextUtils;

import com.huaneng.zhdj.Data;
import com.huaneng.zhdj.utils.DateUtils;

import java.io.Serializable;
import java.util.Date;

/**
 * Created by mashenghai on 2018/3/9.
 */

public class UserInfo implements Serializable {

    public String id;
    public String username;
    public String password;
    public String phone;
    public String email;
    public String token;
    public String time_out;
    public String image;
    public String sex;
    public String signature;
    public String status;
    public String create_time;
    public String update_time;
    public String cardnum;

    public String questionid;
    public String answer;

    public String integral;
    public String company;
    public String postion;// 职位
    public String sort;// 排行
    public String nation;// 民族
    public String school;// 学校
    public String major;// 专业
    public String party_time;// 入党时间

    public String isnew;
    public String isadmin;

    public String getJoinPartyTime() {
//        if (!TextUtils.isEmpty(party_time)) {
//            Date date = new Date(Long.valueOf(party_time));
//            return DateUtils.formatDate(date);
//        }
        return party_time;
    }

    public String getValidImageUrl() {
        if (!TextUtils.isEmpty(image) && !image.startsWith("http")) {
            image = Data.SERVER + image;
        }
        return image;
    }

    public boolean isAdministor() {
        return "1".equals(isadmin);
    }

    public int getSexCode() {
        if ("男".equals(sex)) {
            return 0;
        } else if ("女".equals(sex)) {
            return 1;
        }
        return -1;
    }
}
