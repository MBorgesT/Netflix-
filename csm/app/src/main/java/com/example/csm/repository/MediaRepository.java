package com.example.csm.repository;

import com.example.csm.api.MediaAPI;
import com.example.csm.util.ApiBuilder;
import com.example.csm.model.MediaMetadata;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;

import okhttp3.MediaType;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MediaRepository {

    public interface OnMediaUploadedListener {
        void onMediaUploaded(String message);

        void onMediaUploadFailure(String message);
    }

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

    public void uploadMedia(MediaMetadata toUpload, File file, OnMediaUploadedListener listener) {
        JSONObject json = new JSONObject();
        try {
            json.put("title", toUpload.getTitle());

            String description = toUpload.getDescription();
            if (description != null) {
                json.put("description", description);
            }
        } catch (JSONException e) {
            e.printStackTrace();
            listener.onMediaUploadFailure("Failure on building JSON");
            return;
        }

        RequestBody jsonRequestBody = RequestBody.create(MediaType.parse("application/json"), json.toString());

        RequestBody fileRequestBody = RequestBody.create(MediaType.parse("video/*"), file);
        api.uploadMedia(jsonRequestBody, fileRequestBody).enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                if (response.isSuccessful()) {
                    listener.onMediaUploaded(response.message());
                } else {
                    listener.onMediaUploadFailure(response.message());
                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                t.printStackTrace();
                listener.onMediaUploadFailure("Request failed");
            }
        });
    }

    // ============================== PRIVATE ==============================

}
