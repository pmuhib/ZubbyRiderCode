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
import com.zuby.user.zubbyrider.view.registration_login.model.UpdatePasswordModel;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;

public class UpdatePasswordPresenter {
    private ResultInterface mResultInterface;
    private Context mContext;
    private final String TAG = UpdatePasswordPresenter.class.getSimpleName();
    private String mData;

    public void show(ResultInterface resultInterface, Context context
            , String oldpassword, String newpassword) {
        mContext = context;
        mResultInterface = resultInterface;
        HashMap hashMap = new HashMap();
        hashMap.put("tokenid", PreferenceConnector.readString(mContext, ApiKeys.TOKEN, ""));
        hashMap.put("country_code", PreferenceConnector.readString(mContext, ApiKeys.COUNTRY_CODE, ""));
        hashMap.put("mobile_no", PreferenceConnector.readString(mContext, ApiKeys.MOBILE, ""));
        hashMap.put("old_password", oldpassword);
        hashMap.put("new_password", newpassword);
        Log.d(TAG, new Gson().toJson(hashMap));
        addService(new Gson().toJson(hashMap));
    }


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
                            .url(ApiKeys.BASE_URL + "updatePassword")
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
                    UpdatePasswordModel updateRiderDataModel = new UpdatePasswordModel();
                    updateRiderDataModel.setMessage(oneObject.getString("message"));
                    updateRiderDataModel.setType(oneObject.getString("type"));
                    mResultInterface.onSuccess(updateRiderDataModel);
                } else {
                    mResultInterface.onFailed(oneObject.getString("message"));
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }

        }
    }
}