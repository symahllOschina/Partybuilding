package com.huaneng.zhdj.supervise;

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
import com.huaneng.zhdj.utils.UIUtils;
import com.huaneng.zhdj.utils.WebViewUtils;
import com.huaneng.zhdj.view.NewsInfoView;
import com.huaneng.zhdj.view.PioneersArticleClickApproveView;

import org.xutils.view.annotation.ContentView;
import org.xutils.view.annotation.ViewInject;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;

/**
 * 纪律监督
 */
@ContentView(R.layout.activity_discipline_supervise_detail)
public class DisciplineSuperviseDetailActivity extends BaseActivity {

    @ViewInject(R.id.webView)
    private WebView webView;

    @ViewInject(R.id.time)
    private TextView time;
    @ViewInject(R.id.from)
    private TextView from;
    @ViewInject(R.id.clickCount)
    private TextView clickCount;

    private News news;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        news = (News)getIntent().getSerializableExtra("news");
        setTitle(news.title);
        getDetail();
    }

    public void getDetail() {
        if (!checkNetwork()) {
            return;
        }
        HTTP.service.get("discip/read", MapParam.me().p("id", news.id).build())
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Subscriber(ctx){

                    @Override
                    public void onSuccess(Response response) {
                        News detail = response.getEntity(News.class);
                        if (detail != null) {
                            UIUtils.showText(time, "发布时间：", news.create_time);
                            UIUtils.showText(from, "新闻来源：", news.source);
                            UIUtils.showText(clickCount, detail.read_count);
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
