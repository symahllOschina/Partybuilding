package com.huaneng.zhdj.supervise;

import android.app.DatePickerDialog;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RadioGroup;
import android.widget.TextView;

import com.huaneng.zhdj.BaseActivity;
import com.huaneng.zhdj.R;
import com.huaneng.zhdj.bean.Vote;
import com.huaneng.zhdj.view.MultiVoteView;
import com.huaneng.zhdj.view.SingleVoteView;

import org.xutils.view.annotation.ContentView;
import org.xutils.view.annotation.Event;
import org.xutils.view.annotation.ViewInject;

import java.util.ArrayList;
import java.util.Calendar;

import cn.qqtheme.framework.picker.DateTimePicker;

/**
 * 投票发起
 */
@ContentView(R.layout.activity_vote_create)
public class VoteCreateActivity extends BaseActivity {

    @ViewInject(R.id.optionsBox)
    LinearLayout optionsBox;
    @ViewInject(R.id.descriptionEt)
    EditText descriptionEt;
    @ViewInject(R.id.radioGroup)
    RadioGroup radioGroup;
    @ViewInject(R.id.dateTv)
    TextView dateTv;
    int mYear;
    int mMonth;
    int mDay;
    int mHour;
    int mMinute;
    Vote vote;
    DateTimePicker dateTimePicker;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setTitle("投票发起");
        addOption();
        addOption();
        addOption();
        initDateTimePicker();
    }

    private void initDateTimePicker() {
        mYear = Calendar.getInstance().get(Calendar.YEAR);
        mMonth = Calendar.getInstance().get(Calendar.MONTH);
        mDay = Calendar.getInstance().get(Calendar.DAY_OF_MONTH);
        mHour = Calendar.getInstance().get(Calendar.HOUR_OF_DAY);
        mMinute = Calendar.getInstance().get(Calendar.MINUTE);
        dateTimePicker = new DateTimePicker(this, DateTimePicker.YEAR_MONTH_DAY, DateTimePicker.HOUR_24);
        dateTimePicker.setDateRangeStart(mYear, mMonth, mDay);
        dateTimePicker.setDateRangeEnd(mYear+5, mMonth, mDay);
        dateTimePicker.setSelectedItem(mYear, mMonth, mDay, mHour, mMinute);
        dateTimePicker.setOnDateTimePickListener(new DateTimePicker.OnYearMonthDayTimePickListener(){

            @Override
            public void onDateTimePicked(String year, String month, String day, String hour, String minute) {
                mYear = Integer.valueOf(year);
                mMonth = Integer.valueOf(month);
                mDay = Integer.valueOf(day);
                mHour = Integer.valueOf(hour);
                mMinute = Integer.valueOf(minute);
                dateTv.setText(year + "-" + month + "-" + day + " " + hour + ":" + minute);
            }
        });
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
                submit(null);
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    @Event(R.id.dateTv)
    private void date(View view) {
        dateTimePicker.show();
    }

    public void submit(View view) {
        toast("提交成功.");
        finish();
    }

    public void addOption(View view) {
        addOption();
    }

    public void addOption() {
        final View view = getLayoutInflater().inflate(R.layout.vote_option, null);
        view.findViewById(R.id.delBtn).setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                optionsBox.removeView(view);
            }
        });
        optionsBox.addView(view);
    }

}
