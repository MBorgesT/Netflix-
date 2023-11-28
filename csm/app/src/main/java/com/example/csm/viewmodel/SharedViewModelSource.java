package com.example.csm.viewmodel;

import android.content.Context;

import androidx.lifecycle.ViewModelProvider;
import androidx.lifecycle.ViewModelStoreOwner;

public class SharedViewModelSource {

    private static AccountManagementViewModel accountManagementViewModel;

    public static AccountManagementViewModel getAccountManagementViewModel(Context context) {
        if (accountManagementViewModel == null) {
            ViewModelProvider viewModelProvider = new ViewModelProvider((ViewModelStoreOwner) context);
            accountManagementViewModel = viewModelProvider.get(AccountManagementViewModel.class);
        }
        return accountManagementViewModel;
    }

}
