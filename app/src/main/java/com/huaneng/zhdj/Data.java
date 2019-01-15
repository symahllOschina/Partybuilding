package com.huaneng.zhdj;

import android.text.TextUtils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Data {

//    public static final String SERVER = "http://113.200.203.98:8002/";
    public static final String SERVER = "http://113.200.203.89:81/partybuilding/public/index.php/";
    public static final Map<String, String> work_type = new HashMap<>();
    public static final Map<String, String> work_approve_type = new HashMap<>();
    public static final Map<String, String> meeting_status = new HashMap<>();

    // 党员学习-学习资料（list）
    public static final Map<String, String> souce_type = new HashMap<>();
    public static final Map<String, String> doc_type = new HashMap<>();
    public static final String[] SEX = {"男", "女"};

    public static final String[] meeting_status_arr = {"即将开始", "准备中", "正在进行", "已结束(未归档)", "已结束(已归档）","所有状态"};

    public static final String[] organs = {"市工商局", "市交通局", "市城管局", "市房管局", "市旅游局", "市规划局", "市教育局", "市公安局", "市环保局", "市国税局"};

    static {
        // info_type (1=工作记录描述;2=日常工作总结;3=年度工作总结;4=日常职工思想汇报;5=日常党员思汇;6=年度党员思汇)
        work_type.put("1", "工作记录描述");
        work_type.put("2", "日常工作总结");
        work_type.put("3", "年度工作总结");
        work_type.put("4", "日常职工思想汇报");
        work_type.put("5", "日常党员思汇");
        work_type.put("6", "年度党员思汇");

        work_approve_type.put("1", "优秀");
        work_approve_type.put("2", "良好");
        work_approve_type.put("3", "一般");

        meeting_status.put("0", "即将开始");
        meeting_status.put("1", "准备中");
        meeting_status.put("2", "正在进行");
        meeting_status.put("3", "已结束(未归档)");
        meeting_status.put("4", "已结束(已归档）");

        // 1.souce_type:1.富文本 2.video 3.其它文件
        souce_type.put("1", "富文本");
        souce_type.put("2", "video");
        souce_type.put("3", "其它文件");
        // 2.type 1.学习资料2.网络党课3.内部资料
        doc_type.put("1", "学习资料");
        doc_type.put("2", "网络党课");
        doc_type.put("3", "内部资料");
    }

    // image有可能是个[]数组，此时只返回第一个元素
    public static String decorateUrl(String image) {
        return decorateUrl(image, SERVER);
    }

    public static String decorateUrl(String image, String server) {
        if (TextUtils.isEmpty(server)) {
            server = SERVER;
        }
        if (!TextUtils.isEmpty(image) && !image.startsWith("http")) {
            image = replaceIllegalChar(image);
            image = Arrays.asList(image.split(",")).get(0);
            image = server + image;
        }
        return image;
    }

    /**
     * 替换非法字符
     */
    public static String replaceIllegalChar(String url) {
        if (!TextUtils.isEmpty(url)) {
            url = url.replaceAll(" ", "")
                    .replaceAll("\\\\", "")//反斜杠
                    .replaceAll("\\\"", "")//双引号
                    .replaceAll("\n", "")//双引号
                    .replaceAll("\\[", "")
                    .replaceAll("\\]", "");
        }
        return url;
    }
    // image有可能是个[]数组，返回一个列表
    public static ArrayList<String> decorateUrls(String image) {
        ArrayList<String> urlList = new ArrayList<>();
        if (!TextUtils.isEmpty(image)) {
            if (!image.startsWith("http")) {
                image = replaceIllegalChar(image);
                List<String> urls = Arrays.asList(image.split(","));
                for (String url : urls) {
                    if (!url.startsWith("http")) {
                        urlList.add(SERVER + url);
                    } else {
                        urlList.add(url);
                    }
                }
            } else {
                urlList.add(image);
            }
        }
        return urlList;
    }

    public static ArrayList<String> decorateUrl(ArrayList<String> urls) {
        if (urls == null || urls.isEmpty()) {
            return urls;
        }
        ArrayList<String> urlList = new ArrayList<>();
        for (String url : urls) {
            urlList.addAll(decorateUrls(url));
        }
        return urlList;
    }
}
