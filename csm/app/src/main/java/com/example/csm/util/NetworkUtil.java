package com.example.csm.util;

import android.content.Context;

import com.android.volley.RequestQueue;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.Volley;

import java.net.HttpURLConnection;

public class NetworkUtil {

    private static NetworkUtil instance;
    private static RequestQueue requestQueue;

    private NetworkUtil() {}

    public static NetworkUtil getInstance() {
        if (instance == null) {
            instance = new NetworkUtil();
        }
        return instance;
    }

    public static void initRequestQueue(Context context) {
        requestQueue = Volley.newRequestQueue(context);
    }

    public RequestQueue getRequestQueue() throws Exception {
        if (requestQueue == null) {
            throw new Exception("Request Queue not initialized");
        }
        return requestQueue;
    }

    public static String defaultErrorHandling(VolleyError error) {
        if (error.networkResponse != null) {
            int statusCode = error.networkResponse.statusCode;
            switch (statusCode) {
                case HttpURLConnection.HTTP_UNAUTHORIZED:
                    // Handle unauthorized access (HTTP 401)
                    // For example, show a message or redirect to a login screen
                    return "Unauthorized access";
                case HttpURLConnection.HTTP_NOT_FOUND:
                    // Handle resource not found (HTTP 404)
                    return "Resource not found";
                // Add more cases for other status codes as needed
                default:
                    // Handle other errors
                    return "Error: " + statusCode;
            }
        } else {
            // Handle other types of errors (e.g., no network connection)
            return "Error: " + error.getMessage();
        }
    }

}
