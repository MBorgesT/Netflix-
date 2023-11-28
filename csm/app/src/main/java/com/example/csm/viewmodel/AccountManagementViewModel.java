package com.example.csm.viewmodel;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.csm.repository.UserRepository;
import com.example.csm.model.User;

import java.util.List;

public class AccountManagementViewModel extends ViewModel {

    private final UserRepository userRepository;
    private final MutableLiveData<List<User>> adminsLiveData;
    private final MutableLiveData<List<User>> subscribersLiveData;
    private final MutableLiveData<String> messageLiveData;
    private final MutableLiveData<Boolean> userCreatedLiveData;
    private UserRepository.OnUsersFetchListener onUsersFetchListener;
    private UserRepository.OnUserDeleteListener onUserDeleteListener;
    private UserRepository.OnUserCreateListener onUserCreateListener;

    public AccountManagementViewModel() {
        userRepository = UserRepository.getInstance();
        adminsLiveData = new MutableLiveData<>();
        subscribersLiveData = new MutableLiveData<>();
        messageLiveData = new MutableLiveData<>();
        userCreatedLiveData = new MutableLiveData<>();
        userCreatedLiveData.setValue(false);
        createOnUsersFetchListener();
        createOnUserDeleteListener();
        createOnUserCreateListener();
    }

    public MutableLiveData<List<User>> getAdminsLiveData() {
        return adminsLiveData;
    }

    public MutableLiveData<List<User>> getSubscribersLiveData() {
        return subscribersLiveData;
    }

    public MutableLiveData<String> getMessageLiveData() {
        return messageLiveData;
    }

    public MutableLiveData<Boolean> getUserCreatedLiveData() {
        return userCreatedLiveData;
    }

    public void fetchUsers() {
        userRepository.fetchAdminsInfo(onUsersFetchListener);
        userRepository.fetchSubscribersInfo(onUsersFetchListener);
    }

    public void newUser(String username, String password, User.Role role) {
        List<User> admins = adminsLiveData.getValue();
        userCreatedLiveData.setValue(false);
        userRepository.newUser(username, password, role, onUserCreateListener);
    }

    public void deleteUser(int userId) {
        userRepository.deleteUser(userId, onUserDeleteListener);
    }

    private void createOnUsersFetchListener() {
        onUsersFetchListener = new UserRepository.OnUsersFetchListener() {
            @Override
            public void onAdminsFetched(List<User> admins) {
                adminsLiveData.setValue(admins);
            }

            @Override
            public void onSubsFetched(List<User> subs) {
                subscribersLiveData.setValue(subs);
            }
        };
    }

    private void createOnUserDeleteListener() {
        onUserDeleteListener = new UserRepository.OnUserDeleteListener() {
            @Override
            public void onUserDelete(String message, boolean success) {
                messageLiveData.setValue(message);
                if (success) {
                    fetchUsers();
                }
            }
        };
    }

    private void createOnUserCreateListener() {
        onUserCreateListener = new UserRepository.OnUserCreateListener() {
            @Override
            public void onUserCreate(String message, boolean success) {
                messageLiveData.setValue(message);
                if (success) {
                    fetchUsers();
                    userCreatedLiveData.setValue(true);
                }
            }
        };
    }

}
