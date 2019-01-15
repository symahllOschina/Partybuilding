package com.huaneng.zhdj.view;

import android.content.Context;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.huaneng.zhdj.R;
import com.huaneng.zhdj.bean.SurveyItem;
import com.huaneng.zhdj.utils.IValidate;

import org.xutils.view.annotation.ViewInject;
import org.xutils.x;

/**
 * 简答题--调查问卷
 */
public class SurveyTextareaView extends LinearLayout implements IValidate {

    @ViewInject(R.id.titleTv)
    private TextView titleTv;
    @ViewInject(R.id.contentTv)
    EditText contentTv;
    public SurveyItem surveyItem;

    public SurveyTextareaView(Context context) {
        this(context, null);
    }

    public SurveyTextareaView(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public SurveyTextareaView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        this(context, attrs, defStyleAttr, 0);
    }

    public SurveyTextareaView(Context context, @Nullable AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        init(context);
    }

    private void init(Context context) {
        inflate(context, R.layout.view_survey_extarea, this);
        x.view().inject(this);
    }

    public void setData(int i, SurveyItem item) {
        this.surveyItem = item;
        this.setVisibility(VISIBLE);
        titleTv.setText(i + "、" + item.title);
        contentTv.setTag(item);
    }

    @Override
    public boolean validate() {
        if (TextUtils.isEmpty(titleTv.getText().toString().trim())) {
            return true;
        }
        String content = contentTv.getText().toString().trim();
        return !TextUtils.isEmpty(content);
    }

    public String getQueId() {
        return surveyItem.id;
    }

    public String getAnswer() {
        String content = contentTv.getText().toString().trim();
        return content;
    }
}
