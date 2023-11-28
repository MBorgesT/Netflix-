package com.example.csm.view;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.RadioGroup;

import com.example.csm.R;
import com.example.csm.model.User;
import com.example.csm.viewmodel.AccountManagementViewModel;
import com.example.csm.viewmodel.SharedViewModelSource;

public class UserFormActivity extends AppCompatActivity {

    private AccountManagementViewModel viewModel;
    private EditText usernameTextView;
    private EditText passwordEditText;
    private RadioGroup roleRadioGroup;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_form);

        viewModel = SharedViewModelSource.getAccountManagementViewModel(this);

        usernameTextView = findViewById(R.id.editTextUsername);
        passwordEditText = findViewById(R.id.editTextPassword);
        roleRadioGroup = findViewById(R.id.radioGroupRole);

        setupUserCreatedObserver();
    }

    public void onClickButtonNewUser(View view) {
        String username = usernameTextView.getText().toString();
        String password = passwordEditText.getText().toString();

        int checkedId = roleRadioGroup.getCheckedRadioButtonId();
        User.Role role;
        if (checkedId == R.id.radioButtonAdmin) {
            role = User.Role.ADMIN;
        } else if (checkedId == R.id.radioButtonSubscriber) {
            role = User.Role.SUBSCRIBER;
        } else {
            role = null;
        }

        viewModel.newUser(username, password, role);
    }

    private void setupUserCreatedObserver() {
        viewModel.getUserCreatedLiveData().observe(this, userCreated ->{
            if (userCreated) {
                finish();
            }
        });
    }
}