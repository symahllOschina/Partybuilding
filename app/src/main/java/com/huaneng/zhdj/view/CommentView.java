package com.huaneng.zhdj.view;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.ListView;

import com.huaneng.zhdj.BaseActivity;
import com.huaneng.zhdj.R;
import com.huaneng.zhdj.adapter.CommentAdapter;
import com.huaneng.zhdj.bean.Comment;
import com.huaneng.zhdj.bean.Pager;
import com.huaneng.zhdj.pioneers.CommentsObserver;
import com.huaneng.zhdj.utils.PagerHandler;
import com.scwang.smartrefresh.layout.api.RefreshLayout;
import com.scwang.smartrefresh.layout.listener.OnLoadMoreListener;

import org.xutils.view.annotation.ViewInject;
import org.xutils.x;

import java.util.ArrayList;
import java.util.List;

/**
 * 评论
 */
public class CommentView extends LinearLayout implements CommentTarget, OnLoadMoreListener {

    @ViewInject(R.id.refreshLayout)
    RefreshLayout refreshLayout;
    @ViewInject(R.id.listView)
    ListView mListView;
    @ViewInject(R.id.emptyView)
    EmptyView emptyView;

    List<Comment> mList = new ArrayList<Comment>();
    CommentAdapter adapter;

    protected BaseActivity ctx;
    protected String mArticleId;
    private BaseComment mComment;
    Pager mPager = new Pager();
    protected PagerHandler pagerHandler = new PagerHandler(mPager);
    private CommentsObserver commentsObserver;

    public void setCommentsObserver(CommentsObserver commentsObserver) {
        this.commentsObserver = commentsObserver;
    }

    public void init(BaseActivity ctx, String articleId, BaseComment comment) {
        this.ctx = ctx;
        this.mArticleId = articleId;
        this.mComment = comment;
        comment.init(ctx, this, mPager);
        adapter = new CommentAdapter(ctx, mList, comment.reportUrl);
        mListView.setAdapter(adapter);
        emptyView.setRetryListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mComment.getComments(mArticleId);
            }
        });

        pagerHandler.setRefreshLayout(refreshLayout);
        refreshLayout.setEnableRefresh(false);
        refreshLayout.setOnLoadMoreListener(this);
        refreshLayout.setEnableAutoLoadMore(false);
        if (emptyView != null) {
            pagerHandler.emptyView = emptyView;
        }
        pagerHandler.listView = mListView;
        pagerHandler.adapter = adapter;
        pagerHandler.dataSource = mList;

        comment.getComments(articleId);
    }

    public void refreshComments() {
        mPager.reset();
        mComment.getComments(mArticleId);
    }

    public CommentView(Context context) {
        this(context, null);
    }

    public CommentView(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public CommentView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        this(context, attrs, defStyleAttr, 0);
    }

    public CommentView(Context context, @Nullable AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        init(context);
    }

    private void init(Context context) {
        inflate(context, R.layout.layout_comment, this);
        x.view().inject(this);
    }

    @Override
    public void getCommentsSuccess(List<Comment> list, int total) {
        pagerHandler.requestSuccess(list, total);
        if (commentsObserver != null) {
            commentsObserver.commentTotalCount(total);
        }
    }

    @Override
    public void getCommentsFail() {
        pagerHandler.requestFail();
    }

    @Override
    public void onLoadMore(@NonNull RefreshLayout refreshLayout) {
        mComment.getComments(mArticleId);
    }
}
