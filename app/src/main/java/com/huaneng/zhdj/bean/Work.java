package com.huaneng.zhdj.bean;

import java.io.Serializable;
import java.util.List;

/**
 * 工作
 */
public class Work implements Serializable {

    public String id;
    public String content;
    public String description;
    public String title;
    public String works_date;
    public String read_count;
    public String upvote_count;
    public String is_head_figure;
    public String status;
    public String update_time;
    public String create_time;
    public String user_id;
    public String user_name;
    public String type;
    public String is_upvote;

    public List<String> images_info;

    public String approve_user_id;
    public String approve_type;
    public String approve_content;
    public String approve_time;
    public String username;
    public String approve_user_name;

    public String approve_status;//1=审批通过;2=驳回

    public List<ApproveLog> approve_log;//审核记录

    public String getApproveStatusDisplay() {
        if ("1".equals(approve_status)) {
            return "审批通过";
        } if ("2".equals(approve_status)) {
            return "驳回";
        }
        return "待审核";
    }

    public String getApproveLogContent() {
        if (approve_log != null && !approve_log.isEmpty()) {
            StringBuilder builder = new StringBuilder();
            for (ApproveLog log : approve_log) {
                builder.append(log.content + "\n" + log.create_time + "\n");
            }
            return builder.toString();
        }
        return null;
    }

}
