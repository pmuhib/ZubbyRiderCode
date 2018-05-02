package com.zuby.user.zubbyrider.utils;

import android.app.Application;

import com.zuby.user.zubbyrider.dagger.component.DaggerDraggerComponenet;
import com.zuby.user.zubbyrider.dagger.component.DraggerComponenet;
import com.zuby.user.zubbyrider.dagger.module.DraggerDownloaderModule;


/**
 * Created by Omar on 04/06/2016.
 */
public class MyApp extends Application {
    private static MyApp app;
    private DraggerComponenet draggerComponenet;

    @Override
    public void onCreate() {
        super.onCreate();
        app = this;
       // draggerComponenet = DaggerImageDownloaderComponent.builder().imageDownloaderModule(new DraggerDownloaderModule(this)).build();
        draggerComponenet= DaggerDraggerComponenet.builder()
                .draggerDownloaderModule(
                        new DraggerDownloaderModule(this)).build();
    }

    public static MyApp app(){
        return app;
    }

    public DraggerComponenet getDraggerComponenet(){
        return this.draggerComponenet;
    }

}
