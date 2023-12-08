package com.example.client.db;

public class AppDBContract {

    public static class DownloadManagementTable {

        public static final String TABLE_NAME = "download_management";
        public static final String MEDIA_ID = "media_id";
        public static final String IS_DOWNLOADED = "is_downloaded";

        public static final String CREATE_TABLE = "CREATE TABLE " +
                TABLE_NAME + " (" +
                MEDIA_ID + " INTEGER PRIMARY KEY NOT NULL, " +
                IS_DOWNLOADED + " INTEGER NOT NULL " +
                ")";
    }
}
