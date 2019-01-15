package com.huaneng.zhdj.supervise;

import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.TextView;

import com.huaneng.zhdj.BaseRefreshActivity;
import com.huaneng.zhdj.R;
import com.huaneng.zhdj.adapter.SurveyRecordAdapter;
import com.huaneng.zhdj.bean.Survey;
import com.huaneng.zhdj.bean.SurveyItem;
import com.huaneng.zhdj.bean.SurveyItemPager;
import com.huaneng.zhdj.bean.SurveyRecord;
import com.huaneng.zhdj.bean.SurveyRecordPager;
import com.huaneng.zhdj.network.HTTP;
import com.huaneng.zhdj.network.Response;
import com.huaneng.zhdj.network.Subscriber;
import com.huaneng.zhdj.utils.MapParam;
import com.scwang.smartrefresh.layout.api.RefreshLayout;

import org.xutils.view.annotation.ContentView;
import org.xutils.view.annotation.ViewInject;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;

// 问卷作答记录
@ContentView(R.layout.activity_survey_record)
public class SurveyRecordActivity extends BaseRefreshActivity<SurveyItem> {//

    @ViewInject(R.id.countTv)
    TextView countTv;

    Survey survey;
    SurveyRecordAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setTitle("问卷作答记录");
        survey = getSerializableExtra("survey");
        adapter = new SurveyRecordAdapter(ctx, mList);
        mListView.setAdapter(adapter);
        pagerHandler.adapter = adapter;
        getList();
    }

    public void getList() {
        if (!ctx.checkNetwork()) {
            return;
        }
        HTTP.service.get("survey/read", MapParam.me().p("id", survey.id).build())
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Subscriber(ctx) {

                    @Override
                    public void onSuccess(Response response) {
                        SurveyItemPager pager = response.getEntity(SurveyItemPager.class);
                        pager.decorate();
                        countTv.setText("共" + pager.count + "人参与");
                        pagerHandler.requestSuccess(pager);
                    }

                    @Override
                    public void onWrong(String msg) {
                        pagerHandler.requestFail();
                    }
                });
    }
}
