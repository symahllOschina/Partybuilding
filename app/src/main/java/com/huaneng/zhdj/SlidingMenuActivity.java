package com.huaneng.zhdj;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.NavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.RadioGroup;
import android.widget.TextView;

import com.alibaba.fastjson.JSON;
import com.huaneng.zhdj.bean.RefreshUserInfoEvent;
import com.huaneng.zhdj.bean.UploadImageResult;
import com.huaneng.zhdj.bean.UploadImagesResult;
import com.huaneng.zhdj.bean.User;
import com.huaneng.zhdj.bean.UserInfo;
import com.huaneng.zhdj.bean.UserUpdateSuccessEvent;
import com.huaneng.zhdj.network.HTTP;
import com.huaneng.zhdj.network.Response;
import com.huaneng.zhdj.network.Subscriber;
import com.huaneng.zhdj.news.NewsFragment;
import com.huaneng.zhdj.pioneers.PioneersFragment;
import com.huaneng.zhdj.study.StudyFragment;
import com.huaneng.zhdj.supervise.SuperviseFragment;
import com.huaneng.zhdj.user.MessageActivity;
import com.huaneng.zhdj.user.PartyMemberRelationActivity;
import com.huaneng.zhdj.user.ScoreActivity;
import com.huaneng.zhdj.user.UserInfoActivity;
import com.huaneng.zhdj.utils.AESHelper;
import com.huaneng.zhdj.utils.GlideCircleTransform;
import com.huaneng.zhdj.utils.MapParam;
import com.huaneng.zhdj.utils.SharedPreferencesUtils;
import com.huaneng.zhdj.utils.UIUtils;
import com.huaneng.zhdj.utils.UserUtils;
import com.huaneng.zhdj.work.PartyWorkFragment;
import com.nanchen.compresshelper.CompressHelper;
import com.orhanobut.logger.Logger;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.xutils.view.annotation.ContentView;
import org.xutils.view.annotation.ViewInject;
import org.xutils.x;

import java.io.File;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import cn.jpush.android.api.JPushInterface;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;
import me.iwf.photopicker.PhotoPicker;
import okhttp3.MediaType;
import okhttp3.RequestBody;

@ContentView(R.layout.activity_sliding_menu)
public class SlidingMenuActivity extends BaseActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    @ViewInject(R.id.drawer_layout)
    DrawerLayout drawer;
    @ViewInject(R.id.nav_view)
    NavigationView navigationView;

    @ViewInject(R.id.navRadioGroup)
    private RadioGroup navRadioGroup;
    int tabIndex = 0;
    UserInfo userInfo;
    SharedPreferencesUtils spr;

    private List<Fragment> fragmentList = new ArrayList<Fragment>();
    private List<String> titleList = new ArrayList<String>();

    private class HeadView {
        @ViewInject(R.id.headImg)
        ImageView headImg;
        @ViewInject(R.id.nameTv)
        TextView nameTv;
        @ViewInject(R.id.deptTv)
        TextView deptTv;
        @ViewInject(R.id.scoreTv)
        TextView scoreTv;
        @ViewInject(R.id.titleTv)
        TextView titleTv;
        @ViewInject(R.id.rankingTv)
        TextView rankingTv;
    }

    HeadView headView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this, drawer, mToolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();
        navigationView.setNavigationItemSelectedListener(this);
        spr = SharedPreferencesUtils.create(ctx);
        initNavButtons();
        initFragments();
        changeHomeTab(0);
        checkNetwork();
//        loginValid();

        String json = spr.get(Constants.SPR_USER_JSON);
        initUserInfo(json);
        getUserInfo();
    }

    @Override
    protected boolean isEnableEventBus() {
        return true;
    }

    private void initUserInfo(String json) {
        if (TextUtils.isEmpty(json)) {
            return;
        }
        userInfo = JSON.parseObject(json, UserInfo.class);
        User user = new User();
        user.userid = userInfo.id;
        UserUtils.save(user);
        initHeadView();
        setJPushAlias();
    }

    private void initHeadView() {
        headView = new HeadView();
        x.view().inject(headView, navigationView.getHeaderView(0));
        if (userInfo != null) {
            showText(headView.nameTv, userInfo.username);
            showText(headView.deptTv, "部门：", userInfo.company);
            showNum(headView.scoreTv, "积分：", userInfo.integral);
            showText(headView.titleTv, "职位：", userInfo.postion);
            showText(headView.rankingTv, "排行：", userInfo.sort);
            showHeadImg();
            headView.headImg.setOnClickListener(new View.OnClickListener() {

                @Override
                public void onClick(View view) {
                    pickHeadImg();
                }
            });
        }
    }

    /**
     * 显示用户头像
     */
    private void showHeadImg() {
        GlideApp.with(ctx)
                .load(userInfo.getValidImageUrl())
                .centerCrop()
                .placeholder(R.drawable.my_avatar)
                .transform(new GlideCircleTransform(ctx))
                .into(headView.headImg);

    }

    /**
     * 选择用户头像
     */
    public void pickHeadImg() {
        PhotoPicker.builder()
                .setPhotoCount(1)
                .setShowCamera(true)
                .setShowGif(true)
                .setPreviewEnabled(false)
                .start(this, PhotoPicker.REQUEST_CODE);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK && requestCode == PhotoPicker.REQUEST_CODE) {
            if (data != null) {
                ArrayList<String> images = data.getStringArrayListExtra(PhotoPicker.KEY_SELECTED_PHOTOS);
                if (images != null && !images.isEmpty()) {
                    updateHeadImg(images.get(0));
                }
            }
        }
    }

    /**
     * 上传用户头像
     */
    public void updateHeadImg(String image) {
        File file = new File(image);
        File newFile = CompressHelper.getDefault(this).compressToFile(file);
        RequestBody requestBody = RequestBody.create(MediaType.parse("multipart/form-data"), newFile);
        HTTP.service.uploadFile(requestBody, "image")
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Subscriber(ctx){

                    @Override
                    public void onSuccess(Response response) {
                        UploadImageResult result = response.getEntity(UploadImageResult.class);
                        updateUserHeadImg(result.data);
                    }

                    @Override
                    public void onWrong(String msg) {
                        Logger.e(msg);
                        toast(msg);
                    }
                });
    }

    /**
     * 修改用户头像
     */
    public void updateUserHeadImg(final String image) {
        showWaitDialog("正在提交...");
        HTTP.service.post("user/update", MapParam.me()
                .p("image", image).build())
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Subscriber(ctx){

                    @Override
                    public void onSuccess(Response response) {
                        toast("头像修改成功");
                        userInfo.image = image;
                        spr.put(Constants.SPR_USER_JSON, JSON.toJSONString(userInfo));
                        showHeadImg();
                    }

                    @Override
                    public void onWrong(String msg) {
                        toast(msg, "头像修改失败.");
                    }
                });
    }

    /**
     * 设置极光推送别名
     */
    private void setJPushAlias() {
        if (userInfo != null) {
            JPushInterface.resumePush(getApplicationContext());
            JPushInterface.setAlias(getApplicationContext(), 1, userInfo.id);
            Set<String> set = new HashSet<String>();
            set.add(userInfo.id);
            JPushInterface.setTags(getApplicationContext(), 1, set);
        }
    }

    private void jpushLogout() {
        JPushInterface.setAlias(getApplicationContext(), 1, "");
        Set<String> set = new HashSet<String>();
        JPushInterface.setTags(getApplicationContext(), 1, set);
        JPushInterface.stopPush(getApplicationContext());
    }

    @Subscribe
    public void onUserUpdateSuccess(UserUpdateSuccessEvent event) {
        getUserInfo();
    }

    // 获取用户信息
    public void getUserInfo() {
        if (!checkNetwork()) {
            return;
        }
        HTTP.service.get("user/read")
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Subscriber(ctx){

                    @Override
                    public void onSuccess(Response response) {
                        if (response.data != null && !TextUtils.isEmpty(response.data.toString())) {
                            String json = AESHelper.decrypt(response.data.toString());
                            spr.put(Constants.SPR_USER_JSON, json);
                            EventBus.getDefault().post(new RefreshUserInfoEvent());
                            initUserInfo(json);
                        } else {
//                            toast("没有查询到信息.");
                        }
                    }

                    @Override
                    public void onWrong(String msg) {
                        toast("获取用户信息失败：" + msg);
                    }
                });
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
        fragmentList.add(new NewsFragment());
        fragmentList.add(new StudyFragment());
        fragmentList.add(new PioneersFragment());
        fragmentList.add(new PartyWorkFragment());
        fragmentList.add(new SuperviseFragment());
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

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();
        switch (item.getItemId()) {
            case R.id.partyMemberInfo:
                activity(UserInfoActivity.class);
                break;
            case R.id.partyMemberRelation:// 转入转出记录
                activity(PartyMemberRelationActivity.class);
                break;
            case R.id.personalScore:// 积分获取记录
                activity(ScoreActivity.class);
                break;
            case R.id.message:// 消息中心
                activity(MessageActivity.class);
//                activity(PieChartActivity.class);
                break;
            case R.id.exit:// 退出
                logout();
                break;
        }
//        toast(item.getTitle().toString());
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    private void logout() {
        new AlertDialog.Builder(this)
                .setTitle("提示")
                .setMessage("确定要退出登录吗？")
                .setPositiveButton("确认", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                        app.logout();
                        jpushLogout();
                    }
                })
                .setNegativeButton("取消", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                }).show();
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

    /**
     * 登录验证(后台)
     */
    public void loginValid() {
        if (!getIntent().getBooleanExtra("validUser", false) && !checkNetwork()) {
            return;
        }
//        final String name = SharedPreferencesUtils.create(ctx).get(Constants.SPR_NAME);
//        final String passwd = SharedPreferencesUtils.create(ctx).get(Constants.SPR_PASSWD);
//        final int version = SharedPreferencesUtils.create(ctx).getInt(Constants.SPR_VERSION, 0);
//        if (TextUtils.isEmpty(name) || TextUtils.isEmpty(passwd)) {
//            return;
//        }
//        HTTP.service.login(name, passwd)
//                .subscribeOn(Schedulers.io())
//                .observeOn(AndroidSchedulers.mainThread())
//                .subscribe(new Subscriber<User>(ctx){
//
//                    @Override
//                    public void onSuccess(Response<User> response) {
//                    }
//
//                    @Override
//                    public void onFail(String msg) {
//                        SQLiteHelper.create().clear(User.class);
//                        SharedPreferencesUtils.create(ctx).clear();
//                        new AlertDialog.Builder(ctx)
//                                .setTitle("提示")
//                                .setMessage("用户验证失败,请重新登录.")
//                                .setPositiveButton("确定", new DialogInterface.OnClickListener() {
//                                    @Override
//                                    public void onClick(DialogInterface dialog, int which) {
//                                        dialog.dismiss();
//                                        Intent intent = new Intent(ctx, LoginActivity.class);
//                                        startActivity(intent);
//                                        finish();
//                                    }
//                                }).setCancelable(false).show();
//                    }
//                });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_message:
                activity(MessageActivity.class);
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    private long mBackTime = -1;
    @Override
    public void onBackPressed() {
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
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
}
