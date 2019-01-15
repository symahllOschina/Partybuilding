package com.huaneng.zhdj.supervise;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import com.alibaba.fastjson.JSON;
import com.huaneng.zhdj.BaseActivity;
import com.huaneng.zhdj.Constants;
import com.huaneng.zhdj.R;
import com.huaneng.zhdj.bean.QuestionAnswer;
import com.huaneng.zhdj.bean.UserInfo;
import com.huaneng.zhdj.network.HTTP;
import com.huaneng.zhdj.network.Response;
import com.huaneng.zhdj.network.Subscriber;
import com.huaneng.zhdj.utils.MapParam;
import com.huaneng.zhdj.utils.SharedPreferencesUtils;
import com.huaneng.zhdj.utils.UIUtils;

import org.xutils.view.annotation.ContentView;
import org.xutils.view.annotation.ViewInject;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;

/**
 * 问题批复
 */
@ContentView(R.layout.activity_question_answer)
public class QuestionAnswerActivity extends BaseActivity {

    @ViewInject(R.id.nameTv)
    TextView nameTv;
    @ViewInject(R.id.organTv)
    TextView organTv;
    @ViewInject(R.id.timeTv)
    TextView timeTv;
    @ViewInject(R.id.questionContentTv)
    TextView questionContentTv;
    @ViewInject(R.id.answerTv)
    EditText answerTv;
    private boolean isSubmiting;
    QuestionAnswer questionAnswer;
    UserInfo userInfo;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        questionAnswer = getSerializableExtra("questionAnswer");
        setTitle(questionAnswer.title);
        UIUtils.showText(nameTv, "提问人：", questionAnswer.questions_name);
        UIUtils.showText(organTv, "求问对象：", questionAnswer.department);
        UIUtils.showText(questionContentTv, questionAnswer.questions);
        UIUtils.showText(timeTv, questionAnswer.questions_date);

        String json = SharedPreferencesUtils.create(ctx).get(Constants.SPR_USER_JSON);
        if (!TextUtils.isEmpty(json)) {
            userInfo = JSON.parseObject(json, UserInfo.class);
        }
    }

    // 已读搁置
    public void suspend(View v) {
        if (!checkNetwork() || isSubmiting) {
            return;
        }
        new AlertDialog.Builder(this)
                .setTitle("提示")
                .setMessage("确认搁置吗？")
                .setPositiveButton("确认", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                        suspend();
                    }
                })
                .setNegativeButton("取消", null).show();
    }

    private void suspend() {
        showWaitDialog("正在提交...");
        isSubmiting = true;
        HTTP.service.get("aq/readno", MapParam.me().p("id", questionAnswer.id).build())
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Subscriber(ctx){

                    @Override
                    public void onSuccess(Response response) {
                        isSubmiting = false;
                        toast("提交成功.");
                        finish();
                    }

                    @Override
                    public void onWrong(String msg) {
                        isSubmiting = false;
                        toast(msg, "提交失败.");
                    }
                });
    }

    // 弃置
    public void abandon(View v) {
        if (!checkNetwork() || isSubmiting) {
            return;
        }
        new AlertDialog.Builder(this)
                .setTitle("提示")
                .setMessage("确认弃置吗？")
                .setPositiveButton("确认", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                        abandon();
                    }
                })
                .setNegativeButton("取消", null).show();
    }

    private void abandon() {
        showWaitDialog("正在提交...");
        isSubmiting = true;
        HTTP.service.get("aq/readdel", MapParam.me().p("id", questionAnswer.id).build())
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Subscriber(ctx){

                    @Override
                    public void onSuccess(Response response) {
                        isSubmiting = false;
                        toast("提交成功.");
                        finish();
                    }

                    @Override
                    public void onWrong(String msg) {
                        isSubmiting = false;
                        toast(msg, "提交失败.");
                    }
                });
    }

    public void submit(View v) {
        if (!checkNetwork() || isSubmiting) {
            return;
        }
        String answers_name = null;
        String answers_department = null;
        if (userInfo != null) {
            answers_name = userInfo.username;
            answers_department = userInfo.company;
        }
        showWaitDialog("正在提交...");
        isSubmiting = true;
        HTTP.service.post("aq/asave", MapParam.me().p("id", questionAnswer.id)
                .p("answers", answerTv.getText().toString())
                .p("answers_name", answers_name)
                .p("answers_department", answers_department).build())
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Subscriber(ctx){

                    @Override
                    public void onSuccess(Response response) {
                        isSubmiting = false;
                        toast("提交成功.");
                        finish();
                    }

                    @Override
                    public void onWrong(String msg) {
                        isSubmiting = false;
                        toast(msg, "提交失败.");
                    }
                });
    }

    public void cancel(View v) {
        finish();
    }

}
