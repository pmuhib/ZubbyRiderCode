package com.zuby.user.zubbyrider.utils;
import com.squareup.okhttp.RequestBody;
import com.zuby.user.zubbyrider.view.registration_login.model.OtpVarifyModel;
import com.zuby.user.zubbyrider.view.registration_login.model.SendOtpModel;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;
import org.json.JSONException;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.Headers;
import retrofit2.http.POST;

public interface ApiService2 {
    @Headers("content-type:application/x-www-form-urlencoded")
    @POST("otp_send")
    Call<SendOtpModel> sendOtp(@Body RequestBody title);
    @Headers("content-type:application/x-www-form-urlencoded")
    @POST("otp_verification")
    Call<OtpVarifyModel> varifyOtp(@Body RequestBody title);
}