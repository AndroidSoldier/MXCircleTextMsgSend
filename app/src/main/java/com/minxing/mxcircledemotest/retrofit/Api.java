package com.minxing.mxcircledemotest.retrofit;


import com.google.gson.JsonObject;

import retrofit2.Call;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.POST;

public interface Api {

    @FormUrlEncoded
    @POST("oauth2/token")
    Call<JsonObject> oauth2(@Field("grant_type") String grant_type,
                            @Field("login_name") String login_name,
                            @Field("nonce") String nonce,
                            @Field("password") String password,
                            @Field("client_id") String client_id,
                            @Field("include_user") String include_user,
                            @Field("device_uuid") String device_uuid,
                            @Field("mqtt") String mqtt);


    @FormUrlEncoded
    @POST("api/v1/messages")
    Call<JsonObject> sendTextMessage(@Field("group_id") int group_id, @Field("body") String body);
}
