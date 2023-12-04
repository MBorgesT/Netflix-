package com.example.csm.viewmodel;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.csm.repository.UserRepository;


public class LoginViewModel extends ViewModel {

    private final UserRepository userRepository;

    private final MutableLiveData<String> messageLiveData;
    private final MutableLiveData<Boolean> userAuthenticatedLiveData;

    private UserRepository.OnUserAuthenticateListener onUserAuthenticateListener;

    public LoginViewModel() {
        userRepository = UserRepository.getInstance();

        messageLiveData = new MutableLiveData<>();
        userAuthenticatedLiveData = new MutableLiveData<>();

        createOnUserAuthenticateListener();
    }

    public MutableLiveData<String> getMessageLiveData() {
        return messageLiveData;
    }

    public MutableLiveData<Boolean> getUserAuthenticadedLiveData() {
        return userAuthenticatedLiveData;
    }

    public void login(String username, String password) {
        userRepository.adminLogin(username, password, onUserAuthenticateListener);
    }

    private void createOnUserAuthenticateListener() {
        onUserAuthenticateListener = new UserRepository.OnUserAuthenticateListener() {
            @Override
            public void onUserAuthenticate(String message, boolean success) {
                messageLiveData.setValue(message);
                userAuthenticatedLiveData.setValue(success);
            }
        };
    }

}
