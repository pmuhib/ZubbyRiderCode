package com.zuby.user.zubbyrider.view.registration_login.presenter;

import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;

import com.google.gson.Gson;
import com.google.gson.JsonObject;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;

import com.squareup.okhttp.MediaType;
import com.squareup.okhttp.OkHttpClient;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.RequestBody;
import com.squareup.okhttp.Response;
import com.zuby.user.zubbyrider.interfaces.ResultInterface;
import com.zuby.user.zubbyrider.utils.ApiKeys;
import com.zuby.user.zubbyrider.utils.ApiService;
import com.zuby.user.zubbyrider.utils.PreferenceConnector;
import com.zuby.user.zubbyrider.view.registration_login.model.SendOtpModel;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Retrofit;

public class SendOtpPresenter {
    private ApiService mApiService;
    private ResultInterface mResultInterface;
    private Context mContext;
    private final String TAG = SendOtpPresenter.class.getSimpleName();
    String mData;

    public void show(ResultInterface resultInterface, Context context, ApiService apiService, String country_code
            , String mobileNumber) {
        mContext = context;
        mApiService = apiService;
        mResultInterface = resultInterface;
        String mobile = country_code + mobileNumber;
        String data = "tokenid=" + PreferenceConnector.readString(mContext, ApiKeys.TOKEN, "") + "&mobile=" + mobile;
        Log.d(TAG, data);
        addService(data);
    }

    public void addService(String data) {
        mData = data;
        new MainClass().execute();
    }

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
                    MediaType mediaType = MediaType.parse("application/x-www-form-urlencoded");
                    RequestBody body = RequestBody.create(mediaType, mData);
                    Request request = new Request.Builder()
                            .url("http://ec2-13-54-107-150.ap-southeast-2.compute.amazonaws.com:8000/api/otp_send")
                            .post(body)
                            .addHeader("content-type", "application/x-www-form-urlencoded")
                            .build();
                    Response response = client.newCall(request).execute();
                    String jsonData = response.body().string();
                    JSONObject Jobject = new JSONObject(jsonData);
                    System.out.println(Jobject.toString());
                    current = Jobject.toString();
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
                    SendOtpModel sendOtpModel = new SendOtpModel();
                    sendOtpModel.setMessage(type);
                    sendOtpModel.setMessage(oneObject.getString("message"));
                    mResultInterface.onSuccess(sendOtpModel);
                } else {
                    mResultInterface.onFailed(oneObject.getString("message"));
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }

        }
    }
}