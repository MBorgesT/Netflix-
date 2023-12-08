package com.example.client;

import android.app.Application;
import android.util.Log;

import com.example.client.util.Resources;

public class ClientApp extends Application {

    @Override
    public void onCreate() {
        super.onCreate();

        Resources.createDBHelper(this);

        Log.d("MyApp", "Application started");
    }

}
