package com.huaneng.zhdj.user;

import android.os.Bundle;
import android.view.View;

import com.huaneng.zhdj.BaseRefreshActivity;
import com.huaneng.zhdj.R;
import com.huaneng.zhdj.adapter.PartyMemberRelationAdapter;
import com.huaneng.zhdj.bean.PartyMemberRelation;
import com.huaneng.zhdj.bean.PartyMemberRelationPager;
import com.huaneng.zhdj.network.HTTP;
import com.huaneng.zhdj.network.Response;
import com.huaneng.zhdj.network.Subscriber;
import com.huaneng.zhdj.utils.MapParam;

import org.xutils.view.annotation.ContentView;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;

// 转入转出记录
@ContentView(R.layout.activity_party_member_relation)
public class PartyMemberRelationActivity extends BaseRefreshActivity<PartyMemberRelation> {//

    PartyMemberRelationAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setTitle("转入转出记录");
        adapter = new PartyMemberRelationAdapter(ctx, mList);
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
        View header = getLayoutInflater().inflate(R.layout.layout_party_member_relation_header, null);
        mListView.addHeaderView(header);
        pagerHandler.adapter = adapter;
        getList();
    }

    public void getList() {
        if (!ctx.checkNetwork()) {
            return;
        }
        HTTP.service.get("company/changelog", MapParam.me().p("page", mPager.nextPage()).p("size", mPager.size).build())
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Subscriber(ctx) {

                    @Override
                    public void onSuccess(Response response) {
                        PartyMemberRelationPager pager = response.getEntity(PartyMemberRelationPager.class);
                        pagerHandler.requestSuccess(pager);
                    }

                    @Override
                    public void onWrong(String msg) {
                        pagerHandler.requestFail();
                    }
                });
    }
}
