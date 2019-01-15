package com.huaneng.zhdj.bean;

import java.io.Serializable;

/**
 * 会议记录
 */
public class MeetingNote implements Serializable {
    public String id;
    public String content;
    public String meeting_event_id;
    public String create_time;
    public String user_id;
}
