package com.huaneng.zhdj.network;

import java.util.List;
import java.util.Map;

import io.reactivex.Observable;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.http.Field;
import retrofit2.http.FieldMap;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.Part;
import retrofit2.http.PartMap;
import retrofit2.http.QueryMap;
import retrofit2.http.Url;

/**
 * Created by TH on 2017/11/8.
 */

public interface HTTPService {

    @FormUrlEncoded
    @POST
    Observable<Response> post(@Url String url, @FieldMap Map<String,Object> map);

    @FormUrlEncoded
    @POST
    Observable<Response> post(@Url String url);

    @GET
    Observable<Response> get(@Url String url);

    @GET
    Observable<Response> get(@Url String url, @QueryMap Map<String, Object> map);


    @Multipart
    @POST("image/saveall")
    Observable<Response> uploadFiles(@PartMap Map<String, RequestBody> files, @Part("type") String type);

    @Multipart
    @POST("image/save")
//    Observable<Response> uploadFile(@Part("file" + "\";filename=\"" + "test.png") RequestBody file, @Part("type") String type);
    Observable<Response> uploadFile(@Part("file\";filename=\"test.png\"") RequestBody file, @Part("type") String type);

//    @GET("backconfig/getlist")
//    Observable<Response<AppVersion>> getAppVersion(@Query("type") String type);
}
