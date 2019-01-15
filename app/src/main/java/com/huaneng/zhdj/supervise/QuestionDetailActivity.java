package com.huaneng.zhdj.supervise;

import android.os.Bundle;

import com.huaneng.zhdj.BaseActivity;
import com.huaneng.zhdj.R;
import com.huaneng.zhdj.bean.Cell;
import com.huaneng.zhdj.bean.QuestionAnswer;
import com.huaneng.zhdj.network.HTTP;
import com.huaneng.zhdj.network.Response;
import com.huaneng.zhdj.network.Subscriber;
import com.huaneng.zhdj.utils.MapParam;
import com.huaneng.zhdj.view.TableView;

import org.xutils.view.annotation.ContentView;
import org.xutils.view.annotation.ViewInject;

import java.util.ArrayList;
import java.util.List;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;

/**
 * 问题详情/问题信息
 */
@ContentView(R.layout.activity_question_detail)
public class QuestionDetailActivity extends BaseActivity {

    @ViewInject(R.id.questionTableView)
    TableView questionTableView;
    @ViewInject(R.id.answerTableView)
    TableView answerTableView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setTitle("问题信息");
        QuestionAnswer questionAnswer = getSerializableExtra("questionAnswer");
        init(questionAnswer);
//        getDetail(questionAnswer);
    }

    private void init(QuestionAnswer questionAnswer) {

        List<Cell> cells = new ArrayList<>();
        cells.add(new Cell("提问人：", questionAnswer.questions_name));
        cells.add(new Cell("提问时间：", questionAnswer.questions_date));
        cells.add(new Cell("提问问题：", questionAnswer.questions));
        questionTableView.setData(cells);

        cells = new ArrayList<>();
        cells.add(new Cell("答复人：", questionAnswer.answers_name));
        cells.add(new Cell("答复时间：", questionAnswer.getRealAnswerDate()));
        cells.add(new Cell("答复内容：", questionAnswer.answers));
        answerTableView.setData(cells);
    }

    public void getDetail(QuestionAnswer questionAnswer) {
        if (!checkNetwork()) {
            return;
        }
        HTTP.service.get("aq/read", MapParam.me().p("id", questionAnswer.id).build())
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Subscriber(ctx){

                    @Override
                    public void onSuccess(Response response) {
                        QuestionAnswer questionAnswer = response.getEntity(QuestionAnswer.class);
                        if (questionAnswer != null) {
                            init(questionAnswer);
                        } else {
                            toast("没有查询到信息.");
                        }
                    }

                    @Override
                    public void onError(Throwable e) {
                        toast("没有查询到信息.");
                    }

                    @Override
                    public void onFail(String msg) {
                        toast("没有查询到信息.");
                    }
                });
    }
}
