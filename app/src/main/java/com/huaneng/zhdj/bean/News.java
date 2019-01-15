package com.huaneng.zhdj.bean;

import android.text.TextUtils;

import com.huaneng.zhdj.utils.CompareType;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * 新闻
 */
public class News implements CompareType, Serializable {

    // 党建监督-banner
    public static final String DETAIL_URL_SUPERVISE_BANNER = "banner/read";
    // 新闻详情
    public static final String DETAIL_URL_NEWS = "news/read";
    // 党规文件
//    public static final String DETAIL_URL_PARTY_REGULATION = "news/info";废弃
    public static final String DETAIL_URL_PARTY_REGULATION = "docs/read";
    // 时代先锋
    public static final String DETAIL_URL_PIONEERS = "vanguard/read";
    // 时代先锋 -- 作品详情
    public static final String DETAIL_URL_PIONEERS_WORKS = "works/read";

    public String id;
    public int catid;
    public String catname;
    public String author;
    public String commname;
    public String username;
    public String source;
    public String image;
    public String title;
    public String url;
    public String read_count;
    public String status;
    public String is_head_figure;
    public String update_time;
    public String create_time;

    public String content;
    public String content_type;
    public String upvote_count;
    public String description;
    public String is_allowcomments;

    public String company;
    public String active_status;

    // 党务办公-工作
    public String user_id;
    public String user_name;

    public String department;
    public String date;

    public ArrayList<String> getImageUrls() {
        if (!TextUtils.isEmpty(image)) {
            List<String> urls = new ArrayList<>();
            if (!image.startsWith("[")) {
                urls.add(image);
            } else {
                image = image.replaceAll(" ", "").replaceAll("\\[", "").replaceAll("\\]", "");
                urls = Arrays.asList(image.split(","));
            }
            return new ArrayList<>(urls);
        }
        return null;
    }

    public int getCompareValue() {
        return catid;
    }
}
