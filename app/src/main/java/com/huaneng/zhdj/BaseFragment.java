package com.huaneng.zhdj;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.trello.rxlifecycle2.components.support.RxFragment;

import org.greenrobot.eventbus.EventBus;
import org.xutils.x;

import java.util.List;
import java.util.concurrent.TimeUnit;

import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;

public class BaseFragment extends RxFragment {

    protected final String TAG = getClass().getSimpleName();
    protected App app;
    protected BaseActivity activity;
    protected LayoutInflater inflater;
    protected View rootView;
    private boolean injected = false;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        activity = (BaseActivity) getActivity();
        app = (App) activity.getApplication();
        if (isEnableEventBus()) {
            EventBus.getDefault().register(this);
        }
    }

    @Override
    public void onStart() {
        super.onStart();
        Observable.interval(1, TimeUnit.SECONDS)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .compose(this.<Long>bindToLifecycle())
                .subscribe();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        this.inflater = inflater;
        injected = true;
        rootView = x.view().inject(this, inflater, container);
        return rootView;
    }

    protected boolean isEnableEventBus() {
        return false;
    }

    protected void activity(Class<?> clazz) {
        Intent intent = new Intent(getActivity(), clazz);
        startActivity(intent);
    }

    public void changeFragment(List<Fragment> fragmentList, int index) {
        for (int i = 0; i < fragmentList.size(); i++) {
            if (i == index) {
                showFragment(fragmentList.get(i));
            } else {
                hideFragment(fragmentList.get(i));
            }
        }
    }

    protected void addFragment(Fragment fragment) {
        getChildFragmentManager().beginTransaction().add(R.id.content_frame, fragment).commit();
        getChildFragmentManager().executePendingTransactions();
    }

    protected void hideFragment(Fragment fragment) {
        getChildFragmentManager().beginTransaction().hide(fragment).commit();
        getChildFragmentManager().executePendingTransactions();
    }

    protected void showFragment(Fragment fragment) {
        getChildFragmentManager().beginTransaction().show(fragment).commit();
        getChildFragmentManager().executePendingTransactions();
    }
}
