package com.zuby.user.zubbyrider.utils;


import com.zuby.user.zubbyrider.view.registration_login.model.AddRiderDataModel;
import com.zuby.user.zubbyrider.view.registration_login.model.HasPasswordModel;
import com.zuby.user.zubbyrider.view.registration_login.model.Level2RegistrationModel;
import com.zuby.user.zubbyrider.view.registration_login.model.MobileNumberRegistrationModel;
import com.zuby.user.zubbyrider.view.registration_login.model.SendOtpModel;

import java.util.Map;

import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.Field;
import retrofit2.http.FieldMap;
import retrofit2.http.Header;
import retrofit2.http.Headers;
import retrofit2.http.POST;

public interface ApiService {
    @Headers("Content-Type: application/x-www-form-urlencoded")
    @POST(ApiKeys.DREAMBASE1)
    Call<MobileNumberRegistrationModel> registerMobile(@Body RequestBody title);

    @Headers("Content-Type: application/x-www-form-urlencoded")
    @POST("level2Register")
    Call<Level2RegistrationModel> level2registration(@Body RequestBody title);

    @Headers("content-type:application/x-www-form-urlencoded")
    @POST("otp_send")
    Call<SendOtpModel> sendOtp(@Body RequestBody title);

    @POST("hasPassword")
    Call<HasPasswordModel> checkPassword(@Body RequestBody title);

    @POST("addRiderData")
    Call<AddRiderDataModel> addRiderData(@Body RequestBody title);
}