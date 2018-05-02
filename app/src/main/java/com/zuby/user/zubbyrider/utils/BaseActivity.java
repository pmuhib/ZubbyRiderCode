package com.zuby.user.zubbyrider.utils;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.Context;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.Toast;

import com.zuby.user.zubbyrider.dagger.model.DraggerModule;

import javax.inject.Inject;

import retrofit2.Retrofit;

/**
 * Created by Macbook on 3/27/18.
 */

public class BaseActivity extends AppCompatActivity {
    @Inject
    public DraggerModule mDraggerModule;
    @Inject
    public Context context;
    public Retrofit mRetrofit, mRetrofit1;
    public ApiService mApiService, mApiService1;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        MyApp.app().getDraggerComponenet().inject(BaseActivity.this);
        mRetrofit = mDraggerModule.getClient();
        mApiService = mDraggerModule.getService();
    }

    @SuppressLint("MissingPermission")
    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        switch (requestCode) {
            case Utility.MY_PERMISSIONS_REQUEST_READ_EXTERNAL_STORAGE:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                } else {

                }
                break;
        }
    }
}