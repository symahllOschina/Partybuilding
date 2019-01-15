package com.huaneng.zhdj.bean;

import android.text.TextUtils;

import com.huaneng.zhdj.utils.UIUtils;
import com.huaneng.zhdj.utils.Utils;

import java.util.List;

/**
 * Created by mashenghai on 2018/3/11.
 */
public class Organ {
    public String id;
    public String name;
    public String pid;
    public int level;
    public String create_time;
    public String update_time;
    public String description;
    public String person1;
    public String person2;
    public String count;
    public List<Organ> _child;

    public String getDisplay() {
        return //"部门名称：" + name + "\r\n" +
                "负责人：" + getPerson() + "\r\n" +
                "部门描述：" + Utils.emptyIfNull(description);
    }

    public String getPerson() {
        String person = "";
        if (!TextUtils.isEmpty(person1)) {
            person += person1;
        }
        if (!TextUtils.isEmpty(person2)) {
            if (!TextUtils.isEmpty(person)) {
                person += "、" + person2;
            } else {
                person  = person2;
            }
        }
        return person;
    }
}
