package com.example.client.view;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.widget.ListView;
import android.widget.Toast;

import com.example.client.R;
import com.example.client.util.SharedViewModelSource;
import com.example.client.view.listadapter.MediaDownloadListAdapter;
import com.example.client.viewmodel.MainMenuViewModel;

public class DownloadedContentActivity extends AppCompatActivity implements MediaDownloadListAdapter.OnMediaDownloadCallListener, MediaDownloadListAdapter.OnMediaDeleteListener {

    private MainMenuViewModel viewModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_downloaded_content);

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
            MediaDownloadListAdapter adapter = new MediaDownloadListAdapter(this, mediaList);
            adapter.setOnMediaDownloadListener(this);
            adapter.setOnMediaDeleteListener(this);
            ListView mediasListView = findViewById(R.id.listDownloadMedias);
            mediasListView.setAdapter(adapter);
        });
    }

    private void fetchMedias() {
        viewModel.fetchMedias();
    }

    @Override
    public void onMediaDownloadCall(int mediaId, String folderName) {
        viewModel.downloadMedia(mediaId, folderName);
    }

    @Override
    public void onMediaDelete(int mediaId, String folderName) {
        viewModel.deleteDownloadedMedia(mediaId, folderName);
    }
}