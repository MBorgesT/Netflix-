package com.example.client.viewmodel;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.client.model.MediaMetadata;
import com.example.client.repository.MediaRepository;
import com.example.client.util.Resources;

import org.eclipse.jetty.util.resource.Resource;

import java.io.IOException;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.Collections;
import java.util.List;
import java.util.Random;

public class VideoPlayerViewModel extends ViewModel {

    private final MediaRepository mediaRepository;

    private final MutableLiveData<MediaMetadata> mediaMetadataLiveData;
    private final MutableLiveData<String> messageLiveData;
    private final MutableLiveData<String> streamingSourceLiveData;

    public VideoPlayerViewModel() {
        mediaRepository = MediaRepository.getInstance();

        mediaMetadataLiveData = new MutableLiveData<>();
        messageLiveData = new MutableLiveData<>();
        streamingSourceLiveData = new MutableLiveData<>();
    }

    public MutableLiveData<MediaMetadata> getMediaMetadataLiveData() {
        return mediaMetadataLiveData;
    }

    public MutableLiveData<String> getMessageLiveData() {
        return messageLiveData;
    }

    public MutableLiveData<String> getStreamingSourceLiveData() {
        return streamingSourceLiveData;
    }

    public void fetchStreamingSources(int mediaId) {
        mediaRepository.fetchStreamingSources(mediaId, new MediaRepository.OnStreamingSourcesFetchListener() {
            @Override
            public void onStreamingSourcesFetch(List<String> sources) {
                if (sources.isEmpty()) {
                    streamingSourceLiveData.setValue(Resources.backendResourcesUrl);
                } else {
                    new Thread(() -> {
                        try {
                            boolean flag = false;
                            for (String s : sources) {
                                InetAddress inet = InetAddress.getByName(s);
                                if (inet.isReachable(Resources.pingTimeout)) {
                                    streamingSourceLiveData.postValue("http://" + s + ":9090/");
                                    flag = true;
                                    break;
                                }
                            }
                            if (!flag) streamingSourceLiveData.postValue(Resources.backendResourcesUrl);
                        } catch (IOException e) {
                            streamingSourceLiveData.postValue(Resources.backendResourcesUrl);
                        }
                    }).start();
                }
            }

            @Override
            public void onStreamingSourcesFetchFailed(String message) {
                messageLiveData.setValue(message);
            }
        });
    }

    public void fetchMediaById(int mediaId) {
        mediaRepository.fetchMediaById(mediaId, new MediaRepository.OnMediaFetchListener() {
            @Override
            public void onMediaFetch(MediaMetadata media) {
                mediaMetadataLiveData.setValue(media);
            }

            @Override
            public void onMediaFetchFailure(String message) {
                messageLiveData.setValue(message);
            }
        });
    }

}
