package com.huaneng.zhdj.work;

import android.os.Bundle;
import android.webkit.WebView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.huaneng.zhdj.BaseActivity;
import com.huaneng.zhdj.R;
import com.huaneng.zhdj.bean.Work;
import com.huaneng.zhdj.network.HTTP;
import com.huaneng.zhdj.network.Response;
import com.huaneng.zhdj.network.Subscriber;
import com.huaneng.zhdj.utils.MapParam;
import com.huaneng.zhdj.utils.WebViewUtils;
import com.huaneng.zhdj.view.NineGridlayout;

import org.xutils.view.annotation.ContentView;
import org.xutils.view.annotation.ViewInject;

import java.util.ArrayList;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;

// 党员发展详情
@ContentView(R.layout.activity_party_join_detail)
public class PartyJoinDetailActivity extends BaseActivity {

    @ViewInject(R.id.webView)
    WebView webView;
    @ViewInject(R.id.approveStatusTv)
    TextView approveStatusTv;
    @ViewInject(R.id.approveContentTv)
    TextView approveContentTv;

    @ViewInject(R.id.nineGridlayout)
    NineGridlayout nineGridlayout;

    Work work;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        work = getSerializableExtra("news");
        setTitle(work.title);
        getDetail();
    }

    private void getDetail() {
        if (!ctx.checkNetwork()) {
            return;
        }
        HTTP.service.get("party/read", MapParam.me().p("id", work.id).build())
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Subscriber(ctx) {

                    @Override
                    public void onSuccess(Response response) {
                        work = response.getEntity(Work.class);
                        initImages();

                        WebViewUtils.me(ctx, webView).html(work.content);
                        showText(approveStatusTv, "审核状态：", work.getApproveStatusDisplay());
                        showText(approveContentTv, work.getApproveLogContent());
                    }

                    @Override
                    public void onWrong(String msg) {
                        toast(msg);
                    }
                });
    }

    // 添加图片的按钮
    private void initImages() {
        if (work.images_info != null && !work.images_info.isEmpty()) {
            nineGridlayout.init(this, true);
            nineGridlayout.setImagesData((ArrayList<String>)work.images_info);
        }
    }
}
