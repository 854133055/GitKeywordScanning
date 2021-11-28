package com.mml.plugin.utils;


import okhttp3.OkHttpClient;

import java.util.concurrent.TimeUnit;

public class HttpUtil {

    private static OkHttpClient httpClient;

    private static OkHttpClient.Builder httpClientBuilder;

    private static OkHttpClient init() {
        synchronized (HttpUtil.class) {
            if (httpClient == null) {
                if (httpClientBuilder == null) {
                    httpClientBuilder = new OkHttpClient.Builder()
                            .connectTimeout(20, TimeUnit.SECONDS)
                            .writeTimeout(20, TimeUnit.SECONDS)
                            .readTimeout(30, TimeUnit.SECONDS);
                }
                httpClient = httpClientBuilder.build();
            }
        }
        return httpClient;
    }

    public static OkHttpClient getInstance() {
        return httpClient == null ? init() : httpClient;
    }

    public static void setInstance(OkHttpClient okHttpClient) {
        HttpUtil.httpClient = okHttpClient;
    }

    public static OkHttpClient.Builder getInstanceBuilder() {
        if (httpClientBuilder == null) {
            httpClientBuilder = new OkHttpClient.Builder()
                    .connectTimeout(20, TimeUnit.SECONDS)
                    .writeTimeout(20, TimeUnit.SECONDS)
                    .readTimeout(30, TimeUnit.SECONDS);
        }
        return httpClientBuilder;
    }

}
