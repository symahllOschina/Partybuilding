package com.huaneng.zhdj.supervise;

import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.TextView;

import com.huaneng.zhdj.BaseRefreshActivity;
import com.huaneng.zhdj.R;
import com.huaneng.zhdj.adapter.VoteRecordAdapter;
import com.huaneng.zhdj.bean.VoteRecord;
import com.huaneng.zhdj.bean.VoteRecordPager;
import com.huaneng.zhdj.network.HTTP;
import com.huaneng.zhdj.network.Response;
import com.huaneng.zhdj.network.Subscriber;
import com.huaneng.zhdj.utils.MapParam;

import org.xutils.view.annotation.ContentView;
import org.xutils.view.annotation.ViewInject;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;

// 投票记录
@ContentView(R.layout.activity_vote_record)
public class VoteRecordActivity extends BaseRefreshActivity<VoteRecord> {

    @ViewInject(R.id.totalTv)
    TextView totalTv;
    VoteRecordAdapter adapter;
    String type = "2";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setTitle("投票记录");

        totalTv.setText("共计 12人");

        VoteRecord voteRecord = new VoteRecord();
        voteRecord.name = "张三";
        voteRecord.option = "2";
        voteRecord.time = "2018-03-09 14:09";
        mList.add(voteRecord);

        voteRecord = new VoteRecord();
        voteRecord.name = "李明";
        voteRecord.option = "3";
        voteRecord.time = "2018-03-08 11:09";
        mList.add(voteRecord);

        adapter = new VoteRecordAdapter(ctx, mList);
        mListView.setAdapter(adapter);
        mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
//                if (i == 0) {// 点击了表头
//                    return;
//                }
//                Vote meeting = mList.get(i-1);
//                Intent intent = new Intent(ctx, PartyActivityDetailActivity.class);
//                intent.putExtra("news", meeting);
//                startActivity(intent);
            }
        });
        View header = getLayoutInflater().inflate(R.layout.layout_vote_record_list_header, null);
        mListView.addHeaderView(header);
        mListView.setVisibility(View.VISIBLE);
        pagerHandler.adapter = adapter;
//        getList();
    }

    public void getList() {
        if (!ctx.checkNetwork()) {
            return;
        }
        HTTP.service.get("meeting/list", MapParam.me().p("type", type).build())
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Subscriber(ctx) {

                    @Override
                    public void onSuccess(Response response) {
                        VoteRecordPager pager = response.getEntity(VoteRecordPager.class);
                        pagerHandler.requestSuccess(pager);
                    }

                    @Override
                    public void onWrong(String msg) {
                        pagerHandler.requestFail();
                    }
                });
    }
}
