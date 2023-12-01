package com.example.csm.viewmodel;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.csm.model.MediaMetadata;
import com.example.csm.repository.MediaRepository;

import java.io.File;

public class ContentManagementViewModel extends ViewModel {

    private MediaRepository mediaRepository;
    private final MutableLiveData<String> messageLiveData;
    private final MutableLiveData<Boolean> fileUploadSuccessLiveData;

    public ContentManagementViewModel() {
        mediaRepository = MediaRepository.getInstance();
        messageLiveData = new MutableLiveData<>();
        fileUploadSuccessLiveData = new MutableLiveData<>();
        fileUploadSuccessLiveData.setValue(false);
    }

    // ============================== PUBLIC ==============================

    public void uploadMedia(String title, String description, File file) {
        MediaMetadata toUpload;
        if (description == null) {
            toUpload = new MediaMetadata(title);
        } else {
            toUpload = new MediaMetadata(title, description);
        }
        mediaRepository.uploadMedia(toUpload, file, new MediaRepository.OnMediaUploadedListener() {
            @Override
            public void onMediaUploaded(String message) {
                fileUploadSuccessLiveData.setValue(true);
                messageLiveData.setValue(message);
            }

            @Override
            public void onMediaUploadFailure(String message) {
                messageLiveData.setValue(message);
            }
        });
    }

    public MutableLiveData<Boolean> getFileUploadSuccessLiveData() {
        return fileUploadSuccessLiveData;
    }

    // ============================== PRIVATE ==============================

}
