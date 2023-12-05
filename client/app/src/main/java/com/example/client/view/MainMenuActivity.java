package com.example.client.view;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;
import androidx.lifecycle.ViewModelStoreOwner;

import android.os.Bundle;
import android.widget.ListView;
import android.widget.Toast;

import com.example.client.R;
import com.example.client.view.listadapter.MediaListAdapter;
import com.example.client.viewmodel.MainMenuViewModel;

public class MainMenuActivity extends AppCompatActivity {

    private MainMenuViewModel viewModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_menu);

        ViewModelProvider viewModelProvider = new ViewModelProvider(this);
        viewModel = viewModelProvider.get(MainMenuViewModel.class);

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
            MediaListAdapter adapter = new MediaListAdapter(this, mediaList);
            ListView mediasListView = findViewById(R.id.listMedias);
            mediasListView.setAdapter(adapter);
        });
    }

    private void fetchMedias() {
        viewModel.fetchMedias();
    }

}