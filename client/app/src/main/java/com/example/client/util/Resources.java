package com.example.client.util;

import android.content.Context;

import com.example.client.db.DBHelper;

public class Resources {

//    private static final String baseUrl = "http://10.0.2.8:8080/";
    private static final String baseUrl = "http://104.197.193.0:8080/";

    public static final String backendApiUrl = baseUrl + "api/";
    public static final String backendResourcesUrl = baseUrl + "resources/";
    public static final int pingTimeout = 500;

    private static DBHelper dbHelper;
    private static Context appContext;

    public static void setAppContext(Context context) {
        Resources.appContext = context;
    }

    public static Context getAppContext() {
        return appContext;
    }

    public static void createDBHelper(Context context) {
        dbHelper = new DBHelper(context);
    }

    public static DBHelper getDBHelper() {
        return dbHelper;
    }

    public static String getMediaDownloadFolder() {
        return Resources.getAppContext().getFilesDir() + "/MediaDownload/";
    }

}
