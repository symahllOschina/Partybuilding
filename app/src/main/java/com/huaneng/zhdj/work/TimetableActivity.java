package com.huaneng.zhdj.work;

import android.os.Bundle;

import com.huaneng.zhdj.BaseRefreshActivity;
import com.huaneng.zhdj.R;
import com.huaneng.zhdj.adapter.TimetableAdapter;
import com.huaneng.zhdj.bean.Timetable;
import com.huaneng.zhdj.bean.TimetablePager;
import com.huaneng.zhdj.network.HTTP;
import com.huaneng.zhdj.network.Response;
import com.huaneng.zhdj.network.Subscriber;

import org.xutils.view.annotation.ContentView;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;

// 党务管理-离退休党员-课程表
@ContentView(R.layout.activity_timetable)
public class TimetableActivity extends BaseRefreshActivity<Timetable> {//

    TimetableAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setTitle("离退休活动安排");
        adapter = new TimetableAdapter(ctx, mList);
        mListView.setAdapter(adapter);
        pagerHandler.adapter = adapter;
        getList();
    }

    public void getList() {
        if (!ctx.checkNetwork()) {
            return;
        }
        HTTP.service.get("syllabus/list")
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Subscriber(ctx) {

                    @Override
                    public void onSuccess(Response response) {
                        TimetablePager pager = response.getEntity(TimetablePager.class);
                        pagerHandler.requestSuccess(pager);
                    }

                    @Override
                    public void onWrong(String msg) {
                        pagerHandler.requestFail();
                    }
                });
    }
}
