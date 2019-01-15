package com.huaneng.zhdj.bean;

import java.io.Serializable;

/**
 * 消息
 */
public class Message implements Serializable {

    public String id;
    public String user_id;
    public String message;
    public String jsondata;
    public String status;//1=未读;2=已读
    public String update_time;
    public String create_time;

    public boolean isRead() {
        return "2".equals(status);
    }
}
