package com.huaneng.zhdj.bean;

import java.io.Serializable;

/**
 * 转入转出记录
 */
public class PartyMemberRelation implements Serializable {

    public String id;
    public String userid;
    public String company1;//转入
    public String company2;//转出部门
    public String description;//变动原因
    public String update_time;
    public String create_time;
    public String username;
}
