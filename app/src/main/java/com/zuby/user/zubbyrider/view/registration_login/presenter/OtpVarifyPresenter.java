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
import com.zuby.user.zubbyrider.view.registration_login.model.OtpVarifyModel;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;

import okhttp3.MediaType;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;

public class OtpVarifyPresenter {
    private ApiService mApiService;
    private ResultInterface mResultInterface;
    private Context mContext;
    private final String TAG = OtpVarifyPresenter.class.getSimpleName();
    String mData;

    public void show(ResultInterface resultInterface, Context context, ApiService apiService, String country_code
            , String mobileNumber, String otp) {
        mContext = context;
        mApiService = apiService;
        mResultInterface = resultInterface;
        String mobile = country_code + mobileNumber;
        String data = "tokenid=" + PreferenceConnector.readString(mContext, ApiKeys.TOKEN, "")
                + "&mobile=" + mobile + "&otp=" + otp;
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
                    com.squareup.okhttp.MediaType mediaType = com.squareup.okhttp.MediaType.parse("application/x-www-form-urlencoded");
                    com.squareup.okhttp.RequestBody body = com.squareup.okhttp.RequestBody.create(mediaType, mData);
                    Request request = new Request.Builder()
                            .url("http://ec2-13-54-107-150.ap-southeast-2.compute.amazonaws.com:8000/api/otp_verification")
                            .post(body)
                            .addHeader("content-type", "application/x-www-form-urlencoded")
                            .build();
                    com.squareup.okhttp.Response response = client.newCall(request).execute();
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
                    OtpVarifyModel sendOtpModel = new OtpVarifyModel();
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