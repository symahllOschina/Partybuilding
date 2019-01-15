package com.huaneng.zhdj.work;

import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.LinearLayout;
import android.widget.PopupWindow;

import com.huaneng.zhdj.BaseActivity;
import com.huaneng.zhdj.R;
import com.huaneng.zhdj.adapter.MyFragmentPagerAdapter;

import org.xutils.view.annotation.ContentView;
import org.xutils.view.annotation.ViewInject;

import java.util.ArrayList;
import java.util.List;

/**
 * 党员发展
 */
@ContentView(R.layout.activity_party_join)
public class PartyJoinActivity extends BaseActivity {

    @ViewInject(R.id.tabLayout)
    TabLayout tabLayout;
    @ViewInject(R.id.viewPager)
    ViewPager viewPager;

    List<Fragment> fragments = new ArrayList<Fragment>();
    public String[] titles = {"入党申请中心", "积极分子专区", "预备党员专区"};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setTitle("党员发展");

        tabLayout.setTabMode(TabLayout.MODE_FIXED);
        for (String title : titles) {
            tabLayout.addTab(tabLayout.newTab().setText(title));
        }
        fragments.add(PartyJoinFragment.getInstance(PartyJoinFragment.TYPE_3));
        fragments.add(PartyJoinFragment.getInstance(PartyJoinFragment.TYPE_1));
        fragments.add(PartyJoinFragment.getInstance(PartyJoinFragment.TYPE_2));
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
                openMenu();
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    private void openMenu() {
        final PopupWindow window = new PopupWindow(this);
        window.setWidth(LinearLayout.LayoutParams.WRAP_CONTENT);
        window.setHeight(LinearLayout.LayoutParams.WRAP_CONTENT);
        window.setBackgroundDrawable(getResources().getDrawable(R.color.white));
        View contentView  = LayoutInflater.from(ctx).inflate(R.layout.layout_menu_join, null);
        contentView.findViewById(R.id.applyBtn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                window.dismiss();
                // 上传入党志愿书
                activity(PartyJoinApplyActivity.class);
            }
        });
        contentView.findViewById(R.id.reportBtn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                window.dismiss();
                // 思想汇报
                activity(MindReportActivity.class);
            }
        });
        window.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE);
        window.setFocusable(true);
        window.setContentView(contentView);
        window.setOutsideTouchable(true);
        window.showAsDropDown(mToolbar,mToolbar.getWidth(), 0);
    }
}
