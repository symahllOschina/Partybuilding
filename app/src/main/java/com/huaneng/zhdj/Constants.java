package com.huaneng.zhdj;

import android.os.Environment;

/**
 * Created by Administrator on 2017/11/9.
 */

public class Constants {

    public static final String APP_CODE = "hnsite";
//    public static final String HEWEATHER_KEY = "eda470563d2947d4bb8ddd6a7a5a2c93";
    public static final String HEWEATHER_KEY = "bbf9b7ee438343e397952f1a71be697a";
    public static final String APK_DOWNLOAD_URL = "http://113.200.203.98:8000/api/v1/backconfig/download";
    public static String APK_DOWNLOAD_PATH = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS).getAbsolutePath();

    public static final String SPR_NAME = "name";
    public static final String SPR_PASSWD = "passwd";
    public static final String SPR_TOKEN = "token";
    public static final String SPR_VERSION = "version";
    public static final String SPR_LOGIN = "login";
    public static final String SPR_USER_JSON = "user_info_json";
    public static final String SPR_UPDATE = "apk_update";
    public static final String SPR_LOC_CITY = "loc_city";
    public static final String SPR_LOC_LONGLAT = "loc_long_lat";
    public static final String SPR_MENU_JSON = "menu_json";
    public static final String SPR_CAMERA_PERMISSION_IGNORE = "camera_permission_ignore";
    // 上一次会议的时间
    public static final String SPR_LAST_MEETING_TIME = "last_meeting_time";

    public static final String SPR_VM_IP = "vm_ip";
    public static final String SPR_VM_PORT = "vm_port";
    public static final String SPR_VM_LOGINID = "vm_loginid";
    public static final String SPR_VM_PASSWD = "vm_passwd";

    public static final String DB_NAME = "hnsite.db";
    public static final int DB_VERSION = 1;
}
