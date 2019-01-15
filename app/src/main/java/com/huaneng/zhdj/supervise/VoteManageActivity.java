package com.huaneng.zhdj.supervise;

import android.os.Bundle;
import android.view.View;

import com.huaneng.zhdj.BaseRefreshActivity;
import com.huaneng.zhdj.R;
import com.huaneng.zhdj.adapter.VoteManageAdapter;
import com.huaneng.zhdj.bean.Vote;
import com.huaneng.zhdj.bean.VotePager;
import com.huaneng.zhdj.network.HTTP;
import com.huaneng.zhdj.network.Response;
import com.huaneng.zhdj.network.Subscriber;
import com.huaneng.zhdj.utils.MapParam;

import org.xutils.view.annotation.ContentView;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;

/**
 * 投票管理
 * 后台做，App不做
 */
@Deprecated
@ContentView(R.layout.activity_vote_manage)
public class VoteManageActivity extends BaseRefreshActivity<Vote> {//

    VoteManageAdapter adapter;
    int checkedIndex = -1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setTitle("投票管理");
        adapter = new VoteManageAdapter(ctx, mList);
        mListView.setAdapter(adapter);
        View header = getLayoutInflater().inflate(R.layout.layout_vote_manage_list_header, null);
        mListView.addHeaderView(header);
        mListView.setVisibility(View.VISIBLE);
        pagerHandler.adapter = adapter;
//        getList();
    }

    public void getList() {
        if (!ctx.checkNetwork()) {
            return;
        }
        HTTP.service.get("mVote/list", MapParam.me().p("page", mPager.nextPage()).p("size", mPager.size).build())
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Subscriber(ctx) {

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

    public void filter(View v) {
//        new AlertDialog.Builder(this)
//                .setTitle("请选择状态：")
//                .setSingleChoiceItems(Data.meeting_status_arr, checkedIndex, new DialogInterface.OnClickListener() {
//                    @Override
//                    public void onClick(DialogInterface dialog, int which) {
//                        dialog.dismiss();
//                        checkedIndex = which;
//                    }
//                })
//                .setNegativeButton("取消", new DialogInterface.OnClickListener() {
//                    @Override
//                    public void onClick(DialogInterface dialog, int which) {
//                        dialog.dismiss();
//                    }
//                }).show();
    }
}
