package com.example.csm.view;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import com.example.csm.R;
import com.example.csm.util.SharedViewModelSource;
import com.example.csm.viewmodel.ContentManagementViewModel;

public class ContentManagementActivity extends AppCompatActivity {

    private ContentManagementViewModel viewModel;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_content_management);

        viewModel = SharedViewModelSource.getContentManagementViewModel(this);
    }


    public void onClickButtonUploadMedia(View view) {
        Intent newIntent = new Intent(this, MediaFormActivity.class);
        this.startActivity(newIntent);
    }
}