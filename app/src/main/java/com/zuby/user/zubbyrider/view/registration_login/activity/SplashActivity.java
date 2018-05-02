package com.zuby.user.zubbyrider.view.registration_login.activity;

import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.os.AsyncTask;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.widget.Toast;


import com.zuby.user.zubbyrider.R;
import com.zuby.user.zubbyrider.databinding.ActivitySplashBinding;
import com.zuby.user.zubbyrider.interfaces.ResultInterface1;
import com.zuby.user.zubbyrider.intro_slider.IntroSliderActivity;
import com.zuby.user.zubbyrider.utils.ApiKeys;
import com.zuby.user.zubbyrider.utils.BaseActivity;
import com.zuby.user.zubbyrider.utils.PreferenceConnector;
import com.zuby.user.zubbyrider.view.navigationdrawer.activity.MainActivity;
import com.zuby.user.zubbyrider.view.registration_login.model.ValidSessionModel;
import com.zuby.user.zubbyrider.view.registration_login.presenter.ValidSessionPresenter;
import com.zuby.user.zubbyrider.view.tokengenerate.GenerateTokenClass;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.TimeZone;

public class SplashActivity extends BaseActivity implements ResultInterface1 {
    private ActivitySplashBinding mActivitySplashBinding;
    private Boolean mValid = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mActivitySplashBinding = DataBindingUtil.setContentView(this, R.layout.activity_splash);
        new GenerateTokenClass(this);
        TimeZone tz = TimeZone.getDefault();
        PreferenceConnector.writeString(context, ApiKeys.TIMEZONE, tz.getID());
        Log.e("string", PreferenceConnector.readString(context, ApiKeys.TOKEN, ""));
        new Handler().postDelayed(new Runnable() {


            @Override
            public void run() {
                validateSession();
            }
        }, 4000);
    }

    public void validateSession() {
        new ValidSessionPresenter().show(this, context);
    }

    @Override
    public void onSuccess(Object object) {
        ValidSessionModel validSessionModel = (ValidSessionModel) object;
        Toast.makeText(context, validSessionModel.getMessage(), Toast.LENGTH_LONG).show();
        Intent intent = new Intent(context, MainActivity.class);
        startActivity(intent);
    }

    @Override
    public void onFailed(Object failobject) {
        ValidSessionModel validSessionModel = (ValidSessionModel) failobject;
        Toast.makeText(context, validSessionModel.getMessage(), Toast.LENGTH_LONG).show();
        Intent intent = new Intent(context, IntroSliderActivity.class);
        startActivity(intent);
    }
}
