package com.huaneng.zhdj.supervise;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.View;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.TextView;

import com.alibaba.fastjson.JSON;
import com.huaneng.zhdj.BaseActivity;
import com.huaneng.zhdj.Constants;
import com.huaneng.zhdj.R;
import com.huaneng.zhdj.bean.Organ;
import com.huaneng.zhdj.bean.UserInfo;
import com.huaneng.zhdj.network.HTTP;
import com.huaneng.zhdj.network.Response;
import com.huaneng.zhdj.network.Subscriber;
import com.huaneng.zhdj.utils.MapParam;
import com.huaneng.zhdj.utils.SharedPreferencesUtils;
import com.huaneng.zhdj.utils.UIUtils;
import com.orhanobut.logger.Logger;

import org.xutils.view.annotation.ContentView;
import org.xutils.view.annotation.ViewInject;

import java.util.List;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;

/**
 * 问题提问
 */
@ContentView(R.layout.activity_question_submit)
public class QuestionSubmitActivity extends BaseActivity {

    @ViewInject(R.id.titleTv)
    EditText titleTv;
    @ViewInject(R.id.contentTv)
    EditText contentTv;
    @ViewInject(R.id.organTv)
    TextView organTv;
    @ViewInject(R.id.limitTv)
    TextView limitTv;
    @ViewInject(R.id.noOrganCkb)
    CheckBox noOrganCkb;
    private boolean isSubmiting;
    int checkedIndex = -1;
    List<Organ> organs;
    String[] organNames;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setTitle("问题提问");

        TextWatcher textWatcher = new TextWatcher(){

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }

            @Override
            public void afterTextChanged(Editable s) {
                limitTv.setText(String.format("注：请不要超过500字。(%d/500)", s.length()));
            }
        };
        contentTv.addTextChangedListener(textWatcher);
        getOrgans();
    }

    public void getOrgans() {
        if (!checkNetwork()) {
            return;
        }
        HTTP.service.get("company/get")
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Subscriber(ctx){

                    @Override
                    public void onSuccess(Response response) {
                        organs = response.getEntityList(Organ.class);
                        organNames = new String[organs.size()];
                        for (int i=0; i< organs.size(); i++) {
                            organNames[i] = organs.get(i).name;
                        }
                    }

                    @Override
                    public void onWrong(String msg) {
                        Logger.e("获取部门失败：" + msg);
                    }
                });
    }

    public void submit(View v) {
        if (!checkNetwork() || isSubmiting) {
            return;
        }
        showWaitDialog("正在提交...");
        isSubmiting = true;
        String questions_name = null;
        String json = SharedPreferencesUtils.create(ctx).get(Constants.SPR_USER_JSON);
        if (!TextUtils.isEmpty(json)) {
            UserInfo userInfo = JSON.parseObject(json, UserInfo.class);
            questions_name = userInfo.username;
        }
        String department = UIUtils.value(organTv);
        if (noOrganCkb.isChecked()) {
            department = "不知道部门";
        }
        HTTP.service.post("aq/qsave", MapParam.me().p("title", titleTv.getText().toString())
                .p("questions", UIUtils.value(contentTv))
                .p("questions_name", questions_name)
                .p("department", department).build())
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

    public void organSelect(View v) {
        if (organNames == null || organNames.length < 1) {
            toast("未获取到部门信息.");
            return;
        }
        new AlertDialog.Builder(this)
                .setTitle("请选择部门：")
                .setSingleChoiceItems(organNames, checkedIndex, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                        checkedIndex = which;
                        organTv.setText(organNames[checkedIndex]);
                    }
                })
                .setNegativeButton("取消", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                }).show();
    }

    public void cancel(View v) {
        finish();
    }

}
