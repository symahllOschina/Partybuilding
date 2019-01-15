package com.huaneng.zhdj.study;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.webkit.WebView;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.ListView;
import android.widget.TextView;

import com.huaneng.zhdj.BaseActivity;
import com.huaneng.zhdj.FileWebViewActivity;
import com.huaneng.zhdj.R;
import com.huaneng.zhdj.WebViewActivity;
import com.huaneng.zhdj.adapter.AttachmentArticleAdapter;
import com.huaneng.zhdj.adapter.AttachmentVideoAdapter;
import com.huaneng.zhdj.bean.Attachment;
import com.huaneng.zhdj.bean.News;
import com.huaneng.zhdj.bean.SpecialSubjectDetail;
import com.huaneng.zhdj.network.HTTP;
import com.huaneng.zhdj.network.Response;
import com.huaneng.zhdj.network.Subscriber;
import com.huaneng.zhdj.utils.MapParam;
import com.huaneng.zhdj.utils.WebViewUtils;

import org.xutils.view.annotation.ContentView;
import org.xutils.view.annotation.ViewInject;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;

/**
 * 党员学习-获取专题详情和附件列表
 */
@ContentView(R.layout.activity_special_subject_attachments)
public class SpecialSubjectAttachmentsActivity extends BaseActivity {

    @ViewInject(R.id.articleEmptyLabel)
    TextView articleEmptyLabel;
    @ViewInject(R.id.articleLv)
    ListView articleLv;

//    @ViewInject(R.id.fileEmptyLabel)
//    TextView fileEmptyLabel;
//    @ViewInject(R.id.fileLv)
//    ListView fileLv;

    @ViewInject(R.id.videoEmptyLabel)
    TextView videoEmptyLabel;
    @ViewInject(R.id.videoGv)
    GridView videoGv;

    @ViewInject(R.id.webView)
    WebView webView;

    private News news;
    SpecialSubjectDetail detail;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        news = (News)getIntent().getSerializableExtra("news");
        setTitle(news.title);
        WebViewUtils.me(this, webView).html(news.content);
        getList();
    }

    public void getList() {
        if (!checkNetwork()) {
            return;
        }
        HTTP.service.get("schoolspecial/read", MapParam.me().p("id", news.id).build())
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Subscriber(ctx){

                    @Override
                    public void onSuccess(Response response) {
                        detail = response.getEntity(SpecialSubjectDetail.class);
                        detail.classify();
                        if (detail.articleList != null && !detail.articleList.isEmpty()) {
                            articleEmptyLabel.setVisibility(View.GONE);
                            articleLv.setVisibility(View.VISIBLE);
                            AttachmentArticleAdapter articleAdapter = new AttachmentArticleAdapter(ctx, detail.article);
                            articleLv.setAdapter(articleAdapter);
                            articleLv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                                @Override
                                public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                                    displayHtml(detail.articleList.get(i));
                                }
                            });
                        } else {
                            articleEmptyLabel.setVisibility(View.VISIBLE);
                        }

//                        if (detail.fileList != null && !detail.fileList.isEmpty()) {
//                            fileEmptyLabel.setVisibility(View.GONE);
//                            fileLv.setVisibility(View.VISIBLE);
//                            AttachmentArticleAdapter articleAdapter = new AttachmentArticleAdapter(ctx, detail.fileList);
//                            fileLv.setAdapter(articleAdapter);
//                            fileLv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
//                                @Override
//                                public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
//                                    FileDisplay.displayAttachment(ctx, detail.article.get(i), "schoolspecial/readdownload");
//                                }
//                            });
//                        } else {
//                            fileEmptyLabel.setVisibility(View.VISIBLE);
//                        }

                        if (detail.video != null && !detail.video.isEmpty()) {
                            videoEmptyLabel.setVisibility(View.GONE);
                            videoGv.setVisibility(View.VISIBLE);
                            AttachmentVideoAdapter videoAdapter = new AttachmentVideoAdapter(ctx, detail.video);
                            videoGv.setAdapter(videoAdapter);
                            videoGv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                                @Override
                                public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
//                                    FileDisplay.displayAttachment(ctx, detail.video.get(i), "schoolspecial/readdownload");

                                    Intent intent = new Intent(ctx, FileWebViewActivity.class);
                                    intent.putExtra("attachment", detail.video.get(i));
                                    intent.putExtra("url", "schoolspecial/readdownload");
                                    startActivity(intent);
                                }
                            });
                        } else {
                            videoEmptyLabel.setVisibility(View.VISIBLE);
                        }
                    }

                    @Override
                    public void onWrong(String msg) {
                        toast(msg);
                    }
                });
    }

    private void displayHtml(Attachment attachment) {
        if (!ctx.checkNetwork()) {
            return;
        }
        HTTP.service.get("schoolspecial/readdownload", MapParam.me().p("id", attachment.id).build())
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Subscriber(ctx) {

                    @Override
                    public void onSuccess(Response response) {
                        News news = response.getEntity(News.class);
                        Intent intent = new Intent(ctx, WebViewActivity.class);
                        intent.putExtra("title", news.title);
                        String content = news.content;
                        if (!TextUtils.isEmpty(content) && content.length() > 100000) {
                            WebViewActivity.bigData = content;
                            intent.putExtra("isBigData", true);
                        } else {
                            intent.putExtra("html", content);
                        }
                        ctx.startActivity(intent);
                    }

                    @Override
                    public void onWrong(String msg) {
                        ctx.toast(msg);
                    }
                });
    }
}
