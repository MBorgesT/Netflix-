package com.example.csm.view;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;

import android.os.Bundle;
import android.widget.ListView;
import android.widget.Toast;

import com.example.csm.R;
import com.example.csm.viewmodel.AccountManagementViewModel;

public class AccountManagementActivity extends AppCompatActivity implements UserListAdapter.OnUserDeleteListener{

    private AccountManagementViewModel viewModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_account_management);

        ViewModelProvider viewModelProvider = new ViewModelProvider(this);
        viewModel = viewModelProvider.get(AccountManagementViewModel.class);

        setupToast();
        setupListAdapters();
        populateLists();
    }

    private void setupToast() {
        viewModel.getMessageLiveData().observe(this, message -> {
            if (message != null) {
                Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
                viewModel.getMessageLiveData().setValue(null);
            }
        });
    }

    private void setupListAdapters() {
        viewModel.getAdminsLiveData().observe(this, adminsList -> {
            UserListAdapter adapter = new UserListAdapter(this, adminsList);
            adapter.setOnUserDeleteListener(this);
            ListView adminsListView = findViewById(R.id.listAdmins);
            adminsListView.setAdapter(adapter);
        });
        viewModel.getSubscribersLiveData().observe(this, subsList -> {
            UserListAdapter adapter = new UserListAdapter(this, subsList);
            adapter.setOnUserDeleteListener(this);
            ListView subsListView = findViewById(R.id.listSubscribers);
            subsListView.setAdapter(adapter);
        });
    }

    private void populateLists() {
        viewModel.fetchData();
    }

    @Override
    public void onDeleteUser(int userId) {
        viewModel.deleteUser(userId);
    }

}