package com.huaneng.zhdj.utils;

import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.text.TextUtils;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Toast;

import com.huaneng.zhdj.App;
import com.huaneng.zhdj.BaseActivity;
import com.huaneng.zhdj.OfficeActivity;
import com.huaneng.zhdj.PDFViewActivity;
import com.huaneng.zhdj.TxtViewActivity;

import java.util.ArrayList;
import java.util.Collections;

import me.iwf.photopicker.PhotoPreview;

/**
 * Created by TH on 2017/11/7.
 */

public class MyWebViewClient extends WebViewClient {

    private BaseActivity activity;

    public MyWebViewClient(BaseActivity activity) {
        super();
        this.activity = activity;
    }

    @Override
    public void onReceivedError(WebView view, int errorCode, String description, String failingUrl) {
        super.onReceivedError(view, errorCode, description, failingUrl);
        App.toast("加载失败");
    }

    // 为了继续在WebView中显示，需要重写shouldOverrideUrlLoading方法
    @Override
    public boolean shouldOverrideUrlLoading(WebView view, String url) {
        url = url.toLowerCase();
        if (url.endsWith(".pdf")) {
            Intent intent = new Intent(activity, PDFViewActivity.class);
            intent.putExtra("url", url);
            activity.startActivity(intent);
            return true;
        }
        if (isOffice(url)) {
            Intent intent = new Intent(activity, OfficeActivity.class);
            intent.putExtra("title", "文件预览");
            intent.putExtra("url", url);
            activity.startActivity(intent);
            return true;
        }
        if (url.endsWith(".txt")) {
            Intent intent = new Intent(activity, TxtViewActivity.class);
            intent.putExtra("url", url);
            activity.startActivity(intent);
            return true;
        }
        if (isImage(url)) {
            previewImage(url);
            return true;
        }
        view.loadUrl(url);
        return true;
    }

    private boolean isOffice(String url) {
        return url.endsWith(".doc")||url.endsWith(".docx")||url.endsWith(".xls")||url.endsWith(".xlsx")||url.endsWith(".ppt")|| url.endsWith(".pptx");
    }

    private boolean isImage(String url) {
        return url.endsWith(".jpg")||url.endsWith(".jpeg")||url.endsWith(".png")||url.endsWith(".gif")||url.endsWith(".bmp");
    }

    /**
     * 预览图片
     */
    private void previewImage(String url) {
        ArrayList<String> list = new ArrayList<>();
        list.add(url);
        PhotoPreview.builder()
                .setPhotos(list)
                .setCurrentItem(0)
                .setShowDeleteButton(false)
                .start(activity);
    }

    // 加载完之前隐藏 progress
    @Override
    public void onPageFinished(WebView view, String url) {
        try {
            //  html加载完成之后，调用js的方法
//            imgReset(view);
            activity.hideWaitDialog();
        } catch (Exception e) {
            // DO NOTHING
        }
        addImageClickListener(view);
    }

    // 载入页面开始的事件
    @Override
    public void onPageStarted(WebView view, String url, Bitmap favicon) {
        try {
            activity.showWaitDialog();
        } catch (Exception e) {
            // DO NOTHING
        }
        super.onPageStarted(view, url, favicon);
    }

    // 注入js函数监听
    protected void addImageClickListener(WebView webView) {
        // 这段js函数的功能就是，遍历所有的img几点，并添加onclick函数，函数的功能是在图片点击的时候调用本地java接口并传递url过去
        String imageloadJS = FileUtils.getFromAssets(activity, "imageload.js");
        if (!TextUtils.isEmpty(imageloadJS)) {
            webView.loadUrl(imageloadJS);
        }
    }

    private void imgReset(WebView webView) {
        webView.loadUrl("javascript:(function(){"
                + "var objs = document.getElementsByTagName('img'); "
                + "for(var i=0;i<objs.length;i++)  " + "{"
                + "var img = objs[i];   "
                + "    img.style.width = '100%';   "
                + "    img.style.height = 'auto';   "
                + "}" + "})()");
    }
}