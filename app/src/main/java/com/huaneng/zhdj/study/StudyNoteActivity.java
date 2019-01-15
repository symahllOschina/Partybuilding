package com.huaneng.zhdj.study;

import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.view.Menu;
import android.view.MenuItem;

import com.huaneng.zhdj.BaseActivity;
import com.huaneng.zhdj.R;
import com.huaneng.zhdj.adapter.MyFragmentPagerAdapter;

import org.xutils.view.annotation.ContentView;
import org.xutils.view.annotation.ViewInject;

import java.util.ArrayList;
import java.util.List;

/**
 * 党员学习笔记
 */
@ContentView(R.layout.activity_study_note)
public class StudyNoteActivity extends BaseActivity {

    @ViewInject(R.id.tabLayout)
    TabLayout tabLayout;
    @ViewInject(R.id.viewPager)
    ViewPager viewPager;

    List<Fragment> fragments = new ArrayList<Fragment>();

    public String[] titles = {"学习笔记列表", "我的学习笔记"};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setTitle("学习笔记");

        tabLayout.setTabMode(TabLayout.MODE_FIXED);
        for (String title : titles) {
            tabLayout.addTab(tabLayout.newTab().setText(title));
        }
        fragments.add(StudyNoteFragment.getInstance(StudyNoteFragment.TYPE_ALL));
        fragments.add(StudyNoteFragment.getInstance(StudyNoteFragment.TYPE_MY));
        MyFragmentPagerAdapter fragmentPagerAdapter = new MyFragmentPagerAdapter(getSupportFragmentManager(), fragments, titles);

        viewPager.setAdapter(fragmentPagerAdapter);
        viewPager.setOffscreenPageLimit(titles.length);
        tabLayout.setupWithViewPager(viewPager);
        viewPager.addOnPageChangeListener(new TabLayout.TabLayoutOnPageChangeListener(tabLayout));
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_add, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_menu:
                activity(StudyNotePublishActivity.class);
                break;
        }
        return super.onOptionsItemSelected(item);
    }
}
