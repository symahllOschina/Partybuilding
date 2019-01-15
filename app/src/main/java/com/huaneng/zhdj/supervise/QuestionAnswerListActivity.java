package com.huaneng.zhdj.supervise;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.AdapterView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.alibaba.fastjson.JSON;
import com.huaneng.zhdj.BaseActivity;
import com.huaneng.zhdj.Constants;
import com.huaneng.zhdj.R;
import com.huaneng.zhdj.adapter.QuestionAnswerAdapter;
import com.huaneng.zhdj.bean.Pager;
import com.huaneng.zhdj.bean.QuestionAnswer;
import com.huaneng.zhdj.bean.QuestionAnswerPager;
import com.huaneng.zhdj.bean.UserInfo;
import com.huaneng.zhdj.network.HTTP;
import com.huaneng.zhdj.network.Response;
import com.huaneng.zhdj.network.Subscriber;
import com.huaneng.zhdj.utils.MapParam;
import com.huaneng.zhdj.utils.SharedPreferencesUtils;

import org.xutils.view.annotation.ContentView;
import org.xutils.view.annotation.ViewInject;

import java.util.ArrayList;
import java.util.List;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;

/**
 * 问题批复
 */
@ContentView(R.layout.activity_question_answer_list)
public class QuestionAnswerListActivity extends BaseActivity {

    @ViewInject(R.id.todoQuestionLv)
    ListView todoQuestionLv;
    @ViewInject(R.id.doneQuestionLv)
    ListView doneQuestionLv;
    @ViewInject(R.id.nameTv)
    TextView nameTv;
    @ViewInject(R.id.organTv)
    TextView organTv;

    @ViewInject(R.id.todoEmptyLayout)
    LinearLayout todoEmptyLayout;
    @ViewInject(R.id.doneEmptyLayout)
    LinearLayout doneEmptyLayout;
    Pager mPager = new Pager();

    List<QuestionAnswer> mList = new ArrayList<QuestionAnswer>();
    List<QuestionAnswer> todoList = new ArrayList<>();
    List<QuestionAnswer> doneList = new ArrayList<>();
    QuestionAnswerAdapter todoAdapter;
    QuestionAnswerAdapter doneAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setTitle("问题批复");
        String json = SharedPreferencesUtils.create(ctx).get(Constants.SPR_USER_JSON);
        if (!TextUtils.isEmpty(json)) {
            UserInfo userInfo = JSON.parseObject(json, UserInfo.class);
            if (userInfo != null) {
                showText(nameTv, "您是：", userInfo.username);
                showText(organTv, "部门：", userInfo.company);
            }
        }
        todoAdapter = new QuestionAnswerAdapter(this, todoList);
        todoQuestionLv.setAdapter(todoAdapter);
        todoQuestionLv.setEmptyView(todoEmptyLayout);
        todoQuestionLv.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                Intent intent = new Intent(ctx, QuestionAnswerActivity.class);
                intent.putExtra("questionAnswer", todoList.get(i));
                startActivity(intent);
            }
        });
        doneAdapter = new QuestionAnswerAdapter(this, doneList);
        doneQuestionLv.setAdapter(doneAdapter);
        doneQuestionLv.setEmptyView(doneEmptyLayout);
        doneQuestionLv.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                Intent intent = new Intent(ctx, QuestionAnswerActivity.class);
                intent.putExtra("questionAnswer", doneList.get(i));
                startActivity(intent);
            }
        });
    }

    @Override
    public void onResume() {
        getList();
        super.onResume();
    }

    public void getList() {
        if (!checkNetwork()) {
            return;
        }
        // TODO status:-1=删除;0=显示中;1=已读未回复;2=已回复;
        HTTP.service.get("aq/list", MapParam.me().p("page", mPager.nextPage()).p("size", 100).build())
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Subscriber(ctx){

                    @Override
                    public void onSuccess(Response response) {
                        QuestionAnswerPager pager = response.getEntity(QuestionAnswerPager.class);
                        if (pager == null) {
                            if (mList.isEmpty()) {
                            }
                        } else {
                            List<QuestionAnswer> list = pager.list;
                            if (list == null || list.isEmpty()) {
                                if (mList.isEmpty()) {
                                }
                            } else {
                                mList.clear();
                                for (QuestionAnswer item: list) {
                                    if (!"-1".equals(item.status)) {
                                        mList.add(item);
                                    }
                                }
                            }
                        }
                        load();
                    }

                    @Override
                    public void onWrong(String msg) {
                        toast("信息查询失败：" + msg);
                    }
                });
    }

    private void load() {
        todoList.clear();
        doneList.clear();
        for (QuestionAnswer qa: mList) {
            if (qa.isAnswered()) {
                doneList.add(qa);
            } else {
                todoList.add(qa);
            }
        }
        todoAdapter.notifyDataSetChanged();
        doneAdapter.notifyDataSetChanged();
    }
}
