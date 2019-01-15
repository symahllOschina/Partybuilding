package com.huaneng.zhdj.view;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.TextView;

import com.huaneng.zhdj.App;
import com.huaneng.zhdj.BaseActivity;
import com.huaneng.zhdj.R;
import com.huaneng.zhdj.bean.CommentSuccessEvent;
import com.huaneng.zhdj.network.HTTP;
import com.huaneng.zhdj.network.Response;
import com.huaneng.zhdj.network.Subscriber;
import com.huaneng.zhdj.utils.MapParam;
import com.huaneng.zhdj.utils.UIUtils;

import org.greenrobot.eventbus.EventBus;
import org.xutils.view.annotation.Event;
import org.xutils.view.annotation.ViewInject;
import org.xutils.x;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;

/**
 * 学习笔记：作品评论、打赏、点赞View
 */
public class StudyNoteRewardApproveView extends LinearLayout {

    @ViewInject(R.id.commentsCount)
    protected TextView commentsCount;
    @ViewInject(R.id.approveCountView)
    private ApproveView approveView;
    private BaseActivity ctx;
    private String articleId;

    // 打赏的积分
    int num = -1;

    public void init(BaseActivity ctx, String articleId) {
        this.ctx = ctx;
        this.articleId = articleId;
        approveView.init(ctx, articleId, new StudyNoteApprove());
    }

    public StudyNoteRewardApproveView(Context context) {
        super(context);
    }

    public StudyNoteRewardApproveView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    public StudyNoteRewardApproveView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    public StudyNoteRewardApproveView(Context context, @Nullable AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
    }

    private void init(Context context) {
        inflate(context, R.layout.layout_reward_comments_approve, this);
        x.view().inject(this);
    }

    // 评论
    @Event(R.id.commentTv)
    private void onCommentBtnClick(View view) {
        final PopupWindow window = new PopupWindow(this);
        window.setWidth(LayoutParams.MATCH_PARENT);
        window.setHeight(LayoutParams.WRAP_CONTENT);
        window.setBackgroundDrawable(getResources().getDrawable(R.color.white));
        View contentView  = LayoutInflater.from(ctx).inflate(R.layout.layout_comment_publish, null);
        contentView.findViewById(R.id.cancelBtn).setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                window.dismiss();
            }
        });
        final EditText contentTv = contentView.findViewById(R.id.contentTv);
        contentView.findViewById(R.id.submitBtn).setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                String content = contentTv.getText().toString();
                if (TextUtils.isEmpty(content)) {
                    App.toast("请输入评论的内容.");
                    return;
                }
                window.dismiss();
                publishComment(content);
            }
        });
        window.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE);
        window.setFocusable(true);
        window.setContentView(contentView);
        window.setOutsideTouchable(true);
        window.showAtLocation(this, Gravity.LEFT | Gravity.BOTTOM, 0, -(this.getHeight() + window.getHeight()));
    }

    // 发表评论
    private void publishComment(String content) {
        if (!ctx.checkNetwork()) {
            return;
        }
        HTTP.service.post("schoolnote/commentsave", MapParam.me().p("note_id", articleId).p("to_user_id", "0").p("content", content).build())
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Subscriber(ctx){

                    @Override
                    public void onSuccess(Response response) {
                        EventBus.getDefault().post(new CommentSuccessEvent());
                        ctx.toast("评论成功，审核通过后可查看");
                    }

                    @Override
                    public void onFail(String msg) {
                        ctx.toast(msg);
                    }
                });
    }

    // 打赏
    @Event(R.id.rewardTv)
    private void reward(View view) {
        new AlertDialog.Builder(ctx)
                .setTitle("打赏")
                .setSingleChoiceItems(new String[]{"1分", "2分", "3分"}, num, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        num = which;
                    }
                })
                .setPositiveButton("提交", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                        reward();
                    }
                })
                .setNegativeButton("取消",null)
                .show();
    }

    private void reward() {
        if (!ctx.checkNetwork()) {
            return;
        }
        if (num < 0) {
            App.toast("请选择要打赏的积分.");
            return;
        }
        HTTP.service.post("schoolnote/integral", MapParam.me().p("id", articleId).p("num", (num + 1)).build())
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Subscriber(ctx){

                    @Override
                    public void onSuccess(Response response) {
                        ctx.toast("打赏成功");
                    }

                    @Override
                    public void onWrong(String msg) {
                        ctx.toast("打赏失败：" + msg);
                    }
                });
    }

    // 点击量
    public void setCommentsCount(Object count) {
        UIUtils.showText(commentsCount, count);
    }

    // 点赞数量
    public void setApproveCount(Object count) {
        approveView.setApproveCount(count);
    }
}
