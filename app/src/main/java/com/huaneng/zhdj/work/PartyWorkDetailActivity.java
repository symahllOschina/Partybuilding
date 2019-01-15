package com.huaneng.zhdj.work;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.webkit.WebView;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;

import com.huaneng.zhdj.BaseActivity;
import com.huaneng.zhdj.Data;
import com.huaneng.zhdj.R;
import com.huaneng.zhdj.bean.Work;
import com.huaneng.zhdj.network.HTTP;
import com.huaneng.zhdj.network.Response;
import com.huaneng.zhdj.network.Subscriber;
import com.huaneng.zhdj.utils.MapParam;
import com.huaneng.zhdj.utils.UIUtils;
import com.huaneng.zhdj.utils.Utils;
import com.huaneng.zhdj.utils.WebViewUtils;
import com.huaneng.zhdj.view.ApproveView;
import com.huaneng.zhdj.view.NineGridlayout;
import com.huaneng.zhdj.view.PartyWorkApprove;

import org.xutils.view.annotation.ContentView;
import org.xutils.view.annotation.ViewInject;

import java.lang.reflect.Field;
import java.util.ArrayList;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;

// 党务办公 -- 工作详情
@ContentView(R.layout.activity_party_work_detail)
public class PartyWorkDetailActivity extends BaseActivity {

    @ViewInject(R.id.approveView)
    ApproveView approveView;
    @ViewInject(R.id.clickCount)
    TextView clickCount;
    @ViewInject(R.id.approveTv)
    TextView approveTv;
    @ViewInject(R.id.evaluateTv)
    TextView evaluateTv;
    @ViewInject(R.id.webView)
    WebView webView;

    @ViewInject(R.id.nineGridlayout)
    NineGridlayout nineGridlayout;
    boolean isSubmiting;

    Work work;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        work = getSerializableExtra("news");
        setTitle(work.title);
        getDetail();
    }

    private void getDetail() {
        if (!ctx.checkNetwork()) {
            return;
        }
        HTTP.service.get("worksreport/read", MapParam.me().p("id", work.id).build())
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Subscriber(ctx) {

                    @Override
                    public void onSuccess(Response response) {
                        work = response.getEntity(Work.class);
                        initImages();

                        approveView.init(ctx, work.id, new PartyWorkApprove());
                        approveView.isUpvote = Integer.valueOf(Utils.zeroIfNull(work.is_upvote));

                        WebViewUtils.me(ctx, webView).html(work.content);
                        showNum(clickCount, "点击数：", work.read_count);
                        showNum(approveTv, work.upvote_count);
//                        showText(levelTv, "评阅等级：", Data.work_approve_type.get(work.approve_type));
                    }

                    @Override
                    public void onWrong(String msg) {
                        toast(msg);
                    }
                });
    }

    // 添加图片的按钮
    private void initImages() {
        if (work.images_info != null && !work.images_info.isEmpty()) {
            nineGridlayout.init(this, true);
            nineGridlayout.setImagesData((ArrayList<String>)work.images_info);
        }
    }

    // 评定
    public void evaluate(View v) {
        if (!checkNetwork() || isSubmiting) {
            return;
        }
        final View view = getLayoutInflater().inflate(R.layout.dialog_evaluate, null);
        final RadioGroup radioGroup = view.findViewById(R.id.radioGroup);
        final EditText contentEt = view.findViewById(R.id.contentEt);

        AlertDialog dialog = new AlertDialog.Builder(this)
                .setTitle("评定")
                .setView(view)
                .setCancelable(false)
                .setPositiveButton("确认", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        int checkedId = radioGroup.getCheckedRadioButtonId();
                        if (checkedId == -1) {
                            toast("请选择评定等级");
                            fakeCloseDialog(dialog, false);
                        } else {
                            fakeCloseDialog(dialog, true);
                            RadioButton button = (RadioButton)view.findViewById(checkedId);
                            approve_num = button.getTag().toString();
                            approve_content = UIUtils.value(contentEt);
                            evaluate();
                        }
                    }
                })
                .setNegativeButton("取消", new DialogInterface.OnClickListener() {

                    @Override
                    public void onClick(DialogInterface dialog, int i) {
                        fakeCloseDialog(dialog, true);
                    }
                }).create();
        dialog.show();
        // 解决EditText不弹出软键盘的问题
        Window window = dialog.getWindow();
        window.clearFlags(WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE | WindowManager.LayoutParams.FLAG_ALT_FOCUSABLE_IM);
        window.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_VISIBLE);
    }

    public void fakeCloseDialog(DialogInterface dialog, boolean isClose) {
        try {
            Field field = dialog.getClass().getSuperclass().getDeclaredField("mShowing");
            field.setAccessible(true);
            //   将mShowing变量设为false，表示对话框已关闭
            field.set(dialog, isClose);
            dialog.dismiss();
        } catch (Exception e) {
        }
    }

    String approve_num;
    String approve_content;
    // 评定
    public void evaluate() {
        showWaitDialog("正在提交...");
        isSubmiting = true;
        HTTP.service.post("worksreport/score", MapParam.me()
                .p("id", work.id)
                .p("approve_num", approve_num)
                .p("approve_content", approve_content).build())
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Subscriber(ctx){

                    @Override
                    public void onSuccess(Response response) {
                        isSubmiting = false;
                        toast("提交成功.");
                    }

                    @Override
                    public void onWrong(String msg) {
                        isSubmiting = false;
                        toast(msg, "提交失败.");
                    }
                });
    }
}
