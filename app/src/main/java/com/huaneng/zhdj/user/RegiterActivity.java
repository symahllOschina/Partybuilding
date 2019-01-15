package com.huaneng.zhdj.user;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.huaneng.zhdj.BaseActivity;
import com.huaneng.zhdj.R;
import com.huaneng.zhdj.bean.UserUpdateSuccessEvent;
import com.huaneng.zhdj.network.HTTP;
import com.huaneng.zhdj.network.Response;
import com.huaneng.zhdj.network.Subscriber;
import com.huaneng.zhdj.utils.MapParam;
import com.huaneng.zhdj.utils.UIUtils;

import org.greenrobot.eventbus.EventBus;
import org.xutils.view.annotation.ContentView;
import org.xutils.view.annotation.Event;
import org.xutils.view.annotation.ViewInject;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;

/**
 * 注册帐号
 */
@ContentView(R.layout.activity_regiter)
public class RegiterActivity extends BaseActivity {

    @ViewInject(R.id.nameEt)
    EditText nameEt;
    @ViewInject(R.id.idEt)
    EditText idEt;
    @ViewInject(R.id.phoneEt)
    EditText phoneEt;

    @ViewInject(R.id.newPasswdEt)
    EditText newPasswdEt;
    @ViewInject(R.id.newPasswdConfirmEt)
    EditText newPasswdConfirmEt;

    @ViewInject(R.id.secretQuestionTv)
    TextView secretQuestionTv;
    @ViewInject(R.id.secretAnswerEt)
    EditText secretAnswerEt;
    String[] secretQuestions;
    String[] secretQuestionIds;
    String questionid;
    int checkedIndex = -1;

    private boolean isSubmiting;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setTitle("注册账号");
        getSecretQuestion();
    }

    private void getSecretQuestion() {
        if (!checkNetwork()) {
            return;
        }
        HTTP.service.get("tool/question", MapParam.me().p("size", "50").build())
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Subscriber(ctx){

                    @Override
                    public void onSuccess(Response response) {
                        JSONObject jsonObject = JSON.parseObject(response.getDateString());
                        int size = jsonObject.entrySet().size();
                        secretQuestions = new String[size];
                        secretQuestionIds = new String[size];
                        int i = 0;
                        for (Map.Entry<String, Object> item : jsonObject.entrySet() ) {
                            secretQuestionIds[i] = item.getKey();
                            secretQuestions[i] = item.getValue().toString();
                            i++;
                        }
                    }

                    @Override
                    public void onWrong(String msg) {
                        toast("获取密保问题失败：" + msg);
                    }
                });
    }

    @Event(R.id.secretQuestionTv)
    private void secretQuestion(View v) {
        if (secretQuestions == null || secretQuestions.length < 1) {
            return;
        }
        new AlertDialog.Builder(this)
                .setTitle("请选择密保问题：")
                .setSingleChoiceItems(secretQuestions, checkedIndex, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                        checkedIndex = which;
                        secretQuestionTv.setText(secretQuestions[checkedIndex]);
                        questionid = secretQuestionIds[checkedIndex];
                    }
                })
                .setNegativeButton("取消", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                }).show();
    }

    public void submit(View v) {
        if (!checkNetwork() || isSubmiting) {
            return;
        }
        Map<TextView, String> maps = new LinkedHashMap<>();
//        maps.put(nameEt, "请输入姓名");
        maps.put(phoneEt, "请输入电话");
        maps.put(idEt, "请输入身份证号");
        maps.put(newPasswdEt, "请输入新密码");
        maps.put(newPasswdConfirmEt, "请输入确认密码");
        if (!UIUtils.validRequired(this, maps)) {
            return;
        }
        String newPasswd = UIUtils.value(newPasswdEt);
        String newPasswdConfirm = UIUtils.value(newPasswdConfirmEt);
        String answer = UIUtils.value(secretAnswerEt);
        if (!TextUtils.equals(newPasswd, newPasswdConfirm)) {
            toast("两次输入的密码不一致.");
            return;
        }
        if (TextUtils.isEmpty(questionid)) {
            toast("请选择密保问题");
            return;
        }
        if (TextUtils.isEmpty(answer)) {
            toast("请输入密保答案");
            return;
        }

        showWaitDialog("正在提交...");
        isSubmiting = true;
        HTTP.service.post("login/register", MapParam.me()
                .p("username", UIUtils.value(nameEt))
                .p("cardnum", UIUtils.value(idEt))
                .p("password", newPasswd)
                .p("questionid", questionid)
                .p("answer", answer)
                .p("phone", UIUtils.value(phoneEt)).build())
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Subscriber(ctx){

                    @Override
                    public void onSuccess(Response response) {
                        isSubmiting = false;
                        EventBus.getDefault().post(new UserUpdateSuccessEvent());
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
}
