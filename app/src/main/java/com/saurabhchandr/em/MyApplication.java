package com.saurabhchandr.em;

import android.app.Application;

import com.google.android.gms.ads.MobileAds;
import com.google.android.gms.ads.initialization.InitializationStatus;
import com.google.android.gms.ads.initialization.OnInitializationCompleteListener;

import im.crisp.client.Crisp;

public class MyApplication extends Application {
    private AppOpenManager appOpenManager;

    @Override
    public void onCreate() {
        super.onCreate();
        MobileAds.initialize(
                this,
                new OnInitializationCompleteListener() {
                    @Override
                    public void onInitializationComplete(InitializationStatus initializationStatus) {}
                });
        appOpenManager = new AppOpenManager(this);
        Crisp.configure(MyApplication.this,"2976e398-a0cc-4135-a907-2a82ece511e1");
    }
}
