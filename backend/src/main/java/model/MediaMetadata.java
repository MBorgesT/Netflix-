package model;

import jakarta.persistence.*;

@Entity
@Table(name="media_metadata")
public class MediaMetadata {

    public enum UploadStatus {
        PROCESSING,
        FINISHED,
        ERROR,
    }

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;
    private String title;
    private String description;
    @Column(name="folder_name")
    private String folderName;
    @Column(name="upload_status")
    private UploadStatus uploadStatus;

    public MediaMetadata() {}

    public MediaMetadata(String title, String folderName, UploadStatus uploadStatus) {
        this.title = title;
        this.folderName = folderName;
        this.uploadStatus = uploadStatus;
    }

    public MediaMetadata(String title, String description, String folderName, UploadStatus uploadStatus) {
        this.title = title;
        this.description = description;
        this.folderName = folderName;
        this.uploadStatus = uploadStatus;
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
