package com.huaneng.zhdj.view;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.huaneng.zhdj.BaseActivity;
import com.huaneng.zhdj.R;
import com.huaneng.zhdj.utils.UIUtils;

import org.xutils.view.annotation.Event;
import org.xutils.view.annotation.ViewInject;
import org.xutils.x;

/**
 * 点赞View
 */
public class ApproveView extends LinearLayout implements ApproveTarget {

    @ViewInject(R.id.approveTv)
    protected TextView approveTv;

    protected BaseActivity ctx;
    protected String articleId;
    public Integer isUpvote;

    private BaseApprove BaseApprove;

    public void init(BaseActivity ctx, String articleId, BaseApprove BaseApprove) {
        this.ctx = ctx;
        this.articleId = articleId;
        this.BaseApprove = BaseApprove;
        BaseApprove.init(ctx, this);
        checkIsUpvote();
    }

    public ApproveView(Context context) {
        this(context, null);
    }

    public ApproveView(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public ApproveView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        this(context, attrs, defStyleAttr, 0);
    }

    public ApproveView(Context context, @Nullable AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        init(context);
    }

    private void init(Context context) {
        inflate(context, R.layout.layout_approve, this);
        x.view().inject(this);
    }

    // 点赞
    @Event(R.id.approveTv)
    private void approve(View view) {
        upvote();
    }

    // 点赞数量
    public void setApproveCount(Object count) {
        UIUtils.showNum(approveTv, count);
    }

    public void hasUpvoteAlert() {
                new AlertDialog.Builder(ctx)
                .setTitle("提示")
                .setMessage("您已点过赞，是否删除点赞?")
                .setPositiveButton("删除", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                        delUpvote();
                    }
                })
                .setNegativeButton("返回", null).show();
    }

    /**
     * 检查是否已点赞
     */
    public void checkIsUpvote() {
        BaseApprove.checkIsUpvote(articleId);
    }
    /**
     * 点赞
     */
    public void upvote() {
        BaseApprove.upvote(articleId, isUpvote);
    }
    /**
     * 删除点赞
     */
    public void delUpvote() {
        BaseApprove.delUpvote(this.articleId);
    }


    @Override
    public void checkSuccess(Integer isUpvote) {
        this.isUpvote = isUpvote;
    }

    @Override
    public void approveSuccess() {
        ctx.toast("点赞成功.");
        isUpvote = 1;
        int approveCount = Integer.valueOf(approveTv.getText().toString());
        setApproveCount(approveCount + 1);
    }

    @Override
    public void deleteSuccess() {
        ctx.toast("删除成功.");
        isUpvote = 0;
        int approveCount = Integer.valueOf(approveTv.getText().toString());
        setApproveCount(approveCount - 1);
    }
}
