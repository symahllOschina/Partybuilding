package com.huaneng.zhdj.bean;

import java.io.Serializable;
import java.util.List;

/**
 * 投票
 */
public class Vote implements Serializable {
    
    public String id;
    public String type;//type:0为单选;1为多选
    public String title;
    public String status;// 1 / 0 : 显示 / 删除
    public String total;
    public String end_time;
    public String create_time;

    public List<VoteOption> data;

    public boolean isSingleMode() {
        return "0".equals(type);
    }

    public String getStatusDisplay() {
        if ("1".equals(status)) {
            return "进行中";
        } else {
            return "已结束";
        }
    }

    public boolean isFinished() {
        return "0".equals(status);
    }

    public String getTypeDisplay() {
        if ("0".equals(type)) {
            return "单选";
        } else {
            return "多选";
        }
    }

}
