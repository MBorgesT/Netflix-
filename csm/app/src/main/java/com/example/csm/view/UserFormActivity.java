package com.example.csm.view;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;

import com.example.csm.R;
import com.example.csm.model.User;
import com.example.csm.viewmodel.AccountManagementViewModel;
import com.example.csm.util.SharedViewModelSource;

public class UserFormActivity extends AppCompatActivity {

    public enum Functionality {
        CREATE,
        UPDATE
    }

    private AccountManagementViewModel viewModel;
    private Functionality functionality;
    private User user;
    private EditText usernameTextView;
    private EditText passwordEditText;
    private RadioGroup roleRadioGroup;
    private RadioButton adminRadioButton;
    private RadioButton subscriberRadioButton;
    private Button confirmationButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_form);

        viewModel = SharedViewModelSource.getAccountManagementViewModel(this);

        usernameTextView = findViewById(R.id.editTextUsername);
        passwordEditText = findViewById(R.id.editTextPassword);
        roleRadioGroup = findViewById(R.id.radioGroupRole);
        adminRadioButton = findViewById(R.id.radioButtonAdmin);
        subscriberRadioButton = findViewById(R.id.radioButtonSubscriber);
        confirmationButton = findViewById(R.id.buttonConfirmation);

        functionality = (Functionality) getIntent().getSerializableExtra("functionality");
        if (functionality == Functionality.UPDATE) {
            confirmationButton.setText("Update User");
            user = (User) getIntent().getSerializableExtra("user");
            fillFields();
        } else if (functionality == Functionality.CREATE) {
            confirmationButton.setText("New User");
        } else {
            Log.d("USER_FORM", "No functionality informed");
            finish();
        }

        setupFormSuccessObserver();
    }

    public void onClickButtonConfirmation(View view) {
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

        if (functionality == Functionality.UPDATE) {
            viewModel.updateUser(user.getId(), username, password, role);
        } else if (functionality == Functionality.CREATE) {
            viewModel.newUser(username, password, role);
        } else {
            Log.e("USER_FORM", "This should not happen, as this is validated on onCreate");
            finish();
        }
    }

    private void fillFields() {
        usernameTextView.setText(user.getUsername());
        switch (user.getRole()) {
            case ADMIN:
                adminRadioButton.setChecked(true);
                break;
            case SUBSCRIBER:
                subscriberRadioButton.setChecked(true);
                break;
            default:
                Log.e("USER_FORM", "This should not happen, as this is validated on onCreate");
                finish();
                break;
        }
    }

    private void setupFormSuccessObserver() {
        viewModel.getUserFormSuccessLiveData().observe(this, formSuccess ->{
            if (formSuccess) {
                finish();
            }
        });
    }
}