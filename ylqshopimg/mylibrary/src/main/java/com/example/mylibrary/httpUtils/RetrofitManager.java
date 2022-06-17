package com.example.mylibrary.httpUtils;

import retrofit2.Retrofit;
import retrofit2.adapter.rxjava.RxJavaCallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;

public class RetrofitManager {

    public static <T> T getApi(Class<T> service, String baseUrl) {
        Retrofit mRetrofit = new Retrofit.Builder()
                .client(OkHttpClientManager.getInstance().getOkHttpClient())
                .addCallAdapterFactory(RxJavaCallAdapterFactory.create())
                .addConverterFactory(GsonConverterFactory.create())
                .baseUrl(baseUrl)
                .build();
        return mRetrofit.create(service);
    }
}
