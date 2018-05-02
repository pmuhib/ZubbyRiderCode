package com.zuby.user.zubbyrider.dagger.model;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.support.v7.app.AlertDialog;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.zuby.user.zubbyrider.utils.ApiKeys;
import com.zuby.user.zubbyrider.utils.ApiService;

import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;


/**
 * Created by Omar on 04/06/2016.
 */
public class DraggerModule {
    private Context context;


    public DraggerModule(Context context) {
        this.context = context;
    }

    public void toImageView(ImageView imageView, String url) {
        Glide.with(context).load(url).into(imageView);
    }

    public Boolean checkInternet(Context context) {
        ConnectivityManager ConnectionManager = (ConnectivityManager) context.getSystemService
                (Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = ConnectionManager.getActiveNetworkInfo();
        if (networkInfo != null && networkInfo.isConnected() == true) {
            return true;
        } else {
            return false;
        }
    }

    public ApiService getService() {
        ApiService apiService = getClient().create(ApiService.class);
        return apiService;
    }

    public Retrofit getClient() {
        return new Retrofit.Builder()
                .baseUrl(ApiKeys.BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build();
    }

    public AlertDialog.Builder showDialog(Context context, String message) {
        AlertDialog.Builder alertDialogBuilderUserInput = new AlertDialog.Builder(context);
        alertDialogBuilderUserInput
                .setCancelable(false)
                .setMessage(message);
        return alertDialogBuilderUserInput;
    }
}
