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
    private final MutableLiveData<Boolean> userFormSuccessLiveData;
    private UserRepository.OnUsersFetchListener onUsersFetchListener;
    private UserRepository.OnUserDeleteListener onUserDeleteListener;
    private UserRepository.OnUserCreateOrUpdateListener onUserCreateOrUpdateListener;

    public AccountManagementViewModel() {
        userRepository = UserRepository.getInstance();
        adminsLiveData = new MutableLiveData<>();
        subscribersLiveData = new MutableLiveData<>();
        messageLiveData = new MutableLiveData<>();
        userFormSuccessLiveData = new MutableLiveData<>();
        userFormSuccessLiveData.setValue(false);
        createOnUsersFetchListener();
        createOnUserDeleteListener();
        createOnUserCreateOrUpdateListener();
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

    public MutableLiveData<Boolean> getUserFormSuccessLiveData() {
        return userFormSuccessLiveData;
    }

    public void fetchUsers() {
        userRepository.fetchAdminsInfo(onUsersFetchListener);
        userRepository.fetchSubscribersInfo(onUsersFetchListener);
    }

    public void newUser(String username, String password, User.Role role) {
        userFormSuccessLiveData.setValue(false);
        userRepository.newUser(username, password, role, onUserCreateOrUpdateListener);
    }

    public void updateUser(int id, String username, String password, User.Role role) {
        User toUpdate = new User(id, username, password, role);
        userFormSuccessLiveData.setValue(false);
        userRepository.updateUser(toUpdate, onUserCreateOrUpdateListener);
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

    private void createOnUserCreateOrUpdateListener() {
        onUserCreateOrUpdateListener = new UserRepository.OnUserCreateOrUpdateListener() {
            @Override
            public void onUserCreateOrUpdate(String message, boolean success) {
                messageLiveData.setValue(message);
                if (success) {
                    fetchUsers();
                    userFormSuccessLiveData.setValue(true);
                }
            }
        };
    }

}
