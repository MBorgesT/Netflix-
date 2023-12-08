package com.example.client.repository;

import android.app.Application;
import android.content.ContentValues;
import android.database.Cursor;
import android.database.SQLException;

import com.example.client.api.MediaAPI;
import com.example.client.db.AppDBContract;
import com.example.client.db.DBHelper;
import com.example.client.model.MediaMetadata;
import com.example.client.util.ApiBuilder;
import com.example.client.util.Resources;

import java.util.List;

import javax.inject.Inject;

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
    private static DBHelper dbHelper;

    private MediaRepository() {
        api = ApiBuilder.create(MediaAPI.class);
        dbHelper = Resources.getDBHelper();
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
                    List<MediaMetadata> medias = response.body();
                    assert medias != null;
                    medias = getDownloadStatus(medias);
                    listener.onMediaListFetch(medias);
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
                    MediaMetadata media = response.body();
                    assert media != null;
                    media = getDownloadStatus(media);
                    listener.onMediaFetch(media);
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

    // ============================== PRIVATE ==============================

    public List<MediaMetadata> getDownloadStatus(List<MediaMetadata> medias) {
        for (MediaMetadata mm : medias) {
            mm.setDownloaded(isMediaDownloaded(mm.getId()));
        }
        return medias;
    }

    public MediaMetadata getDownloadStatus(MediaMetadata media) {
        media.setDownloaded(isMediaDownloaded(media.getId()));
        return media;
    }

    private boolean isMediaDownloaded(int mediaId) {
        String[] projection = {
                AppDBContract.DownloadManagementTable.IS_DOWNLOADED
        };
        String selection = AppDBContract.DownloadManagementTable.MEDIA_ID + " = ?";
        String[] selectionArgs = {String.valueOf(mediaId)};

        Cursor cursor = dbHelper.getReadableDatabase().query(
                AppDBContract.DownloadManagementTable.TABLE_NAME,   // The table to query
                projection,                                         // The columns to return
                selection,                                          // The columns for the WHERE clause
                selectionArgs,                                      // The values for the WHERE clause
                null,                                               // Don't group the rows
                null,                                               // Don't filter by row groups
                null                                                // The sort order
        );

        assert cursor != null;
        if (cursor.moveToFirst()) {
            boolean downloaded = (cursor.getInt(cursor.getColumnIndexOrThrow(AppDBContract.DownloadManagementTable.IS_DOWNLOADED)) != 0);
            cursor.close();
            return downloaded;
        } else {
            insertNewMediaToDB(mediaId);
            return false;
        }
    }

    private void insertNewMediaToDB(int mediaId) {
        ContentValues values = new ContentValues();
        values.put(AppDBContract.DownloadManagementTable.MEDIA_ID, mediaId); // Example data for a column named 'COLUMN_NAME'
        values.put(AppDBContract.DownloadManagementTable.IS_DOWNLOADED, false);

        long newRowId = dbHelper.getWritableDatabase().insert(AppDBContract.DownloadManagementTable.TABLE_NAME, null, values);

        // Check if the insertion was successful
        if (newRowId == -1) {
            throw new SQLException("Error inserting media to DB");
        }
    }

}
