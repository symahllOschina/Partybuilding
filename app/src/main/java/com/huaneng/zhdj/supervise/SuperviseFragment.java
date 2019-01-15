package com.huaneng.zhdj.supervise;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.util.Base64;
import android.view.View;

import com.huaneng.zhdj.App;
import com.huaneng.zhdj.BaseFragment;
import com.huaneng.zhdj.BaseRefreshFragment;
import com.huaneng.zhdj.Constants;
import com.huaneng.zhdj.Data;
import com.huaneng.zhdj.R;
import com.huaneng.zhdj.WebViewActivity;
import com.huaneng.zhdj.bean.News;
import com.huaneng.zhdj.bean.NewsPager;
import com.huaneng.zhdj.network.HTTP;
import com.huaneng.zhdj.network.Response;
import com.huaneng.zhdj.network.Subscriber;
import com.huaneng.zhdj.news.NewsDetailActivity;
import com.huaneng.zhdj.utils.AESHelper;
import com.huaneng.zhdj.utils.GlideImageLoader;
import com.huaneng.zhdj.utils.MapParam;
import com.huaneng.zhdj.utils.SharedPreferencesUtils;
import com.scwang.smartrefresh.layout.api.RefreshLayout;
import com.scwang.smartrefresh.layout.listener.OnLoadMoreListener;
import com.scwang.smartrefresh.layout.listener.OnRefreshListener;
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
 * 党建监督
 */
@ContentView(R.layout.fragment_supervise)
public class SuperviseFragment extends BaseFragment implements OnRefreshListener, OnLoadMoreListener{

    @ViewInject(R.id.refreshLayout)
    RefreshLayout refreshLayout;

    @ViewInject(R.id.banner)
    Banner banner;
    List<News> bannerNewsList;

    List<Fragment> fragmentList = new ArrayList<Fragment>();

    int index;

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        PartyPublicFragment partyPublicFragment = new PartyPublicFragment();
        partyPublicFragment.setRefreshLayout(refreshLayout);

        DisciplineSuperviseFragment disciplineSuperviseFragment = new DisciplineSuperviseFragment();
        disciplineSuperviseFragment.setRefreshLayout(refreshLayout);

        fragmentList.add(partyPublicFragment);
        fragmentList.add(disciplineSuperviseFragment);
        for (Fragment fragment : fragmentList) {
            addFragment(fragment);
        }
        changeFragment(fragmentList, 0);
        getBanners();
    }

    // partyPublicTv 党务公开
    // disciplineSuperviseTv 纪律监督
    @Event({R.id.partyPublicTv, R.id.disciplineSuperviseTv})
    private void changeFragment(View view) {
        index = Integer.valueOf(view.getTag().toString());
        changeFragment(fragmentList, index);
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
                intent.putExtra("url", bannerNewsList.get(position).url);
                startActivity(intent);
            }
        });
    }

    public void getBanners() {
        if (!activity.checkNetwork()) {
            return;
        }
        // type: 1=党务管理;2=党建监督;3=党建缴费
        HTTP.service.get("banner/list", MapParam.me().p("type", "2").build())
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Subscriber(activity){

                    @Override
                    public void onSuccess(Response response) {
                        NewsPager newsPager = response.getEntity(NewsPager.class);
                        bannerNewsList = newsPager.list;
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
                        activity.toast(msg);
                    }
                });
    }

    // 党建地图
    @Event({R.id.mapTv})
    private void map(View view) {
        if (!activity.checkNetwork()) {
            return;
        }
        String token = SharedPreferencesUtils.create(App.myself).get(Constants.SPR_TOKEN, "");
        String signstr = Base64.encodeToString((System.currentTimeMillis() + "sfsdf").getBytes(), Base64.NO_WRAP);
        String version = "pabudv1";
        String signtoken = AESHelper.encrypt("signstr=" + signstr + "&version=" + version + "&time=" + System.currentTimeMillis());

//        String url = Data.SERVER + "api/v1/map/index?accesstoken=" + token;
        String url = Data.SERVER + "api/v1/map/index";
//        String url = Data.SERVER + "api/v1/map/index?accesstoken=" + signtoken;
        Intent intent = new Intent(activity, WebViewActivity.class);
        intent.putExtra("title", "党建地图");
        intent.putExtra("url", url);
        startActivity(intent);

    }

    @Override
    public void onStart() {
        super.onStart();
        //开始轮播
        banner.startAutoPlay();
    }

    @Override
    public void onStop() {
        super.onStop();
        //结束轮播
        banner.stopAutoPlay();
    }

    // 政民互动/互动空间
    @Event({R.id.peopleInteractTv})
    private void peopleInteract(View view) {
        activity(PeopleInteractActivity.class);
    }

    // 我要举报
    @Event({R.id.accusationTv})
    private void accusation(View view) {
        activity(AccusationActivity.class);
    }

    @Override
    public void onRefresh(RefreshLayout refreshlayout) {
        ((BaseRefreshFragment)fragmentList.get(index)).onRefresh(refreshlayout);
    }

    @Override
    public void onLoadMore(@NonNull RefreshLayout refreshLayout) {
        ((BaseRefreshFragment)fragmentList.get(index)).getList();
    }
}
