package com.example.csm.api;


import com.example.csm.model.User;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Path;

public interface UserAPI {
    @GET("userManagement/getAdminsInfo/")
    Call<List<User>> getAdminsInfo();

    @GET("userManagement/getSubscribersInfo/")
    Call<List<User>> getSubscribersInfo();
}