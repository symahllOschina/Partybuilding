package com.huaneng.zhdj.user;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TextView;

import com.alibaba.fastjson.JSON;
import com.huaneng.zhdj.BaseActivity;
import com.huaneng.zhdj.Constants;
import com.huaneng.zhdj.Data;
import com.huaneng.zhdj.R;
import com.huaneng.zhdj.bean.UserInfo;
import com.huaneng.zhdj.bean.UserUpdateSuccessEvent;
import com.huaneng.zhdj.network.HTTP;
import com.huaneng.zhdj.network.Response;
import com.huaneng.zhdj.network.Subscriber;
import com.huaneng.zhdj.utils.MapParam;
import com.huaneng.zhdj.utils.SharedPreferencesUtils;
import com.huaneng.zhdj.utils.UIUtils;

import org.greenrobot.eventbus.EventBus;
import org.xutils.view.annotation.ContentView;
import org.xutils.view.annotation.Event;
import org.xutils.view.annotation.ViewInject;

import java.util.Calendar;
import java.util.LinkedHashMap;
import java.util.Map;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;

/**
 * 个人基本信息--修改
 */
@ContentView(R.layout.activity_user_update)
public class UserInfoUpdateActivity extends BaseActivity {

    @ViewInject(R.id.nameEt)
    EditText nameEt;
    @ViewInject(R.id.sexEt)
    TextView sexEt;
    @ViewInject(R.id.nationEt)
    EditText nationEt;
    @ViewInject(R.id.phoneEt)
    EditText phoneEt;
    @ViewInject(R.id.idEt)
    EditText idEt;
    @ViewInject(R.id.integralEt)
    EditText integralEt;
    @ViewInject(R.id.schoolEt)
    EditText schoolEt;
    @ViewInject(R.id.majorEt)
    EditText majorEt;
    @ViewInject(R.id.joinPartyDateEt)
    TextView joinPartyDateEt;

    private boolean isSubmiting;
    int checkedSexIndex = -1;

    private int mYear;
    private int mMonth;
    private int mDay;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setTitle("个人信息");

        String json = SharedPreferencesUtils.create(ctx).get(Constants.SPR_USER_JSON);
        if (TextUtils.isEmpty(json)) {
            toast("未获取到用户信息");
            finish();
        } else {
            UserInfo userInfo = JSON.parseObject(json, UserInfo.class);
            String joinPartyDate = userInfo.getJoinPartyTime();
            UIUtils.showText(nameEt, userInfo.username);
            UIUtils.showText(sexEt, userInfo.sex);
            UIUtils.showText(nationEt, userInfo.nation);
            UIUtils.showText(phoneEt, userInfo.phone);
            UIUtils.showText(idEt, userInfo.cardnum);
            UIUtils.showText(integralEt, userInfo.integral);
            UIUtils.showText(schoolEt, userInfo.school);
            UIUtils.showText(majorEt, userInfo.major);
            UIUtils.showText(joinPartyDateEt, joinPartyDate);
            checkedSexIndex = userInfo.getSexCode();
            if (TextUtils.isEmpty(joinPartyDate)) {
                mYear = Calendar.getInstance().get(Calendar.YEAR);
                mMonth = Calendar.getInstance().get(Calendar.MONTH);
                mDay = Calendar.getInstance().get(Calendar.DAY_OF_MONTH);
            } else {
                String[] date = joinPartyDate.split("-");
                mYear = Integer.valueOf(date[0]);
                mMonth = Integer.valueOf(date[1])-1;
                mDay = Integer.valueOf(date[2]);
            }
        }
    }

    // 性别
    @Event(R.id.sexEt)
    private void sexSelect(View v) {
        new AlertDialog.Builder(this)
                .setTitle("请选择性别：")
                .setSingleChoiceItems(Data.SEX, checkedSexIndex, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                        checkedSexIndex = which;
                        sexEt.setText(Data.SEX[checkedSexIndex]);
                    }
                })
                .setNegativeButton("取消", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                }).show();
    }

    public void joinPartyDate(View view) {
        DatePickerDialog datePickerDialog = new DatePickerDialog(ctx,
                new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                        mYear = year;
                        mMonth = month;
                        mDay = dayOfMonth;
                        joinPartyDateEt.setText(mYear + "-" + fix((mMonth + 1)) + "-" + fix(mDay));
                    }
                },
                mYear, mMonth, mDay);
        //设置起始日期和结束日期
        DatePicker datePicker = datePickerDialog.getDatePicker();
        //datePicker.setMinDate();
        datePicker.setMaxDate(System.currentTimeMillis());
        datePickerDialog.show();
    }

    private String fix(int value) {
        if (value < 10) {
            return "0" + value;
        }
        return "" + value;
    }

    public void submit(View v) {
        if (!checkNetwork() || isSubmiting) {
            return;
        }
        Map<TextView, String> maps = new LinkedHashMap<>();
        maps.put(nameEt, "请输入姓名");
        maps.put(phoneEt, "请输入电话");
        if (!UIUtils.validRequired(this, maps)) {
            return;
        }

        showWaitDialog("正在提交...");
        isSubmiting = true;
        HTTP.service.post("user/update", MapParam.me()
                .p("username", UIUtils.value(nameEt))
                .p("sex", checkedSexIndex+1)
                .p("nation", UIUtils.value(nationEt))
                .p("cardnum", UIUtils.value(idEt))
                .p("school", UIUtils.value(schoolEt))
                .p("major", UIUtils.value(majorEt))
                .p("party_time", UIUtils.value(joinPartyDateEt))
//                .p("password", "")
//                .p("images", "")
                .p("phone", phoneEt.getText().toString()).build())
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Subscriber(ctx){

                    @Override
                    public void onSuccess(Response response) {
                        isSubmiting = false;
                        EventBus.getDefault().post(new UserUpdateSuccessEvent());
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
}
