package com.example.csm.util;

import android.content.Context;

import androidx.lifecycle.ViewModelProvider;
import androidx.lifecycle.ViewModelStoreOwner;

import com.example.csm.viewmodel.AccountManagementViewModel;

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
