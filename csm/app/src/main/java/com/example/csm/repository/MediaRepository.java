package com.example.csm.repository;

import android.util.Log;

import androidx.annotation.NonNull;

import com.example.csm.api.MediaAPI;
import com.example.csm.util.ApiBuilder;
import com.example.csm.model.MediaMetadata;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.util.List;

import okhttp3.MediaType;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MediaRepository {

    public interface OnMediaUploadOrUpdateListener {
        void onMediaUploadOrUpdate(String message);

        void onMediaUploadOrUpdateFailure(String message);
    }

    public interface OnMediaDeleteListener {
        void onMediaDelete(String message, boolean success);
    }

    public interface OnMediasInfoFetchedListener {
        void onMediasFetched(List<MediaMetadata> metadatas);
    }

    private static final String TAG = "MediaRepository";

    private static MediaRepository instance;
    private MediaAPI api;

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

    public void uploadMedia(MediaMetadata toUpload, File file, OnMediaUploadOrUpdateListener listener) {
        JSONObject json = new JSONObject();
        try {
            json.put("title", toUpload.getTitle());

            String description = toUpload.getDescription();
            if (description != null) {
                json.put("description", description);
            }
        } catch (JSONException e) {
            e.printStackTrace();
            listener.onMediaUploadOrUpdateFailure("Failure on building JSON");
            return;
        }

        RequestBody jsonRequestBody = RequestBody.create(MediaType.parse("application/json"), json.toString());

        RequestBody fileRequestBody = RequestBody.create(MediaType.parse("video/*"), file);
        api.uploadMedia(jsonRequestBody, fileRequestBody).enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                if (response.isSuccessful()) {
                    listener.onMediaUploadOrUpdate(response.message());
                } else {
                    listener.onMediaUploadOrUpdateFailure(response.message());
                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                t.printStackTrace();
                listener.onMediaUploadOrUpdateFailure("Request failed");
            }
        });
    }

    public void updateMedia(MediaMetadata toUpdate, OnMediaUploadOrUpdateListener listener) {
        JSONObject json = new JSONObject();
        try {
            json.put("id", toUpdate.getId());
            json.put("title", toUpdate.getTitle());
            String description = toUpdate.getDescription();
            if (description != null) {
                json.put("description", description);
            }
        } catch (JSONException e) {
            e.printStackTrace();
            listener.onMediaUploadOrUpdateFailure("Failure on building JSON");
            return;
        }

        RequestBody jsonRequestBody = RequestBody.create(MediaType.parse("application/json"), json.toString());
        api.updateMedia(jsonRequestBody).enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                if (response.isSuccessful()) {
                    listener.onMediaUploadOrUpdate(response.message());
                } else {
                    listener.onMediaUploadOrUpdateFailure(response.message());
                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                t.printStackTrace();
                listener.onMediaUploadOrUpdateFailure("Request failed");
            }
        });
    }

    public void deleteMedia(int mediaId, OnMediaDeleteListener listener) {
        api.deleteMedia(mediaId).enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                listener.onMediaDelete(response.message(), response.code() == 200);
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                listener.onMediaDelete("Error deleting media", false);
            }
        });
    }

    public void fetchMediaInfo(OnMediasInfoFetchedListener listener) {
        api.getMediasInfo().enqueue(new Callback<List<MediaMetadata>>() {
            @Override
            public void onResponse(@NonNull Call<List<MediaMetadata>> call, @NonNull Response<List<MediaMetadata>> response) {
                listener.onMediasFetched(response.body());
            }

            @Override
            public void onFailure(@NonNull Call<List<MediaMetadata>> call, @NonNull Throwable t) {
                Log.d(TAG, "onFailure: failed to get admins");
            }
        });
    }

    // ============================== PRIVATE ==============================

}
