package com.zuby.user.zubbyrider.view.registration_login.presenter;

import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;

import com.google.gson.Gson;
import com.squareup.okhttp.OkHttpClient;
import com.squareup.okhttp.Request;
import com.zuby.user.zubbyrider.interfaces.ResultInterface;
import com.zuby.user.zubbyrider.utils.ApiKeys;
import com.zuby.user.zubbyrider.utils.ApiService;
import com.zuby.user.zubbyrider.utils.PreferenceConnector;
import com.zuby.user.zubbyrider.view.registration_login.model.GetRiderDataModel;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;

public class GetRiderDataPresenter {
    private ApiService mApiService;
    private ResultInterface mResultInterface;
    private Context mContext;
    private String mData;
    private final String TAG = GetRiderDataPresenter.class.getSimpleName();

    public void show(ResultInterface resultInterface, Context context) {
        mContext = context;
        mResultInterface = resultInterface;
        HashMap hashMap = new HashMap();
        hashMap.put("tokenid", PreferenceConnector.readString(mContext, ApiKeys.TOKEN, ""));
        hashMap.put("rider_id", PreferenceConnector.readString(mContext, ApiKeys.UID, ""));
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
                            .url(ApiKeys.RIDER_BASE_URL + "riderData")
                            .post(body)
                            .addHeader("content-type", "application/x-www-form-urlencoded")
                            .build();
                    com.squareup.okhttp.Response response = client.newCall(request).execute();
                    Log.e(TAG, "" + response.body());
                    String jsonData = response.body().string();
                    Log.e(TAG, jsonData);
                    mResponse = response;
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
                    GetRiderDataModel level2RegistrationModel = new GetRiderDataModel();
                    GetRiderDataModel.DataBean dataBean = new GetRiderDataModel.DataBean();
                    String data = oneObject.getString("data");
                    JSONObject jsonObject = new JSONObject(data);
                    dataBean.setFirst_name(jsonObject.getString("first_name"));
                    dataBean.setRider_id(jsonObject.getString("rider_id"));
                    dataBean.setLast_name(jsonObject.getString("last_name"));
                    dataBean.setEmail_id(jsonObject.getString("email_id"));
                    dataBean.setPhone_no(jsonObject.getLong("phone_no"));
                    dataBean.setPreferred_payment_method(jsonObject.getString("preferred_payment_method"));
                    dataBean.setArea_name(jsonObject.getString("area_name"));
                    dataBean.setHome(jsonObject.getString("home"));
                    dataBean.setWork(jsonObject.getString("work"));
                    dataBean.setReferring_driver_id(jsonObject.getString("referring_driver_id"));
                    dataBean.setRefer_gifted(jsonObject.getInt("refer_gifted"));
                    dataBean.setReferral_date(jsonObject.getString("referral_date"));
                    dataBean.setCity(jsonObject.getString("city"));
                    dataBean.setLast_updated(jsonObject.getString("last_updated"));
                    dataBean.setRider_created_on(jsonObject.getString("rider_created_on"));
                    dataBean.setNum_completed_rides(jsonObject.getInt("num_completed_rides"));
                    dataBean.setSelected_language(jsonObject.getString("selected_language"));
                    dataBean.setCredit_balance(jsonObject.getInt("credit_balance"));
                    dataBean.setProfile_image_path(jsonObject.getString("profile_image_path"));
                    dataBean.setDownload_latitude(jsonObject.getString("download_latitude"));
                    dataBean.setDownload_longitude(jsonObject.getString("download_longitude"));
                    level2RegistrationModel.setData(dataBean);
                    level2RegistrationModel.setMessage(oneObject.getString("message"));
                    level2RegistrationModel.setType(oneObject.getString("type"));
                    mResultInterface.onSuccess(level2RegistrationModel);
                } else {
                    mResultInterface.onFailed(oneObject.getString("message"));
                }
            } catch (JSONException e) {
                e.printStackTrace();
                mResultInterface.onFailed("No Data Found");
            }

        }
    }
}