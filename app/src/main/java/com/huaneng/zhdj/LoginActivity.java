package com.huaneng.zhdj;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.ImageView;

import com.huaneng.zhdj.bean.LoginResInfo;
import com.huaneng.zhdj.bean.User;
import com.huaneng.zhdj.network.HTTP;
import com.huaneng.zhdj.network.Response;
import com.huaneng.zhdj.network.Subscriber;
import com.huaneng.zhdj.user.ForgotPasswdActivity;
import com.huaneng.zhdj.user.RegiterActivity;
import com.huaneng.zhdj.utils.MapParam;
import com.huaneng.zhdj.utils.SharedPreferencesUtils;
import com.huaneng.zhdj.utils.UIUtils;
import com.huaneng.zhdj.utils.UserUtils;

import org.xutils.view.annotation.ContentView;
import org.xutils.view.annotation.Event;
import org.xutils.view.annotation.ViewInject;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;

@ContentView(R.layout.activity_login)
public class LoginActivity extends BaseActivity {

    @ViewInject(R.id.nameTv)
    private EditText nameTv;
    @ViewInject(R.id.passwdTv)
    private EditText passwdTv;
    @ViewInject(R.id.passwordVisibleImg)
    private ImageView passwordVisibleImg;
    boolean passwordVisible = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (SharedPreferencesUtils.create(ctx).getBoolean(Constants.SPR_LOGIN)) {
            Intent intent = new Intent(this, SlidingMenuActivity.class);
            intent.putExtra("validUser", true);
            startActivity(intent);
            finish();
        } else {
            // 15012562585
//            nameTv.setText(SharedPreferencesUtils.create(ctx).get(Constants.SPR_NAME));
            if (BuildConfig.DEBUG) {
                nameTv.setText("13900000000");
                passwdTv.setText("123456");
            }
            passwordVisibleImg.setSelected(passwordVisible);
            checkNetwork();
        }
    }

    @Event(R.id.passwordVisibleImg)
    private void passwordVisible(View view) {
        passwordVisible = !passwordVisible;
        passwordVisibleImg.setSelected(passwordVisible);
        int type = EditorInfo.TYPE_CLASS_TEXT | EditorInfo.TYPE_TEXT_VARIATION_PASSWORD;
        if (passwordVisible) {
            type = EditorInfo.TYPE_CLASS_TEXT | EditorInfo.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD;
        }
        passwdTv.setInputType(type);
        passwdTv.setSelection(passwdTv.getText().length());
    }

    public void forgotPasswd(View view) {
        activity(ForgotPasswdActivity.class);
    }

    public void register(View view) {
        activity(RegiterActivity.class);
    }

    public void login(View view) {
        if (!checkNetwork()) {
            return;
        }
        if (UIUtils.validRequired(this, nameTv, passwdTv)) {
            showWaitDialog("正在登录...");
            final String name = UIUtils.value(nameTv);
            final String passwd = UIUtils.value(passwdTv);
            HTTP.service.post("login/save", MapParam.me().p("phone", name).p("password", passwd).build())
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(new Subscriber(ctx){

                        @Override
                        public void onSuccess(Response response) {
//                            toast("登录成功.");
                            LoginResInfo loginResInfo = response.getEntity(LoginResInfo.class);
                            User user = loginResInfo.user;
                            UserUtils.save(user);
                            SharedPreferencesUtils.create(ctx).put(Constants.SPR_NAME, name);
                            SharedPreferencesUtils.create(ctx).put(Constants.SPR_PASSWD, passwd);
                            SharedPreferencesUtils.create(ctx).put(Constants.SPR_TOKEN, loginResInfo.token);
                            SharedPreferencesUtils.create(ctx).putBoolean(Constants.SPR_LOGIN, true);
                            activity(SlidingMenuActivity.class);
                            finish();
                        }

                        @Override
                        public void onFail(String msg) {
                            snackError("登录失败.");
                            super.onFail(msg);
                        }
                    });
        }
    }
}
