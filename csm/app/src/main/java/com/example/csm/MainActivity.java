package com.example.csm;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;

import com.example.csm.util.NetworkUtil;
import com.example.csm.view.LoginActivity;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        NetworkUtil.getInstance().initRequestQueue(this);

        Intent newIntent = new Intent(this, LoginActivity.class);
        this.startActivity(newIntent);
    }
}