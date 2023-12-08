package com.example.client;

import android.app.Application;
import android.util.Log;

import com.example.client.util.Resources;
import com.example.client.util.dagger.AppComponent;
import com.example.client.util.dagger.AppModule;

public class ClientApp extends Application {

//    private AppComponent appComponent;

    @Override
    public void onCreate() {
        super.onCreate();

//        appComponent = DaggerAppComponent.builder()
//                .appModule(new AppModule(this))
//
        Resources.createDBHelper(this);

        Log.d("MyApp", "Application started");
    }

//    public AppComponent getAppComponent() {
//        return appComponent;
//    }

}
