package com.huaneng.zhdj.pioneers;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.EditText;

import com.alibaba.fastjson.JSON;
import com.huaneng.zhdj.BaseActivity;
import com.huaneng.zhdj.R;
import com.huaneng.zhdj.bean.UploadImagesResult;
import com.huaneng.zhdj.network.HTTP;
import com.huaneng.zhdj.network.Response;
import com.huaneng.zhdj.network.Subscriber;
import com.huaneng.zhdj.utils.MapParam;
import com.huaneng.zhdj.utils.UIUtils;
import com.huaneng.zhdj.view.NineGridlayout;
import com.nanchen.compresshelper.CompressHelper;
import com.orhanobut.logger.Logger;

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
 * 发布作品
 */
@ContentView(R.layout.activity_pionners_works_publish)
public class PioneersWorksPublishActivity extends BaseActivity {

    @ViewInject(R.id.titleEt)
    private EditText titleEt;
    @ViewInject(R.id.descriptionEt)
    private EditText descriptionEt;
    @ViewInject(R.id.nineGridlayout)
    private NineGridlayout nineGridlayout;

    private ArrayList<String> images;

    UploadImagesResult imagesUploadResult;
    private boolean isSubmiting;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setTitle("作品发布");
        initAddImgButton();
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
        getMenuInflater().inflate(R.menu.menu_submit_audit, menu);
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
        if (!checkNetwork() || isSubmiting) {
            return;
        }
        showWaitDialog("正在提交...");
        isSubmiting = true;
        String content = null;
        if (imagesUploadResult != null && imagesUploadResult.data != null && !imagesUploadResult.data.isEmpty()) {
            content = JSON.toJSONString(imagesUploadResult.data);
        }
        HTTP.service.post("works/save", MapParam.me().p("title", titleEt.getText().toString())
                .p("description", descriptionEt.getText().toString())
                .p("image", content).build())
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Subscriber(ctx){

                    @Override
                    public void onSuccess(Response response) {
                        isSubmiting = false;
                        toast("作品提交成功.");
                        finish();
                    }

                    @Override
                    public void onWrong(String msg) {
                        isSubmiting = false;
                        toast(msg, "作品提交失败.");
                    }
                });
    }

    public void uploadFiles() {
        if (!ctx.checkNetwork() || !UIUtils.validRequired(this, titleEt, descriptionEt)) {
            return;
        }
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

//            String path = images.get(0).getCompressPath();
//            if (!TextUtils.isEmpty(path)) {
//                File file = new File(path);
//                RequestBody requestBody = RequestBody.create(MediaType.parse("image/png"), file);
//
//                HTTP.service.uploadFile(requestBody, "image")
//                        .subscribeOn(Schedulers.io())
//                        .observeOn(AndroidSchedulers.mainThread())
//                        .subscribe(new Subscriber(ctx){
//
//                            @Override
//                            public void onSuccess(Response response) {
//                                String data = response.getDateString();
//                                Logger.e(data);
//                            }
//
//                            @Override
//                            public void onWrong(String message) {
//                                Logger.e(message);
//                                toast(message);
//                            }
//                        });
//            }

        } else {
            save();
        }
    }
}
