package com.huaneng.zhdj.view;

import android.content.Context;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.widget.CheckBox;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.huaneng.zhdj.R;
import com.huaneng.zhdj.bean.SurveyItem;
import com.huaneng.zhdj.bean.SurveyItemOption;
import com.huaneng.zhdj.utils.IValidate;

import org.xutils.view.annotation.ViewInject;
import org.xutils.x;

import java.util.ArrayList;
import java.util.List;

/**
 * 多选题--调查问卷
 */
public class MultiSurveyView extends LinearLayout implements IValidate {

    @ViewInject(R.id.checkBoxGroup)
    private LinearLayout checkBoxGroup;
    @ViewInject(R.id.titleTv)
    private TextView titleTv;

    public MultiSurveyView(Context context) {
        this(context, null);
    }

    public MultiSurveyView(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public MultiSurveyView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        this(context, attrs, defStyleAttr, 0);
    }

    public MultiSurveyView(Context context, @Nullable AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        init(context);
    }

    private void init(Context context) {
        inflate(context, R.layout.view_multi_survey, this);
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
            CheckBox checkBox = new CheckBox(getContext());
            checkBox.setText(option.content);
            checkBox.setTag(option);
            checkBoxGroup.addView(checkBox, i);
        }
    }

    @Override
    public boolean validate() {
        int count = checkBoxGroup.getChildCount();
        if (count < 1) {
            return true;
        }
        return isRealValid();
    }

    private boolean isRealValid() {
        int count = checkBoxGroup.getChildCount();
        for (int i=0; i< count; i++) {
            CheckBox checkBox = (CheckBox)checkBoxGroup.getChildAt(i);
            if (checkBox.isChecked()) {
                return true;
            }
        }
        return false;
    }

    public List<SurveyItemOption> getAnswer() {
        if (isRealValid()) {
            List<SurveyItemOption> list = new ArrayList<>();
            int count = checkBoxGroup.getChildCount();
            for (int i=0; i< count; i++) {
                CheckBox checkBox = (CheckBox)checkBoxGroup.getChildAt(i);
                if (checkBox.isChecked()) {
                    SurveyItemOption answer = (SurveyItemOption)checkBox.getTag();
                    list.add(answer);
                }
            }
            return list;
        }
        return null;
    }

}
