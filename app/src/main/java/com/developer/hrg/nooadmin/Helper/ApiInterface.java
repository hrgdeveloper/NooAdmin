package com.developer.hrg.nooadmin.Helper;

import com.developer.hrg.nooadmin.Models.SimpleResponse;

import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import retrofit2.http.Part;
import retrofit2.http.Path;

/**
 * Created by hamid on 5/30/2018.
 */

public interface ApiInterface {
    @FormUrlEncoded
    @POST("LogAdmin_noor")
    Call<SimpleResponse> login(@Field("username") String username,@Field("password") String password);

    @GET("getUsers")
    Call<SimpleResponse> getUsers(@Header("AuthorizationMyAd") String header);

    @FormUrlEncoded
    @PUT("changeUserActive/{user_id}")
    Call<SimpleResponse> updateUserActive(@Header("AuthorizationMyAd") String header , @Path("user_id") int user_id , @Field("status") int status);

    @Multipart
    @POST("makeChanel")
    Call<SimpleResponse> mChanel(@Header("AuthorizationMyAd") String header ,@Part MultipartBody.Part pic , @Part("details")RequestBody details);


    @GET("getAllChanels")
    Call<SimpleResponse> getAllChanels(@Header("AuthorizationMyAd") String header);

}
