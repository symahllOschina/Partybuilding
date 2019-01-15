package com.huaneng.zhdj.work;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;

import com.huaneng.zhdj.BaseRefreshActivity;
import com.huaneng.zhdj.Data;
import com.huaneng.zhdj.R;
import com.huaneng.zhdj.adapter.ActivityAdapter;
import com.huaneng.zhdj.bean.Meeting;
import com.huaneng.zhdj.bean.MeetingPager;
import com.huaneng.zhdj.network.HTTP;
import com.huaneng.zhdj.network.Response;
import com.huaneng.zhdj.network.Subscriber;
import com.huaneng.zhdj.utils.MapParam;

import org.xutils.view.annotation.ContentView;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;

// 党务办公 -- 活动列表
@ContentView(R.layout.activity_party_activity)
public class PartyActivityActivity extends BaseRefreshActivity<Meeting> {//

    ActivityAdapter adapter;
    String type = "2";
    int checkedIndex = -1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setTitle("活动列表");
        adapter = new ActivityAdapter(ctx, mList);
        mListView.setAdapter(adapter);
        mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                Meeting meeting = mList.get(i);
                Intent intent = new Intent(ctx, PartyActivityDetailActivity.class);
                intent.putExtra("news", meeting);
                startActivity(intent);
            }
        });
        pagerHandler.adapter = adapter;

        getList();
    }

    @Override
    public void getList() {
        if (!ctx.checkNetwork()) {
            return;
        }
        String condition = (checkedIndex == -1)?"0" : (checkedIndex + "");
        if (checkedIndex == Data.meeting_status_arr.length-1) {
            condition = "0,1,2,3,4";
        }
        HTTP.service.get("meeting/list", MapParam.me()
                .p("page", mPager.nextPage())
                .p("size", mPager.size)
                .p("condition", condition)
                .p("type", type).build())
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Subscriber(ctx) {

                    @Override
                    public void onSuccess(Response response) {
                        MeetingPager pager = response.getEntity(MeetingPager.class);
                        pagerHandler.requestSuccess(pager);
                    }

                    @Override
                    public void onWrong(String msg) {
                        pagerHandler.requestFail();
                    }
                });
    }

    public void filter(View v) {
        new AlertDialog.Builder(this)
                .setTitle("请选择状态：")
                .setSingleChoiceItems(Data.meeting_status_arr, checkedIndex, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                        checkedIndex = which;
                        mPager.reset();
                        getList();
                    }
                })
                .setNegativeButton("取消", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                }).show();
    }
}
