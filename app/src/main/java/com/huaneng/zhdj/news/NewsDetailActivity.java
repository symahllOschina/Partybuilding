package com.huaneng.zhdj.news;

import android.os.Bundle;
import android.view.View;
import android.webkit.WebView;
import android.widget.TextView;

import com.huaneng.zhdj.BaseActivity;
import com.huaneng.zhdj.R;
import com.huaneng.zhdj.bean.News;
import com.huaneng.zhdj.network.HTTP;
import com.huaneng.zhdj.network.Response;
import com.huaneng.zhdj.network.Subscriber;
import com.huaneng.zhdj.utils.MapParam;
import com.huaneng.zhdj.utils.WebViewUtils;
import com.huaneng.zhdj.view.NewsInfoView;

import org.xutils.view.annotation.ContentView;
import org.xutils.view.annotation.ViewInject;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;

/**
 * 新闻详情
 */
@ContentView(R.layout.activity_news_detail)
public class NewsDetailActivity extends BaseActivity {

    @ViewInject(R.id.titleTv)
    private TextView titleTv;
    @ViewInject(R.id.newsInfoView)
    private NewsInfoView newsInfoView;
    @ViewInject(R.id.webView)
    private WebView webView;

    private News news;
    private String url;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        url = getIntent().getStringExtra("url");
        news = (News)getIntent().getSerializableExtra("news");
        if (news.title.length() > 16) {
            setTitle("文章详情");
            titleTv.setVisibility(View.VISIBLE);
            titleTv.setText(news.title);
        } else {
            setTitle(news.title);
        }
        getList();
    }

    public void getList() {
        if (!checkNetwork()) {
            return;
        }
        HTTP.service.get(url, MapParam.me().p("id", news.id).build())
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Subscriber(ctx){

                    @Override
                    public void onSuccess(Response response) {
                        News detail = response.getEntity(News.class);
                        if (detail != null) {
                            newsInfoView.setClickCount(news.read_count);
                            newsInfoView.setFrom(news.source);
                            newsInfoView.setTime(news.create_time);

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
