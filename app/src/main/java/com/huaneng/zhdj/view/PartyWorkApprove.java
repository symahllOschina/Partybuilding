package com.huaneng.zhdj.view;

import android.text.TextUtils;

import com.huaneng.zhdj.network.HTTP;
import com.huaneng.zhdj.network.Response;
import com.huaneng.zhdj.network.Subscriber;
import com.huaneng.zhdj.utils.MapParam;
import com.orhanobut.logger.Logger;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;

/**
 * 党务办公-点赞
 */
public class PartyWorkApprove extends BaseApprove {

    /**
     * 检查是否已点赞
     * 文章详情已经直接返回了isUpvote，不用单独查询
     */
    public void checkIsUpvote(String articleId) {
    }

    /**
     * 点赞
     */
    public void upvote(String articleId, Integer isUpvote) {
        if (TextUtils.isEmpty(articleId) || isUpvote == null) {
            return;
        }
        if (isUpvote == 1) {
            target.hasUpvoteAlert();
            return;
        }
        if (!ctx.checkNetwork()) {
            return;
        }
        HTTP.service.post("worksreport/upvote", MapParam.me().p("id", articleId).build())
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Subscriber(ctx){

                    @Override
                    public void onSuccess(Response response) {
                        target.approveSuccess();
                    }

                    @Override
                    public void onFail(String msg) {
                        ctx.toast(msg);
                    }
                });
    }

    /**
     * 删除点赞
     */
    public void delUpvote(String articleId) {
        if (!ctx.checkNetwork()) {
            return;
        }
        HTTP.service.post("worksreport/downVote", MapParam.me().p("id", articleId).build())
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Subscriber(ctx){

                    @Override
                    public void onSuccess(Response response) {
                        target.deleteSuccess();
                    }

                    @Override
                    public void onFail(String msg) {
                        ctx.toast(msg);
                    }
                });
    }
}
