package com.example.csm.dao;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.RequestFuture;
import com.android.volley.toolbox.StringRequest;
import com.example.csm.model.User;
import com.example.csm.util.NetworkUtil;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.List;

public class UserHandler extends Handler {
    protected static final String controllerPath = "api/";

    public static List<User> getAdminsInfo() throws Exception {
        String url = buildUrl(controllerPath, "userManagement/getAdminsInfo");
        RequestQueue queue = NetworkUtil.getInstance().getRequestQueue();

        RequestFuture<String> future = RequestFuture.newFuture();
        StringRequest request = new StringRequest(Request.Method.GET, url, future, future);

        queue.add(request);
        String result = future.get();

        ObjectMapper mapper = new ObjectMapper();
        List<User> admins = mapper.readValue(result, new TypeReference<List<User>>() {});

        return admins;
    }

    public static List<User> getSubscribersInfo() throws Exception {
        String url = buildUrl(controllerPath, "userManagement/getSubscribersInfo");
        RequestQueue queue = NetworkUtil.getInstance().getRequestQueue();

        RequestFuture<String> future = RequestFuture.newFuture();
        StringRequest request = new StringRequest(Request.Method.GET, url, future, future);

        queue.add(request);
        String result = future.get();

        ObjectMapper mapper = new ObjectMapper();
        List<User> subs = mapper.readValue(result, new TypeReference<List<User>>() {});

        return subs;
    }

//    public boolean login(String username, String password) throws LoginException {
//        String url = buildUrl(controllerPath, "auth/adminLogin");
//
//        RequestQueue queue = null;
//        try {
//            queue = NetworkUtil.getInstance().getRequestQueue();
//        } catch (Exception e) {
//            throw new LoginException("Error on the http request module");
//        }
//
//        JSONObject jsonBody = new JSONObject();
//        try {
//            jsonBody.put("username", username);
//            jsonBody.put("password", password);
//        } catch (JSONException e) {
//            throw new LoginException("Error on the json body building");
//        }
//
//        // Request a string response from the provided URL.
//        RequestFuture<String> future = RequestFuture.newFuture();
//        JsonObjectRequest request = new JsonObjectRequest(Request.Method.POST, url, jsonBody, future, future);
//
//        // Add the request to the RequestQueue.
//        queue.add(request);
//    }

    public void delete(int id) {

    }

    private class LoginException extends Exception {
        public LoginException(String msg) {
            super(msg);
        }
    }

}
