package com.huaneng.zhdj.supervise;

import android.os.Bundle;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.huaneng.zhdj.BaseActivity;
import com.huaneng.zhdj.R;
import com.huaneng.zhdj.bean.Vote;
import com.huaneng.zhdj.bean.VoteOption;
import com.huaneng.zhdj.network.HTTP;
import com.huaneng.zhdj.network.Response;
import com.huaneng.zhdj.network.Subscriber;
import com.huaneng.zhdj.utils.MapParam;

import org.xutils.view.annotation.ContentView;
import org.xutils.view.annotation.ViewInject;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;

/**
 * 投票结果修正
 */
@ContentView(R.layout.activity_vote_result)
public class VoteResultActivity extends BaseActivity {

    @ViewInject(R.id.statusTv)
    TextView statusTv;
    @ViewInject(R.id.countTv)
    TextView countTv;
    @ViewInject(R.id.typeTv)
    TextView typeTv;
    @ViewInject(R.id.optionsBox)
    LinearLayout optionsBox;

    Vote mVote;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mVote = getSerializableExtra("vote");
        setTitle(mVote.title);

        showText(statusTv, "状态：", mVote.getStatusDisplay());
        showText(typeTv, mVote.getTypeDisplay());
        getDetail();
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
                            init(mVote);
                        }
                    }

                    @Override
                    public void onWrong(String msg) {
                        toast("获取投票信息失败：" + msg);
                    }
                });
    }

    private void init(Vote vote) {
        showText(countTv, vote.total + "人已投票");
        if (vote.data != null && !vote.data.isEmpty()) {
            for (VoteOption option : vote.data) {
                final View view = getLayoutInflater().inflate(R.layout.vote_option_result, null);
                TextView titleTv = view.findViewById(R.id.titleTv);
                TextView ticketCountEt = view.findViewById(R.id.ticketCountEt);
                titleTv.setText(option.name);
                ticketCountEt.setText("" + option.count);
                optionsBox.addView(view);
            }
        }
    }
}
