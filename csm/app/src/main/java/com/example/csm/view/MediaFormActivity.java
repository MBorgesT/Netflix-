package com.example.csm.view;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;

import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.example.csm.R;
import com.example.csm.model.MediaMetadata;
import com.example.csm.model.User;
import com.example.csm.util.SharedViewModelSource;
import com.example.csm.viewmodel.ContentManagementViewModel;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.file.Files;

public class MediaFormActivity extends AppCompatActivity {

    public enum Functionality {
        CREATE,
        UPDATE
    }

    private ContentManagementViewModel viewModel;
    private ActivityResultLauncher<String> filePickerLauncher;
    private Uri selectedFileUri = null;
    private Functionality functionality;
    private MediaMetadata mediaMetadata;

    private EditText titleEditText;
    private EditText descriptionEditText;
    private Button selectFileButton;
    private Button confirmationButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_media_form);

        viewModel = SharedViewModelSource.getContentManagementViewModel(this);
        viewModel.getFileUploadSuccessLiveData().setValue(false);

        titleEditText = findViewById(R.id.editTextTitle);
        descriptionEditText = findViewById(R.id.editTextDescription);
        selectFileButton = findViewById(R.id.buttonSelectFile);
        confirmationButton = findViewById(R.id.buttonConfirmation);

        functionality = (Functionality) getIntent().getSerializableExtra("functionality");
        if (functionality == Functionality.UPDATE) {
            selectFileButton.setVisibility(View.GONE);
            confirmationButton.setText("Update Media Info");
            mediaMetadata = (MediaMetadata) getIntent().getSerializableExtra("mediaMetadata");
            fillFields();
        } else if (functionality == Functionality.CREATE) {
            selectFileButton.setVisibility(View.VISIBLE);
            confirmationButton.setText("Upload Media");
        } else {
            Log.d("USER_FORM", "No functionality informed");
            finish();
        }

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

        if (functionality == Functionality.CREATE) {
            // TODO: add loading animation for this
            File file = createFileFromUri(selectedFileUri);
            viewModel.uploadMedia(title, description, file);
        } else {
            viewModel.updateMedia(mediaMetadata.getId(), title, description);
        }
    }

    // ============================== PRIVATE ==============================

    private void fillFields() {
        titleEditText.setText(mediaMetadata.getTitle());
        descriptionEditText.setText(mediaMetadata.getDescription());
    }

    private File createFileFromUri(Uri uri) {
        File file = new File(getCacheDir(), "video.mp4");
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            try (InputStream inputStream = getContentResolver().openInputStream(uri);
                 OutputStream outputStream = Files.newOutputStream(file.toPath())) {

                byte[] buffer = new byte[16 * 1024]; // Increased buffer size
                int bytesRead;
                while ((bytesRead = inputStream.read(buffer)) != -1) {
                    outputStream.write(buffer, 0, bytesRead);
                }
                return file;
            } catch (IOException e) {
                e.printStackTrace();
            }
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