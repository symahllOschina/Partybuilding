package com.huaneng.zhdj.pioneers;

import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.webkit.WebView;

import com.huaneng.zhdj.BaseActivity;
import com.huaneng.zhdj.R;
import com.huaneng.zhdj.bean.CommentSuccessEvent;
import com.huaneng.zhdj.bean.News;
import com.huaneng.zhdj.network.HTTP;
import com.huaneng.zhdj.network.Response;
import com.huaneng.zhdj.network.Subscriber;
import com.huaneng.zhdj.utils.MapParam;
import com.huaneng.zhdj.utils.WebViewUtils;
import com.huaneng.zhdj.view.CommentView;
import com.huaneng.zhdj.view.NewsInfoView;
import com.huaneng.zhdj.view.NineGridlayout;
import com.huaneng.zhdj.view.PioneersArticleRewardApproveView;
import com.huaneng.zhdj.view.PioneersComment;

import org.greenrobot.eventbus.Subscribe;
import org.xutils.view.annotation.ContentView;
import org.xutils.view.annotation.ViewInject;

import java.util.ArrayList;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;

/**
 * 时代先锋 -- 作品详情
 */
@ContentView(R.layout.activity_pioneers_works_detail)
public class PioneersWorksDetailActivity extends BaseActivity implements CommentsObserver {

    @ViewInject(R.id.webView)
    private WebView webView;
    @ViewInject(R.id.newsInfoView)
    private NewsInfoView newsInfoView;
    @ViewInject(R.id.commentView)
    private CommentView commentView;
    @ViewInject(R.id.rewardApproveCountView)
    private PioneersArticleRewardApproveView rewardApproveCountView;

    @ViewInject(R.id.nineGridlayout)
    NineGridlayout nineGridlayout;

    private News news;
    private String url;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        url = getIntent().getStringExtra("url");
        news = (News)getIntent().getSerializableExtra("news");
        newsInfoView.setBgColor(R.color.white);
        setTitle(news.title);
        getNews();
    }

    @Override
    protected boolean isEnableEventBus() {
        return true;
    }

    @Subscribe
    public void onCommentSuccess(CommentSuccessEvent event) {
        commentView.refreshComments();
    }

    public void getNews() {
        if (!checkNetwork()) {
            return;
        }
        HTTP.service.get(url, MapParam.me().p("id", news.id).build())
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Subscriber(ctx){

                    @Override
                    public void onSuccess(Response response) {
                        News detail = response.getEntity(News.class);
                        if (detail != null) {
                            newsInfoView.setTime(detail.create_time);
                            newsInfoView.setAuthor(detail.username);

                            commentView.init(ctx, detail.id, new PioneersComment("comment/report"));
                            commentView.setCommentsObserver(PioneersWorksDetailActivity.this);

                            rewardApproveCountView.init(ctx, detail.id);
                            rewardApproveCountView.setApproveCount(detail.upvote_count);
                            rewardApproveCountView.setVisibility(View.VISIBLE);
                            initImages(detail);
                            WebViewUtils.me(ctx, webView).html(detail.description);
                        } else {
                            toast("没有查询到信息：" + response.message);
                        }
                    }

                    @Override
                    public void onWrong(String msg) {
                        toast("查询失败：" + msg);
                    }
                });
    }

    // 添加图片的按钮
    private void initImages(News detail) {
        ArrayList<String> urls = (ArrayList<String>)detail.getImageUrls();
        if (urls != null && !urls.isEmpty()) {
            nineGridlayout.init(this, true);
            nineGridlayout.setImagesData(urls);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_works, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_publish:
                publish();
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    private void publish() {
        activity(PioneersWorksPublishActivity.class);
    }

    @Override
    public void commentTotalCount(int count) {
        rewardApproveCountView.setCommentsCount("" + count);
    }
}
