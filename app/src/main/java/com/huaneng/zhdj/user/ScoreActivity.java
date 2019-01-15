package com.huaneng.zhdj.user;

import android.os.Bundle;
import android.view.View;

import com.huaneng.zhdj.BaseRefreshActivity;
import com.huaneng.zhdj.R;
import com.huaneng.zhdj.adapter.ScoreRecordAdapter;
import com.huaneng.zhdj.bean.ScoreRecord;
import com.huaneng.zhdj.bean.ScoreRecordPager;
import com.huaneng.zhdj.network.HTTP;
import com.huaneng.zhdj.network.Response;
import com.huaneng.zhdj.network.Subscriber;
import com.huaneng.zhdj.utils.MapParam;

import org.xutils.view.annotation.ContentView;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;

// 积分获取记录
@ContentView(R.layout.activity_score)
public class ScoreActivity extends BaseRefreshActivity<ScoreRecord> {//

    ScoreRecordAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setTitle("积分获取记录");
        adapter = new ScoreRecordAdapter(ctx, mList);
        mListView.setAdapter(adapter);
//        mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
//
//            @Override
//            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
//                if (i == 0) {// 点击了表头
//                    return;
//                }
//                Intent intent = new Intent(ctx, PartyWorkDetailActivity.class);
//                intent.putExtra("news", mList.get(i-1));
//                startActivity(intent);
//            }
//        });
        View header = getLayoutInflater().inflate(R.layout.layout_score_header, null);
        mListView.addHeaderView(header);
        pagerHandler.adapter = adapter;
        getList();
    }

    public void getList() {
        if (!ctx.checkNetwork()) {
            return;
        }
        HTTP.service.get("integral/changelog", MapParam.me().p("page", mPager.nextPage()).p("size", mPager.size).build())
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Subscriber(ctx) {

                    @Override
                    public void onSuccess(Response response) {
                        ScoreRecordPager pager = response.getEntity(ScoreRecordPager.class);
                        pagerHandler.requestSuccess(pager);
                    }

                    @Override
                    public void onWrong(String msg) {
                        pagerHandler.requestFail();
                    }
                });
    }
}
