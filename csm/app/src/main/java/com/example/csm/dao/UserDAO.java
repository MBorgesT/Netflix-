package com.example.csm.dao;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.RequestFuture;
import com.android.volley.toolbox.StringRequest;
import com.example.csm.model.User;
import com.example.csm.network.NetworkUtil;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.util.List;

public class UserDAO {

    public static List<User> getAdminsInfo() throws Exception {
        RequestQueue queue = NetworkUtil.getInstance().getRequestQueue();

        String url = "http://192.168.1.68:8080/api/userManagement/getAdminsInfo";

        RequestFuture<String> future = RequestFuture.newFuture();
        StringRequest request = new StringRequest(Request.Method.GET, url, future, future);

        queue.add(request);
        String result = future.get();

        ObjectMapper mapper = new ObjectMapper();
        List<User> admins = mapper.readValue(result, new TypeReference<List<User>>() {});

        return admins;
    }

    public static List<User> getSubscribersInfo() throws Exception {
        RequestQueue queue = NetworkUtil.getInstance().getRequestQueue();

        String url = "http://192.168.1.68:8080/api/userManagement/getSubscribersInfo";

        RequestFuture<String> future = RequestFuture.newFuture();
        StringRequest request = new StringRequest(Request.Method.GET, url, future, future);

        queue.add(request);
        String result = future.get();

        ObjectMapper mapper = new ObjectMapper();
        List<User> subs = mapper.readValue(result, new TypeReference<List<User>>() {});

        return subs;
    }

}
