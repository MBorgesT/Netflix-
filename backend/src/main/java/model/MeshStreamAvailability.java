package model;

import jakarta.persistence.*;

@Entity
@Table(name="mesh_stream_availability")
public class MeshStreamAvailability {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @Column(name="media_id")
    private int mediaId;

    @Column(name="client_ip")
    private String clientIP;

    public MeshStreamAvailability() {
    }

    public MeshStreamAvailability(int mediaId, String clientIP) {
        this.mediaId = mediaId;
        this.clientIP = clientIP;
    }

    public MeshStreamAvailability(int id, int mediaId, String clientIP) {
        this.id = id;
        this.mediaId = mediaId;
        this.clientIP = clientIP;
    }

    public int getMediaId() {
        return mediaId;
    }

    public void setMediaId(int mediaId) {
        this.mediaId = mediaId;
    }

    public String getClientIP() {
        return clientIP;
    }

    public void setClientIP(String clientIP) {
        this.clientIP = clientIP;
    }

}
