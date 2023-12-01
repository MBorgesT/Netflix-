package com.example.csm.api;

import com.example.csm.model.MediaMetadata;

import java.util.List;

import okhttp3.RequestBody;
import okhttp3.Response;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.Part;

public interface MediaAPI {

    @GET("contentManagement/getMediaMetadatas")
    Call<List<MediaMetadata>> getMediasInfo();

    @Multipart
    @POST("contentManagement/uploadMedia")
    Call<ResponseBody> uploadMedia(@Part("json") RequestBody json, @Part("file") RequestBody file);

}
