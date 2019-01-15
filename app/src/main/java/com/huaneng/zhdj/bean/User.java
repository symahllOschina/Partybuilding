package com.huaneng.zhdj.bean;

import org.xutils.db.annotation.Column;
import org.xutils.db.annotation.Table;

/**
 * Created by Administrator on 2017/11/11.
 */
@Table(name = "user")
public class User {

    @Column(name = "id", isId = true)
    public String userid;

}
