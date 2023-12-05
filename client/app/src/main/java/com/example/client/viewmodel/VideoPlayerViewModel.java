package com.example.client.viewmodel;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.client.model.MediaMetadata;
import com.example.client.repository.MediaRepository;

public class VideoPlayerViewModel extends ViewModel {

    private final MediaRepository mediaRepository;

    private final MutableLiveData<MediaMetadata> mediaMetadataLiveData;
    private final MutableLiveData<String> messageLiveData;

    private MediaRepository.OnMediaFetchListener onMediaFetchListener;

    public VideoPlayerViewModel() {
        mediaRepository = MediaRepository.getInstance();

        mediaMetadataLiveData = new MutableLiveData<>();
        messageLiveData = new MutableLiveData<>();

        createOnMediaFetchListener();
    }

    public MutableLiveData<MediaMetadata> getMediaMetadataLiveData() {
        return mediaMetadataLiveData;
    }

    public MutableLiveData<String> getMessageLiveData() {
        return messageLiveData;
    }

    public void fetchMediaById(int mediaId) {
        mediaRepository.fetchMediaById(mediaId, onMediaFetchListener);
    }

    private void createOnMediaFetchListener() {
        onMediaFetchListener = new MediaRepository.OnMediaFetchListener() {
            @Override
            public void onMediaFetch(MediaMetadata media) {
                mediaMetadataLiveData.setValue(media);
            }

            @Override
            public void onMediaFetchFailure(String message) {
                messageLiveData.setValue(message);
            }
        };
    }

}
