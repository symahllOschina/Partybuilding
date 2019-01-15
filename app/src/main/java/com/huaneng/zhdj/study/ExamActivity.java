package com.huaneng.zhdj.study;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.SystemClock;
import android.support.v4.view.ViewPager;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.Chronometer;
import android.widget.TextView;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.huaneng.zhdj.BaseActivity;
import com.huaneng.zhdj.Constants;
import com.huaneng.zhdj.R;
import com.huaneng.zhdj.adapter.MyPagerAdapter;
import com.huaneng.zhdj.bean.ExamResult;
import com.huaneng.zhdj.bean.Question;
import com.huaneng.zhdj.bean.QuestionPager;
import com.huaneng.zhdj.bean.TestQuestion;
import com.huaneng.zhdj.bean.UserInfo;
import com.huaneng.zhdj.network.HTTP;
import com.huaneng.zhdj.network.Response;
import com.huaneng.zhdj.network.Subscriber;
import com.huaneng.zhdj.utils.MapParam;
import com.huaneng.zhdj.utils.SharedPreferencesUtils;
import com.huaneng.zhdj.view.SingleQuestionView;
import com.orhanobut.logger.Logger;

import org.xutils.view.annotation.ContentView;
import org.xutils.view.annotation.Event;
import org.xutils.view.annotation.ViewInject;

import java.util.ArrayList;
import java.util.List;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;

/**
 * 考试
 */
@ContentView(R.layout.activity_exam)
public class ExamActivity extends BaseActivity {

    @ViewInject(R.id.nameTv)
    TextView nameTv;
    @ViewInject(R.id.chronometer)
    Chronometer chronometer;
    @ViewInject(R.id.timeTv)
    TextView timeTv;
    @ViewInject(R.id.lastBtn)
    Button lastBtn;
    @ViewInject(R.id.submitBtn)
    Button submitBtn;
    @ViewInject(R.id.nextBtn)
    Button nextBtn;
    @ViewInject(R.id.viewPager)
    ViewPager viewPager;
    List<View> mViewList = new ArrayList<>();

    private int count;
    private static int MAX_TIME;

    TestQuestion question;
    List<Question> questions;
    int index;
    // 强制交卷
    private boolean isForceSubmit;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        question = getSerializableExtra("question");
        MAX_TIME = question.getExamTimeSecond();
        setTitle(question.title);

        String json = SharedPreferencesUtils.create(ctx).get(Constants.SPR_USER_JSON);
        if (!TextUtils.isEmpty(json)) {
            UserInfo userInfo = JSON.parseObject(json, UserInfo.class);
            showText(nameTv, "姓名：", userInfo.username);
        }

        chronometer.setBase(SystemClock.elapsedRealtime());
        chronometer.start();
        chronometer.setOnChronometerTickListener(new Chronometer.OnChronometerTickListener() {

            @Override
            public void onChronometerTick(Chronometer chronometer) {
                count++;
                chronometer.setText(count + "/" + MAX_TIME + " s");
                if (count >= MAX_TIME) {
                    chronometer.stop();
                    isForceSubmit = true;
                    lastBtn.setEnabled(false);
                    submitBtn.setEnabled(false);
                    nextBtn.setVisibility(View.GONE);
                    nextBtn.setEnabled(false);

                    new AlertDialog.Builder(ctx)
                            .setTitle("提示")
                            .setMessage("考试时间到，将强制交卷.")
                            .setCancelable(false)
                            .setPositiveButton("确认", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    dialog.dismiss();
                                    doSubmit();
                                }
                            }).show();
                } else {
//                    chronometer.setText(count + "/" + MAX_TIME + " s");
                }
            }
        });
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

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_give_up, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_giveup:
                giveup();
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    private void giveup() {
        new AlertDialog.Builder(this)
        .setTitle("提示")
        .setMessage("确定放弃考试?")
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
            giveup();
        }else {
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
            index = (index < 0)?0:index;
            viewPager.setCurrentItem(index, true);
        }
    }

    // 下一题
    @Event(R.id.nextBtn)
    private void nextQuestion(View view) {
        if (index >= questions.size() - 1) {
            toast("已经是最后一题了.");
        } else {
            index++;
            viewPager.setCurrentItem(index, true);
        }
        buttonVisibleToggle();
    }

    private void buttonVisibleToggle() {
        if ((index == questions.size()-1)) {
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
        HTTP.service.get("schoolexam/read", MapParam.me().p("size", 1000).p("id", question.id).build())
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Subscriber(ctx){

                    @Override
                    public void onSuccess(Response response) {
                        QuestionPager pager = response.getEntity(QuestionPager.class);
                        if (pager != null && pager.list != null && !pager.list.isEmpty()) {
                            questions = pager.list;
                            initQuestion();
                        } else {
                            toast("没有查询到试题信息.");
                        }
                    }
                });
    }

    private void initQuestion() {
        int index = 1;
        for (Question question: questions) {
            SingleQuestionView singleQuestionView = new SingleQuestionView(this);
            singleQuestionView.setData(index, question);
            mViewList.add(singleQuestionView);
            index++;
        }
        MyPagerAdapter pagerAdapter = new MyPagerAdapter(mViewList, new String[questions.size()]);
        viewPager.setAdapter(pagerAdapter);
    }

    // 交卷
    @Event(R.id.submitBtn)
    private void submit(View view) {
        new AlertDialog.Builder(this)
                .setTitle("提示")
                .setMessage("确定交卷吗?")
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
        JSONObject jsonObject = new JSONObject();
        for (int i=1;i<=mViewList.size();i++) {
            SingleQuestionView singleQuestionView = (SingleQuestionView)mViewList.get(i-1);
            String answer = singleQuestionView.getAnswer();
            if (TextUtils.isEmpty(answer)) {
                if (isForceSubmit) {
                    jsonObject.put("" + i, "");
                } else {
                    toast(String.format("第%d题没有答", i));
                    return;
                }
            } else {
                jsonObject.put("" + i, answer);
            }
        }
        showWaitDialog("正在提交...");
        HTTP.service.post("schoolexam/save", MapParam.me().p("questionid", question.id).p("answer", jsonObject.toJSONString()).build())
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Subscriber(ctx){

                    @Override
                    public void onSuccess(Response response) {
                        ExamResult result = response.getEntity(ExamResult.class);
                        result.time = count;
                        Intent intent = new Intent(ctx, ExamResultActivity.class);
                        intent.putExtra("result", result);
                        intent.putExtra("question", question);
                        startActivity(intent);
                        finish();
                    }

                    @Override
                    public void onFail(String msg) {
                        snackError("交卷失败：" + msg);
                        super.onFail(msg);
                    }
                });
    }

    private void displayScore(String score) {
        submitBtn.setEnabled(false);
        new AlertDialog.Builder(this)
                .setTitle("交卷成功")
                .setMessage("得分：" + score)
                .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                        finish();
                    }
                }).setCancelable(false).show();
    }
}
