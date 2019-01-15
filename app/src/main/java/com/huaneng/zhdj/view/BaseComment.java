package com.huaneng.zhdj.view;

import com.huaneng.zhdj.BaseActivity;
import com.huaneng.zhdj.bean.Pager;

/**
 * 评论
 */
public abstract class BaseComment {

    public BaseActivity ctx;

    protected CommentTarget target;

    protected Pager mPager;

    // 举报的url
    public String reportUrl;

    public BaseComment() {
    }

    public BaseComment(String reportUrl) {
        this.reportUrl = reportUrl;
    }

    public void init(BaseActivity ctx, CommentTarget target, Pager pager) {
        this.ctx = ctx;
        this.target = target;
        this.mPager = pager;
    }

    /**
     * 获取评论
     */
    public abstract void getComments(String articleId);


}
