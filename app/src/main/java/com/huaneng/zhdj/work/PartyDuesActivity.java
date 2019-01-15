package com.huaneng.zhdj.work;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.TextView;

import com.huaneng.zhdj.BaseRefreshActivity;
import com.huaneng.zhdj.Data;
import com.huaneng.zhdj.R;
import com.huaneng.zhdj.adapter.PartyDuesAdapter;
import com.huaneng.zhdj.bean.NewsPager;
import com.huaneng.zhdj.bean.PartyDues;
import com.huaneng.zhdj.bean.PartyDuesPager;
import com.huaneng.zhdj.network.HTTP;
import com.huaneng.zhdj.network.Response;
import com.huaneng.zhdj.network.Subscriber;
import com.huaneng.zhdj.utils.MapParam;

import org.xutils.view.annotation.ContentView;
import org.xutils.view.annotation.Event;
import org.xutils.view.annotation.ViewInject;

import java.util.Calendar;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;

// 党务办公 -- 党费缴纳
@ContentView(R.layout.activity_party_dues)
public class PartyDuesActivity extends BaseRefreshActivity<PartyDues> {//

    @ViewInject(R.id.startDateTv)
    TextView startDateTv;
    @ViewInject(R.id.endDateTv)
    TextView endDateTv;

    PartyDuesAdapter adapter;
    String[] months = new String[6];
    int checkedStartDateIndex = -1;
    int checkedEndDateIndex = -1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setTitle("党费缴纳");
        mPager.size = 20;
        adapter = new PartyDuesAdapter(ctx, mList);
        mListView.setAdapter(adapter);
        mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                if (i == 0) {// 点击了表头
                    return;
                }
//                Intent intent = new Intent(ctx, PartyWorkDetailActivity.class);
//                intent.putExtra("news", mList.get(i-1));
//                startActivity(intent);
            }
        });
        View header = getLayoutInflater().inflate(R.layout.layout_party_dues_list_header, null);
        mListView.addHeaderView(header);
        pagerHandler.adapter = adapter;
        initMonths();
        getList();
    }

    private void initMonths() {
        for (int i=0;i<6;i++) {
            Calendar calendar = Calendar.getInstance();
            calendar.add(Calendar.MONTH, -i);
            int month = (calendar.get(Calendar.MONTH) + 1);
            String yearMonth = null;
            if (month < 10) {
                yearMonth = "" + calendar.get(Calendar.YEAR) + "0" + month;
            } else {
                yearMonth = "" + calendar.get(Calendar.YEAR) + month;
            }
            months[i] = yearMonth;
        }
    }

    @Event(R.id.startDateTv)
    private void startDate(View view) {
        new AlertDialog.Builder(this)
                .setTitle("请选择月份：")
                .setSingleChoiceItems(months, checkedStartDateIndex, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                        checkedStartDateIndex = which;
                        startDateTv.setText(months[checkedStartDateIndex]);
                    }
                })
                .setNegativeButton("取消", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                }).show();
    }

    @Event(R.id.endDateTv)
    private void endDate(View view) {
        new AlertDialog.Builder(this)
                .setTitle("请选择月份：")
                .setSingleChoiceItems(months, checkedEndDateIndex, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                        checkedEndDateIndex = which;
                        endDateTv.setText(months[checkedEndDateIndex]);
                    }
                })
                .setNegativeButton("取消", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                }).show();
    }

    public void search(View view) {
        mPager.reset();
        getList();
    }

    public void getList() {
        if (!ctx.checkNetwork()) {
            return;
        }
        String date_start = startDateTv.getText().toString().trim();
        String date_end = endDateTv.getText().toString().trim();
        if (!(TextUtils.isEmpty(date_start) && TextUtils.isEmpty(date_end))) {
            if (TextUtils.isEmpty(date_start)) {
                toast("开始月份不能为空.");
                return;
            }
            if (TextUtils.isEmpty(date_end)) {
                toast("结束月份不能为空.");
                return;
            }
            if (Integer.valueOf(date_start) > Integer.valueOf(date_end)) {
                toast("开始月份不能大于结束月份.");
                return;
            }
        }
        // flag=uid时为登录者的信息
        HTTP.service.get("partydues/list", MapParam.me().p("page", mPager.nextPage()).p("size", mPager.size).p("flag", "uid").p("date_start", date_start).p("date_end", date_end).build())
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Subscriber(ctx) {

                    @Override
                    public void onSuccess(Response response) {
                        PartyDuesPager pager = response.getEntity(PartyDuesPager.class);
                        pagerHandler.requestSuccess(pager);
                    }

                    @Override
                    public void onWrong(String msg) {
                        pagerHandler.requestFail();
                    }
                });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_party_dues, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_party_dues:
                partyDues();
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    /**
     * 缴党费
     */
    private void partyDues() {
        if (!checkNetwork()) {
            return;
        }
        // type: 1=党务管理;2=党建监督;3=党建缴费
        HTTP.service.get("banner/list", MapParam.me().p("type", "3").build())
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Subscriber(ctx){

                    @Override
                    public void onSuccess(Response response) {
                        NewsPager newsPager = response.getEntity(NewsPager.class);
                        if (newsPager != null && newsPager.list != null && !newsPager.list.isEmpty()) {
                            String image = Data.decorateUrl(newsPager.list.get(0).image);
                            Intent intent = new Intent(ctx, QRCodeActivity.class);
                            intent.putExtra("title", "缴纳党费");
                            intent.putExtra("url", image);
                            startActivity(intent);
                        } else {
                            toast("数据不存在.");
                        }
                    }

                    @Override
                    public void onWrong(String msg) {
                        toast(msg);
                    }
                });
    }
}
