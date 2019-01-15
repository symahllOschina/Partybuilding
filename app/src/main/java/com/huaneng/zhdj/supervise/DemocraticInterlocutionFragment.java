package com.huaneng.zhdj.supervise;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ListView;

import com.huaneng.zhdj.BaseRefreshFragment;
import com.huaneng.zhdj.R;
import com.huaneng.zhdj.adapter.DemocraticInterlocutionAdapter;
import com.huaneng.zhdj.bean.QuestionAnswer;
import com.huaneng.zhdj.bean.QuestionAnswerPager;
import com.huaneng.zhdj.network.HTTP;
import com.huaneng.zhdj.network.Response;
import com.huaneng.zhdj.network.Subscriber;
import com.huaneng.zhdj.utils.MapParam;

import org.xutils.view.annotation.ContentView;
import org.xutils.view.annotation.Event;
import org.xutils.view.annotation.ViewInject;

import java.util.ArrayList;
import java.util.List;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;

/**
 * 民主问答
 */
@ContentView(R.layout.fragment_democratic_interlocution)
public class DemocraticInterlocutionFragment extends BaseRefreshFragment<QuestionAnswer> {//

    @ViewInject(R.id.keywordsTv)
    EditText keywordsTv;

    DemocraticInterlocutionAdapter adapter;

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        adapter = new DemocraticInterlocutionAdapter(activity, mList);
        mListView.setAdapter(adapter);
        mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                Intent intent = new Intent(activity, QuestionDetailActivity.class);
                intent.putExtra("questionAnswer", mList.get(i));
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
        // TODO status:-1=删除;0=显示中;1=已读未回复;2=已回复;
        String keywords = keywordsTv.getText().toString().trim();
        HTTP.service.get("aq/list", MapParam.me().p("page", mPager.nextPage()).p("size", mPager.size).p("title", keywords).build())
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Subscriber(activity){

                    @Override
                    public void onSuccess(Response response) {
                        QuestionAnswerPager pager = response.getEntity(QuestionAnswerPager.class);
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

    // 我要批复
    @Event(R.id.answerBtn)
    private void answer(View view) {
        activity(QuestionAnswerListActivity.class);
    }

    // 我要提问
    @Event(R.id.askBtn)
    private void ask(View view) {
        activity(QuestionSubmitActivity.class);
    }
}
