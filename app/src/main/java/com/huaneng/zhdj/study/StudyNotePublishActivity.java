package com.huaneng.zhdj.study;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.alibaba.fastjson.JSON;
import com.huaneng.zhdj.BaseActivity;
import com.huaneng.zhdj.R;
import com.huaneng.zhdj.bean.NoteType;
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
import java.util.List;
import java.util.Map;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;
import me.iwf.photopicker.PhotoPicker;
import okhttp3.MediaType;
import okhttp3.RequestBody;

/**
 * 学习笔记上传
 */
@ContentView(R.layout.activity_study_note_publish)
public class StudyNotePublishActivity extends BaseActivity {

    @ViewInject(R.id.typeLayout)
    private LinearLayout typeLayout;
    @ViewInject(R.id.noteTypeTv)
    private TextView noteTypeTv;
    @ViewInject(R.id.titleEt)
    private EditText titleEt;
    @ViewInject(R.id.descriptionEt)
    private EditText descriptionEt;
    @ViewInject(R.id.nineGridlayout)
    private NineGridlayout nineGridlayout;

    private ArrayList<String> images;
    private List<NoteType> noteTypeList;

    UploadImagesResult imagesUploadResult;
    private boolean isSubmiting;
    private int checkedNoteTypeIndex = -1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setTitle("学习笔记上传");
        initAddImgButton();
        searchNoteType();
    }

    // 查询笔记类型
    private void searchNoteType() {
        HTTP.service.get("schoolnote/notetype")
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Subscriber(ctx){

                    @Override
                    public void onSuccess(Response response) {
                        noteTypeList = response.getEntityList(NoteType.class);
                        if (noteTypeList != null && !noteTypeList.isEmpty()) {
                            typeLayout.setVisibility(View.VISIBLE);
                        }
                    }

                    @Override
                    public void onWrong(String msg) {
                        toast("获取笔记类型失败：" + msg);
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
        String content = null;
        if (imagesUploadResult != null && imagesUploadResult.data != null && !imagesUploadResult.data.isEmpty()) {
            content = JSON.toJSONString(imagesUploadResult.data);
        }
        if (TextUtils.isEmpty(content)) {
            toast("请添加图片");
            return;
        }
        showWaitDialog("正在提交...");
        isSubmiting = true;
        HTTP.service.post("schoolnote/save", MapParam.me().p("title", titleEt.getText().toString())
                .p("description", descriptionEt.getText().toString())
//                .p("content", content)
                .p("note_type", getNoteType())
                .p("image", content).build())
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
                        toast(msg, "提交失败：" + msg);
                    }
                });
    }

    public void uploadFiles() {
        if (!ctx.checkNetwork() || !UIUtils.validRequired(this, titleEt, descriptionEt)) {
            return;
        }
        if (images != null && !images.isEmpty()) {
            showWaitDialog("正在提交...");
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
                            toast("图片长传失败：" + msg);
                        }
                    });
        } else {
            save();
        }
    }

    private String getNoteType() {
        if (noteTypeList != null && !noteTypeList.isEmpty() && checkedNoteTypeIndex > -1) {
            return noteTypeList.get(checkedNoteTypeIndex).catid;
        }
        return null;
    }

    public void selectNoteType(View v) {
        if (noteTypeList == null || noteTypeList.isEmpty()) {
            toast("未获取到笔记类型.");
            return;
        }
        String[] items = new String[noteTypeList.size()];
        for (int i=0; i< noteTypeList.size(); i++) {
            items[i] = noteTypeList.get(i).catname;
        }
        new AlertDialog.Builder(this)
                .setTitle("请选择笔记类型")
                .setSingleChoiceItems(items, checkedNoteTypeIndex, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        checkedNoteTypeIndex = which;
                        dialog.dismiss();
                        noteTypeTv.setText(noteTypeList.get(which).catname);
                    }
                })
                .setNegativeButton("取消", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                }).show();
    }
}
