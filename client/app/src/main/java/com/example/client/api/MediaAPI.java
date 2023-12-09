package com.example.client.api;

import com.example.client.model.MediaMetadata;

import java.util.List;

import okhttp3.RequestBody;
import okhttp3.Response;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.DELETE;
import retrofit2.http.GET;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import retrofit2.http.Part;
import retrofit2.http.Path;
import retrofit2.http.Streaming;
import retrofit2.http.Url;

public interface MediaAPI {

    @GET("contentManagement/getMediasReadyToPlay")
    Call<List<MediaMetadata>> getMediasReadyToPlay();

    @GET("contentManagement/getMediaById/{mediaId}")
    Call<MediaMetadata> getMediaById(@Path("mediaId") int mediaId);

    @GET("contentManagement/getChunkUris/{mediaId}")
    Call<List<String>> getChunkUris(@Path("mediaId") int mediaId);

    @GET
    @Streaming
    Call<ResponseBody> downloadMedia(@Url String fileUrl);

}
