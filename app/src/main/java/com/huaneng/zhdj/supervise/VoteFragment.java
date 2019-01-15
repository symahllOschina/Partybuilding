package com.huaneng.zhdj.supervise;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.EditText;

import com.huaneng.zhdj.BaseRefreshFragment;
import com.huaneng.zhdj.R;
import com.huaneng.zhdj.adapter.VoteAdapter;
import com.huaneng.zhdj.bean.Vote;
import com.huaneng.zhdj.bean.VotePager;
import com.huaneng.zhdj.network.HTTP;
import com.huaneng.zhdj.network.Response;
import com.huaneng.zhdj.network.Subscriber;
import com.huaneng.zhdj.utils.MapParam;

import org.xutils.view.annotation.ContentView;
import org.xutils.view.annotation.Event;
import org.xutils.view.annotation.ViewInject;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;

/**
 * 民众投票
 */
@ContentView(R.layout.fragment_vote)
public class VoteFragment extends BaseRefreshFragment<Vote> {//

    @ViewInject(R.id.keywordsTv)
    EditText keywordsTv;
    VoteAdapter adapter;

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        adapter = new VoteAdapter(activity, mList);
        mListView.setAdapter(adapter);

        mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                Intent intent = new Intent(activity, VoteActivity.class);
                intent.putExtra("vote", mList.get(i));
                startActivity(intent);
            }
        });
        pagerHandler.adapter = adapter;
    }

    @Override
    public void onResume() {
        mPager.reset();
        getList();
        super.onResume();
    }

    public void getList() {
        if (!activity.checkNetwork()) {
            return;
        }
        String keywords = keywordsTv.getText().toString().trim();
        HTTP.service.get("vote/list", MapParam.me().p("page", mPager.nextPage()).p("size", mPager.size).p("title", keywords).build())
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Subscriber(activity){

                    @Override
                    public void onSuccess(Response response) {
                        VotePager pager = response.getEntity(VotePager.class);
                        pagerHandler.requestSuccess(pager);
                    }

                    @Override
                    public void onWrong(String msg) {
                        pagerHandler.requestFail();
                    }
                });
    }

    // 关键字搜索
    @Event(R.id.searchBtn)
    private void search(View view) {
        mPager.reset();
        getList();
    }

    // 投票管理
    @Event(R.id.voteManageBtn)
    private void voteManage(View view) {
        activity(VoteManageActivity.class);
    }

    // 投票发起
    @Event(R.id.voteBtn)
    private void vote(View view) {
        activity(VoteCreateActivity.class);
    }
}
