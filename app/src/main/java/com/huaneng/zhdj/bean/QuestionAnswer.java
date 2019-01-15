package com.huaneng.zhdj.bean;

import android.text.TextUtils;

import java.io.Serializable;

/**
 * 民主问答
 */
public class QuestionAnswer implements Serializable {
    public String id;
    public String as_id;
    public String title;
    public String questions_name;//
    public String questions;
    public String department;
    public String questions_date;
    public String answers_name;//
    public String answers;
    public String answers_date;
    public String answers_department;
    public String user_id;
    public String status;//status:-1=删除;0=显示中;1=已读未回复;2=已回复;
    public String read_count;
    public String create_time;
    public String update_time;

    public String getRealAnswerDate() {
        if (!TextUtils.isEmpty(answers)) {
            return answers_date;
        }
        return "";
    }

    public boolean isAnswered() {
        return !"0".equals(status);
    }
}
