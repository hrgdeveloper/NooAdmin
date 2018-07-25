package com.developer.hrg.nooadmin.Helper;

import android.support.annotation.Nullable;

import com.developer.hrg.nooadmin.Models.SimpleResponse;

import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.http.Body;
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


    @Multipart
    @POST("updateChanelPic/{chanel_id}")
    Call<SimpleResponse> updateChanelPic(@Header("AuthorizationMyAd") String header ,@Path("chanel_id") int chanel_id ,  @Part MultipartBody.Part pic , @Part("last_pic_name") RequestBody content);

    @Multipart
    @POST("addChanelPic/{chanel_id}")
    Call<SimpleResponse> addChanelPic(@Header("AuthorizationMyAd") String header ,@Path("chanel_id") int chanel_id ,  @Part MultipartBody.Part pic );


    @GET("getAllChanels")
    Call<SimpleResponse> getAllChanels(@Header("AuthorizationMyAd") String header);

    @Multipart
    @POST("chanels/{id}/message")
    Call<SimpleResponse> makeSimpleMessage(@Header("AuthorizationMyAd") String header,@Path("id") int chanel_id, @Part("content") RequestBody content

    );

    @Multipart
    @POST("chanels/{id}/message")
    Call<SimpleResponse> makePictureMessage(@Header("AuthorizationMyAd") String header,@Path("id") int chanel_id, @Part("content") RequestBody content
            , @Part MultipartBody.Part file
    );

    @Multipart
    @POST("chanels/{id}/message")
    Call<SimpleResponse> makeVideoMessage(@Header("AuthorizationMyAd") String header,@Path("id") int chanel_id, @Part("content") RequestBody content
            , @Part MultipartBody.Part videoFile , @Part MultipartBody.Part file
    );

    @Multipart
    @POST("chanels/{id}/message")
    Call<SimpleResponse> makeAudioMessage(@Header("AuthorizationMyAd") String header,@Path("id") int chanel_id, @Part("content") RequestBody content
            , @Part MultipartBody.Part audiFile
    );

    @Multipart
    @POST("chanels/{id}/message")
    Call<SimpleResponse> makeFileMessage(@Header("AuthorizationMyAd") String header,@Path("id") int chanel_id, @Part("content") RequestBody content
            , @Part MultipartBody.Part audiFile
    );


    @GET("getAllComments/{chanel_id}")
    Call<SimpleResponse> getAllComments(@Header("AuthorizationMyAd") String header,@Path("chanel_id") int chanel_id
    );

    @FormUrlEncoded
    @PUT("setCommentState/{comment_id}")
    Call<SimpleResponse> setCommentState(@Header("AuthorizationMyAd") String header , @Path("comment_id") int user_id , @Field("state") int status);




}
