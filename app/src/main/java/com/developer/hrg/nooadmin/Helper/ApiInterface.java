package com.developer.hrg.nooadmin.Helper;

import com.developer.hrg.nooadmin.Models.SimpleResponse;

import retrofit2.Call;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.POST;

/**
 * Created by hamid on 5/30/2018.
 */

public interface ApiInterface {
    @FormUrlEncoded
    @POST("LogAdmin_noor")
    Call<SimpleResponse> login(@Field("username") String username,@Field("password") String password);




}
