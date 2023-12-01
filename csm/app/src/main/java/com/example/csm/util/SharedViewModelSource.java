package com.example.csm.util;

import android.content.Context;

import androidx.lifecycle.ViewModelProvider;
import androidx.lifecycle.ViewModelStoreOwner;

import com.example.csm.viewmodel.AccountManagementViewModel;
import com.example.csm.viewmodel.ContentManagementViewModel;

public class SharedViewModelSource {

    private static ContentManagementViewModel contentManagementViewModel;

    private static AccountManagementViewModel accountManagementViewModel;

    public static ContentManagementViewModel getContentManagementViewModel(Context context) {
        if (contentManagementViewModel == null) {
            ViewModelProvider viewModelProvider = new ViewModelProvider((ViewModelStoreOwner) context);
            contentManagementViewModel = viewModelProvider.get(ContentManagementViewModel.class);
        }
        return contentManagementViewModel;
    }

    public static AccountManagementViewModel getAccountManagementViewModel(Context context) {
        if (accountManagementViewModel == null) {
            ViewModelProvider viewModelProvider = new ViewModelProvider((ViewModelStoreOwner) context);
            accountManagementViewModel = viewModelProvider.get(AccountManagementViewModel.class);
        }
        return accountManagementViewModel;
    }

}
