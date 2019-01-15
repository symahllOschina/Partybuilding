package com.huaneng.zhdj.bean;

import android.text.TextUtils;

import java.io.Serializable;
import java.util.List;

/**
 * 民意调查
 */
public class SurveyItem implements Serializable {

    public String id;
    public String content_id;
    public String type;
    public String title;
    public List<SurveyItemOption> data;

    public boolean valid() {
        if (TextUtils.isEmpty(title)) {
            return false;
        }
        return true;
    }

    public boolean isRadio() {
        return "radio".equals(type);
    }

    public boolean isCheckbox() {
        return "checkbox".equals(type);
    }

    public boolean isTextarea() {
        return "textarea".equals(type);
    }

    public String getStatInfo() {
        StringBuilder stat = new StringBuilder();
        if (data != null && !data.isEmpty()) {
            for (int i=0; i< data.size(); i++) {
                SurveyItemOption option = data.get(i);
                String count = option.count;
                if (TextUtils.isEmpty(count)) {
                    count = "0";
                }
                stat.append((i+1) + "、" + option.content + " (" + count + "人选择)");
                if (i < data.size() - 1) {
                    stat.append("\n");
                }
            }
        }
        return stat.toString();
    }
}
