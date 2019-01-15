package com.huaneng.zhdj.study;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;

import com.huaneng.zhdj.BaseRefreshFragment;
import com.huaneng.zhdj.R;
import com.huaneng.zhdj.adapter.StudyActivityAdapter;
import com.huaneng.zhdj.bean.News;
import com.huaneng.zhdj.bean.NewsPager;
import com.huaneng.zhdj.network.HTTP;
import com.huaneng.zhdj.network.Response;
import com.huaneng.zhdj.network.Subscriber;
import com.huaneng.zhdj.utils.MapParam;
import com.scwang.smartrefresh.layout.listener.OnLoadMoreListener;

import org.xutils.view.annotation.ContentView;
import org.xutils.view.annotation.Event;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;

/**
 * 党员学习
 */
@ContentView(R.layout.fragment_study)
public class StudyFragment extends BaseRefreshFragment<News> {//

    StudyActivityAdapter mAdapter;

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        mAdapter = new StudyActivityAdapter(activity, mList);
        mListView.setAdapter(mAdapter);
        mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                Intent intent = new Intent(activity, SpecialSubjectAttachmentsActivity.class);
                intent.putExtra("news", mList.get(i));
                startActivity(intent);
            }
        });
        pagerHandler.adapter = mAdapter;

        getList();
    }

    // 移动课堂
    @Event(R.id.mobileClassroomTv)
    private void onMobileClassroomClick(View view) {
        Intent intent = new Intent(activity, MobileClassroomActivity.class);
        startActivity(intent);
    }

    // 考试中心
    @Event(R.id.examCenterTv)
    private void onExamCenterClick(View view) {
        Intent intent = new Intent(activity, ExamCenterActivity.class);
        startActivity(intent);
    }

    // 学习笔记
    @Event(R.id.studyNoteTv)
    private void onStudyNoteClick(View view) {
        Intent intent = new Intent(activity, StudyNoteActivity.class);
        startActivity(intent);
    }

    public void getList() {
        if (!activity.checkNetwork()) {
            return;
        }
        HTTP.service.get("schoolspecial/list", MapParam.me().p("page", mPager.nextPage()).p("size", mPager.size).build())
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Subscriber(activity){

                    @Override
                    public void onSuccess(Response response) {
                        NewsPager pager = response.getEntity(NewsPager.class);
                        pagerHandler.requestSuccess(pager);
                    }

                    @Override
                    public void onWrong(String msg) {
                        pagerHandler.requestFail();
                    }
                });
    }
}
