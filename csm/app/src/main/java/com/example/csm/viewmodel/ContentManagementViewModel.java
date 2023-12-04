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
    private MediaRepository.OnMediaUploadOrUpdateListener onMediaUploadOrUpdateListener;
    private MediaRepository.OnMediaDeleteListener onMediaDeleteListener;

    public ContentManagementViewModel() {
        mediaRepository = MediaRepository.getInstance();
        mediaMetadataLiveData = new MutableLiveData<>();
        messageLiveData = new MutableLiveData<>();
        fileUploadSuccessLiveData = new MutableLiveData<>();
        fileUploadSuccessLiveData.setValue(false);

        createOnMediasInfoFetchedListener();
        createOnMediaUploadOrUpdateListener();
        createOnMediaDeleteListener();
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
        mediaRepository.uploadMedia(toUpload, file, onMediaUploadOrUpdateListener);
    }

    public void updateMedia(int mediaId, String title, String description) {
        MediaMetadata toUpdate;
        if (description == null) {
            toUpdate = new MediaMetadata(mediaId, title);
        } else {
            toUpdate = new MediaMetadata(mediaId, title, description);
        }
        mediaRepository.updateMedia(toUpdate, onMediaUploadOrUpdateListener);
    }


    public void deleteMedia(int mediaId) {
        mediaRepository.deleteMedia(mediaId, onMediaDeleteListener);
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

    private void createOnMediaUploadOrUpdateListener() {
        onMediaUploadOrUpdateListener = new MediaRepository.OnMediaUploadOrUpdateListener() {
            @Override
            public void onMediaUploadOrUpdate(String message) {
                fileUploadSuccessLiveData.setValue(true);
                messageLiveData.setValue(message);
                fetchMetadatas();
            }

            @Override
            public void onMediaUploadOrUpdateFailure(String message) {
                messageLiveData.setValue(message);
            }
        };
    }

    private void createOnMediasInfoFetchedListener() {
        onMediasInfoFetchedListener = new MediaRepository.OnMediasInfoFetchedListener() {
            @Override
            public void onMediasFetched(List<MediaMetadata> metadatas) {
                mediaMetadataLiveData.setValue(metadatas);
            }
        };
    }

    private void createOnMediaDeleteListener() {
        onMediaDeleteListener = new MediaRepository.OnMediaDeleteListener() {
            @Override
            public void onMediaDelete(String message, boolean success) {
                messageLiveData.setValue(message);
                if (success) {
                    fetchMetadatas();
                }
            }
        };
    }

}
