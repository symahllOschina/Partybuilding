package com.huaneng.zhdj.work;

import android.os.Bundle;
import android.webkit.WebSettings;
import android.webkit.WebView;

import com.huaneng.zhdj.BaseActivity;
import com.huaneng.zhdj.R;
import com.huaneng.zhdj.bean.Cell;
import com.huaneng.zhdj.bean.Meeting;
import com.huaneng.zhdj.network.HTTP;
import com.huaneng.zhdj.network.Response;
import com.huaneng.zhdj.network.Subscriber;
import com.huaneng.zhdj.utils.MapParam;
import com.huaneng.zhdj.utils.WebViewUtils;
import com.huaneng.zhdj.view.TableView;

import org.xutils.view.annotation.ContentView;
import org.xutils.view.annotation.ViewInject;

import java.util.ArrayList;
import java.util.List;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;

// 党务办公 -- 活动详情
@ContentView(R.layout.activity_party_activity_detail)
public class PartyActivityDetailActivity extends BaseActivity {

    @ViewInject(R.id.tableView)
    TableView tableView;
    @ViewInject(R.id.webView)
    WebView webView;

    Meeting entity;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        entity = getSerializableExtra("news");
        setTitle(entity.title);
        init();
        getDetail();
    }

    private void init() {
        List<Cell> cells = new ArrayList<>();
        cells.add(new Cell("活动主题：", entity.title));
        cells.add(new Cell("活动时间：", entity.mdate));
        cells.add(new Cell("活动地点：", entity.maddress));
        cells.add(new Cell("活动组织方：", entity.organize));
        cells.add(new Cell("主讲人：", entity.lecturer));
        tableView.setData(cells);
        WebViewUtils.me(this, webView).html(entity.content);
    }

    private void getDetail() {
        if (!ctx.checkNetwork()) {
            return;
        }
        HTTP.service.get("meeting/read", MapParam.me().p("id", entity.id).build())
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Subscriber(ctx) {

                    @Override
                    public void onSuccess(Response response) {
                        entity = response.getEntity(Meeting.class);
                    }

                    @Override
                    public void onWrong(String msg) {
                        toast(msg);
                    }
                });
    }
}
