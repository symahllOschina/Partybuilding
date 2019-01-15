package com.huaneng.zhdj.view;

import com.huaneng.zhdj.bean.Comment;

import java.util.List;

/**
 * Created by mashenghai on 2018/1/26.
 */

public interface CommentTarget {

    void getCommentsSuccess(List<Comment> list, int total);
    void getCommentsFail();
}
