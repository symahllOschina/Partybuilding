package com.huaneng.zhdj.work;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.webkit.WebView;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;

import com.huaneng.zhdj.BaseActivity;
import com.huaneng.zhdj.R;
import com.huaneng.zhdj.WebViewActivity;
import com.huaneng.zhdj.adapter.MeetingNoteAdapter;
import com.huaneng.zhdj.bean.Cell;
import com.huaneng.zhdj.bean.Meeting;
import com.huaneng.zhdj.bean.MeetingNote;
import com.huaneng.zhdj.network.HTTP;
import com.huaneng.zhdj.network.Response;
import com.huaneng.zhdj.network.Subscriber;
import com.huaneng.zhdj.utils.MapParam;
import com.huaneng.zhdj.utils.WebViewUtils;
import com.huaneng.zhdj.view.NineGridlayout;
import com.huaneng.zhdj.view.TableView;

import org.xutils.view.annotation.ContentView;
import org.xutils.view.annotation.ViewInject;

import java.util.ArrayList;
import java.util.List;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;

// 党务办公 -- 会议详情
@ContentView(R.layout.activity_party_meeting_detail)
public class PartyMeetingDetailActivity extends BaseActivity {

    @ViewInject(R.id.tableView)
    TableView tableView;
    @ViewInject(R.id.webView)
    WebView webView;
    @ViewInject(R.id.meetingNoteLabel)
    TextView meetingNoteLabel;
    @ViewInject(R.id.meetingNoteLv)
    ListView meetingNoteLv;

    Meeting entity;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        entity = getSerializableExtra("news");
        setTitle(entity.title);
        getDetail();
    }

    private void init() {
        List<Cell> cells = new ArrayList<>();
        cells.add(new Cell("会议标题：", entity.title));
        cells.add(new Cell("会议时间：", entity.mdate));
        cells.add(new Cell("会议地点：", entity.maddress));
        cells.add(new Cell("主会人：", entity.lecturer));
        cells.add(new Cell("参会人：", entity.getMeetingPerson()));
        tableView.setData(cells);
        WebViewUtils.me(this, webView).html(entity.content);
    }

    private void getDetail() {
        if (!ctx.checkNetwork()) {
            return;
        }
        HTTP.service.get("meeting/read", MapParam.me().p("id", entity.id).build())
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Subscriber(ctx) {

                    @Override
                    public void onSuccess(Response response) {
                        entity = response.getEntity(Meeting.class);
                        init();
                        final List<MeetingNote> meetingNotes = entity.getMeetingNote();
                        if (meetingNotes != null && !meetingNotes.isEmpty()) {
                            meetingNoteLabel.setVisibility(View.VISIBLE);
                            meetingNoteLv.setVisibility(View.VISIBLE);
                            MeetingNoteAdapter meetingNoteAdapter = new MeetingNoteAdapter(ctx, meetingNotes);
                            meetingNoteLv.setAdapter(meetingNoteAdapter);
                            meetingNoteLv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                                @Override
                                public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                                    Intent intent = new Intent(ctx, WebViewActivity.class);
                                    intent.putExtra("title", "会议记录");

                                    String content = meetingNotes.get(i).content;
                                    if (!TextUtils.isEmpty(content) && content.length() > 100000) {
                                        WebViewActivity.bigData = content;
                                        intent.putExtra("isBigData", true);
                                    } else {
                                        intent.putExtra("html", content);
                                    }
                                    startActivity(intent);
                                }
                            });
                        }
                    }

                    @Override
                    public void onWrong(String msg) {
                        toast(msg);
                    }
                });
    }
}
