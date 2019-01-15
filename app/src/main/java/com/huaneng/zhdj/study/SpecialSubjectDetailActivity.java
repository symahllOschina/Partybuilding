package com.huaneng.zhdj.study;

import android.os.Bundle;
import android.view.View;
import android.webkit.WebView;

import com.huaneng.zhdj.BaseActivity;
import com.huaneng.zhdj.R;
import com.huaneng.zhdj.bean.News;
import com.huaneng.zhdj.bean.SpecialSubjectDetail;
import com.huaneng.zhdj.network.HTTP;
import com.huaneng.zhdj.network.Response;
import com.huaneng.zhdj.network.Subscriber;
import com.huaneng.zhdj.utils.MapParam;
import com.huaneng.zhdj.utils.WebViewUtils;
import com.huaneng.zhdj.view.NewsInfoView;
import com.huaneng.zhdj.view.PioneersArticleClickApproveView;
import com.huaneng.zhdj.view.SpecialSubjectClickApproveView;

import org.xutils.view.annotation.ContentView;
import org.xutils.view.annotation.ViewInject;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;

/**
 * 党员学习-专题文章详情
 */
@ContentView(R.layout.activity_special_subject_detail)
public class SpecialSubjectDetailActivity extends BaseActivity {

    @ViewInject(R.id.webView)
    private WebView webView;

    @ViewInject(R.id.newsInfoView)
    private NewsInfoView newsInfoView;
    @ViewInject(R.id.clickApproveCountView)
    private SpecialSubjectClickApproveView clickApproveCountView;

    private News news;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        news = (News)getIntent().getSerializableExtra("news");
        setTitle(news.title);
        getList();
    }

    public void getList() {
        if (!checkNetwork()) {
            return;
        }
        HTTP.service.get("schoolspecial/readdownload", MapParam.me().p("id", news.id).build())
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Subscriber(ctx){

                    @Override
                    public void onSuccess(Response response) {
                        News detail = response.getEntity(News.class);
                        if (detail != null) {
                            clickApproveCountView.init(ctx, detail.id);
                            newsInfoView.setAuthor(news.author);
                            newsInfoView.setFrom(news.source);
                            newsInfoView.setTime(news.create_time);

                            clickApproveCountView.setClickCount(detail.read_count);
                            clickApproveCountView.setApproveCount(detail.upvote_count);
                            clickApproveCountView.setVisibility(View.VISIBLE);

                            WebViewUtils.me(ctx, webView).html(detail.content);
                        } else {
                            toast("没有查询到信息.");
                        }
                    }

                    @Override
                    public void onError(Throwable e) {
                        toast("没有查询到信息.");
                    }

                    @Override
                    public void onFail(String msg) {
                        toast("没有查询到信息.");
                    }
                });
    }
}
