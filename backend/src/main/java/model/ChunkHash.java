package model;

import jakarta.persistence.*;

@Entity
@Table(name="chunk_hash")
public class ChunkHash {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @Column(name="media_id")
    private int mediaId;

    @Column(name="file_name")
    private String fileName;

    private byte[] hash;

    public ChunkHash() {}

    public ChunkHash(int mediaId, String fileName, byte[] hash) {
        this.mediaId = mediaId;
        this.fileName = fileName;
        this.hash = hash;
    }

    public ChunkHash(int id, int mediaId, String fileName, byte[] hash) {
        this.id = id;
        this.mediaId = mediaId;
        this.fileName = fileName;
        this.hash = hash;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

//    public MediaMetadata getMediaMetadata() {
//        return mediaMetadata;
//    }
//
//    public void setMediaMetadata(MediaMetadata mediaMetadata) {
//        this.mediaMetadata = mediaMetadata;
//    }

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public byte[] getHash() {
        return hash;
    }

    public void setHash(byte[] hash) {
        this.hash = hash;
    }

}
