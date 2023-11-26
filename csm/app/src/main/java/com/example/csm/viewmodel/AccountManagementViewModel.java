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
    private MutableLiveData<List<User>> admins;
    private MutableLiveData<List<User>> subs;

    public AccountManagementViewModel() {
        admins = new MutableLiveData<>();
        subs = new MutableLiveData<>();
        userRepository = UserRepository.getInstance();
    }

    public LiveData<List<User>> getAdmins() {
        return admins;
    }

    public LiveData<List<User>> getSubscribers() {
        return subs;
    }

    public void fetchData() {
        admins = userRepository.fetchAdminsInfo();
        subs = userRepository.fetchSubscribersInfo();
    }

}
