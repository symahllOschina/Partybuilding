package com.huaneng.zhdj.study;

import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.webkit.WebView;

import com.huaneng.zhdj.BaseActivity;
import com.huaneng.zhdj.R;
import com.huaneng.zhdj.bean.CommentSuccessEvent;
import com.huaneng.zhdj.bean.News;
import com.huaneng.zhdj.bean.StudyNote;
import com.huaneng.zhdj.network.HTTP;
import com.huaneng.zhdj.network.Response;
import com.huaneng.zhdj.network.Subscriber;
import com.huaneng.zhdj.pioneers.CommentsObserver;
import com.huaneng.zhdj.pioneers.PioneersWorksPublishActivity;
import com.huaneng.zhdj.utils.MapParam;
import com.huaneng.zhdj.utils.WebViewUtils;
import com.huaneng.zhdj.view.CommentView;
import com.huaneng.zhdj.view.NewsInfoView;
import com.huaneng.zhdj.view.NineGridlayout;
import com.huaneng.zhdj.view.PioneersArticleRewardApproveView;
import com.huaneng.zhdj.view.PioneersComment;
import com.huaneng.zhdj.view.StudyNoteComment;
import com.huaneng.zhdj.view.StudyNoteRewardApproveView;

import org.greenrobot.eventbus.Subscribe;
import org.xutils.view.annotation.ContentView;
import org.xutils.view.annotation.ViewInject;

import java.util.ArrayList;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;

/**
 * 学习笔记详情
 */
@ContentView(R.layout.activity_study_note_detail)
public class StudyNoteDetailActivity extends BaseActivity implements CommentsObserver {

    @ViewInject(R.id.webView)
    private WebView webView;
    @ViewInject(R.id.newsInfoView)
    private NewsInfoView newsInfoView;
    @ViewInject(R.id.commentView)
    private CommentView commentView;
    @ViewInject(R.id.rewardApproveCountView)
    private StudyNoteRewardApproveView rewardApproveCountView;

    @ViewInject(R.id.nineGridlayout)
    NineGridlayout nineGridlayout;

    private StudyNote news;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        news = (StudyNote)getIntent().getSerializableExtra("news");
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
        HTTP.service.get("schoolnote/read", MapParam.me().p("id", news.id).build())
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Subscriber(ctx){

                    @Override
                    public void onSuccess(Response response) {
                        News detail = response.getEntity(News.class);
                        if (detail != null) {
                            newsInfoView.setTime(news.create_time);
                            newsInfoView.setAuthor(news.user_name);

                            commentView.init(ctx, detail.id, new StudyNoteComment("schoolnote/report"));
                            commentView.setCommentsObserver(StudyNoteDetailActivity.this);

                            rewardApproveCountView.init(ctx, detail.id);
                            rewardApproveCountView.setApproveCount(detail.upvote_count);
                            rewardApproveCountView.setVisibility(View.VISIBLE);
                            initImages(detail);
                            WebViewUtils.me(ctx, webView).html(detail.description);
                        } else {
                            toast("没有查询到信息.");
                        }
                    }

                    @Override
                    public void onWrong(String msg) {
                        toast("信息查询失败：" + msg);
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
    public void commentTotalCount(int count) {
        rewardApproveCountView.setCommentsCount("" + count);
    }
}
