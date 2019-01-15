package com.huaneng.zhdj.study;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.text.Html;
import android.text.SpannableString;
import android.text.TextUtils;
import android.text.style.ForegroundColorSpan;
import android.text.style.RelativeSizeSpan;
import android.text.style.StyleSpan;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import com.alibaba.fastjson.JSON;
import com.github.mikephil.charting.animation.Easing;
import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.data.PieEntry;
import com.github.mikephil.charting.formatter.DefaultValueFormatter;
import com.github.mikephil.charting.formatter.PercentFormatter;
import com.github.mikephil.charting.utils.ColorTemplate;
import com.github.mikephil.charting.utils.MPPointF;
import com.huaneng.zhdj.BaseActivity;
import com.huaneng.zhdj.Constants;
import com.huaneng.zhdj.R;
import com.huaneng.zhdj.adapter.MyPagerAdapter;
import com.huaneng.zhdj.bean.Answer;
import com.huaneng.zhdj.bean.ExamResult;
import com.huaneng.zhdj.bean.TestQuestion;
import com.huaneng.zhdj.bean.UserInfo;
import com.huaneng.zhdj.utils.SharedPreferencesUtils;
import com.huaneng.zhdj.view.QuestionResultView;

import org.xutils.view.annotation.ContentView;
import org.xutils.view.annotation.Event;
import org.xutils.view.annotation.ViewInject;

import java.util.ArrayList;
import java.util.List;

/**
 * 考试结算
 */
@ContentView(R.layout.activity_exam_result)
public class ExamResultActivity extends BaseActivity {

    @ViewInject(R.id.nameTv)
    TextView nameTv;
    @ViewInject(R.id.timeTv)
    TextView timeTv;
    @ViewInject(R.id.scoreTv)
    TextView scoreTv;
    @ViewInject(R.id.resultTv)
    TextView resultTv;
    @ViewInject(R.id.wrongAnswerListLabel)
    TextView wrongAnswerListLabel;
    @ViewInject(R.id.wrongAnswerBox)
    FrameLayout wrongAnswerBox;

    @ViewInject(R.id.lastBtn)
    ImageView lastBtn;
    @ViewInject(R.id.nextBtn)
    ImageView nextBtn;
    @ViewInject(R.id.viewPager)
    ViewPager viewPager;

    @ViewInject(R.id.pieChart)
    PieChart mChart;

    List<View> mViewList = new ArrayList<>();
    int index;

    ExamResult result;
    TestQuestion question;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setTitle("考试结算");
        question = getSerializableExtra("question");
        result = getSerializableExtra("result");
        result.calcuCount();
        initPieChart();
        setPieData();
        if (result.wrongCount > 0) {
            wrongAnswerListLabel.setVisibility(View.VISIBLE);
            wrongAnswerBox.setVisibility(View.VISIBLE);
            initQuestion();
        }

        resultTv.setText(Html.fromHtml(String.format("答对 <font color='#FF0000'>%d</font> 道 答错 <font color='#FF0000'>%d</font> 道", result.correctCount, result.wrongCount)));
        showText(timeTv, "共用时：", result.time + " s");
        showText(scoreTv, "得分：", result.score + " 分");

        String json = SharedPreferencesUtils.create(ctx).get(Constants.SPR_USER_JSON);
        if (!TextUtils.isEmpty(json)) {
            UserInfo userInfo = JSON.parseObject(json, UserInfo.class);
            showText(nameTv, "姓名：", userInfo.username);
        }
        viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
                index = position;
                buttonVisibleToggle();
            }

            @Override
            public void onPageSelected(int position) {
                index = position;
                buttonVisibleToggle();
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });
    }

    private void initPieChart() {
        mChart.getDescription().setEnabled(false);
        mChart.setExtraOffsets(0, 0, 0, 0);
        Legend legend = mChart.getLegend();
        legend.setHorizontalAlignment(Legend.LegendHorizontalAlignment.CENTER);
        mChart.setDragDecelerationFrictionCoef(0.95f);
        mChart.setDrawHoleEnabled(false);
        mChart.setTransparentCircleColor(Color.WHITE);
        mChart.setTransparentCircleAlpha(110);

        mChart.setHoleRadius(58f);
        mChart.setTransparentCircleRadius(61f);
        mChart.setDrawCenterText(false);

        mChart.setRotationAngle(0);
        mChart.setRotationEnabled(true);
        mChart.setHighlightPerTapEnabled(true);
        mChart.animateY(1400, Easing.EasingOption.EaseInOutQuad);
    }

    private void setPieData() {
        ArrayList<PieEntry> entries = new ArrayList<PieEntry>();
        PieEntry pieEntry1 = new PieEntry(result.correctCount, "正确");
        PieEntry pieEntry2 = new PieEntry(result.wrongCount, "错误");
        entries.add(pieEntry1);
        entries.add(pieEntry2);
        int rate = (int)(result.correctCount /(float)result.data.size() * 100);
        PieDataSet dataSet = new PieDataSet(entries, "正确率" + rate + "%");

        dataSet.setSliceSpace(3f);
        dataSet.setIconsOffset(new MPPointF(0, 40));
        dataSet.setSelectionShift(5f);

        ArrayList<Integer> colors = new ArrayList<Integer>();
        colors.add(Color.rgb(122,212,129));
        colors.add(Color.rgb(219,105,88));
        dataSet.setColors(colors);

        PieData data = new PieData(dataSet);
        data.setValueFormatter(new DefaultValueFormatter(0));
        data.setValueTextSize(12f);
        data.setValueTextColor(Color.WHITE);
        mChart.setData(data);
        mChart.highlightValues(null);
        mChart.invalidate();
    }

    public void again(View view) {
        Intent intent = new Intent(ctx, ExamActivity.class);
        intent.putExtra("question", question);
        startActivity(intent);
        finish();
    }

    public void cancel(View view) {
        finish();
    }

    // 上一题
    @Event(R.id.lastBtn)
    private void lastQuestion(View view) {
        if (index == 0) {
            toast("已经是第1题了.");
        } else {
            index--;
            index = (index < 0)?0:index;
            viewPager.setCurrentItem(index, true);
        }
        buttonVisibleToggle();
    }

    // 下一题
    @Event(R.id.nextBtn)
    private void nextQuestion(View view) {
        if (index >= result.data.size() - 1) {
            toast("已经是最后一题了.");
        } else {
            index++;
            viewPager.setCurrentItem(index, true);
        }
        buttonVisibleToggle();
    }

    private void buttonVisibleToggle() {
//        if (index == 0) {
//            lastBtn.setVisibility(View.GONE);
//        } else {
//            lastBtn.setVisibility(View.VISIBLE);
//        }
//
//        if ((index == result.data.size()-1)) {
//            nextBtn.setVisibility(View.GONE);
//        } else {
//            nextBtn.setVisibility(View.VISIBLE);
//        }
    }

    private void initQuestion() {
        int index = 1;
        for (Answer answer: result.data) {
            if (!answer.isCorrect()) {
                QuestionResultView questionResultView = new QuestionResultView(this);
                questionResultView.setData(index, answer);
                mViewList.add(questionResultView);
            }
            index++;
        }
        MyPagerAdapter pagerAdapter = new MyPagerAdapter(mViewList, new String[result.data.size()]);
        viewPager.setAdapter(pagerAdapter);
    }
}
