package com.huaneng.zhdj.work;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.EditText;

import com.huaneng.zhdj.BaseRefreshActivity;
import com.huaneng.zhdj.R;
import com.huaneng.zhdj.adapter.WorkAdapter;
import com.huaneng.zhdj.bean.PartyWorkSaveSuccessEvent;
import com.huaneng.zhdj.bean.Work;
import com.huaneng.zhdj.bean.WorkPager;
import com.huaneng.zhdj.network.HTTP;
import com.huaneng.zhdj.network.Response;
import com.huaneng.zhdj.network.Subscriber;
import com.huaneng.zhdj.utils.MapParam;

import org.greenrobot.eventbus.Subscribe;
import org.xutils.view.annotation.ContentView;

import java.util.ArrayList;
import java.util.List;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;

// 党务办公 -- 工作列表
@ContentView(R.layout.activity_party_work)
public class PartyWorkActivity extends BaseRefreshActivity<Work> {//

    WorkAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setTitle("工作汇报列表");
        adapter = new WorkAdapter(ctx, mList);
        mListView.setAdapter(adapter);
        mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                if (i == 0) {// 点击了表头
                    return;
                }
                Intent intent = new Intent(ctx, PartyWorkDetailActivity.class);
                intent.putExtra("news", mList.get(i-1));
                startActivity(intent);
            }
        });
        View header = getLayoutInflater().inflate(R.layout.layout_work_list_header, null);
        mListView.addHeaderView(header);
        pagerHandler.adapter = adapter;
        getList();
    }

    @Override
    protected boolean isEnableEventBus() {
        return true;
    }

    @Subscribe
    public void onPartyWorkSaveSuccess(PartyWorkSaveSuccessEvent event) {
        mPager.reset();
        getList();
    }

    public void getList() {
        if (!ctx.checkNetwork()) {
            return;
        }
        HTTP.service.get("worksreport/getlist", MapParam.me().p("page", mPager.nextPage()).p("size", mPager.size).build())
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Subscriber(ctx) {

                    @Override
                    public void onSuccess(Response response) {
                        WorkPager pager = response.getEntity(WorkPager.class);
                        pagerHandler.requestSuccess(pager);
                    }

                    @Override
                    public void onWrong(String msg) {
                        pagerHandler.requestFail();
                    }
                });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_search_add, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_search:
                search();
                break;
            case R.id.action_menu:
                activity(PartyWorkAddActivity.class);
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    /**
     * 搜索
     */
    private void search() {
        if (mList == null || mList.isEmpty()) {
            return;
        }
        final EditText edit = new EditText(this);
        AlertDialog dialog = new AlertDialog.Builder(this)
                .setTitle("请输入关键字")
                .setView(edit)
                .setPositiveButton("确认", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        String name = edit.getText().toString();
                        if (TextUtils.isEmpty(name)) {
                            adapter.setData(mList);
                        } else {
                            List<Work> list = new ArrayList<>();
                            for (Work work: mList) {
                                if (work.title.contains(name)) {
                                    list.add(work);
                                }
                            }
                            if (list.isEmpty()) {
                                toast("没有查询到数据");
                            }
                            adapter.setData(list);
                        }
                        adapter.notifyDataSetChanged();
                    }
                })
                .setNegativeButton("取消", null).create();
        dialog.show();
    }
}
