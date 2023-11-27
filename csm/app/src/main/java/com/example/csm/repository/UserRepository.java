package com.example.csm.repository;

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

public class UserRepository {
    private static final String TAG = "UserRepository";

    private static UserRepository instance;
    private final UserAPI api;

    // USER MANAGEMENT
    private static MutableLiveData<List<User>> adminsLiveData;
    private static MutableLiveData<List<User>> subscribersLiveData;

    // LOGIN
    private MutableLiveData<String> messageLiveData;
    private static MutableLiveData<Boolean> userAuthenticatedLiveData;

    private UserRepository() {
        api = ApiBuilder.create(UserAPI.class);
        adminsLiveData = new MutableLiveData<>();
        subscribersLiveData = new MutableLiveData<>();
        messageLiveData = new MutableLiveData<>();
        userAuthenticatedLiveData = new MutableLiveData<>();
    };

    public static UserRepository getInstance() {
        if (instance == null) {
            instance = new UserRepository();
        }
        return instance;
    }

    public static MutableLiveData<List<User>> getAdminsLiveData() {
        return adminsLiveData;
    }

    public static MutableLiveData<List<User>> getSubscribersLiveData() {
        return subscribersLiveData;
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

    public void fetchAdminsInfo() {
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
    }

    public void fetchSubscribersInfo() {
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
    }

    public void newUser(String username, String password, User.Role role) {
        api.newUser(username, password, role).enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {

            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {

            }
        });
    }

    public void deleteUser(int userId) {
        api.deleteUser(userId).enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                if (response.code() == 200) {
                    fetchAdminsInfo();
                    fetchSubscribersInfo();
                    messageLiveData.setValue(response.message());
                } else {
                    messageLiveData.setValue("Error deleting user");
                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                messageLiveData.setValue("Error deleting user");
            }
        });
    }

}
