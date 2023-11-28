package com.example.csm.repository;

import android.app.Application;
import android.content.Context;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.lifecycle.MutableLiveData;

import com.example.csm.api.UserAPI;
import com.example.csm.model.User;
import com.example.csm.util.ApiBuilder;

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

    public interface OnUserCreateListener {
        void onUserCreate(String message, boolean success);
    }

    public interface OnUsersFetchListener {
        void onAdminsFetched(List<User> admins);
        void onSubsFetched(List<User> subs);
    }

    public interface OnUserDeleteListener {
        void onUserDelete(String message, boolean success);
    }

    private static final String TAG = "UserRepository";

    private static UserRepository instance;
    private final UserAPI api;

    // LOGIN
    private MutableLiveData<String> messageLiveData;
    private static MutableLiveData<Boolean> userAuthenticatedLiveData;

    private UserRepository() {
        api = ApiBuilder.create(UserAPI.class);
        messageLiveData = new MutableLiveData<>();
        userAuthenticatedLiveData = new MutableLiveData<>();
    };

    public static UserRepository getInstance() {
        if (instance == null) {
            instance = new UserRepository();
        }
        return instance;
    }

    public MutableLiveData<String> getMessageLiveData() {
        return messageLiveData;
    }

    public MutableLiveData<Boolean> getUserAuthenticatedLiveData() {
        return userAuthenticatedLiveData;
    }

    public MutableLiveData<Boolean> adminLogin(String username, String password) {
        JSONObject json = new JSONObject();
        try {
            json.put("username", username);
            json.put("password", password);
        } catch (JSONException e) {
            e.printStackTrace();
            messageLiveData.setValue("Error on authentication");
            userAuthenticatedLiveData.setValue(false);
            return userAuthenticatedLiveData;
        }

        RequestBody requestBody = RequestBody.create(MediaType.parse("application/json"), json.toString());
        api.adminLogin(requestBody).enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                messageLiveData.setValue(response.message());
                userAuthenticatedLiveData.setValue(response.code() == 200);
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                t.printStackTrace();
                messageLiveData.setValue("Error on connecting to server");
                userAuthenticatedLiveData.setValue(false);
            }
        });

        return userAuthenticatedLiveData;
    }

    public void fetchAdminsInfo(OnUsersFetchListener listener) {
        api.getAdminsInfo().enqueue(new Callback<List<User>>() {
            @Override
            public void onResponse(@NonNull Call<List<User>> call, @NonNull Response<List<User>> response) {
                listener.onAdminsFetched(response.body());
            }

            @Override
            public void onFailure(@NonNull Call<List<User>> call, @NonNull Throwable t) {
                Log.d(TAG, "onFailure: failed to get admins");
            }
        });
    }

    public void fetchSubscribersInfo(OnUsersFetchListener listener) {
        api.getSubscribersInfo().enqueue(new Callback<List<User>>() {
            @Override
            public void onResponse(@NonNull Call<List<User>> call, @NonNull Response<List<User>> response) {
                listener.onSubsFetched(response.body());
            }

            @Override
            public void onFailure(@NonNull Call<List<User>> call, @NonNull Throwable t) {
                Log.d(TAG, "onFailure: failed to get admins");
            }
        });
    }

    public void newUser(String username, String password, User.Role role, OnUserCreateListener listener) {
        JSONObject json = new JSONObject();
        try {
            json.put("username", username);
            json.put("password", password);
            json.put("role", role.toString());
        } catch (JSONException e) {
            e.printStackTrace();
            messageLiveData.setValue("Error on JSON creation");
        }

        RequestBody requestBody = RequestBody.create(MediaType.parse("application/json"), json.toString());

        api.newUser(requestBody).enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                listener.onUserCreate(response.message(), response.code() == 201);
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                listener.onUserCreate("Error creating user", false);
            }
        });
    }

    public void deleteUser(int userId, OnUserDeleteListener listener) {
        api.deleteUser(userId).enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                listener.onUserDelete(response.message(), response.code() == 200);
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                listener.onUserDelete("Error deleting user", false);
            }
        });
    }

}
