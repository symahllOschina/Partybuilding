package com.huaneng.zhdj.work;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;

import com.huaneng.zhdj.BaseActivity;
import com.huaneng.zhdj.R;

import org.xutils.view.annotation.ContentView;
import org.xutils.view.annotation.Event;

// 党务办公 -- 入口
@ContentView(R.layout.activity_party_office)
public class PartyOfficeActivity extends BaseActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setTitle("党务办公");
    }

    // 工作
    @Event({R.id.workTv})
    private void work(View view) {
        activity(PartyWorkActivity.class);
    }

    // 会议
    @Event({R.id.meetingTv})
    private void meeting(View view) {
        Intent intent = new Intent(this, PartyMeetingActivity.class);
        intent.putExtra("type", "1");
        startActivity(intent);
    }

    // 活动
    @Event({R.id.activityTv})
    private void activity(View view) {
        Intent intent = new Intent(this, PartyActivityActivity.class);
        intent.putExtra("type", "2");
        startActivity(intent);
    }
}
