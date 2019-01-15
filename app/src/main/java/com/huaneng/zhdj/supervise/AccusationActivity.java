package com.huaneng.zhdj.supervise;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import com.alibaba.fastjson.JSON;
import com.huaneng.zhdj.BaseActivity;
import com.huaneng.zhdj.R;
import com.huaneng.zhdj.bean.Accusation;
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
import org.xutils.view.annotation.Event;
import org.xutils.view.annotation.ViewInject;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;
import me.iwf.photopicker.PhotoPicker;
import okhttp3.MediaType;
import okhttp3.RequestBody;

/**
 * 我要举报
 */
@ContentView(R.layout.activity_accusation)
public class AccusationActivity extends BaseActivity {

    @ViewInject(R.id.nameEt)
    private EditText nameEt;
    @ViewInject(R.id.jobEt)
    private EditText jobEt;
    @ViewInject(R.id.titleEt)
    private EditText titleEt;
    @ViewInject(R.id.typeTv)
    private TextView typeTv;
    @ViewInject(R.id.detailTv)
    private TextView detailTv;
    @ViewInject(R.id.descriptionEt)
    private EditText descriptionEt;
    @ViewInject(R.id.nineGridlayout)
    private NineGridlayout nineGridlayout;

    private ArrayList<String> images;

    UploadImagesResult imagesUploadResult;
    private boolean isSubmiting;

    // 问题类别(1=组织,2=政治;3=廉洁;4=群众;5=生活;6=工作;7=涉法;;)
    public static final String[] types = {"组织", "政治", "廉洁", "群众", "生活", "工作", "涉法"};
    // 问题细节（1=贪污;2=渎职;3=破坏市场经济;4=侵犯人民权利;5=妨害社会秩序;6=其他涉法行为;）
    public static final String[] details = {"贪污", "渎职", "破坏市场经济", "侵犯人民权利", "妨害社会秩序", "其他涉法行为"};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setTitle("实名举报");
        initAddImgButton();
    }

    int checkedTypeIndex = -1;
    @Event(R.id.typeTv)
    private void onTypeClick(View v) {
        new AlertDialog.Builder(this)
                .setTitle("请选择问题类别：")
                //.setSingleChoiceItems(Accusation.types, checkedTypeIndex, new DialogInterface.OnClickListener() {
                .setSingleChoiceItems(types, checkedTypeIndex, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                        checkedTypeIndex = which;
//                        typeTv.setText(Accusation.types[checkedTypeIndex]);
                        typeTv.setText(types[checkedTypeIndex]);
                        detailTv.setText(null);
                        checkedDetailIndex = -1;
                    }
                })
                .setNegativeButton("取消", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                }).show();
    }

    int checkedDetailIndex = -1;
    @Event(R.id.detailTv)
    private void onDetailClick(View v) {
        if (checkedTypeIndex < 0) {
            toast("请先选择问题类别");
            return;
        }
        new AlertDialog.Builder(this)
                .setTitle("请选择问题细类：")
                //.setSingleChoiceItems(Accusation.toArray(Accusation.typeList.get(checkedTypeIndex).children), checkedDetailIndex, new DialogInterface.OnClickListener() {
                .setSingleChoiceItems(details, checkedDetailIndex, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                        checkedDetailIndex = which;
//                        detailTv.setText(Accusation.typeList.get(checkedTypeIndex).children.get(checkedDetailIndex).title);
                        detailTv.setText(details[checkedDetailIndex]);
                    }
                })
                .setNegativeButton("取消", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                }).show();
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
        Map<TextView, String> maps = new LinkedHashMap<>();
        maps.put(nameEt, "请输入姓名");
        maps.put(jobEt, "请输入职务");
        maps.put(titleEt, "请输入问题标题");
        maps.put(typeTv, "请输入问题类别");
        maps.put(detailTv, "请输入问题细节");
        maps.put(descriptionEt, "请输入问题描述");
        if (UIUtils.validRequired(this, maps)) {
            uploadFiles();
        }
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
        String content = null;
        if (imagesUploadResult != null && imagesUploadResult.data != null && !imagesUploadResult.data.isEmpty()) {
            content = JSON.toJSONString(imagesUploadResult.data);
        }
        HTTP.service.post("rreport/save", MapParam.me()
                .p("name", nameEt.getText().toString())
                .p("duties", jobEt.getText().toString())
                .p("title", titleEt.getText().toString())
                .p("type", checkedTypeIndex + 1)
                .p("details", checkedDetailIndex + 1)
                .p("description", descriptionEt.getText().toString())
                .p("images_info[]", content).build())
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Subscriber(ctx){

                    @Override
                    public void onSuccess(Response response) {
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

    public void uploadFiles() {
        if (isSubmiting) {
            toast("正在处理，请勿重复提交.");
            return;
        }
        if (!checkNetwork() || !UIUtils.validRequired(this, titleEt, descriptionEt)) {
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
            showWaitDialog("正在提交...");
            isSubmiting = true;
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
                            isSubmiting = false;
                        }
                    });
        } else {
            isSubmiting = true;
            save();
        }
    }
}
