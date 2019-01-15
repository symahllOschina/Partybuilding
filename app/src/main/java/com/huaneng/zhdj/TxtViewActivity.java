package com.huaneng.zhdj;

import android.os.Bundle;
import android.text.method.ScrollingMovementMethod;
import android.widget.TextView;

import com.common.ajax.EncodingDetect;
import com.huaneng.zhdj.utils.FileUtils;

import org.xutils.view.annotation.ContentView;
import org.xutils.view.annotation.ViewInject;

import java.io.File;

/**
 * Txt文件预览
 */
@ContentView(R.layout.activity_txt_view)
public class TxtViewActivity extends FilePreviewActivity {

    @ViewInject(R.id.textView)
    private TextView textView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        textView.setMovementMethod(ScrollingMovementMethod.getInstance());
    }

    @Override
    protected void previewFile(File file) {
        String code = EncodingDetect.getJavaEncode(file.getAbsolutePath());
        textView.setText(FileUtils.read(file, code));
    }
}