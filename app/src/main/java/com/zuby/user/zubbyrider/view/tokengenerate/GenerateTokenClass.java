package com.zuby.user.zubbyrider.view.tokengenerate;

import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;

import com.google.android.gms.ads.internal.gmsg.HttpClient;
import com.zuby.user.zubbyrider.utils.ApiKeys;
import com.zuby.user.zubbyrider.utils.PreferenceConnector;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;
import org.json.JSONException;
import org.json.JSONObject;

public class GenerateTokenClass {
    private int mStatus = 0;
    private Context mContext;

    public GenerateTokenClass(Context context) {
        mContext = context;
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

            // implement API in background and store the response in current variable
            String current = "";
            try {
                try {
                    org.apache.http.client.HttpClient client = new DefaultHttpClient();
                    String getURL = ApiKeys.TOKEN_BASE_URL;
                    HttpPost httpGet = new HttpPost(getURL);
                    httpGet.setHeader("X-OpenAM-Username", "cabindia_android");
                    httpGet.setHeader("X-OpenAM-Password", "cabindia@android");
                    HttpResponse response = client.execute(httpGet);
                    mStatus = response.getStatusLine().getStatusCode();
                    Log.e("status", "" + mStatus);
                    // return the data to onPostExecute method
                    HttpEntity entity = response.getEntity();
                    String data = EntityUtils.toString(entity);
                    current = data;
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
            if (mStatus == 200) {
                try {
                    JSONObject oneObject = new JSONObject(s);
                    String token = oneObject.getString("tokenId");
                    PreferenceConnector.writeString(mContext, ApiKeys.TOKEN, token);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            } else {

            }
        }
    }
}