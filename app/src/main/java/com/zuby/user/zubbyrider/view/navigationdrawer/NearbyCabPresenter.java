package com.zuby.user.zubbyrider.view.navigationdrawer;

import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;

import com.google.gson.Gson;
import com.squareup.okhttp.OkHttpClient;
import com.squareup.okhttp.Request;
import com.zuby.user.zubbyrider.interfaces.ResultInterface;
import com.zuby.user.zubbyrider.utils.ApiKeys;
import com.zuby.user.zubbyrider.utils.PreferenceConnector;
import com.zuby.user.zubbyrider.view.navigationdrawer.model.NearbyDriverModel;
import com.zuby.user.zubbyrider.view.registration_login.model.GetRiderDataModel;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class NearbyCabPresenter {

    private ResultInterface mResultInterface;
    private Context mContext;
    private String mData;
    private final String TAG = NearbyCabPresenter.class.getSimpleName();

    public void show(ResultInterface resultInterface, Context context, String currentLatitudee,
                     String currentLongitude, String cityCode, String postalCode) {
        mContext = context;
        mResultInterface = resultInterface;
        HashMap hashMap = new HashMap();
        hashMap.put("tokenid", PreferenceConnector.readString(mContext, ApiKeys.TOKEN, ""));
        hashMap.put("rider_current_latitude", currentLatitudee);
        hashMap.put("rider_current_longitude", currentLongitude);
        hashMap.put("city_code", cityCode);
        hashMap.put("postal_code", postalCode);
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
                            .url(ApiKeys.NEARBY_DRIVER_URL + "getNAvailableDriver")
                            .post(body)
                            .addHeader("content-type", "application/json")
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
                    NearbyDriverModel nearbyDriverModel = new NearbyDriverModel();
                    nearbyDriverModel.setMessage(oneObject.getString("message"));
                    nearbyDriverModel.setType(oneObject.getString("type"));
                    JSONArray array1 = new JSONArray(oneObject.getString("data"));
                    List<NearbyDriverModel.DataBean> list = new ArrayList<>();
                    list.clear();
                    for (int i = 0; i < array1.length() - 1; i++) {
                        JSONObject oneObject1 = array1.getJSONObject(i);
                        NearbyDriverModel.DataBean dataBean = new NearbyDriverModel.DataBean();
                        dataBean.setCurrent_car_category_id_selected(oneObject1.getInt("current_car_category_id_selected"));
                        dataBean.setCurrent_car_id(oneObject1.getInt("current_car_id"));
                        dataBean.setDistance(oneObject1.getInt("distance"));
                        dataBean.setDriver_id(oneObject1.getString("driver_id"));
                        dataBean.setDriver_lat(oneObject1.getString("driver_lat"));
                        dataBean.setDriver_long(oneObject1.getString("driver_long"));
                        dataBean.setETA(oneObject1.getString("ETA"));
                        list.add(dataBean);
                    }
                    nearbyDriverModel.setData(list);
                    mResultInterface.onSuccess(nearbyDriverModel);
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
