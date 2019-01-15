package com.huaneng.zhdj.news;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;

import com.huaneng.zhdj.BaseRefreshActivity;
import com.huaneng.zhdj.R;
import com.huaneng.zhdj.adapter.NewsAdapter;
import com.huaneng.zhdj.bean.News;
import com.huaneng.zhdj.bean.NewsPager;
import com.huaneng.zhdj.network.HTTP;
import com.huaneng.zhdj.network.Response;
import com.huaneng.zhdj.network.Subscriber;
import com.huaneng.zhdj.utils.MapParam;

import org.xutils.view.annotation.ContentView;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;

/**
 * 新闻列表
 */
@ContentView(R.layout.activity_news)
public class NewsActivity extends BaseRefreshActivity<News> {//

    NewsAdapter adapter;
    private News news;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        news = (News)getIntent().getSerializableExtra("news");
        setTitle(news.catname);

        adapter = new NewsAdapter(ctx, mList);
        mListView.setAdapter(adapter);
        mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                Intent intent = new Intent(ctx, NewsDetailActivity.class);
                intent.putExtra("news", mList.get(i));
                intent.putExtra("url", News.DETAIL_URL_NEWS);
                startActivity(intent);
            }
        });
        pagerHandler.adapter = adapter;
        getList();
    }

    @Override
    public void getList() {
        if (!checkNetwork()) {
            return;
        }
        HTTP.service.get("news/listall", MapParam.me().p("catid", news.catid).p("page", 1).p("size", 10).build())
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Subscriber(ctx){

                    @Override
                    public void onSuccess(Response response) {
                        NewsPager pager = response.getEntity(NewsPager.class);
                        pagerHandler.requestSuccess(pager);
                    }

                    @Override
                    public void onWrong(String msg) {
                        pagerHandler.requestFail();
                    }
                });
    }
}
