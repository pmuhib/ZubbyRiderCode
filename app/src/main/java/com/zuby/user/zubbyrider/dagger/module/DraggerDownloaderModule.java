package com.zuby.user.zubbyrider.dagger.module;


import android.content.Context;
import com.zuby.user.zubbyrider.dagger.model.DraggerModule;
import com.zuby.user.zubbyrider.utils.ApiService;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;

import retrofit2.Retrofit;


@Module
public class DraggerDownloaderModule {
    private Context context;

    public DraggerDownloaderModule(Context context) {
        this.context = context;
    }

    @Provides
    @Singleton
    Context provideContext() {
        return context;
    }

    @Provides
    @Singleton
    DraggerModule provideImageDownloader(Context context) {
        return new DraggerModule(context);
    }

    @Provides
    @Singleton
    Boolean checkInternet(Context context) {
        return false;
    }

    @Provides
    @Singleton
    ApiService apiService() {
        return null;
    }

    @Provides
    @Singleton
    Retrofit getClient() {
        return null;
    }


//    @Provides @Singleton
//    ApiService getApiService(){
//        return null;
//    }
}
