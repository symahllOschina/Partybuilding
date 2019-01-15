package com.huaneng.zhdj.bean;

import java.io.Serializable;

/**
 * 学习笔记
 */
public class StudyNote implements Serializable{

    public String id;
    public String user_id;
    public String user_name;//?作者
    public String title;
    public String image;
    public String description;
    public String company;
    public String note_type;
    public String status;
    public String read_count;
    public String upvote_count;
    public String update_time;
    public String create_time;

}
