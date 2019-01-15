package com.huaneng.zhdj.user;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.TextView;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.huaneng.zhdj.BaseActivity;
import com.huaneng.zhdj.R;
import com.huaneng.zhdj.network.HTTP;
import com.huaneng.zhdj.network.Response;
import com.huaneng.zhdj.network.Subscriber;
import com.huaneng.zhdj.utils.MapParam;
import com.huaneng.zhdj.utils.UIUtils;

import org.xutils.view.annotation.ContentView;
import org.xutils.view.annotation.Event;
import org.xutils.view.annotation.ViewInject;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;

/**
 * 修改密码
 */
@ContentView(R.layout.activity_update_passwd)
public class UpdatePasswdActivity extends BaseActivity {

    @ViewInject(R.id.passwdEt)
    EditText passwdEt;
    @ViewInject(R.id.newPasswdEt)
    EditText newPasswdEt;
    @ViewInject(R.id.newPasswdConfirmEt)
    EditText newPasswdConfirmEt;
    @ViewInject(R.id.updateSecretAnswerCb)
    CheckBox updateSecretAnswerCb;

    private boolean isSubmiting;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setTitle("密码修改");
    }

    public void submit(View v) {
        if (!checkNetwork() || isSubmiting) {
            return;
        }
        Map<TextView, String> maps = new LinkedHashMap<>();
//        maps.put(passwdEt, "请输入原密码");
        maps.put(newPasswdEt, "请输入新密码");
        maps.put(newPasswdConfirmEt, "请输入确认密码");
        if (!UIUtils.validRequired(this, maps)) {
            return;
        }
        String newPasswd = newPasswdEt.getText().toString().toString();
        String newPasswdConfirm = newPasswdConfirmEt.getText().toString().toString();
        if (!TextUtils.equals(newPasswd, newPasswdConfirm)) {
            toast("两次输入的密码不一致.");
            return;
        }

        showWaitDialog("正在提交...");
        isSubmiting = true;
        HTTP.service.post("user/updatePassword", MapParam.me()
//                .p("beforepassword", UIUtils.value(passwdEt))
                .p("password", newPasswd)
                .p("type", "forget")//忘记密码：type=forget 其余情况，type可不传
                .build())
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Subscriber(ctx) {

                    @Override
                    public void onSuccess(Response response) {
                        isSubmiting = false;
                        toast("密码修改成功.");
                        finish();
                    }

                    @Override
                    public void onWrong(String msg) {
                        isSubmiting = false;
                        toast(msg, "密码修改失败.");
                    }
                });
    }

    public void cancel(View view) {
        finish();
    }
}
