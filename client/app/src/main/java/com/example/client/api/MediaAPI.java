package com.example.client.api;

import com.example.client.model.MediaMetadata;

import java.util.List;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.POST;
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

    @POST("contentManagement/signalStreamAvailability/{mediaId}")
    Call<ResponseBody> signalStreamAvailability(@Path("mediaId") int mediaId);

    @POST("contentManagement/signalStreamUnavailability/{mediaId}")
    Call<ResponseBody> signalStreamUnavailability(@Path("mediaId") int mediaId);

    @GET("contentManagement/getStreamingSources/{mediaId}")
    Call<List<String>> getStreamingSources(@Path("mediaId") int mediaId);

    @GET
    @Streaming
    Call<ResponseBody> downloadMedia(@Url String fileUrl);

}
