package com.huaneng.zhdj.user;

import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;

import com.huaneng.zhdj.BaseRefreshActivity;
import com.huaneng.zhdj.R;
import com.huaneng.zhdj.adapter.MessageAdapter;
import com.huaneng.zhdj.bean.Message;
import com.huaneng.zhdj.bean.MessagePager;
import com.huaneng.zhdj.bean.Pager;
import com.huaneng.zhdj.network.HTTP;
import com.huaneng.zhdj.network.Response;
import com.huaneng.zhdj.network.Subscriber;
import com.huaneng.zhdj.utils.MapParam;

import org.xutils.view.annotation.ContentView;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;

// 消息中心
@ContentView(R.layout.activity_message)
public class MessageActivity extends BaseRefreshActivity<Message> {//

    MessageAdapter adapter;
    private boolean isSubmiting;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setTitle("消息中心");
        adapter = new MessageAdapter(ctx, mList);
        mListView.setAdapter(adapter);
        pagerHandler.adapter = adapter;
        getList();
    }

    public void getList() {
        if (!ctx.checkNetwork()) {
            return;
        }
        HTTP.service.get("message/list", MapParam.me().p("page", mPager.nextPage()).p("size", mPager.size).build())
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Subscriber(ctx) {

                    @Override
                    public void onSuccess(Response response) {
                        MessagePager pager = response.getEntity(MessagePager.class);
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
        getMenuInflater().inflate(R.menu.menu_read, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_read:
                setRead();
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    public void setRead() {
        if (mList.isEmpty()) {
            toast("没有消息可设置.");
            return;
        }
        if (!checkNetwork() || isSubmiting) {
            return;
        }
        showWaitDialog("正在提交...");
        isSubmiting = true;
        HTTP.service.post("message/status", MapParam.me().build())
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Subscriber(ctx){

                    @Override
                    public void onSuccess(Response response) {
                        isSubmiting = false;
                        toast("设置成功.");
                        getList();
                    }

                    @Override
                    public void onWrong(String msg) {
                        isSubmiting = false;
                        toast(msg, "设置失败.");
                    }
                });
    }
}
