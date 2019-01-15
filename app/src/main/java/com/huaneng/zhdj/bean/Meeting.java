package com.huaneng.zhdj.bean;

import android.text.TextUtils;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * 会议
 */
public class Meeting implements Serializable {
    public String id;
    public String title;
    public String mdate;
    public String maddress;
    public String name;
    public String organize;
    public String content;
    public String status;
    public String type;// 1=会议;2=活动
    public String condition;
    public String condition_name;//会议状态
    public String update_time;
    public String create_time;
    public String user_id;
    public String user_name;
    public List<MeetingPerson> data_uid;//为参会人员
    public String lecturer;
    public List<MeetingNote> data_log;

    // 参会人员
    public String getMeetingPerson() {
        if (data_uid != null && !data_uid.isEmpty()) {
            List<String> persons = new ArrayList<>();
            for (MeetingPerson person : data_uid) {
                persons.add(person.user_name);
            }
            String result = persons.toString();
            result = result.substring(1, result.length()-1);
            return result;
        }
        return "";
    }

    // 会议记录
    public List<MeetingNote> getMeetingNote() {
        if (data_log != null && !data_log.isEmpty()) {
            List<MeetingNote> meetingNotes = new ArrayList<>();
            for (MeetingNote note : data_log) {
                if (!TextUtils.isEmpty(note.content)) {
                    meetingNotes.add(note);
                }
            }
            return meetingNotes;
        }
        return null;
    }

    // 获取状态名称
    // condition :0=即将开始;1=准备中;2=正在进行;3=已结束(未归档);4=已结束(已归档);5=已激活;6=已搁置;
    public String getStatusName() {
        String name = null;
        if ("0".equals(condition)) {
            name = "即将开始";
        } else if ("1".equals(condition)) {
            name = "准备中";
        } else if ("2".equals(condition)) {
            name = "正在进行";
        } else if ("3".equals(condition)) {
            name = "已结束(未归档)";
        } else if ("4".equals(condition)) {
            name = "已结束(已归档)";
        } else if ("5".equals(condition)) {
            name = "已激活";
        } else if ("6".equals(condition)) {
            name = "已搁置";
        }
        return name;
    }

    public boolean isMeeting() {
        return "1".equals(type);
    }

    public boolean isActivity() {
        return "2".equals(type);
    }
}
