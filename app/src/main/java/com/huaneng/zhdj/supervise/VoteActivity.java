package com.huaneng.zhdj.supervise;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.alibaba.fastjson.JSON;
import com.huaneng.zhdj.BaseActivity;
import com.huaneng.zhdj.R;
import com.huaneng.zhdj.bean.Vote;
import com.huaneng.zhdj.bean.VoteOption;
import com.huaneng.zhdj.network.HTTP;
import com.huaneng.zhdj.network.Response;
import com.huaneng.zhdj.network.Subscriber;
import com.huaneng.zhdj.utils.MapParam;
import com.huaneng.zhdj.utils.UIUtils;
import com.huaneng.zhdj.view.MultiVoteView;
import com.huaneng.zhdj.view.SingleVoteView;

import org.xutils.view.annotation.ContentView;
import org.xutils.view.annotation.ViewInject;

import java.util.ArrayList;
import java.util.List;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;

/**
 * 投票界面
 */
@ContentView(R.layout.activity_vote)
public class VoteActivity extends BaseActivity {

    @ViewInject(R.id.statusTv)
    TextView statusTv;
    @ViewInject(R.id.countTv)
    TextView countTv;
    @ViewInject(R.id.typeTv)
    TextView typeTv;
    @ViewInject(R.id.submitBtn)
    Button submitBtn;
    @ViewInject(R.id.singleVoteView)
    SingleVoteView singleVoteView;
    @ViewInject(R.id.multiVoteView)
    MultiVoteView multiVoteView;
    private boolean isSubmiting;
    Vote mVote;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mVote = getSerializableExtra("vote");
        setTitle(mVote.title);
        UIUtils.showText(statusTv, "状态：", mVote.getStatusDisplay());
        UIUtils.showText(countTv, mVote.total + "人已投票");
        UIUtils.showText(typeTv, mVote.getTypeDisplay());
        if (mVote.isFinished()) {
            submitBtn.setVisibility(View.GONE);
        }
        getDetail();
    }

    private void init(Vote vote) {
//        mVote.type = "1";
        mVote = vote;
        if (vote.isSingleMode()) {
            singleVoteView.setData(vote);
        } else {
            multiVoteView.setData(vote);
        }
    }

    public void submit(View view) {
        if (!checkNetwork() || isSubmiting) {
            return;
        }
        if (!validate()) {
            toast("请选择");
            return;
        }
        showWaitDialog("正在提交...");
        isSubmiting = true;
        HTTP.service.post("vote/add", MapParam.me().p("tid", mVote.id)
                .p("type", mVote.type)
                .p("vid", getAnswer()).build())
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Subscriber(ctx){

                    @Override
                    public void onSuccess(Response response) {
                        isSubmiting = false;
                        toast(response.message, "投票成功.");
                        Intent intent = new Intent(ctx, VoteResultActivity.class);
                        intent.putExtra("vote", mVote);
                        startActivity(intent);
                        finish();
                    }

                    @Override
                    public void onWrong(String msg) {
                        isSubmiting = false;
                        toast(msg, "投票失败.");
                    }
                });
    }

    private boolean validate() {
        if (mVote.isSingleMode()) {
            return singleVoteView.validate();
        } else {
            return multiVoteView.validate();
        }
    }

    private String getAnswer() {
        if (mVote.isSingleMode()) {
            return singleVoteView.getAnswer().id;
        } else {
            List<VoteOption> list = multiVoteView.getAnswer();
            List<String> answers = new ArrayList<>();
            for (VoteOption item: list) {
                answers.add(item.id);
            }
            String answer = JSON.toJSONString(answers);
            answer = answer.replaceAll("\\\"\\[", "\\[").replaceAll("\\]\\\"", "\\]").replaceAll("\\\\", "");
            return answer;
        }
    }

    public void getDetail() {
        if (!checkNetwork()) {
            return;
        }
        HTTP.service.get("vote/read", MapParam.me().p("id", mVote.id).build())
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Subscriber(ctx){

                    @Override
                    public void onSuccess(Response response) {
                        Vote detail = response.getEntity(Vote.class);
                        if (detail != null) {
                            init(detail);
                        } else {
                            toast("没有查询到信息.");
                        }
                    }

                    @Override
                    public void onError(Throwable e) {
                        toast("没有查询到信息.");
                    }

                    @Override
                    public void onFail(String msg) {
                        toast("没有查询到信息.");
                    }
                });
    }

}
