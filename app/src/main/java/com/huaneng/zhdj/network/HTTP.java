package com.huaneng.zhdj.network;

/**
 * Created by TH on 2017/11/8.
 */

public class HTTP {

    public static final HTTPService service;
    static {
        service = RetrofitUtils.getService(HTTPService.class);
    }
}
