package com.minxing.mxcircledemotest.retrofit;



import com.minxing.mxcircledemotest.MXContact;

import java.io.IOException;
import java.util.Map;

import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class RetrofitManager {

    private static RetrofitManager retrofitManager;

    private RetrofitManager() {
    }

    public static RetrofitManager getInstance() {
        if (retrofitManager == null) {
            retrofitManager = new RetrofitManager();
        }

        return retrofitManager;
    }

    public Retrofit getDefaultRetrofit() {
        Retrofit retrofit = new Retrofit.Builder()
                .client(new OkHttpClient())
                .addConverterFactory(GsonConverterFactory.create())
                .baseUrl(MXContact.serverAddress + "/")
                .build();

        return retrofit;
    }

    public Retrofit getRetrofitWithHeader(Map<String, String> headers) {
        Retrofit retrofit = new Retrofit.Builder()
                .client(getOkHttpClientWithHeader(headers))
                .addConverterFactory(GsonConverterFactory.create())
                .baseUrl(MXContact.serverAddress + "/")
                .build();

        return retrofit;
    }


//    public OkHttpClient getOkHttpClientWithNetIDHeader() {
//        OkHttpClient okHttpClient = new OkHttpClient.Builder()
//                .addInterceptor(new Interceptor() {
//                    @Override
//                    public Response intercept(Chain chain) throws IOException {
//
//                        UserAccount user = MXCacheManager.getInstance().getCurrentUser();
//                        String netWorkID = "";
//                        if (user != null) {
//                            UserIdentity userIdentity = user.getCurrentIdentity();
//                            if (userIdentity != null) {
//                                netWorkID = String.valueOf(userIdentity.getNetwork_id());
//                            }
//                        }
//                        Request request = chain.request()
//                                .newBuilder()
//                                .addHeader("NETWORK-ID", netWorkID)
//                                .build();
//
//
//                        return chain.proceed(request);
//                    }
//                }).build();
//
//        return okHttpClient;
//    }


//    public OkHttpClient getOkHttpClientWithTokenHeader() {
//        OkHttpClient okHttpClient = new OkHttpClient.Builder()
//                .addInterceptor(new Interceptor() {
//                    @Override
//                    public Response intercept(Chain chain) throws IOException {
//
//                        UserAccount user = MXCacheManager.getInstance().getCurrentUser();
//                        String netWorkID = "";
//                        if (user != null) {
//                            UserIdentity userIdentity = user.getCurrentIdentity();
//                            if (userIdentity != null) {
//
//                            }
//                        }
//                        Request request = chain.request()
//                                .newBuilder()
//                                .addHeader("Authorization-ID", netWorkID)
//                                .build();
//
//
//                        return chain.proceed(request);
//                    }
//                }).build();
//
//        return okHttpClient;
//    }

    public OkHttpClient getOkHttpClientWithHeader(final Map<String, String> headers) {
        OkHttpClient client = new OkHttpClient
                .Builder()
                .addInterceptor(new Interceptor() {
                    @Override
                    public Response intercept(Chain chain) throws IOException {
                        Request request = chain.request();
                        Request.Builder builder = request.newBuilder();
                        for (String key : headers.keySet()){
                            builder = builder.addHeader(key,headers.get(key));
                        }
                        request = builder.build();
                        return chain.proceed(request);
                    }
                }).build();

        return client;
    }

    public OkHttpClient getOkHttpClientWithInterceptor(Interceptor interceptor) {
        OkHttpClient client = new OkHttpClient
                .Builder()
                .addInterceptor(interceptor).build();

        return client;
    }

}
