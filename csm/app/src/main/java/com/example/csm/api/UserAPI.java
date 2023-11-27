package com.example.csm.api;


import com.example.csm.model.User;

import org.json.JSONObject;

import java.util.List;
import java.util.Map;

import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.Field;
import retrofit2.http.FieldMap;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Path;

public interface UserAPI {
    @GET("userManagement/getAdminsInfo/")
    Call<List<User>> getAdminsInfo();

    @GET("userManagement/getSubscribersInfo/")
    Call<List<User>> getSubscribersInfo();

    @POST("auth/adminLogin")
    Call<ResponseBody> adminLogin(@Body RequestBody body);

    @POST("auth/subscriberLogin")
    Call<String> subscriberLogin(@Field("username") String username, @Field("password") String password);
}