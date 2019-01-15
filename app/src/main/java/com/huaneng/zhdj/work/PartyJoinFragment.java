package com.huaneng.zhdj.work;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;

import com.huaneng.zhdj.BaseRefreshFragment;
import com.huaneng.zhdj.R;
import com.huaneng.zhdj.adapter.PartyJoinAdapter;
import com.huaneng.zhdj.bean.PartySaveSuccessEvent;
import com.huaneng.zhdj.bean.Work;
import com.huaneng.zhdj.bean.WorkPager;
import com.huaneng.zhdj.network.HTTP;
import com.huaneng.zhdj.network.Response;
import com.huaneng.zhdj.network.Subscriber;
import com.huaneng.zhdj.utils.MapParam;

import org.greenrobot.eventbus.Subscribe;
import org.xutils.view.annotation.ContentView;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;

/**
 * 党员发展 - 入党申请中心
 */
@ContentView(R.layout.fragment_party_join)
public class PartyJoinFragment extends BaseRefreshFragment<Work> {//

    public static final String TYPE_1 = "1";//思想汇报 = 积极分子专区
    public static final String TYPE_2 = "2";//入党志愿书 = 预备党员专区
    public static final String TYPE_3 = "3";//入党申请中心内容

    PartyJoinAdapter adapter;
    String type;

    public static PartyJoinFragment getInstance(String type) {
        PartyJoinFragment fragment = new PartyJoinFragment();
        Bundle bundle = new Bundle();
        bundle.putString("type", type);
        fragment.setArguments(bundle);
        return fragment;
    }

    @Override
    protected boolean isEnableEventBus() {
        return true;
    }

    @Subscribe
    public void onPartySaveSuccess(PartySaveSuccessEvent event) {
        if (event.type.equals(this.type)) {
            mPager.reset();
            getList();
        }
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        type = getArguments().getString("type");
        adapter = new PartyJoinAdapter(activity, mList);
        mListView.setAdapter(adapter);
        mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                Intent intent = new Intent(activity, PartyJoinDetailActivity.class);
                intent.putExtra("news", mList.get(i));
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
        HTTP.service.get("party/list", MapParam.me().p("page", mPager.nextPage()).p("size", mPager.size).p("type", type).build())
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Subscriber(activity) {

                    @Override
                    public void onSuccess(Response response) {
                        WorkPager pager = response.getEntity(WorkPager.class);
                        pagerHandler.requestSuccess(pager);
                    }

                    @Override
                    public void onWrong(String msg) {
                        pagerHandler.requestFail();
                    }
                });
    }
}
