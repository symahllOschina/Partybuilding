package com.huaneng.zhdj;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.NotificationManagerCompat;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.RadioGroup;

import com.huaneng.zhdj.bean.MyMenu;
import com.huaneng.zhdj.bean.User;
import com.huaneng.zhdj.utils.JumpPermissionManagement;
import com.huaneng.zhdj.utils.UserUtils;

import org.xutils.view.annotation.ContentView;
import org.xutils.view.annotation.ViewInject;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import cn.jpush.android.api.JPushInterface;

@Deprecated
@ContentView(R.layout.activity_main)
public class MainActivity extends BaseActivity {

    @ViewInject(R.id.navRadioGroup)
    private RadioGroup navRadioGroup;
    int tabIndex = 0;

    private List<Fragment> fragmentList = new ArrayList<Fragment>();
    private List<String> titleList = new ArrayList<String>();
    BuildingSiteFragment buildingSiteFragment;
    MyMenu myMenu;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initNavButtons();
        initFragments();
        changeHomeTab(0);
        checkNetwork();
        setJPushAlias();
        notificationAuth();
    }

    boolean buildingSiteMenuFlag = false;
    public void initBuildingSiteMenu() {
        buildingSiteMenuFlag = true;
        if (myMenu != null) {
            buildingSiteFragment.initMenus(myMenu.menu);
        }
    }

    /**
     * 通知栏权限判断
     */
    private void notificationAuth() {
        boolean isNotiAuth = NotificationManagerCompat.from(this).areNotificationsEnabled();
        if (!isNotiAuth) {
            new AlertDialog.Builder(this)
                    .setTitle("提示")
                    .setMessage("开启通知栏权限，以便接收重要通知消息？")
                    .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                            JumpPermissionManagement.goToSettings(ctx);
                        }
                    })
                    .setNegativeButton("取消", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                        }
                    }).show();
        }
    }

    /**
     * 设置极光推送别名
     */
    private void setJPushAlias() {
        JPushInterface.resumePush(getApplicationContext());
        User user = UserUtils.getUser();
        if (user != null) {
            JPushInterface.setAlias(getApplicationContext(), 1, user.userid);
            Set<String> set = new HashSet<String>();
            set.add(user.userid);
            JPushInterface.setTags(getApplicationContext(), 1, set);
        }
    }

    private void initNavButtons() {
        navRadioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {

            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                switch (checkedId) {
                    case R.id.main_tab1:
                        tabIndex = 0;
                        break;
                    case R.id.main_tab2:
                        tabIndex = 1;
                        break;
                    case R.id.main_tab3:
                        tabIndex = 2;
                        break;
                    case R.id.main_tab4:
                        tabIndex = 3;
                        break;
                    case R.id.main_tab5:
                        tabIndex = 4;
                        break;
                }
                changeHomeTab(tabIndex);
            }
        });
    }

    private void initFragments() {
        buildingSiteFragment = new BuildingSiteFragment();
        fragmentList.add(buildingSiteFragment);
        fragmentList.add(new NoticeFragment());
        for (Fragment fragment : fragmentList) {
            addFragment(fragment);
        }
        titleList.add(getString(R.string.main_tab1));
        titleList.add(getString(R.string.main_tab2));
        titleList.add(getString(R.string.main_tab3));
        titleList.add(getString(R.string.main_tab4));
        titleList.add(getString(R.string.main_tab5));
    }

    public void changeHomeTab(int index) {
        setTitle(titleList.get(index));
        for (int i = 0; i < fragmentList.size(); i++) {
            if (i == index) {
                showFragment(fragmentList.get(i));
            } else {
                hideFragment(fragmentList.get(i));
            }
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_info, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_info:
                info();
//                UIUtils.goToAppSetting(ctx);
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    private void info() {
        Intent intent = new Intent(this, WebViewActivity.class);
        intent.putExtra("title", "关于");
        intent.putExtra("url", "file:///android_asset/about.html");
        startActivity(intent);
    }

    private void addFragment(Fragment fragment) {
        getSupportFragmentManager().beginTransaction().add(R.id.content_frame, fragment).commit();
        getSupportFragmentManager().executePendingTransactions();
    }

    private void hideFragment(Fragment fragment) {
        getSupportFragmentManager().beginTransaction().hide(fragment).commit();
        getSupportFragmentManager().executePendingTransactions();
    }

    private void showFragment(Fragment fragment) {
        getSupportFragmentManager().beginTransaction().show(fragment).commit();
        getSupportFragmentManager().executePendingTransactions();
    }

    @SuppressLint("MissingSuperCall")
    @Override
    public void onSaveInstanceState(Bundle outState) {
//        super.onSaveInstanceState(outState);
    }


//    private void getAppVersion() {
//        HTTP.service.getAppVersion("1")
//                .subscribeOn(Schedulers.io())
//                .observeOn(AndroidSchedulers.mainThread())
//                .subscribe(new Subscriber<AppVersion>(ctx){
//
//                    @Override
//                    public void onSuccess(Response<AppVersion> response) {
//                        AppVersion appVersion = response.data;
//                        if (appVersion != null && UIUtils.isNeedUpdate(ctx, appVersion)) {
//                            SharedPreferencesUtils.create(ctx).putBoolean(Constants.SPR_UPDATE, true);
//                            VersionParams.Builder builder = new VersionParams.Builder()
//                                    .setOnlyDownload(true)
//                                    .setDownloadUrl(Constants.APK_DOWNLOAD_URL)
//                                    .setDownloadAPKPath(Constants.APK_DOWNLOAD_PATH)
//                                    .setTitle("提示")
//                                    .setUpdateMsg("检测到新版本，现在更新？");
//                            AllenChecker.startVersionCheck(ctx, builder.build());
//                        } else {
//                            SharedPreferencesUtils.create(ctx).putBoolean(Constants.SPR_UPDATE, true);
//                        }
//                    }
//                });
//    }

    protected boolean isShowBacking() {
        return false;
    }

    private long mBackTime = -1;
    @Override
    public void onBackPressed() {
        long nowTime = System.currentTimeMillis();
        long diff = nowTime - mBackTime;
        if (diff >= 2000) {
            mBackTime = nowTime;
            toast("再按一次退出.");
        } else {
            app.exit();
        }
    }
}
