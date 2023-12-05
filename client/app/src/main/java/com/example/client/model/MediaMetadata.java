package com.example.client.model;

import java.io.Serializable;

public class MediaMetadata implements Serializable {

    public enum UploadStatus {
        PROCESSING,
        FINISHED,
        ERROR,
    }

    private int id;
    private String title;
    private String description;

    private String folderName;
    private UploadStatus uploadStatus;

    public MediaMetadata() {

    }

    public MediaMetadata(int id, String title) {
        this.id = id;
        this.title = title;
    }

    public MediaMetadata(int id, String title, String description) {
        this.id = id;
        this.title = title;
        this.description = description;
    }

    public MediaMetadata(String title, UploadStatus uploadStatus) {
        this.title = title;
        this.uploadStatus = uploadStatus;
    }

    public MediaMetadata(String title, String description, UploadStatus uploadStatus) {
        this.title = title;
        this.description = description;
        this.uploadStatus = uploadStatus;
    }

    public MediaMetadata(String title, String description) {
        this.title = title;
        this.description = description;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getFolderName() {
        return folderName;
    }

    public void setFolderName(String folderName) {
        this.folderName = folderName;
    }

    public UploadStatus getUploadStatus() {
        return uploadStatus;
    }

    public void setUploadStatus(UploadStatus uploadStatus) {
        this.uploadStatus = uploadStatus;
    }
}
