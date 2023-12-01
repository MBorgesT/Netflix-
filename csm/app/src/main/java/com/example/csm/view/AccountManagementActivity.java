package com.example.csm.view;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ListView;
import android.widget.Toast;

import com.example.csm.R;
import com.example.csm.view.listadapter.UserListAdapter;
import com.example.csm.viewmodel.AccountManagementViewModel;
import com.example.csm.util.SharedViewModelSource;

public class AccountManagementActivity extends AppCompatActivity implements UserListAdapter.OnUserDeleteListener{

    private AccountManagementViewModel viewModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_account_management);

        viewModel = SharedViewModelSource.getAccountManagementViewModel(this);

        setupToast();
        setupListAdapters();
        fetchUsers();
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

    private void fetchUsers() {
        viewModel.fetchUsers();
    }

    @Override
    public void onDeleteUser(int userId) {
        viewModel.deleteUser(userId);
    }

    public void onClickButtonNewUser(View view) {
        Intent newIntent = new Intent(this, UserFormActivity.class);
        newIntent.putExtra("functionality", UserFormActivity.Functionality.CREATE);
        this.startActivity(newIntent);
    }

}