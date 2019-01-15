package com.huaneng.zhdj;

import android.os.Bundle;
import android.view.View;
import android.widget.ListView;

import com.huaneng.zhdj.adapter.NoticeAdapter;
import com.huaneng.zhdj.bean.Notice;
import com.huaneng.zhdj.network.HTTP;
import com.huaneng.zhdj.network.Response;
import com.huaneng.zhdj.network.Subscriber;

import org.xutils.view.annotation.ContentView;
import org.xutils.view.annotation.ViewInject;

import java.util.ArrayList;
import java.util.List;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;

/**
 * 紧急通知
 */
@ContentView(R.layout.fragment_notice)
public class NoticeFragment extends BaseFragment {

    @ViewInject(R.id.listView)
    ListView mListView;
    @ViewInject(R.id.emptyView)
    View emptyView;

    List<Notice> notices = new ArrayList<Notice>();
    NoticeAdapter noticeAdapter;

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        noticeAdapter = new NoticeAdapter(activity, notices);
        mListView.setAdapter(noticeAdapter);
        load();
    }

    public void load() {
//        if (!activity.checkNetwork()) {
//            return;
//        }
//        activity.showWaitDialog();
//        HTTP.service.getNotices()
//                .subscribeOn(Schedulers.io())
//                .observeOn(AndroidSchedulers.mainThread())
//                .subscribe(new Subscriber<List<Notice>>(activity){
//
//                    @Override
//                    public void onSuccess(Response<List<Notice>> response) {
//                        List<Notice> list = response.data;
//                        if (list == null || list.isEmpty()) {
//                            mListView.setEmptyView(emptyView);
//                        } else {
//                            notices.addAll(list);
//                        }
//                        noticeAdapter.notifyDataSetChanged();
//                    }
//
//                    @Override
//                    public void onError(Throwable e) {
//                        if (notices.isEmpty()) {
//                            mListView.setEmptyView(emptyView);
//                        }
//                    }
//
//                    @Override
//                    public void onFail(String msg) {
//                        if (notices.isEmpty()) {
//                            mListView.setEmptyView(emptyView);
//                        }
//                    }
//                });
    }
}
