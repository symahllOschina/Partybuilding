package com.huaneng.zhdj.user;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;

import com.alibaba.fastjson.JSON;
import com.huaneng.zhdj.BaseActivity;
import com.huaneng.zhdj.Constants;
import com.huaneng.zhdj.R;
import com.huaneng.zhdj.bean.RefreshUserInfoEvent;
import com.huaneng.zhdj.bean.UserInfo;
import com.huaneng.zhdj.bean.UserUpdateSuccessEvent;
import com.huaneng.zhdj.utils.SharedPreferencesUtils;
import com.huaneng.zhdj.utils.UIUtils;

import org.greenrobot.eventbus.Subscribe;
import org.xutils.view.annotation.ContentView;
import org.xutils.view.annotation.ViewInject;

/**
 * 个人基本信息
 */
@ContentView(R.layout.activity_user_info)
public class UserInfoActivity extends BaseActivity {

    @ViewInject(R.id.nameEt)
    TextView nameEt;
    @ViewInject(R.id.sexEt)
    TextView sexEt;
    @ViewInject(R.id.nationEt)
    TextView nationEt;
    @ViewInject(R.id.phoneEt)
    TextView phoneEt;
    @ViewInject(R.id.idEt)
    TextView idEt;
    @ViewInject(R.id.integralEt)
    TextView integralEt;
    @ViewInject(R.id.schoolEt)
    TextView schoolEt;
    @ViewInject(R.id.majorEt)
    TextView majorEt;
    @ViewInject(R.id.joinPartyDateEt)
    TextView joinPartyDateEt;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setTitle("个人信息");
        init();
    }

    @Override
    protected boolean isEnableEventBus() {
        return true;
    }

    @Subscribe
    public void onEvent(RefreshUserInfoEvent event) {
        init();
    }

    private void init() {
        String json = SharedPreferencesUtils.create(ctx).get(Constants.SPR_USER_JSON);
        if (TextUtils.isEmpty(json)) {
            toast("未获取到用户信息");
            finish();
        } else {
            UserInfo userInfo = JSON.parseObject(json, UserInfo.class);
            String joinPartyDate = userInfo.getJoinPartyTime();
            UIUtils.showText(nameEt, userInfo.username);
            UIUtils.showText(sexEt, userInfo.sex);
            UIUtils.showText(nationEt, userInfo.nation);
            UIUtils.showText(phoneEt, userInfo.phone);
            UIUtils.showText(idEt, userInfo.cardnum);
            UIUtils.showText(integralEt, userInfo.integral);
            UIUtils.showText(schoolEt, userInfo.school);
            UIUtils.showText(majorEt, userInfo.major);
            UIUtils.showText(joinPartyDateEt, joinPartyDate);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_edit, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_edit:
                activity(UserInfoUpdateActivity.class);
                break;
        }
        return super.onOptionsItemSelected(item);
    }
}
