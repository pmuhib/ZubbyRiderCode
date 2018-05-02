package com.zuby.user.zubbyrider.view.registration_login.presenter;

import android.content.Context;
import android.util.Log;

import com.google.gson.Gson;
import com.zuby.user.zubbyrider.interfaces.ResultInterface;
import com.zuby.user.zubbyrider.utils.ApiKeys;
import com.zuby.user.zubbyrider.utils.ApiService;
import com.zuby.user.zubbyrider.utils.PreferenceConnector;
import com.zuby.user.zubbyrider.view.registration_login.model.HasPasswordModel;

import java.util.HashMap;

import okhttp3.MediaType;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class HasPasswordPresenter {
    private ApiService mApiService;
    private ResultInterface mResultInterface;
    private Context mContext;
    private final String TAG = HasPasswordPresenter.class.getSimpleName();

    public void show(ResultInterface resultInterface, Context context, ApiService apiService, String country_code
            , String mobileNumber) {
        mContext = context;
        mApiService = apiService;
        mResultInterface = resultInterface;
        HashMap hashMap = new HashMap();
        hashMap.put("country_code", country_code);
        hashMap.put("mobile_no", mobileNumber);
        hashMap.put("tokenid", PreferenceConnector.readString(mContext, ApiKeys.TOKEN, ""));
        Log.d(TAG, new Gson().toJson(hashMap));
        addService(new Gson().toJson(hashMap));
    }


    public void addService(String data) {
        RequestBody requestBody = RequestBody.create(MediaType.parse("application/octet-stream"),data);
        Call<HasPasswordModel> addService = mApiService.checkPassword(requestBody);
        addService.enqueue(new Callback<HasPasswordModel>() {
            @Override
            public void onResponse(Call<HasPasswordModel> call,
                                   Response<HasPasswordModel> response) {
                try {
                    Log.e(TAG,new Gson().toJson(response.body()));
                    if (response.body().getType().equals("success")) {
                        mResultInterface.onSuccess(response.body());
                    } else {
                        mResultInterface.onFailed(response.body().getMessage());
                    }
                } catch (Exception e) {
                    mResultInterface.onFailed("No Data Found");
                }
            }

            @Override
            public void onFailure(Call<HasPasswordModel> call, Throwable t) {
                mResultInterface.onFailed("No Data Found");
                Log.d(TAG, "" + t);
            }
        });
    }
}