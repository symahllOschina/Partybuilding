package com.huaneng.zhdj.view;

/**
 * 点赞结果
 */
public interface ApproveTarget {

    // 检查是否已点赞成功
    void checkSuccess(Integer isUpvote);

    // 点赞成功
    void approveSuccess();

    // 删除点赞成功
    void deleteSuccess();

    void hasUpvoteAlert();
}
