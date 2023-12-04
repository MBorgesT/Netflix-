package com.example.csm.api;

import com.example.csm.model.MediaMetadata;

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

public interface MediaAPI {

    @GET("contentManagement/getMediaMetadatas")
    Call<List<MediaMetadata>> getMediasInfo();

    @Multipart
    @POST("contentManagement/uploadMedia")
    Call<ResponseBody> uploadMedia(@Part("json") RequestBody json, @Part("file") RequestBody file);

    @PUT("contentManagement/updateMedia")
    Call<ResponseBody> updateMedia(@Body RequestBody body);

    @DELETE("contentManagement/deleteMedia/{mediaId}")
    Call<ResponseBody> deleteMedia(@Path("mediaId") int mediaId);

}
