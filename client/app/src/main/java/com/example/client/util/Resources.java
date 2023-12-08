package com.example.client.util;

import android.content.Context;

import com.example.client.db.DBHelper;

public class Resources {

    private static final String baseUrl = "http://10.0.2.2:8080/";
    public static final String backendApiUrl = baseUrl + "api/";
    public static final String backendResourcesUrl = baseUrl + "resources/";

    private static DBHelper dbHelper;

    public static void createDBHelper(Context context) {
        dbHelper = new DBHelper(context);
    }

    public static DBHelper getDBHelper() {
        return dbHelper;
    }

}
