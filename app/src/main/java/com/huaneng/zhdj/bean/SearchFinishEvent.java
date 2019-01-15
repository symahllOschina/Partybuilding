package com.huaneng.zhdj.bean;

/**
 * 查询结束事件
 */

public class SearchFinishEvent {

    public String flag;

    public boolean hasMore;

    public SearchFinishEvent() {
    }

    public SearchFinishEvent(String flag, boolean hasMore) {
        this.flag = flag;
        this.hasMore = hasMore;
    }
}
