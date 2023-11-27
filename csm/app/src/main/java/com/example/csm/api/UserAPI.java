package com.example.csm.api;


import com.example.csm.model.User;

import java.util.List;

import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.DELETE;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Path;

public interface UserAPI {
    @GET("userManagement/getAdminsInfo/")
    Call<List<User>> getAdminsInfo();

    @GET("userManagement/getSubscribersInfo/")
    Call<List<User>> getSubscribersInfo();

    @DELETE("userManagement/deleteUser/{userId}")
    Call<ResponseBody> deleteUser(@Path("userId") int userId);

    @POST("auth/adminLogin")
    Call<ResponseBody> adminLogin(@Body RequestBody body);
}