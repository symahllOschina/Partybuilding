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
import com.huaneng.zhdj.bean.SurveyItem;
import com.huaneng.zhdj.bean.SurveyItemOption;
import com.huaneng.zhdj.bean.VoteOption;
import com.huaneng.zhdj.utils.IValidate;

import org.xutils.view.annotation.ViewInject;
import org.xutils.x;

/**
 * 单选题--调查问卷
 */
public class SingleSurveyView extends LinearLayout implements IValidate {

    @ViewInject(R.id.titleTv)
    private TextView titleTv;
    @ViewInject(R.id.radioGroup)
    private RadioGroup radioGroup;

    public SingleSurveyView(Context context) {
        this(context, null);
    }

    public SingleSurveyView(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public SingleSurveyView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        this(context, attrs, defStyleAttr, 0);
    }

    public SingleSurveyView(Context context, @Nullable AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        init(context);
    }

    private void init(Context context) {
        inflate(context, R.layout.view_single_vote, this);
        x.view().inject(this);
    }

    public void setData(int i, SurveyItem question) {
        this.setVisibility(VISIBLE);
        titleTv.setText(i + "、" + question.title);
        if (question.data != null && !question.data.isEmpty()) {
            int index = 0;
            for (SurveyItemOption option: question.data) {
                addOption(option, index);
                index++;
            }
        }
    }

    private void addOption(SurveyItemOption option, int i) {
        if (!TextUtils.isEmpty(option.content)) {
            RadioButton radioButton = new RadioButton(getContext());
            radioButton.setText(option.content);
            radioButton.setTag(option);
            radioGroup.addView(radioButton, i);
        }
    }

    @Override
    public boolean validate() {
        if (radioGroup.getChildCount() < 1) {
            return true;
        }
        return isRealValid();
    }

    private boolean isRealValid() {
        int checkedId = radioGroup.getCheckedRadioButtonId();
        return checkedId != -1;
    }

    public SurveyItemOption getAnswer() {
        if (isRealValid()) {
            int checkedId = radioGroup.getCheckedRadioButtonId();
            RadioButton button = (RadioButton)findViewById(checkedId);
            SurveyItemOption answer = (SurveyItemOption)button.getTag();
            return answer;
        }
        return null;
    }

}
