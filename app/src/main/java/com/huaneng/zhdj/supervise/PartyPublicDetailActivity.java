package com.huaneng.zhdj.supervise;

import android.os.Bundle;
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

import org.xutils.view.annotation.ContentView;
import org.xutils.view.annotation.ViewInject;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;

/**
 * 党务公开详情
 */
@ContentView(R.layout.activity_party_public_detail)
public class PartyPublicDetailActivity extends BaseActivity {

    @ViewInject(R.id.dateTv)
    private TextView dateTv;
//    @ViewInject(R.id.date2Tv)
//    private TextView date2Tv;
//    @ViewInject(R.id.organTv)
//    private TextView organTv;
    @ViewInject(R.id.webView)
    private WebView webView;

    private News news;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        news = (News)getIntent().getSerializableExtra("news");
        setTitle(news.title);
        UIUtils.showText(dateTv, "发布日期：", news.date);
//        UIUtils.showText(date2Tv, news.date);
//        UIUtils.showText(organTv, news.department);
        WebViewUtils.me(ctx, webView).html(news.content);
//        getDetail();
    }

    public void getDetail() {
        if (!checkNetwork()) {
            return;
        }
        HTTP.service.get("partyinfo/read", MapParam.me().p("id", news.id).build())
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Subscriber(ctx){

                    @Override
                    public void onSuccess(Response response) {
                        News detail = response.getEntity(News.class);
                        if (detail != null) {
                            UIUtils.showText(dateTv, "发布日期：", detail.date);
//                            UIUtils.showText(date2Tv, news.date);
//                            UIUtils.showText(organTv, detail.department);
                            WebViewUtils.me(ctx, webView).html(detail.content);
                        } else {
                            toast(response.message);
                        }
                    }

                    @Override
                    public void onWrong(String msg) {
                        toast(msg);
                    }
                });
    }
}
