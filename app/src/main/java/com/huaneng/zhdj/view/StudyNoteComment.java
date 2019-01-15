package com.huaneng.zhdj.view;

import com.huaneng.zhdj.bean.CommentPager;
import com.huaneng.zhdj.network.HTTP;
import com.huaneng.zhdj.network.Response;
import com.huaneng.zhdj.network.Subscriber;
import com.huaneng.zhdj.utils.MapParam;
import com.orhanobut.logger.Logger;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;

/**
 * 学习笔记-评论列表
 */
public class StudyNoteComment extends BaseComment {

    public StudyNoteComment(String reportUrl) {
        super(reportUrl);
    }

    @Override
    public void getComments(String articleId) {
        if (!ctx.checkNetwork()) {
            return;
        }
        HTTP.service.get("schoolnote/commentread", MapParam.me().p("status", "1").p("note_id", articleId).p("page", mPager.nextPage()).p("size", mPager.size).build())
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Subscriber(ctx){

                    @Override
                    public void onSuccess(Response response) {
                        CommentPager pager = response.getEntity(CommentPager.class);
                        if (pager != null) {
                            target.getCommentsSuccess(pager.list, pager.total);
//                            mPager.update(pager);
                        } else {
                            Logger.e(response.message);
                            target.getCommentsFail();
                        }
                    }

                    @Override
                    public void onWrong(String msg) {
                        Logger.e(msg);
                        target.getCommentsFail();
                    }
                });
    }
}
