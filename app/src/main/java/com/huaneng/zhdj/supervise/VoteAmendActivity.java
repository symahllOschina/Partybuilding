package com.huaneng.zhdj.supervise;

import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.huaneng.zhdj.BaseActivity;
import com.huaneng.zhdj.R;
import com.huaneng.zhdj.bean.Vote;
import com.huaneng.zhdj.bean.VoteOption;

import org.xutils.view.annotation.ContentView;
import org.xutils.view.annotation.ViewInject;

/**
 * 投票结果修正
 * PS.后台做
 */
@Deprecated
@ContentView(R.layout.activity_vote_amend)
public class VoteAmendActivity extends BaseActivity {

    @ViewInject(R.id.statusTv)
    TextView statusTv;
    @ViewInject(R.id.countTv)
    TextView countTv;
    @ViewInject(R.id.typeTv)
    TextView typeTv;
    @ViewInject(R.id.optionsBox)
    LinearLayout optionsBox;

    Vote vote;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        vote = getSerializableExtra("mVote");
        setTitle(vote.title);

        for (VoteOption option : vote.data) {
            final View view = getLayoutInflater().inflate(R.layout.vote_option_amend, null);
            TextView titleTv = view.findViewById(R.id.titleTv);
            EditText ticketCountEt = view.findViewById(R.id.ticketCountEt);
            titleTv.setText(option.name);
            ticketCountEt.setText("" + option.count);
            optionsBox.addView(view);
        }
    }

    public void submit(View view) {

    }

}
