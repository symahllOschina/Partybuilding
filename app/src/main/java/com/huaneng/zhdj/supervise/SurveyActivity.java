package com.huaneng.zhdj.supervise;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.huaneng.zhdj.BaseActivity;
import com.huaneng.zhdj.R;
import com.huaneng.zhdj.adapter.MyPagerAdapter;
import com.huaneng.zhdj.bean.Pager;
import com.huaneng.zhdj.bean.Survey;
import com.huaneng.zhdj.bean.SurveyItem;
import com.huaneng.zhdj.bean.SurveyItemOption;
import com.huaneng.zhdj.bean.SurveyItemPager;
import com.huaneng.zhdj.network.HTTP;
import com.huaneng.zhdj.network.Response;
import com.huaneng.zhdj.network.Subscriber;
import com.huaneng.zhdj.utils.IValidate;
import com.huaneng.zhdj.utils.MapParam;
import com.huaneng.zhdj.utils.UIUtils;
import com.huaneng.zhdj.view.MultiSurveyView;
import com.huaneng.zhdj.view.SingleSurveyView;
import com.huaneng.zhdj.view.SurveyTextareaView;

import org.xutils.view.annotation.ContentView;
import org.xutils.view.annotation.Event;
import org.xutils.view.annotation.ViewInject;

import java.util.ArrayList;
import java.util.List;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;

/**
 * 问卷调查
 */
@ContentView(R.layout.activity_survey)
public class SurveyActivity extends BaseActivity {

    @ViewInject(R.id.expiredDateTv)
    TextView expiredDateTv;
    @ViewInject(R.id.currentIndexTv)
    TextView currentIndexTv;
    @ViewInject(R.id.lastBtn)
    Button lastBtn;
    @ViewInject(R.id.submitBtn)
    Button submitBtn;
    @ViewInject(R.id.nextBtn)
    Button nextBtn;
    @ViewInject(R.id.viewPager)
    ViewPager viewPager;
    List<View> mViewList = new ArrayList<>();
    Pager mPager = new Pager();
    private int count;

    int index;
    Survey survey;
    List<SurveyItem> surveys;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        survey = getSerializableExtra("survey");
        setTitle(survey.title);
        UIUtils.showText(expiredDateTv, "有效期：", survey.end_time);
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
        getList();
    }

    private void backConfirm() {
        new AlertDialog.Builder(this)
                .setTitle("提示")
                .setMessage("确定返回吗?")
                .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                        finish();
                    }
                })
                .setNegativeButton("取消", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                }).show();
    }

    @Override
    public void onBackPressed() {
        if (index > 0) {
            backConfirm();
        } else {
            super.onBackPressed();
        }
    }

    // 上一题
    @Event(R.id.lastBtn)
    private void lastQuestion(View view) {
        if (index == 0) {
            toast("已经是第1题了.");
        } else {
            index--;
            index = (index < 0) ? 0 : index;
            viewPager.setCurrentItem(index, true);
        }
    }

    private void showCurrentIndex() {
        if (surveys != null) {
            currentIndexTv.setText(String.format("%d/%d 道", viewPager.getCurrentItem() + 1, surveys.size()));
        }
    }

    // 下一题
    @Event(R.id.nextBtn)
    private void nextQuestion(View view) {
        if (index >= surveys.size() - 1) {
            toast("已经是最后一题了.");
        } else {
            index++;
            viewPager.setCurrentItem(index, true);
        }
        buttonVisibleToggle();
    }

    private void buttonVisibleToggle() {
        showCurrentIndex();
        if ((index == surveys.size() - 1)) {
            submitBtn.setVisibility(View.VISIBLE);
            nextBtn.setVisibility(View.GONE);
        } else {
            submitBtn.setVisibility(View.GONE);
            nextBtn.setVisibility(View.VISIBLE);
        }
    }

    public void getList() {
        if (!checkNetwork()) {
            return;
        }
        // survey/read 是加了分页，survey/readlist 是所有的 --- 不对，survey/read是获取统计结果
        HTTP.service.get("survey/readlist", MapParam.me().p("page", mPager.nextPage()).p("size", mPager.size).p("id", survey.id).build())
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Subscriber(ctx) {

                    @Override
                    public void onSuccess(Response response) {
                        SurveyItemPager pager = response.getEntity(SurveyItemPager.class);
                        pager.decorate();
                        if (pager != null && pager.list != null && !pager.list.isEmpty()) {
                            surveys = pager.list;
                            initQuestion();
                        } else {
                            toast("没有查询到信息.");
                        }
                    }

                    @Override
                    public void onWrong(String msg) {
                        toast(msg);
                    }
                });
    }

    private void initQuestion() {
        int index = 1;
        for (SurveyItem survey : surveys) {
            if (!survey.valid()) {
                continue;
            }
            if (survey.isRadio()) {
                SingleSurveyView singleSurveyView = new SingleSurveyView(this);
                singleSurveyView.setData(index, survey);
                mViewList.add(singleSurveyView);
            } else if (survey.isCheckbox()) {
                MultiSurveyView multiSurveyView = new MultiSurveyView(this);
                multiSurveyView.setData(index, survey);
                mViewList.add(multiSurveyView);
            } else if (survey.isTextarea()) {
                SurveyTextareaView surveyTextareaView = new SurveyTextareaView(this);
                surveyTextareaView.setData(index, survey);
                mViewList.add(surveyTextareaView);
            } else {
                // 未知题型
                continue;
            }
            index++;
        }
        MyPagerAdapter pagerAdapter = new MyPagerAdapter(mViewList, new String[surveys.size()]);
        viewPager.setAdapter(pagerAdapter);
    }

    // 交卷
    @Event(R.id.submitBtn)
    private void submit(View view) {
        new AlertDialog.Builder(this)
                .setTitle("提示")
                .setMessage("确定提交吗?")
                .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                        doSubmit();
                    }
                })
                .setNegativeButton("取消", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                }).show();
    }

    // 交卷
    private void doSubmit() {
        if (!checkNetwork()) {
            return;
        }
//        List<String> answers = new ArrayList<>();
        MapParam mapParam = MapParam.me().p("con_id", survey.id);
        for (int i = 0; i < mViewList.size(); i++) {
            SurveyItem item = surveys.get(i);
            if (!item.valid()) {
                continue;
            }
            IValidate validate = (IValidate) mViewList.get(i);
            if (!validate.validate()) {
                toast(String.format("第%d题没有答", (i+1)));
                return;
            }
            SurveyItemOption surveyItemOption = null;
            if (item.isRadio()) {
                SingleSurveyView singleSurveyView = (SingleSurveyView)validate;
                surveyItemOption = singleSurveyView.getAnswer();
                if (surveyItemOption == null) {
                    continue;
                }
                mapParam.p("answer_id_" + surveyItemOption.que_id, surveyItemOption.id);
            } else if (item.isCheckbox()) {
                MultiSurveyView multiSurveyView = (MultiSurveyView)validate;
                List<SurveyItemOption> list = multiSurveyView.getAnswer();
                if (surveyItemOption == null) {
                    continue;
                }
                surveyItemOption = list.get(0);
                List<String> ids = new ArrayList<>();
                for (SurveyItemOption option: list) {
                    ids.add(option.id);
                }
                mapParam.p("answer_id_" + surveyItemOption.que_id, JSONArray.toJSONString(ids));
            } else if (item.isTextarea()) {
                SurveyTextareaView surveyTextareaView = (SurveyTextareaView)validate;
                if (TextUtils.isEmpty(surveyTextareaView.getAnswer())) {
                    continue;
                }
                mapParam.p("answer_id_" + surveyTextareaView.getQueId(), surveyTextareaView.getAnswer());
            } else {
                continue;
            }
        }
        String postdata = JSON.toJSONString(mapParam.build());
        postdata = postdata.replaceAll("\\\"\\[", "\\[").replaceAll("\\]\\\"", "\\]").replaceAll("\\\\", "");
        showWaitDialog("正在提交...");
        HTTP.service.post("survey/add", MapParam.me().p("postdata", postdata).build())
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Subscriber(ctx) {

                    @Override
                    public void onSuccess(Response response) {
                        toast("提交成功");
                        viewResult();
                        finish();
                    }

                    @Override
                    public void onWrong(String msg) {
                        toast(msg);
                        viewResult();
                        finish();
                    }
                });
    }

    /**
     * 查看结果
     */
    private void viewResult() {
        Intent intent = new Intent(this, SurveyRecordActivity.class);
        intent.putExtra("survey", survey);
        startActivity(intent);
    }
}
