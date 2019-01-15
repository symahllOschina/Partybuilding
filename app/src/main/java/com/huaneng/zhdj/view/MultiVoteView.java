package com.huaneng.zhdj.view;

import android.content.Context;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.widget.CheckBox;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.huaneng.zhdj.R;
import com.huaneng.zhdj.bean.Vote;
import com.huaneng.zhdj.bean.VoteOption;

import org.xutils.view.annotation.ViewInject;
import org.xutils.x;

import java.util.ArrayList;
import java.util.List;

/**
 * 多选题--投票
 */
public class MultiVoteView extends LinearLayout {

    @ViewInject(R.id.checkBoxGroup)
    private LinearLayout checkBoxGroup;
    @ViewInject(R.id.titleTv)
    private TextView titleTv;

    public MultiVoteView(Context context) {
        this(context, null);
    }

    public MultiVoteView(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public MultiVoteView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        this(context, attrs, defStyleAttr, 0);
    }

    public MultiVoteView(Context context, @Nullable AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        init(context);
    }

    private void init(Context context) {
        inflate(context, R.layout.view_multi_vote, this);
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
            CheckBox checkBox = new CheckBox(getContext());
            checkBox.setText(answer.name);
            checkBox.setTag(answer);
            checkBoxGroup.addView(checkBox, i);
        }
    }

    public boolean validate() {
        int count = checkBoxGroup.getChildCount();
        for (int i=0; i< count; i++) {
            CheckBox checkBox = (CheckBox)checkBoxGroup.getChildAt(i);
            if (checkBox.isChecked()) {
                return true;
            }
        }
        return false;
    }

    public List<VoteOption> getAnswer() {
        if (validate()) {
            List<VoteOption> list = new ArrayList<>();
            int count = checkBoxGroup.getChildCount();
            for (int i=0; i< count; i++) {
                CheckBox checkBox = (CheckBox)checkBoxGroup.getChildAt(i);
                if (checkBox.isChecked()) {
                    VoteOption answer = (VoteOption)checkBox.getTag();
                    list.add(answer);
                }
            }
            return list;
        }
        return null;
    }

}
