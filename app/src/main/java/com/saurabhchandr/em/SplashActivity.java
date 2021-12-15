package com.saurabhchandr.em;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.preference.PreferenceManager;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;

public class SplashActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);

        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(SplashActivity.this);
        if(preferences.getBoolean("launchMy",false)) {
            startActivity(new Intent(SplashActivity.this,MyActivity.class));
            finish();
        } else {
            startActivity(new Intent(SplashActivity.this,MainActivity.class));
            finish();
        }
    }
}