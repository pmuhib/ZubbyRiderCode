package com.zuby.user.zubbyrider.dagger.component;
import com.zuby.user.zubbyrider.dagger.module.DraggerDownloaderModule;
import com.zuby.user.zubbyrider.utils.BaseActivity;

import javax.inject.Singleton;

import dagger.Component;

/**
 * Created by Omar on 04/06/2016.
 */
@Singleton
@Component(modules = DraggerDownloaderModule.class)
public interface DraggerComponenet {
    void inject(BaseActivity baseActivity);
}
