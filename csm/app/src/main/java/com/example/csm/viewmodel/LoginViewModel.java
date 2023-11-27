package com.example.csm.viewmodel;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.csm.repository.UserRepository;

import org.json.JSONException;

import java.io.IOException;

import retrofit2.Response;


public class LoginViewModel extends ViewModel {

    private UserRepository userRepository;

    public LoginViewModel() {
        userRepository = UserRepository.getInstance();
    }

    public MutableLiveData<String> getMessageLiveData() {
        return userRepository.getMessageLiveData();
    }

    public MutableLiveData<Boolean> getUserAuthenticadedLiveData() {
        return userRepository.getUserAuthenticatedLiveData();
    }

    public void login(String username, String password) {
        userRepository.adminLogin(username, password);
    }

}
