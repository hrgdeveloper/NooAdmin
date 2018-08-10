package com.developer.hrg.nooadmin.Helper;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.util.concurrent.TimeUnit;

import okhttp3.OkHttpClient;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.converter.scalars.ScalarsConverterFactory;

/**
 * Created by hamid on 5/30/2018.
 */

public class Client {
    private static Retrofit retrofit =null ;
    public static Retrofit getClient() {
        if (retrofit==null) {
            Gson gson = new GsonBuilder().setLenient().create();
            final OkHttpClient okHttpClient = new OkHttpClient.Builder()
                    .connectTimeout(15, TimeUnit.SECONDS)
                    .writeTimeout(90, TimeUnit.MINUTES)
                    .readTimeout(90, TimeUnit.MINUTES)
                    .build();
            retrofit=new Retrofit.Builder()
                    .baseUrl(Config.OFFLINE_URL)
                    .client(okHttpClient)
                    .addConverterFactory(ScalarsConverterFactory.create())
                    .addConverterFactory(GsonConverterFactory.create(gson))
                    .build();
        }
        return retrofit;
    }
}
