package com.huaneng.zhdj.study;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;

import com.huaneng.zhdj.BaseRefreshFragment;
import com.huaneng.zhdj.Data;
import com.huaneng.zhdj.FileWebViewActivity;
import com.huaneng.zhdj.R;
import com.huaneng.zhdj.adapter.AttachmentAdapter;
import com.huaneng.zhdj.bean.Attachment;
import com.huaneng.zhdj.bean.AttachmentPager;
import com.huaneng.zhdj.network.HTTP;
import com.huaneng.zhdj.network.Response;
import com.huaneng.zhdj.network.Subscriber;
import com.huaneng.zhdj.utils.MapParam;

import org.xutils.view.annotation.ContentView;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;

/**
 * 学习资料
 */
@ContentView(R.layout.fragment_study_doc)
public class StudyDocFragment extends BaseRefreshFragment<Attachment> {//

    AttachmentAdapter mAdapter;

    public static final String TYPE_1 = "1";//学习资料
    public static final String TYPE_2 = "2";//网络党课
    public static final String TYPE_3 = "3";//内部资料
    // 1.学习资料2.网络党课3.内部资料
    String type;

    public static StudyDocFragment getInstance(String type) {
        StudyDocFragment fragment = new StudyDocFragment();
        Bundle bundle = new Bundle();
        bundle.putString("type", type);
        fragment.setArguments(bundle);
        return fragment;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        type = getArguments().getString("type");
        mAdapter = new AttachmentAdapter(activity, mList);
        mListView.setAdapter(mAdapter);
        mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
//                FileDisplay.displayAttachment(activity, mList.get(i), "schoolfile/read");

                Intent intent = new Intent(activity, FileWebViewActivity.class);
                intent.putExtra("attachment", mList.get(i));
                intent.putExtra("url", "schoolfile/read");
                startActivity(intent);
            }

        });
        pagerHandler.adapter = mAdapter;
        getList();
    }

    public void getList() {
        if (!activity.checkNetwork()) {
            return;
        }
        HTTP.service.get("schoolfile/list", MapParam.me().p("type", type).build())
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Subscriber(activity){

                    @Override
                    public void onSuccess(Response response) {
                        AttachmentPager pager = response.getEntity(AttachmentPager.class);
                        if (pager.list != null && !pager.list.isEmpty()) {
                            for (Attachment attachment: pager.list) {
                                attachment.path = Data.decorateUrl(attachment.path, attachment.configurl);
                            }
                        }
                        pagerHandler.requestSuccess(pager);
                    }

                    @Override
                    public void onWrong(String msg) {
                        pagerHandler.requestFail();
                    }
                });
    }
}
