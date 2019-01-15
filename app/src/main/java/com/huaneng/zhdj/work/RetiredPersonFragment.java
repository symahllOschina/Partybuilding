package com.huaneng.zhdj.work;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;

import com.huaneng.zhdj.BaseRefreshFragment;
import com.huaneng.zhdj.R;
import com.huaneng.zhdj.adapter.NewsTextAdapter;
import com.huaneng.zhdj.bean.News;
import com.huaneng.zhdj.bean.NewsPager;
import com.huaneng.zhdj.bean.SearchFinishEvent;
import com.huaneng.zhdj.network.HTTP;
import com.huaneng.zhdj.network.Response;
import com.huaneng.zhdj.network.Subscriber;
import com.huaneng.zhdj.news.NewsDetailActivity;
import com.huaneng.zhdj.utils.MapParam;

import org.greenrobot.eventbus.EventBus;
import org.xutils.view.annotation.ContentView;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;

/**
 * 退休人员
 */
@Deprecated
@ContentView(R.layout.fragment_retired_person)
public class RetiredPersonFragment extends BaseRefreshFragment<News> {//

    NewsTextAdapter adapter;

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        adapter = new NewsTextAdapter(activity, mList);
        mListView.setAdapter(adapter);
        mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                Intent intent = new Intent(activity, NewsDetailActivity.class);
                intent.putExtra("news", mList.get(i));
                intent.putExtra("url", News.DETAIL_URL_PARTY_REGULATION);
                startActivity(intent);
            }
        });
        pagerHandler.adapter = adapter;
        getList();
    }

    public void getList() {
        if (!activity.checkNetwork()) {
            return;
        }
        HTTP.service.get("syllabus/list", MapParam.me().p("page", mPager.nextPage()).p("size", mPager.size).build())
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Subscriber(activity){

                    @Override
                    public void onSuccess(Response response) {
//                        EventBus.getDefault().post(new SearchFinishEvent(PartyWorkFragment.class.getSimpleName()));
                        NewsPager pager = response.getEntity(NewsPager.class);
                        pagerHandler.requestSuccess(pager);
                    }

                    @Override
                    public void onWrong(String msg) {
//                        EventBus.getDefault().post(new SearchFinishEvent(PartyWorkFragment.class.getSimpleName()));
                        pagerHandler.requestFail();
                    }
                });
    }
}
