package com.example.client.viewmodel;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.client.model.MediaMetadata;
import com.example.client.repository.MediaRepository;

import java.util.List;

public class MainMenuViewModel extends ViewModel {

    private final MediaRepository mediaRepository;

    private final MutableLiveData<List<MediaMetadata>> mediaListLiveData;
    private final MutableLiveData<String> messageLiveData;

    private MediaRepository.OnMediaListFetchListener mediaListFetchListener;

    public MainMenuViewModel() {
        mediaRepository = MediaRepository.getInstance();

        mediaListLiveData = new MutableLiveData<>();
        messageLiveData = new MutableLiveData<>();

        createOnMediaListFetchListener();
    }

    public MutableLiveData<List<MediaMetadata>> getMediaListLiveData() {
        return mediaListLiveData;
    }

    public MutableLiveData<String> getMessageLiveData() {
        return messageLiveData;
    }

    public void fetchMedias() {
        mediaRepository.fetchMediaList(mediaListFetchListener);
    }

    private void createOnMediaListFetchListener() {
        mediaListFetchListener = new MediaRepository.OnMediaListFetchListener() {
            @Override
            public void onMediaListFetch(List<MediaMetadata> medias) {
                mediaListLiveData.setValue(medias);
            }

            @Override
            public void onMediaListFetchFailure(String message) {
                messageLiveData.setValue(message);
            }
        };
    }

}
