package com.huaneng.zhdj.study;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;

import com.huaneng.zhdj.BaseRefreshActivity;
import com.huaneng.zhdj.R;
import com.huaneng.zhdj.adapter.QuestionAdapter;
import com.huaneng.zhdj.bean.TestQuestion;
import com.huaneng.zhdj.bean.TestQuestionPager;
import com.huaneng.zhdj.network.HTTP;
import com.huaneng.zhdj.network.Response;
import com.huaneng.zhdj.network.Subscriber;

import org.xutils.view.annotation.ContentView;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;

/**
 * 考试中心
 */
@ContentView(R.layout.activity_exam_center)
public class ExamCenterActivity extends BaseRefreshActivity<TestQuestion> {//

    QuestionAdapter mAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setTitle("试题库");
        mAdapter = new QuestionAdapter(this, mList);
        mListView.setAdapter(mAdapter);
        mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                Intent intent = new Intent(ctx, ExamActivity.class);
                intent.putExtra("question", mList.get(i));
                startActivity(intent);
            }
        });
        pagerHandler.adapter = mAdapter;
    }

    @Override
    protected void onResume() {
        mPager.reset();
        getList();
        super.onResume();
    }

    public void getList() {
        if (!checkNetwork()) {
            return;
        }
        HTTP.service.get("schoolexam/list")
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Subscriber(ctx){

                    @Override
                    public void onSuccess(Response response) {
                        TestQuestionPager pager = response.getEntity(TestQuestionPager.class);
                        pagerHandler.requestSuccess(pager);
                    }

                    @Override
                    public void onWrong(String msg) {
                        pagerHandler.requestFail();
                    }
                });
    }
}
