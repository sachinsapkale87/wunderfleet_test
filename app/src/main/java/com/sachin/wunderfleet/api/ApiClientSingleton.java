package com.sachin.wunderfleet.api;

import android.app.Application;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.sachin.wunderfleet.BuildConfig;

import java.io.IOException;
import java.util.concurrent.TimeUnit;

import io.reactivex.schedulers.Schedulers;
import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;

public class ApiClientSingleton extends Application {
    private static String TOKEN = "";
    private static Retrofit retrofit;
    private static Request original;

    public static Retrofit getRetrofitInstance() {
        if (retrofit == null) {

            OkHttpClient.Builder httpClient = new OkHttpClient.Builder();


            httpClient.readTimeout(30, TimeUnit.SECONDS);
            httpClient.connectTimeout(30, TimeUnit.SECONDS);
            httpClient.writeTimeout(30, TimeUnit.SECONDS);

            httpClient.addInterceptor(new Interceptor() {
                @Override
                public Response intercept(Chain chain) throws IOException {
                    original = chain.request();

                    Request.Builder builder = original.newBuilder();
                    builder.method(original.method(), original.body());
//                    builder.header("Accept", "application/json");
                    if (TOKEN.length() > 0)
                        builder.header("Authorization", "Bearer "+TOKEN);
                    return chain.proceed(builder.build());
                }
            });

            HttpLoggingInterceptor interceptor = new HttpLoggingInterceptor();
            if (BuildConfig.DEBUG) {
                interceptor.setLevel(HttpLoggingInterceptor.Level.BODY);
            } else {
                interceptor.setLevel(HttpLoggingInterceptor.Level.NONE);
            }
            httpClient.addInterceptor(interceptor);

            OkHttpClient client = httpClient.build();
            Gson gson = new GsonBuilder()
                    .setLenient()
                    .create();

            retrofit = new Retrofit.Builder()
                    .baseUrl(WebAPI.BASE_URL)
                    .client(client)
                    .addCallAdapterFactory(RxJava2CallAdapterFactory.createWithScheduler(Schedulers.io()))
                    .addConverterFactory(GsonConverterFactory.create(gson))
                    .build();
        }
        return retrofit;
    }

    public static void resetRetrofitInstance() {
        retrofit = null;
    }

    public static void setToken(String token) {
        TOKEN = token;
        retrofit = null;
    }
}
