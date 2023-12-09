package com.example.client.util;

import android.content.Context;

import androidx.lifecycle.ViewModelProvider;
import androidx.lifecycle.ViewModelStoreOwner;

import com.example.client.viewmodel.MainMenuViewModel;

public class SharedViewModelSource {

    private static MainMenuViewModel mainMenuViewModel;

    public static MainMenuViewModel getMainMenuViewModel(Context context) {
        if (mainMenuViewModel == null) {
            ViewModelProvider viewModelProvider = new ViewModelProvider((ViewModelStoreOwner) context);
            mainMenuViewModel = viewModelProvider.get(MainMenuViewModel.class);
        }
        return mainMenuViewModel;
    }

}
