package com.example.client.repository;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;

import com.example.client.api.MediaAPI;
import com.example.client.db.AppDBContract;
import com.example.client.db.DBHelper;
import com.example.client.model.MediaMetadata;
import com.example.client.util.ApiBuilder;
import com.example.client.util.Resources;

import java.util.List;

import okhttp3.ResponseBody;
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

    public interface OnChunkUrisFetchListener {
        void onChunkUrisFetch(List<String> uris);
        void onChunkUrisFetchFailure(String message);
    }

    public interface OnDownloadStatusUpdateListener {
        void onDownloadStatusUpdate(String message);
    }

    public interface OnSignalAvailabityListener {
        void onSignalAvailability(String message);
    }

    public interface OnStreamingSourcesFetchListener {
        void onStreamingSourcesFetch(List<String> sources);
        void onStreamingSourcesFetchFailed(String message);
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

    public void fetchChunkUris(int mediaId, OnChunkUrisFetchListener listener) {
        api.getChunkUris(mediaId).enqueue(new Callback<List<String>>() {
            @Override
            public void onResponse(Call<List<String>> call, Response<List<String>> response) {
                if (response.isSuccessful()) {
                    listener.onChunkUrisFetch(response.body());
                } else {
                    listener.onChunkUrisFetchFailure(response.code() + ": Error on fetching uris");
                }
            }

            @Override
            public void onFailure(Call<List<String>> call, Throwable t) {
                listener.onChunkUrisFetchFailure("Error on fetching uris");
            }
        });
    }

    public void updateDownloadStatus(int mediaId, MediaMetadata.DownloadStatus status,
                                     OnDownloadStatusUpdateListener listener) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("download_status", status.toString());

        String selection = "media_id = ?";
        String[] selectionArgs = { String.valueOf(mediaId) };

        int rowsAffected = db.update(
                "download_management",  // Replace "table_name" with the name of your table
                values,
                selection,
                selectionArgs
        );

        if (rowsAffected > 0) {
            listener.onDownloadStatusUpdate("Update successful");
        } else {
            listener.onDownloadStatusUpdate("Update failed");
        }
    }

    public void singalMeshAvailability(int mediaId, OnSignalAvailabityListener listener) {
        api.signalStreamAvailability(mediaId).enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                if (response.isSuccessful()) {
                    listener.onSignalAvailability("Streaming availability signaled");
                } else {
                    listener.onSignalAvailability("Error on signaling streaming availability");
                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                listener.onSignalAvailability("Error on signaling streaming availability");
            }
        });
    }

    public void singalMeshUnavailability(int mediaId, OnSignalAvailabityListener listener) {
        api.signalStreamUnavailability(mediaId).enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                if (response.isSuccessful()) {
                    listener.onSignalAvailability("Streaming availability signaled");
                } else {
                    listener.onSignalAvailability("Error on signaling streaming availability");
                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                listener.onSignalAvailability("Error on signaling streaming availability");
            }
        });
    }

    public void fetchStreamingSources(int mediaId, OnStreamingSourcesFetchListener listener) {
        api.getStreamingSources(mediaId).enqueue(new Callback<List<String>>() {
            @Override
            public void onResponse(Call<List<String>> call, Response<List<String>> response) {
                if (response.isSuccessful()) {
                    listener.onStreamingSourcesFetch(response.body());
                } else {
                    listener.onStreamingSourcesFetchFailed("Failed to fetch streaming sources");
                }
            }

            @Override
            public void onFailure(Call<List<String>> call, Throwable t) {
                listener.onStreamingSourcesFetchFailed("Failed to fetch streaming sources");
            }
        });
    }

    // ============================== PRIVATE ==============================

    private List<MediaMetadata> getDownloadStatus(List<MediaMetadata> medias) {
        for (MediaMetadata mm : medias) {
            mm.setDownloadStatus(getDownloadStatus(mm.getId()));
        }
        return medias;
    }

    private MediaMetadata getDownloadStatus(MediaMetadata media) {
        media.setDownloadStatus(getDownloadStatus(media.getId()));
        return media;
    }

    private MediaMetadata.DownloadStatus getDownloadStatus(int mediaId) {
        String[] projection = {
                AppDBContract.DownloadManagementTable.DOWNLOAD_STATUS
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
            String download_status = cursor.getString(cursor.getColumnIndexOrThrow(AppDBContract.DownloadManagementTable.DOWNLOAD_STATUS));
            cursor.close();
            return MediaMetadata.DownloadStatus.valueOf(download_status);
        } else {
            insertNewMediaToDB(mediaId);
            return MediaMetadata.DownloadStatus.NOT_DOWNLOADED;
        }
    }

    private void insertNewMediaToDB(int mediaId) {
        ContentValues values = new ContentValues();
        values.put(AppDBContract.DownloadManagementTable.MEDIA_ID, mediaId); // Example data for a column named 'COLUMN_NAME'
        values.put(AppDBContract.DownloadManagementTable.DOWNLOAD_STATUS, MediaMetadata.DownloadStatus.NOT_DOWNLOADED.toString());

        long newRowId = dbHelper.getWritableDatabase().insert(AppDBContract.DownloadManagementTable.TABLE_NAME, null, values);

        // Check if the insertion was successful
        if (newRowId == -1) {
            throw new SQLException("Error inserting media to DB");
        }
    }

}
