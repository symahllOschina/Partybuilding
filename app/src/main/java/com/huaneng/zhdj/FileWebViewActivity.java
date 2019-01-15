package com.huaneng.zhdj;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.webkit.WebView;
import android.widget.Button;

import com.huaneng.zhdj.bean.Attachment;
import com.huaneng.zhdj.bean.News;
import com.huaneng.zhdj.network.HTTP;
import com.huaneng.zhdj.network.Response;
import com.huaneng.zhdj.network.Subscriber;
import com.huaneng.zhdj.utils.FileDisplay;
import com.huaneng.zhdj.utils.MapParam;
import com.huaneng.zhdj.utils.WebViewUtils;

import org.xutils.view.annotation.ContentView;
import org.xutils.view.annotation.ViewInject;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;

// 附件及其说明展示
@ContentView(R.layout.activity_file_webview)
public class FileWebViewActivity extends BaseActivity {

    @ViewInject(R.id.webView)
    private WebView webView;
    @ViewInject(R.id.fileBtn)
    private Button fileBtn;
    private Attachment attachment;
    private String url;

    public static String bigData;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        attachment = getSerializableExtra("attachment");
        url = getIntent().getStringExtra("url");
        setTitle(attachment.title);
        if (TextUtils.isEmpty(attachment.path)) {
            fileBtn.setVisibility(View.GONE);
        }
        displayHtml();
    }

    public void displayAttachment(View v) {
        FileDisplay.displayAttachment(this, attachment, url);
    }

    private void displayHtml() {
        if (!checkNetwork()) {
            return;
        }
        HTTP.service.get(url, MapParam.me().p("id", attachment.id).build())
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Subscriber(ctx) {

                    @Override
                    public void onSuccess(Response response) {
                        News news = response.getEntity(News.class);
                        String content = news.content;
                        if (TextUtils.isEmpty(content)) {
                            content = "暂无说明";
                        }
                        WebViewUtils.me(ctx, webView).html(content);
                    }

                    @Override
                    public void onWrong(String msg) {
                        WebViewUtils.me(ctx, webView).html("暂无说明");
                    }
                });
    }

    @Override
    protected void onDestroy() {
        bigData = null;
        super.onDestroy();
    }
}
