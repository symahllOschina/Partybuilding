package com.huaneng.zhdj.bean;

import org.xutils.db.annotation.Column;
import org.xutils.db.annotation.Table;

/**
 * 登录成功返回信息
 */
public class LoginResInfo {

    public String token;

    public User user;
}
