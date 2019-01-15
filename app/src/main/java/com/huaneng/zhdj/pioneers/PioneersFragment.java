package com.huaneng.zhdj.pioneers;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;

import com.huaneng.zhdj.BaseRefreshFragment;
import com.huaneng.zhdj.Data;
import com.huaneng.zhdj.R;
import com.huaneng.zhdj.adapter.PioneersAdapter;
import com.huaneng.zhdj.bean.News;
import com.huaneng.zhdj.bean.NewsPager;
import com.huaneng.zhdj.network.HTTP;
import com.huaneng.zhdj.network.Response;
import com.huaneng.zhdj.network.Subscriber;
import com.huaneng.zhdj.news.NewsDetailActivity;
import com.huaneng.zhdj.utils.GlideImageLoader;
import com.huaneng.zhdj.utils.MapParam;
import com.youth.banner.Banner;
import com.youth.banner.BannerConfig;
import com.youth.banner.listener.OnBannerListener;

import org.xutils.view.annotation.ContentView;
import org.xutils.view.annotation.Event;
import org.xutils.view.annotation.ViewInject;

import java.util.ArrayList;
import java.util.List;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;

/**
 * 时代先锋
 */
@ContentView(R.layout.fragment_pioneers)
public class PioneersFragment extends BaseRefreshFragment<News> {//

    @ViewInject(R.id.banner)
    Banner banner;
    PioneersAdapter adapter;
    List<News> bannerNewsList;

    private String mCatId = "11";
    private String mUrl = "vanguard/get";

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        adapter = new PioneersAdapter(activity, mList);
        mGridView.setAdapter(adapter);
        mGridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                Intent intent = null;
                News news = mList.get(i);
                if (news.catid == 0) {
                    intent = new Intent(activity, PioneersWorksDetailActivity.class);
                    intent.putExtra("url", News.DETAIL_URL_PIONEERS_WORKS);
                } else {
                    String authorLabel = null;
                    boolean authorVisible = false;
                    boolean fromVisible = false;
                    // 11 => '先锋事迹', 12 => '党建硕果', 13 => '德贤标兵'
                    if (news.catid == 11) {
                        authorVisible = true;
                    } else if (news.catid == 12) {
                        fromVisible = true;
                    } else {
                        authorVisible = true;
                        authorLabel = "发布人：";
                    }
                    intent = new Intent(activity, PioneersDetailActivity.class);
                    intent.putExtra("url", News.DETAIL_URL_PIONEERS);
                    intent.putExtra("authorLabel", authorLabel);
                    intent.putExtra("authorVisible", authorVisible);
                    intent.putExtra("fromVisible", fromVisible);
                }
                intent.putExtra("news", news);
                startActivity(intent);
            }
        });

        emptyView.setRetryListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getList();
                getBanners();
            }
        });
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
                intent.putExtra("url", News.DETAIL_URL_PIONEERS);
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

    @Event({R.id.pioneersCat1, R.id.pioneersCat2, R.id.pioneersCat3})
    private void pioneersCat(View view) {
        mPager.reset();
        mCatId = view.getTag().toString();
        mUrl = "vanguard/get";
        getList();
    }

    // 作品列表
    @Event({R.id.pioneersCat4})
    private void pioneersWorksCat(View view) {
        mPager.reset();
        mCatId = null;
        mUrl = "works/getlist";
        getList();
    }

    public void getList() {
        if (!activity.checkNetwork()) {
            return;
        }
        // 不选全部，11 => '先锋事迹', 12 => '党建硕果', 13 => '德贤标兵'
        // status 0-审批中 1-已审批 2-未通过
        HTTP.service.get(mUrl, MapParam.me().p("status", "1").p("catid", mCatId).p("page", mPager.nextPage()).p("size", mPager.size).build())
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Subscriber(activity){

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

    public void getBanners() {
        if (!activity.checkNetwork()) {
            return;
        }
        HTTP.service.get("vanguard/headimage")
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
                                images.add(Data.decorateUrl(banner.image));
                            }
                            initBanner(titles, images);
                        }
                    }

                    @Override
                    public void onWrong(String msg) {
                    }
                });
    }
}
