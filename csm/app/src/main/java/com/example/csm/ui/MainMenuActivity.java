package com.example.csm.ui;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import com.example.csm.R;

public class MainMenuActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_menu);
    }

    public void onClickButtonSubscriberManagement(View view) {
        Intent newIntent = new Intent(this, AccountManagementActivity.class);
        this.startActivity(newIntent);
    }
}