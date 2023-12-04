package com.example.csm.view;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ListView;
import android.widget.Toast;

import com.example.csm.R;
import com.example.csm.util.SharedViewModelSource;
import com.example.csm.view.listadapter.MediaListAdapter;
import com.example.csm.view.listadapter.UserListAdapter;
import com.example.csm.viewmodel.ContentManagementViewModel;

public class ContentManagementActivity extends AppCompatActivity implements MediaListAdapter.OnMediaDeleteListener {

    private ContentManagementViewModel viewModel;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_content_management);

        viewModel = SharedViewModelSource.getContentManagementViewModel(this);

        setupToast();
        setupListAdapter();
        fetchMetadatas();
    }

    // ============================== PUBLIC ==============================

    public void onClickButtonUploadMedia(View view) {
        Intent newIntent = new Intent(this, MediaFormActivity.class);
        newIntent.putExtra("functionality", MediaFormActivity.Functionality.CREATE);
        this.startActivity(newIntent);
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
        viewModel.getMediaMetadataLiveData().observe(this, metadataList -> {
            MediaListAdapter adapter = new MediaListAdapter(this, metadataList);
            adapter.setOnMediaDeleteListener(this);
            ListView mediasListView = findViewById(R.id.listMedias);
            mediasListView.setAdapter(adapter);
        });
    }

    private void fetchMetadatas() {
        viewModel.fetchMetadatas();
    }

    @Override
    public void onDeleteMedia(int mediaId){
        viewModel.deleteMedia(mediaId);
    }

    // ============================== PRIVATE ==============================

}