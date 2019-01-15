package com.huaneng.zhdj.bean;

import java.util.ArrayList;
import java.util.List;

/**
 * 专题详情
 */

public class SpecialSubjectDetail extends News {

    public List<Attachment> video;
    public List<Attachment> article;

    public List<Attachment> articleList;
    public List<Attachment> fileList;

    public void classify() {
        if (article != null && !article.isEmpty()) {
            articleList = new ArrayList<>();
            fileList = new ArrayList<>();
            for (Attachment item: article) {
                if (item.isRichText()) {
                    articleList.add(item);
                } else {
                    fileList.add(item);
                }
            }
        }
    }
}
