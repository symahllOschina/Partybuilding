package com.huaneng.zhdj.view;

import com.huaneng.zhdj.BaseActivity;

/**
 * 点赞接口
 */
public abstract class BaseApprove {

    public BaseActivity ctx;

    protected ApproveTarget target;

    public void init(BaseActivity ctx, ApproveTarget target) {
        this.ctx = ctx;
        this.target = target;
    }

    /**
     * 检查是否已点赞
     */
    public abstract void checkIsUpvote(String articleId);
    /**
     * 点赞
     */
    public abstract void upvote(String articleId, Integer isUpvote);
    /**
     * 删除点赞
     */
    public abstract void delUpvote(String articleId);
}
