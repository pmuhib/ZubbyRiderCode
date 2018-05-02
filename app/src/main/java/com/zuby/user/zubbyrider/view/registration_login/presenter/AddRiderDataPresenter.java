package com.zuby.user.zubbyrider.view.registration_login.presenter;

import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;

import com.google.gson.Gson;
import com.squareup.okhttp.OkHttpClient;
import com.squareup.okhttp.Request;
import com.zuby.user.zubbyrider.interfaces.ResultInterface;
import com.zuby.user.zubbyrider.utils.ApiKeys;
import com.zuby.user.zubbyrider.utils.PreferenceConnector;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;

import okhttp3.MediaType;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;

public class AddRiderDataPresenter {
    private ResultInterface mResultInterface;
    private Context mContext;
    private final String TAG = AddRiderDataPresenter.class.getSimpleName();
    private String mData;

    public void show(ResultInterface resultInterface, Context context, String country_code
            , String mobileNumber, String firstname, String lastname,  String referalcode, String selectedlanguage, String city
            , String latitude, String longitude, String emailid,String areaname) {
        mContext = context;
        mResultInterface = resultInterface;
        HashMap hashMap = new HashMap();
        String mobile = country_code + mobileNumber;
        hashMap.put("phone_no", mobile);
        hashMap.put("rider_id", PreferenceConnector.readString(mContext, ApiKeys.UID, ""));
        hashMap.put("tokenid", PreferenceConnector.readString(mContext, ApiKeys.TOKEN, ""));
        hashMap.put("first_name", firstname);
        hashMap.put("last_name", lastname);
        hashMap.put("area_name", areaname);
        hashMap.put("referring_driver_id", referalcode);
        hashMap.put("selected_language", selectedlanguage);
        hashMap.put("city", city);
        hashMap.put("download_latitude", latitude);
        hashMap.put("download_longitude", longitude);
        hashMap.put("time_zone", PreferenceConnector.readString(mContext, ApiKeys.TIMEZONE, ""));
        hashMap.put("email_id", emailid);
        Log.d(TAG, new Gson().toJson(hashMap));
        addService(new Gson().toJson(hashMap));
    }
//    {"message":"Mobile no. already exists","type":"failure"}
//
//    {"data":{"country_code":"91","mobile_no":"1234567890","registration_time":"2018-04-03 15:46:23",
//            "time_zone":"Asia/Calcutta","user_id":"CAB7890_00000007"},"message":"","type":"success"}


    public void addService(String data) {
        this.mData = data;
        Log.e(TAG, mData);
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
                            .url(ApiKeys.RIDER_BASE_URL + "addRiderData")
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
                    mResultInterface.onSuccess(mResponse);
                } else {
                    mResultInterface.onFailed(oneObject.getString("message"));
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }

        }
    }
}