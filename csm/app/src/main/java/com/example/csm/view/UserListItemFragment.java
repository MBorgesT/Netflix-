package com.example.csm.view;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;

import com.example.csm.R;

public class UserListItemFragment extends AppCompatActivity {

    private int id;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_list_item);
    }

    public void onClickButtonDelete(View view) {

    }

    public void setId(int id) {
        this.id = id;
    }

}