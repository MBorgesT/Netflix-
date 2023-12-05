package com.example.client.repository;

import com.example.client.api.MediaAPI;
import com.example.client.model.MediaMetadata;
import com.example.client.util.ApiBuilder;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MediaRepository {

    public interface OnMediaListFetchListener {
        void onMediaListFetch(List<MediaMetadata> medias);
        void onMediaListFetchFailure(String message);
    }

    public interface OnMediaFetchListener {
        void onMediaFetch(MediaMetadata media);
        void onMediaFetchFailure(String message);
    }

    private static MediaRepository instance;
    private static MediaAPI api;

    private MediaRepository() {
        api = ApiBuilder.create(MediaAPI.class);
    }

    public static MediaRepository getInstance() {
        if (instance == null) {
            instance = new MediaRepository();
        }
        return instance;
    }

    // ============================== PUBLIC ==============================

    public void fetchMediaList(OnMediaListFetchListener listener) {
        api.getMediasReadyToPlay().enqueue(new Callback<List<MediaMetadata>>() {
            @Override
            public void onResponse(Call<List<MediaMetadata>> call, Response<List<MediaMetadata>> response) {
                if (response.isSuccessful()) {
                    listener.onMediaListFetch(response.body());
                } else {
                    listener.onMediaListFetchFailure(response.code() + ": " + response.body());
                }
            }

            @Override
            public void onFailure(Call<List<MediaMetadata>> call, Throwable t) {
                listener.onMediaListFetchFailure("Error on fetching medias");
            }
        });
    }

    public void fetchMediaById(int mediaId, OnMediaFetchListener listener) {
        api.getMediaById(mediaId).enqueue(new Callback<MediaMetadata>() {
            @Override
            public void onResponse(Call<MediaMetadata> call, Response<MediaMetadata> response) {
                if (response.isSuccessful()) {
                    listener.onMediaFetch(response.body());
                } else {
                    listener.onMediaFetchFailure(response.code() + ": " + response.body());
                }
            }

            @Override
            public void onFailure(Call<MediaMetadata> call, Throwable t) {
                listener.onMediaFetchFailure("Error on fetching media");
            }
        });
    }

}
