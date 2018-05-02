package com.zuby.user.zubbyrider.view.registration_login.presenter;

import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;

import com.google.gson.Gson;
import com.squareup.okhttp.OkHttpClient;
import com.squareup.okhttp.Request;
import com.zuby.user.zubbyrider.interfaces.ResultInterface1;
import com.zuby.user.zubbyrider.utils.ApiKeys;
import com.zuby.user.zubbyrider.utils.PreferenceConnector;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;

public class LoginPresenter {
    private ResultInterface1 mResultInterface;
    private Context mContext;
    private String mData;
    private final String TAG = LoginPresenter.class.getSimpleName();

    public void show(ResultInterface1 resultInterface, Context context, String country_code
            , String mobileNumber, String password) {
        mContext = context;
        mResultInterface = resultInterface;
        HashMap hashMap = new HashMap();
        hashMap.put("country_code", country_code);
        hashMap.put("mobile_no", mobileNumber);
        hashMap.put("time_zone", PreferenceConnector.readString(mContext, ApiKeys.TIMEZONE, ""));
        hashMap.put("tokenid", PreferenceConnector.readString(mContext, ApiKeys.TOKEN, ""));
        hashMap.put("password", password);
        hashMap.put("login_device", "rider_android");
        hashMap.put("login_method", "inapp");
        hashMap.put("os_version_name", "7.1");
        hashMap.put("cabi_version_used", "1");
        hashMap.put("session_login_type", "rider");
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
                            .url(ApiKeys.BASE_URL + "login")
                            .post(body)
                            .addHeader("content-type", "application/x-www-form-urlencoded")
                            .build();
                    com.squareup.okhttp.Response response = client.newCall(request).execute();
                    Log.e("login", "" + response.body());
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
                   PreferenceConnector.writeString(mContext, ApiKeys.SESSION_ID, oneObject.getString("session_id"));
                   mResultInterface.onSuccess(mResponse);
                } else {
                    mResultInterface.onFailed(mResponse);
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }

        }
    }
}