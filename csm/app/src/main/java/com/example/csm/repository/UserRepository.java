package com.example.csm.repository;

import android.util.Log;

import androidx.annotation.NonNull;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

//import com.android.volley.Request;
//import com.android.volley.RequestQueue;
//import com.android.volley.Response;
//import com.android.volley.VolleyError;
//import com.android.volley.toolbox.StringRequest;
import com.example.csm.api.UserAPI;
import com.example.csm.model.User;
import com.example.csm.util.ApiBuilder;
import com.example.csm.util.NetworkUtil;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
//import retrofit2.Response;

import java.util.ArrayList;
import java.util.List;

public class UserRepository extends Repository {
    private static final String TAG = "UserRepository";

    private static UserRepository instance;

    protected static final String controllerPath = "api/";
    private UserAPI api;

    private static MutableLiveData<List<User>> adminsLiveData;
    private static MutableLiveData<List<User>> subscribersLiveData;

    private UserRepository() {
        api = ApiBuilder.create(UserAPI.class);
        adminsLiveData = new MutableLiveData<>();
        subscribersLiveData = new MutableLiveData<>();
    };

    public static UserRepository getInstance() {
        if (instance == null) {
            instance = new UserRepository();
        }
        return instance;
    }

    public MutableLiveData<List<User>> fetchAdminsInfo() {
        api.getAdminsInfo().enqueue(new Callback<List<User>>() {
            @Override
            public void onResponse(@NonNull Call<List<User>> call, @NonNull Response<List<User>> response) {
                adminsLiveData.setValue(response.body());
            }

            @Override
            public void onFailure(@NonNull Call<List<User>> call, @NonNull Throwable t) {
                Log.d(TAG, "onFailure: failed to get admins");
            }
        });
        return adminsLiveData;
    }

    public MutableLiveData<List<User>> fetchSubscribersInfo() {
        api.getSubscribersInfo().enqueue(new Callback<List<User>>() {
            @Override
            public void onResponse(@NonNull Call<List<User>> call, @NonNull Response<List<User>> response) {
                List<User> users = response.body();
                subscribersLiveData.setValue(users);
            }

            @Override
            public void onFailure(@NonNull Call<List<User>> call, @NonNull Throwable t) {
                Log.d(TAG, "onFailure: failed to get admins");
            }
        });
        return subscribersLiveData;
    }

//    public static MutableLiveData<List<User>> fetchUserInfo(User.Role role) throws Exception {
//        //resultLiveData = new MutableLiveData<>();
//        String roleStr;
//        switch (role) {
//            case ADMIN:
//                roleStr = "Admins";
//                break;
//            case SUBSCRIBER:
//                roleStr = "Subscribers";
//                break;
//            default:
//                throw new Exception("Not a valid role");
//        }
//
//        String url = buildUrl(controllerPath, "userManagement/get" + roleStr + "Info");
//        RequestQueue queue = NetworkUtil.getInstance().getRequestQueue();
//
//        StringRequest request = new StringRequest(Request.Method.GET, url,
//                new Response.Listener<String>() {
//                    @Override
//                    public void onResponse(String response) {
//                        // Handle successful response
//                        ObjectMapper mapper = new ObjectMapper();
//                        try {
//                            List<User> users = mapper.readValue(response, new TypeReference<List<User>>() {});
//                            switch (role) {
//                                case ADMIN:
//                                    adminsLiveData.setValue(users);
//                                    break;
//                                case SUBSCRIBER:
//                                    subscribersLiveData.setValue(users);
//                                    break;
//                            }
//                        } catch (JsonProcessingException e) {
//                            throw new RuntimeException(e);
//                        }
//                    }
//                }, new Response.ErrorListener() {
//            @Override
//            public void onErrorResponse(VolleyError error) {
//                switch (role) {
//                    case ADMIN:
//                        adminsLiveData.setValue(new ArrayList<>());
//                        break;
//                    case SUBSCRIBER:
//                        subscribersLiveData.setValue(new ArrayList<>());
//                        break;
//                }
//            }
//        });
//
//        queue.add(request);
//        switch (role) {
//            case ADMIN:
//                return adminsLiveData;
//            case SUBSCRIBER:
//                return subscribersLiveData;
//        }
//        return null;
//    }

}
