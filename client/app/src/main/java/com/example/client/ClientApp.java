package com.example.client;

import android.app.Application;
import android.util.Log;

import com.example.client.mesh.JettyServerManager;
import com.example.client.util.MediaDownloadUtil;
import com.example.client.util.Resources;

public class ClientApp extends Application {

    @Override
    public void onCreate() {
        super.onCreate();

        Resources.setAppContext(this);
        Resources.createDBHelper(this);
        MediaDownloadUtil.initMediaDownloadFolder();

        JettyServerManager.startServer();

        Log.d("MyApp", "Application started");
    }

}
