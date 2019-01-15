package com.huaneng.zhdj;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;

import com.huaneng.zhdj.utils.SharedPreferencesUtils;

import org.xutils.view.annotation.ContentView;

@ContentView(R.layout.activity_splash)
public class SplashActivity extends BaseActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        CrashReport.testJavaCrash();
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                Class<?> clazz = LoginActivity.class;
                if (SharedPreferencesUtils.create(ctx).getBoolean(Constants.SPR_LOGIN)) {
                    clazz = MainActivity.class;
                }
                Intent intent = new Intent(SplashActivity.this, clazz);
                startActivity(intent);
                finish();
            }
        }, 2000);
    }
}
