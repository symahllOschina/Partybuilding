package com.huaneng.zhdj.view;

import android.content.Context;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;

import com.huaneng.zhdj.R;
import com.huaneng.zhdj.bean.Vote;
import com.huaneng.zhdj.bean.VoteOption;

import org.xutils.view.annotation.ViewInject;
import org.xutils.x;

/**
 * 单选题--投票
 */
public class SingleVoteView extends LinearLayout {

    @ViewInject(R.id.titleTv)
    private TextView titleTv;
    @ViewInject(R.id.radioGroup)
    private RadioGroup radioGroup;

    public SingleVoteView(Context context) {
        this(context, null);
    }

    public SingleVoteView(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public SingleVoteView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        this(context, attrs, defStyleAttr, 0);
    }

    public SingleVoteView(Context context, @Nullable AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        init(context);
    }

    private void init(Context context) {
        inflate(context, R.layout.view_single_survey, this);
        x.view().inject(this);
    }

    public void setData(Vote question) {
        this.setVisibility(VISIBLE);
        titleTv.setText(question.title);
        if (question.data != null && !question.data.isEmpty()) {
            int index = 0;
            for (VoteOption option: question.data) {
                addOption(option, index);
                index++;
            }
        }
    }

    private void addOption(VoteOption answer, int i) {
        if (!TextUtils.isEmpty(answer.name)) {
            RadioButton radioButton = new RadioButton(getContext());
            radioButton.setText(answer.name);
            radioButton.setTag(answer);
            radioGroup.addView(radioButton, i);
        }
    }

    public boolean validate() {
        int checkedId = radioGroup.getCheckedRadioButtonId();
        return checkedId != -1;
    }

    public VoteOption getAnswer() {
        if (validate()) {
            int checkedId = radioGroup.getCheckedRadioButtonId();
            RadioButton button = (RadioButton)findViewById(checkedId);
            VoteOption answer = (VoteOption)button.getTag();
            return answer;
        }
        return null;
    }

}
