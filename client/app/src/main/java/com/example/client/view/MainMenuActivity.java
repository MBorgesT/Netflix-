package com.example.client.view;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ListView;
import android.widget.Toast;

import com.example.client.R;
import com.example.client.util.SharedViewModelSource;
import com.example.client.view.listadapter.MediaPlayListAdapter;
import com.example.client.viewmodel.MainMenuViewModel;

public class MainMenuActivity extends AppCompatActivity {

    private MainMenuViewModel viewModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_menu);

        viewModel = SharedViewModelSource.getMainMenuViewModel(this);

        setupToast();
        setupListAdapter();
        fetchMedias();
    }

    private void setupToast() {
        viewModel.getMessageLiveData().observe(this, message -> {
            if (message != null) {
                Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
                viewModel.getMessageLiveData().setValue(null);
            }
        });
    }

    private void setupListAdapter() {
        viewModel.getMediaListLiveData().observe(this, mediaList -> {
            MediaPlayListAdapter adapter = new MediaPlayListAdapter(this, mediaList);
            ListView mediasListView = findViewById(R.id.listPlayMedias);
            mediasListView.setAdapter(adapter);
        });
    }

    private void fetchMedias() {
        viewModel.fetchMedias();
    }

    public void onClickButtonDownloadContent(View view) {
        Intent newIntent = new Intent(this, DownloadedContentActivity.class);
        this.startActivity(newIntent);
    }
}