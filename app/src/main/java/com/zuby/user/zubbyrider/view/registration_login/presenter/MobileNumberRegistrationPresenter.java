package com.zuby.user.zubbyrider.view.registration_login.presenter;

import android.content.Context;
import android.util.Log;

import com.google.gson.Gson;
import com.zuby.user.zubbyrider.interfaces.ResultInterface1;
import com.zuby.user.zubbyrider.utils.ApiKeys;
import com.zuby.user.zubbyrider.utils.ApiService;
import com.zuby.user.zubbyrider.utils.PreferenceConnector;
import com.zuby.user.zubbyrider.view.registration_login.model.MobileNumberRegistrationModel;

import java.util.HashMap;
import java.util.Map;

import okhttp3.MediaType;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;

public class MobileNumberRegistrationPresenter {
    private ApiService mApiService;
    private ResultInterface1 mResultInterface;
    private Context mContext;
    private final String TAG = MobileNumberRegistrationPresenter.class.getSimpleName();

    public void show(ResultInterface1 resultInterface, Context context, ApiService apiService, String country_code
            , String mobileNumber) {
        mContext = context;
        mApiService = apiService;
        mResultInterface = resultInterface;
        HashMap hashMap = new HashMap();
        hashMap.put("country_code", country_code);
        hashMap.put("mobile_no", mobileNumber);
        hashMap.put("time_zone", PreferenceConnector.readString(mContext, ApiKeys.TIMEZONE, ""));
        hashMap.put("tokenid", PreferenceConnector.readString(mContext, ApiKeys.TOKEN, ""));
        Log.d(TAG, new Gson().toJson(hashMap));
        addService(new Gson().toJson(hashMap));
    }


    public void addService(String data) {
        RequestBody requestBody = RequestBody.create(MediaType.parse(""), data);
        Call<MobileNumberRegistrationModel> addService = mApiService.registerMobile(requestBody);
        addService.enqueue(new Callback<MobileNumberRegistrationModel>() {
            @Override
            public void onResponse(Call<MobileNumberRegistrationModel> call,
                                   Response<MobileNumberRegistrationModel> response) {
                try {
                    Log.e(TAG, new Gson().toJson(response.body()));
                    if (response.body().getType().equals("success")) {
                        mResultInterface.onSuccess(response.body());
                    } else {
                        mResultInterface.onFailed(response.body());
                    }
                } catch (Exception e) {
                    mResultInterface.onFailed("No Data Found");
                }
            }

            @Override
            public void onFailure(Call<MobileNumberRegistrationModel> call, Throwable t) {
                mResultInterface.onFailed("No Data Found");
                Log.d(TAG, "" + t);
            }
        });
    }
}