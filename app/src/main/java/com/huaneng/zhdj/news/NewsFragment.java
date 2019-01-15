package com.huaneng.zhdj.news;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;

import com.huaneng.zhdj.BaseRefreshFragment;
import com.huaneng.zhdj.R;
import com.huaneng.zhdj.adapter.NewsCenterAdapter;
import com.huaneng.zhdj.bean.News;
import com.huaneng.zhdj.network.HTTP;
import com.huaneng.zhdj.network.Response;
import com.huaneng.zhdj.network.Subscriber;
import com.huaneng.zhdj.utils.GlideImageLoader;
import com.youth.banner.Banner;
import com.youth.banner.BannerConfig;
import com.youth.banner.listener.OnBannerListener;

import org.xutils.view.annotation.ContentView;
import org.xutils.view.annotation.ViewInject;

import java.util.ArrayList;
import java.util.List;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;

/**
 * 新闻中心
 */
@ContentView(R.layout.fragment_news)
public class NewsFragment extends BaseRefreshFragment<News> {//

    @ViewInject(R.id.banner)
    Banner banner;
    NewsCenterAdapter adapter;
    List<News> bannerNewsList;

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        adapter = new NewsCenterAdapter(activity, mList);
        mListView.setAdapter(adapter);
        mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                Intent intent = new Intent(activity, NewsDetailActivity.class);
                intent.putExtra("news", mList.get(i));
                intent.putExtra("url", News.DETAIL_URL_NEWS);
                startActivity(intent);
            }
        });
        refreshLayout.setEnableLoadMore(false);
        pagerHandler.adapter = adapter;

        getList();
        getBanners();
    }

    private void initBanner(List titles, List images) {
        banner.setVisibility(View.VISIBLE);
        //设置banner样式
        banner.setBannerStyle(BannerConfig.CIRCLE_INDICATOR_TITLE_INSIDE);
        //设置图片加载器
        banner.setImageLoader(new GlideImageLoader());
        //设置图片集合
        banner.setImages(images);
        //设置banner动画效果
//        banner.setBannerAnimation(Transformer.Default);
        //设置标题集合（当banner样式有显示title时）
        banner.setBannerTitles(titles);
        //设置自动轮播，默认为true
        banner.isAutoPlay(true);
        //设置轮播时间
        banner.setDelayTime(5000);
        //设置指示器位置（当banner模式中有指示器时）
//        banner.setIndicatorGravity(BannerConfig.CENTER);
        //banner设置方法全部调用完毕时最后调用
        banner.start();
        banner.setOnBannerListener(new OnBannerListener() {

            @Override
            public void OnBannerClick(int position) {
                Intent intent = new Intent(activity, NewsDetailActivity.class);
                intent.putExtra("news", bannerNewsList.get(position));
                intent.putExtra("url", News.DETAIL_URL_NEWS);
                startActivity(intent);
            }
        });
    }

    @Override
    public void onStart() {
        super.onStart();
        if (banner != null) {
            //开始轮播
            banner.startAutoPlay();
        }
    }

    @Override
    public void onStop() {
        super.onStop();
        if (banner != null) {
            //结束轮播
            banner.stopAutoPlay();
        }
    }

    @Override
    public void getList() {
        if (!activity.checkNetwork()) {
            return;
        }
        HTTP.service.get("news/listone")
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Subscriber(activity){

                    @Override
                    public void onSuccess(Response response) {
                        List<News> list = response.getEntityList(News.class);
                        pagerHandler.requestSuccess(list, list.size());
                    }

                    @Override
                    public void onWrong(String msg) {
                        pagerHandler.requestFail();
                    }
                });
    }

    public void getBanners() {
        if (!activity.checkNetwork()) {
            return;
        }
        HTTP.service.get("news/headimage")
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Subscriber(activity){

                    @Override
                    public void onSuccess(Response response) {
                        bannerNewsList = response.getEntityList(News.class);
                        if (bannerNewsList != null && !bannerNewsList.isEmpty()) {
                            List titles = new ArrayList();
                            List images = new ArrayList();
                            for (News banner: bannerNewsList) {
                                titles.add(banner.title);
                                images.add(banner.image);
                            }
                            initBanner(titles, images);
                        }
                    }

                    @Override
                    public void onWrong(String msg) {
                        activity.toast(msg);
                    }
                });
    }
}
