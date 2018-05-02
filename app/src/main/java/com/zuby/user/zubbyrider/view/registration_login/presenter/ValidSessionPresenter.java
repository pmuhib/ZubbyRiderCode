package com.zuby.user.zubbyrider.view.registration_login.presenter;

import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;

import com.google.gson.Gson;
import com.squareup.okhttp.OkHttpClient;
import com.squareup.okhttp.Request;
import com.zuby.user.zubbyrider.interfaces.ResultInterface1;
import com.zuby.user.zubbyrider.utils.ApiKeys;
import com.zuby.user.zubbyrider.utils.ApiService;
import com.zuby.user.zubbyrider.utils.PreferenceConnector;
import com.zuby.user.zubbyrider.view.registration_login.model.ValidSessionModel;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;

public class ValidSessionPresenter {
    private ApiService mApiService;
    private ResultInterface1 mResultInterface;
    private Context mContext;
    private String mData;
    private final String TAG = ValidSessionPresenter.class.getSimpleName();

    public void show(ResultInterface1 resultInterface, Context context) {
        mContext = context;
        mResultInterface = resultInterface;
        HashMap hashMap = new HashMap();
        Log.e(TAG, PreferenceConnector.readString(context, ApiKeys.UID, ""));
        hashMap.put("user_id", PreferenceConnector.readString(mContext, ApiKeys.UID, ""));
        hashMap.put("session_login_type", "rider");
        hashMap.put("session_id", PreferenceConnector.readString(mContext, ApiKeys.SESSION_ID, ""));
        hashMap.put("tokenid", PreferenceConnector.readString(mContext, ApiKeys.TOKEN, ""));

        addService(new Gson().toJson(hashMap));
    }


    public void addService(String data) {
        this.mData = data;
        Log.d(TAG, new Gson().toJson(mData));
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
                            .url(ApiKeys.BASE_URL + "isSessionValid")
                            .post(body)
                            .addHeader("content-type", "application/x-www-form-urlencoded")
                            .build();
                    com.squareup.okhttp.Response response = client.newCall(request).execute();
                    // Log.e(TAG, "" + response.body());
                    String jsonData = response.body().string();
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
                ValidSessionModel validSessionModel = new ValidSessionModel();
                validSessionModel.setType(type);
                validSessionModel.setMessage(oneObject.getString("message"));
                if (type.equalsIgnoreCase("success")) {
                    mResultInterface.onSuccess(validSessionModel);
                } else {
                    mResultInterface.onFailed(validSessionModel);
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }

        }
    }
}