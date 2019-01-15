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
 * 党员学习-专题-点赞
 */
public class SpecialSubjectApprove extends BaseApprove {

    /**
     * 检查是否已点赞
     */
    public void checkIsUpvote(String articleId) {
        HTTP.service.get("schoolspecial/upvoteread", MapParam.me().p("id", articleId).build())
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Subscriber(ctx){

                    @Override
                    public void onSuccess(Response response) {
                        Integer isUpvote = response.getField("isUpvote");
                        target.checkSuccess(isUpvote);
                    }

                    @Override
                    public void onWrong(String msg) {
                        Logger.e(msg);
                    }
                });
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
        HTTP.service.post("schoolspecial/upvotesave", MapParam.me().p("id", articleId).build())
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
        HTTP.service.post("schoolspecial/upvotedelete", MapParam.me().p("id", articleId).build())
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
