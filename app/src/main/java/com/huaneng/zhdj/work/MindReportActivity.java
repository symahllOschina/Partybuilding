package com.huaneng.zhdj.work;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;

import com.alibaba.fastjson.JSON;
import com.huaneng.zhdj.BaseActivity;
import com.huaneng.zhdj.R;
import com.huaneng.zhdj.bean.PartySaveSuccessEvent;
import com.huaneng.zhdj.bean.UploadImagesResult;
import com.huaneng.zhdj.network.HTTP;
import com.huaneng.zhdj.network.Response;
import com.huaneng.zhdj.network.Subscriber;
import com.huaneng.zhdj.utils.MapParam;
import com.huaneng.zhdj.utils.UIUtils;
import com.huaneng.zhdj.view.NineGridlayout;
import com.nanchen.compresshelper.CompressHelper;
import com.orhanobut.logger.Logger;

import org.greenrobot.eventbus.EventBus;
import org.xutils.view.annotation.ContentView;
import org.xutils.view.annotation.ViewInject;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;
import me.iwf.photopicker.PhotoPicker;
import okhttp3.MediaType;
import okhttp3.RequestBody;

/**
 * 思想汇报
 */
@ContentView(R.layout.activity_mind_report)
public class MindReportActivity extends BaseActivity {

    @ViewInject(R.id.titleEt)
    private EditText titleEt;
    @ViewInject(R.id.descriptionEt)
    private EditText descriptionEt;
    @ViewInject(R.id.radioGroup)
    private RadioGroup radioGroup;
    @ViewInject(R.id.nineGridlayout)
    private NineGridlayout nineGridlayout;

    private ArrayList<String> images;

    UploadImagesResult imagesUploadResult;
    public String info_type;
    private boolean isSubmiting;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setTitle("思想汇报");
        initAddImgButton();
        radioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                RadioButton button = (RadioButton)findViewById(checkedId);
                info_type = button.getTag().toString();
            }
        });
    }

    // 添加图片的按钮
    private void initAddImgButton() {
        nineGridlayout.init(this);
        ArrayList<String> list = new ArrayList<>();
        list.add("");
        nineGridlayout.setImagesData(list);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_submit, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_submit:
                submit();
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    private void submit() {
        if (!checkNetwork() || isSubmiting) {
            return;
        }
        uploadFiles();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK && requestCode == PhotoPicker.REQUEST_CODE) {
            if (data != null) {
                images = data.getStringArrayListExtra(PhotoPicker.KEY_SELECTED_PHOTOS);
                nineGridlayout.setImagesData(images);
            }
        }
    }

    public void save() {
        showWaitDialog("正在提交...");
        String content = null;
        if (imagesUploadResult != null && imagesUploadResult.data != null && !imagesUploadResult.data.isEmpty()) {
            content = JSON.toJSONString(imagesUploadResult.data);
        }
        // 1.type:1=思想汇报；2=入党志愿书；3=入党申请中心内容。
        HTTP.service.post("party/save", MapParam.me().p("title", titleEt.getText().toString())
                .p("content", descriptionEt.getText().toString())
                .p("type", PartyJoinFragment.TYPE_1)
                // info_type (1=工作记录描述;2=日常工作总结;3=年度工作总结;4=日常职工思想汇报;5=日常党员思汇;6=年度党员思汇)
                .p("info_type", info_type)
                .p("images_info[]", content).build())
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Subscriber(ctx){

                    @Override
                    public void onSuccess(Response response) {
                        isSubmiting = false;
                        hideWaitDialog();
                        toast("提交成功.");
                        EventBus.getDefault().post(new PartySaveSuccessEvent(PartyJoinFragment.TYPE_1));
                        finish();
                    }

                    @Override
                    public void onWrong(String msg) {
                        isSubmiting = false;
                        hideWaitDialog();
                        toast(msg, "提交失败：" + msg);
                    }
                });
    }

    public void uploadFiles() {
        if (!ctx.checkNetwork() || !UIUtils.validRequired(this, titleEt, descriptionEt)) {
            return;
        }
        if (TextUtils.isEmpty(info_type)) {
            toast("请选择汇报类型");
            return;
        }
        showWaitDialog("正在提交...");
        isSubmiting = true;
        if (images != null && !images.isEmpty()) {
            Map<String, RequestBody> map = new HashMap<>();

            for (int i = 0; i < images.size(); i++) {
                String path = images.get(i);
                if (!TextUtils.isEmpty(path)) {
                    File file = new File(path);
                    File newFile = CompressHelper.getDefault(this).compressToFile(file);
                    RequestBody requestBody = RequestBody.create(MediaType.parse("multipart/form-data"), newFile);

                    map.put("file[]\";filename=\"" + file.getName(), requestBody);
                }
            }
            HTTP.service.uploadFiles(map, "image")
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(new Subscriber(ctx){

                        @Override
                        public void onSuccess(Response response) {
                            imagesUploadResult = response.getEntity(UploadImagesResult.class);
                            Logger.e(imagesUploadResult.toString());
                            save();
                        }

                        @Override
                        public void onWrong(String msg) {
                            Logger.e(msg);
                            toast(msg);
                        }
                    });
        } else {
            save();
        }
    }
}
