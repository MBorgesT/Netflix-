package com.example.csm.view;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;

import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;

import com.example.csm.R;
import com.example.csm.util.SharedViewModelSource;
import com.example.csm.viewmodel.ContentManagementViewModel;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

public class MediaFormActivity extends AppCompatActivity {

    private ContentManagementViewModel viewModel;

    private ActivityResultLauncher<String> filePickerLauncher;
    private Uri selectedFileUri = null;

    private EditText titleEditText;
    private EditText descriptionEditText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_media_form);

        viewModel = SharedViewModelSource.getContentManagementViewModel(this);

        titleEditText = findViewById(R.id.editTextTitle);
        descriptionEditText = findViewById(R.id.editTextDescription);

        filePickerLauncher = registerForActivityResult(new ActivityResultContracts.GetContent(),
                this::onFilePicked);

        setupFormSuccessObserver();
    }

    // ============================== PUBLIC ==============================

    public void onClickButtonSelectFile(View view) {
        filePickerLauncher.launch("*/*");
    }

    private void onFilePicked(Uri uri) {
        selectedFileUri = uri;
    }

    public void onClickButtonConfirmation(View view) {
        String title = String.valueOf(titleEditText.getText());
        String description = String.valueOf(descriptionEditText.getText());
        File file = createFileFromUri(selectedFileUri);

        viewModel.uploadMedia(title, description, file);
    }

    // ============================== PRIVATE ==============================

    private File createFileFromUri(Uri uri) {
        File file = new File(getCacheDir(), "video.mp4");
        try (InputStream inputStream = getContentResolver().openInputStream(uri);
             OutputStream outputStream = new FileOutputStream(file)) {

            byte[] buffer = new byte[4 * 1024]; // Adjust buffer size as needed
            int bytesRead;
            while ((bytesRead = inputStream.read(buffer)) != -1) {
                outputStream.write(buffer, 0, bytesRead);
            }
            return file;
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    private void setupFormSuccessObserver() {
        viewModel.getFileUploadSuccessLiveData().observe(this, formSuccess ->{
            if (formSuccess) {
                finish();
            }
        });
    }

}