package com.huaneng.zhdj.work;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.view.View;

import com.huaneng.zhdj.BaseFragment;
import com.huaneng.zhdj.BaseRefreshFragment;
import com.huaneng.zhdj.R;
import com.huaneng.zhdj.bean.SearchFinishEvent;
import com.scwang.smartrefresh.layout.api.RefreshLayout;
import com.scwang.smartrefresh.layout.listener.OnLoadMoreListener;
import com.scwang.smartrefresh.layout.listener.OnRefreshListener;

import org.greenrobot.eventbus.Subscribe;
import org.xutils.view.annotation.ContentView;
import org.xutils.view.annotation.Event;
import org.xutils.view.annotation.ViewInject;

import java.util.ArrayList;
import java.util.List;

/**
 * 党务管理
 */
@ContentView(R.layout.fragment_party_work)
public class PartyWorkFragment extends BaseFragment implements OnRefreshListener, OnLoadMoreListener {

    @ViewInject(R.id.refreshLayout)
    RefreshLayout refreshLayout;

    List<Fragment> fragmentList = new ArrayList<Fragment>();
    int index = 0;

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        PartyRegulationFragment partyRegulationFragment = new PartyRegulationFragment();
//        RetiredPersonFragment retiredPersonFragment = new RetiredPersonFragment();
        partyRegulationFragment.setRefreshLayout(refreshLayout);
        fragmentList.add(partyRegulationFragment);
//        fragmentList.add(retiredPersonFragment);
//        refreshLayout.setOnRefreshListener(this);
//        refreshLayout.setOnLoadMoreListener(this);

        for (Fragment fragment : fragmentList) {
            addFragment(fragment);
        }
        changeFragment(fragmentList, index);
    }

    @Override
    protected boolean isEnableEventBus() {
        return true;
    }

    // 党务办公
    @Event({R.id.officeTv})
    private void office(View view) {
        activity(PartyOfficeActivity.class);
    }

    // 党员发展
    @Event({R.id.partyJoinTv})
    private void partyJoin(View view) {
        activity(PartyJoinActivity.class);
    }

    // 组织架构
    @Event({R.id.organTv})
    private void organ(View view) {
        activity(OrganActivity.class);
    }

    // partyRegulationTv 党规文件
    @Event({R.id.partyRegulationTv})
    private void changeFragment(View view) {
        index = Integer.valueOf(view.getTag().toString());
        changeFragment(fragmentList, index);
    }

    // 离退休党员-课程表
    @Event({R.id.timetableTv})
    private void timetable(View view) {
        Intent intent = new Intent(activity, TimetableActivity.class);
        startActivity(intent);
    }

    // 党费缴纳
    @Event({R.id.partyDuesTv})
    private void partyDues(View view) {
        activity(PartyDuesActivity.class);
    }

    @Override
    public void onRefresh(RefreshLayout refreshlayout) {
        ((BaseRefreshFragment)fragmentList.get(index)).onRefresh(refreshlayout);
    }

    @Subscribe
    public void onSearchFinish(SearchFinishEvent event) {
        if (PartyWorkFragment.class.getSimpleName().equals(event.flag)) {
            refreshLayout.finishRefresh(true);
            refreshLayout.setEnableLoadMore(event.hasMore);
        }
    }

    @Override
    public void onLoadMore(@NonNull RefreshLayout refreshLayout) {
        ((BaseRefreshFragment)fragmentList.get(index)).getList();
    }
}
