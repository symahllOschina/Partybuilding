package com.huaneng.zhdj;

import android.os.Bundle;
import android.text.TextUtils;
import android.webkit.WebView;

import com.huaneng.zhdj.utils.WebViewUtils;

import org.xutils.view.annotation.ContentView;
import org.xutils.view.annotation.ViewInject;

@ContentView(R.layout.activity_webview)
public class WebViewActivity extends BaseActivity {

    @ViewInject(R.id.webView)
    private WebView webView;

    public static String bigData;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setTitle(getIntent().getStringExtra("title"));
        boolean isBigData = getIntent().getBooleanExtra("isBigData", false);
        if (isBigData) {
            WebViewUtils.me(this, webView).html(bigData);
            return;
        }
        String url = getIntent().getStringExtra("url");
        if (!TextUtils.isEmpty(url)) {
            WebViewUtils.me(this, webView).url(url);
        } else {
            String html = getIntent().getStringExtra("html");
            if (!TextUtils.isEmpty(html)) {
                WebViewUtils.me(this, webView).html(html);
            }
        }
    }

    @Override
    protected void onDestroy() {
        bigData = null;
        super.onDestroy();
    }
}
