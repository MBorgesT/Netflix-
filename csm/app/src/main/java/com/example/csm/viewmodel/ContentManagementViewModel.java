package com.example.csm.viewmodel;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.csm.model.MediaMetadata;
import com.example.csm.repository.MediaRepository;

import java.io.File;
import java.util.List;

public class ContentManagementViewModel extends ViewModel {

    private final MediaRepository mediaRepository;
    private final MutableLiveData<List<MediaMetadata>> mediaMetadataLiveData;
    private final MutableLiveData<String> messageLiveData;
    private final MutableLiveData<Boolean> fileUploadSuccessLiveData;
    private MediaRepository.OnMediasInfoFetchedListener onMediasInfoFetchedListener;

    public ContentManagementViewModel() {
        mediaRepository = MediaRepository.getInstance();
        mediaMetadataLiveData = new MutableLiveData<>();
        messageLiveData = new MutableLiveData<>();
        fileUploadSuccessLiveData = new MutableLiveData<>();
        fileUploadSuccessLiveData.setValue(false);

        createOnMediasInfoFetchedListener();
    }

    // ============================== PUBLIC ==============================

    public void fetchMetadatas() {
        mediaRepository.fetchMediaInfo(onMediasInfoFetchedListener);
    }

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
                fetchMetadatas();
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

    public MutableLiveData<List<MediaMetadata>> getMediaMetadataLiveData() {
        return mediaMetadataLiveData;
    }

    public MutableLiveData<String> getMessageLiveData() {
        return messageLiveData;
    }

    // ============================== PRIVATE ==============================

    private void createOnMediasInfoFetchedListener() {
        onMediasInfoFetchedListener = new MediaRepository.OnMediasInfoFetchedListener() {
            @Override
            public void onMediasFetched(List<MediaMetadata> metadatas) {
                mediaMetadataLiveData.setValue(metadatas);
            }
        };
    }

}
