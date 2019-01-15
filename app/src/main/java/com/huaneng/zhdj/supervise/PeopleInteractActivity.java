package com.huaneng.zhdj.supervise;

import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;

import com.huaneng.zhdj.BaseActivity;
import com.huaneng.zhdj.R;
import com.huaneng.zhdj.adapter.MyFragmentPagerAdapter;

import org.xutils.view.annotation.ContentView;
import org.xutils.view.annotation.ViewInject;

import java.util.ArrayList;
import java.util.List;

/**
 * 政民互动/互动空间
 */
@ContentView(R.layout.activity_people_interact)
public class PeopleInteractActivity extends BaseActivity {

    @ViewInject(R.id.tabLayout)
    TabLayout tabLayout;
    @ViewInject(R.id.viewPager)
    ViewPager viewPager;

    List<Fragment> fragments = new ArrayList<Fragment>();

    public String[] titles = {"民主问答", "民众投票", "民意调查"};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setTitle("互动空间");

        tabLayout.setTabMode(TabLayout.MODE_FIXED);
        for (String title : titles) {
            tabLayout.addTab(tabLayout.newTab().setText(title));
        }
        Fragment fragment1 = new DemocraticInterlocutionFragment();
        Fragment fragment2 = new VoteFragment();
        Fragment fragment3 = new SurveyFragment();
        fragments.add(fragment1);
        fragments.add(fragment2);
        fragments.add(fragment3);
        MyFragmentPagerAdapter fragmentPagerAdapter = new MyFragmentPagerAdapter(getSupportFragmentManager(), fragments, titles);

        viewPager.setAdapter(fragmentPagerAdapter);
        viewPager.setOffscreenPageLimit(titles.length);
        tabLayout.setupWithViewPager(viewPager);
        viewPager.addOnPageChangeListener(new TabLayout.TabLayoutOnPageChangeListener(tabLayout));
    }
}
