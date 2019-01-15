package com.huaneng.zhdj.study;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.AdapterView;

import com.huaneng.zhdj.BaseRefreshFragment;
import com.huaneng.zhdj.R;
import com.huaneng.zhdj.adapter.StudyNoteAdapter;
import com.huaneng.zhdj.bean.StudyNote;
import com.huaneng.zhdj.bean.StudyNotePager;
import com.huaneng.zhdj.bean.User;
import com.huaneng.zhdj.network.HTTP;
import com.huaneng.zhdj.network.Response;
import com.huaneng.zhdj.network.Subscriber;
import com.huaneng.zhdj.utils.MapParam;
import com.huaneng.zhdj.utils.UserUtils;

import org.xutils.view.annotation.ContentView;

import java.util.Map;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;

/**
 * 党员学习笔记
 */
@ContentView(R.layout.fragment_study_note)
public class StudyNoteFragment extends BaseRefreshFragment<StudyNote> {//

    public static final String TYPE_ALL = "1";//所有笔记
    public static final String TYPE_MY = "2";//我的笔记
    String type;

    StudyNoteAdapter mAdapter;

    public static StudyNoteFragment getInstance(String type) {
        StudyNoteFragment fragment = new StudyNoteFragment();
        Bundle bundle = new Bundle();
        bundle.putString("type", type);
        fragment.setArguments(bundle);
        return fragment;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        type = getArguments().getString("type");
        mAdapter = new StudyNoteAdapter(activity, mList);
        mListView.setAdapter(mAdapter);
        mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                Intent intent = new Intent(activity, StudyNoteDetailActivity.class);
                intent.putExtra("news", mList.get(i));
                startActivity(intent);
            }
        });
        pagerHandler.adapter = mAdapter;
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
        Map<String, Object> map = MapParam.me().p("type", type).p("page", mPager.nextPage()).p("size", mPager.size).build();
        if (TYPE_MY.equals(type)) {
            User user = UserUtils.getUser();
            if (!TextUtils.isEmpty(user.userid)) {
                map.put("user_id", user.userid);
            }
        }
        HTTP.service.get("schoolnote/list", map)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Subscriber(activity){

                    @Override
                    public void onSuccess(Response response) {
                        StudyNotePager pager = response.getEntity(StudyNotePager.class);
                        pagerHandler.requestSuccess(pager);
                    }

                    @Override
                    public void onWrong(String msg) {
                        pagerHandler.requestFail();
                    }
                });
    }
}
