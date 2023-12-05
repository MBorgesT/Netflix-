package com.example.client.repository;

import android.app.Application;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.lifecycle.MutableLiveData;

import com.example.client.api.UserAPI;
import com.example.client.model.User;
import com.example.client.util.ApiBuilder;

import org.json.JSONException;
import org.json.JSONObject;

import okhttp3.MediaType;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import java.util.List;

public class UserRepository extends Application {



    public interface OnUserAuthenticateListener {
        void onUserAuthenticate(String message, boolean success);
    }

    private static final String TAG = "UserRepository";

    private static UserRepository instance;
    private final UserAPI api;

    private UserRepository() {
        api = ApiBuilder.create(UserAPI.class);
    };

    public static UserRepository getInstance() {
        if (instance == null) {
            instance = new UserRepository();
        }
        return instance;
    }

    public void subscriberLogin(String username, String password, OnUserAuthenticateListener listener) {
        JSONObject json = new JSONObject();
        try {
            json.put("username", username);
            json.put("password", password);
        } catch (JSONException e) {
            e.printStackTrace();
            listener.onUserAuthenticate("Error on authentication", false);
        }

        RequestBody requestBody = RequestBody.create(MediaType.parse("application/json"), json.toString());
        api.subscriberLogin(requestBody).enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                listener.onUserAuthenticate(response.message(), response.code() == 200);
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                t.printStackTrace();
                listener.onUserAuthenticate("Error on connecting to server", false);
            }
        });
    }

}
