package com.zuby.user.zubbyrider.view.registration_login.presenter;

import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;

import com.google.android.gms.common.api.Api;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.squareup.okhttp.OkHttpClient;
import com.squareup.okhttp.Request;
import com.zuby.user.zubbyrider.interfaces.ResultInterface1;
import com.zuby.user.zubbyrider.utils.ApiKeys;
import com.zuby.user.zubbyrider.utils.ApiService;
import com.zuby.user.zubbyrider.utils.PreferenceConnector;
import com.zuby.user.zubbyrider.view.registration_login.model.Level2RegistrationModel;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;

import okhttp3.MediaType;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class Level2RegistraionPresenter {
    private ApiService mApiService;
    private ResultInterface1 mResultInterface;
    private Context mContext;
    private String mData;
    private final String TAG = Level2RegistraionPresenter.class.getSimpleName();

    public void show(ResultInterface1 resultInterface, Context context, ApiService apiService, String country_code
            , String mobileNumber, String firstname, String lastname) {
        mContext = context;
        mApiService = apiService;
        mResultInterface = resultInterface;
        HashMap hashMap = new HashMap();
        hashMap.put("country_code", country_code);
        hashMap.put("mobile_no", mobileNumber);
        hashMap.put("time_zone", PreferenceConnector.readString(mContext, ApiKeys.TIMEZONE, ""));
        hashMap.put("tokenid", PreferenceConnector.readString(mContext, ApiKeys.TOKEN, ""));
        hashMap.put("first_name", firstname);
        hashMap.put("last_name", lastname);
        hashMap.put("access_type", "rider");
        Log.d(TAG, new Gson().toJson(hashMap));
        addService(new Gson().toJson(hashMap));
    }

    public void addService(String data) {
        this.mData = data;
        new MainClass().execute();
    }

    com.squareup.okhttp.Response mResponse;

    class MainClass extends AsyncTask<String, String, String> {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            // display a progress dialog for good user experiance
        }

        @Override
        protected String doInBackground(String... params) {
            String current = "";
            try {
                try {
                    OkHttpClient client = new OkHttpClient();
                    com.squareup.okhttp.MediaType mediaType = com.squareup.okhttp.MediaType.parse("application/json");
                    com.squareup.okhttp.RequestBody body = com.squareup.okhttp.RequestBody.create(mediaType, mData);
                    Request request = new Request.Builder()
                            .url(ApiKeys.BASE_URL + "level2Register")
                            .post(body)
                            .addHeader("content-type", "application/x-www-form-urlencoded")
                            .build();
                    com.squareup.okhttp.Response response = client.newCall(request).execute();
                    Log.e(TAG, "" + response.body());
                    String jsonData = response.body().string();
                    Log.e(TAG, jsonData);
                    mResponse = response;
//                    JSONObject Jobject = new JSONObject(jsonData);
//                    System.out.println(Jobject.toString());
//                    current = Jobject.toString();
                    current = jsonData;
                } catch (Exception e) {
                    e.printStackTrace();
                } finally {
                }
            } catch (Exception e) {
                e.printStackTrace();
                return "Exception: " + e.getMessage();
            }
            return current;
        }

        @Override
        protected void onPostExecute(String s) {
            Log.e(TAG, s);
            try {
                JSONObject oneObject = new JSONObject(s);
                String type = oneObject.getString("type");
                if (type.equalsIgnoreCase("success")) {
                    Level2RegistrationModel level2RegistrationModel = new Level2RegistrationModel();
                    Level2RegistrationModel.DataBean dataBean = new Level2RegistrationModel.DataBean();
                    String data = oneObject.getString("data");
                    JSONObject jsonObject = new JSONObject(data);
                    dataBean.setAccess_type(jsonObject.getString("access_type"));
                    dataBean.setFirst_name(jsonObject.getString("first_name"));
                    dataBean.setLast_name(jsonObject.getString("last_name"));
                    dataBean.setMobile_no(jsonObject.getString("mobile_no"));
                    dataBean.setStatus(jsonObject.getString("status"));
                    dataBean.setUser_id(jsonObject.getString("user_id"));
                    dataBean.setCountry_code(jsonObject.getString("country_code"));
                    level2RegistrationModel.setData(dataBean);
                    level2RegistrationModel.setMessage(oneObject.getString("message"));
                    level2RegistrationModel.setType(oneObject.getString("type"));
                    mResultInterface.onSuccess(level2RegistrationModel);
                } else {
                    mResultInterface.onFailed(oneObject.getString("message"));
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }

        }
    }
}