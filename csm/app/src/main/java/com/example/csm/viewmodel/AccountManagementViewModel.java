package com.example.csm.viewmodel;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.csm.repository.UserRepository;
import com.example.csm.model.User;

import java.util.ArrayList;
import java.util.List;

public class AccountManagementViewModel extends ViewModel {

    private UserRepository userRepository;

    public AccountManagementViewModel() {
        userRepository = UserRepository.getInstance();
    }

    public MutableLiveData<String> getMessageLiveData() {
        return userRepository.getMessageLiveData();
    }

    public LiveData<List<User>> getAdminsLiveData() {
        return userRepository.getAdminsLiveData();
    }

    public LiveData<List<User>> getSubscribersLiveData() {
        return userRepository.getSubscribersLiveData();
    }

    public void fetchData() {
        userRepository.fetchAdminsInfo();
        userRepository.fetchSubscribersInfo();
    }

    public void deleteUser(int userId) {
        userRepository.deleteUser(userId);
    }

}
