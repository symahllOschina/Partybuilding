package com.huaneng.zhdj.work;

import android.os.Bundle;
import android.widget.ImageView;

import com.huaneng.zhdj.BaseActivity;
import com.huaneng.zhdj.GlideApp;
import com.huaneng.zhdj.R;

import org.xutils.view.annotation.ContentView;
import org.xutils.view.annotation.ViewInject;

// 二维码
@ContentView(R.layout.activity_qrcode)
public class QRCodeActivity extends BaseActivity {

    @ViewInject(R.id.image)
    ImageView image;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setTitle(getIntent().getStringExtra("title"));
        String url = getIntent().getStringExtra("url");
        GlideApp.with(ctx)
                .load(url)
                .into(image);
    }
}
