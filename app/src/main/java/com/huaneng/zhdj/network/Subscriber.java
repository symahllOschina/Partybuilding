package com.huaneng.zhdj.network;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.huaneng.zhdj.BaseActivity;
import com.jakewharton.retrofit2.adapter.rxjava2.HttpException;
import com.orhanobut.logger.Logger;

import java.io.IOException;

import io.reactivex.Observer;
import io.reactivex.disposables.Disposable;
import okhttp3.ResponseBody;

/**
 * Created by Administrator on 2017/11/10.
 */

public class Subscriber implements Observer<Response> {

    private static final String ERROR_MESSAGE = "数据请求失败.";

    private BaseActivity act;

    public Subscriber() {
    }

    public Subscriber(BaseActivity act) {
        this.act = act;
    }

    @Override
    public void onSubscribe(Disposable d) {
    }

    @Override
    public void onNext(Response response) {
        hideWaitDialog();
        if (response.suceess()) {
            onSuccess(response);
        } else {
            onFail(response.message);
        }
    }

    public void onSuccess(Response response) {

    }

    public void onFail(String msg) {
        onWrong(msg);
    }

    @Override
    public void onError(Throwable e) {
        hideWaitDialog();
        String msg = ERROR_MESSAGE;
        if(e instanceof HttpException){
            ResponseBody body = ((HttpException) e).response().errorBody();
            try {
                JSONObject jsonObject = JSON.parseObject(body.string());
                msg = jsonObject.getString("message");
                Logger.d(msg);
            } catch (IOException IOe) {
                IOe.printStackTrace();
            }
        }
        onWrong(msg);
    }

    @Override
    public void onComplete() {
        hideWaitDialog();
    }

    private void hideWaitDialog() {
        if (act != null) {
            act.hideWaitDialog();
        }
    }

    /**
     * 非正常情况：网络问题、返回的数据status不为1
     */
    public void onWrong(String msg) {
        if (act != null) {
            act.snackError(msg);
            act.hideWaitDialog();
        }
    }
}
