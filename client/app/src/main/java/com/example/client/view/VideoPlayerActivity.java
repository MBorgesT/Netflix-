package com.example.client.view;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;
import androidx.media3.common.MediaItem;
import androidx.media3.common.MimeTypes;
import androidx.media3.exoplayer.ExoPlayer;
import androidx.media3.ui.PlayerView;

import android.annotation.SuppressLint;
import android.content.pm.ActivityInfo;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.widget.Toast;

import com.example.client.R;
import com.example.client.model.MediaMetadata;
import com.example.client.util.Resources;
import com.example.client.viewmodel.VideoPlayerViewModel;

public class VideoPlayerActivity extends AppCompatActivity {

    private VideoPlayerViewModel viewModel;

    private ExoPlayer player;
    private MediaMetadata mediaMetadata;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_video_player);

        ViewModelProvider viewModelProvider = new ViewModelProvider(this);
        viewModel = viewModelProvider.get(VideoPlayerViewModel.class);

        int mediaId = getIntent().getIntExtra("mediaId", -1);
        viewModel.fetchMediaById(mediaId);

        setupVideoPlayer();
        setupToast();
        setupMediaFetchObserver();

        this.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (player != null) {
            player.release();
            player = null;
        }
    }

    private void setupVideoPlayer() {
        player = new ExoPlayer.Builder(this)
                .build();

        PlayerView playerView = findViewById(R.id.playerView);
        playerView.setPlayer(player);
    }

    private void setupToast() {
        viewModel.getMessageLiveData().observe(this, message -> {
            if (message != null) {
                Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
                viewModel.getMessageLiveData().setValue(null);
            }
        });
    }

    private void setupMediaFetchObserver() {
        viewModel.getMediaMetadataLiveData().observe(this, media -> {
            mediaMetadata = media;
            if (mediaMetadata.getDownloadStatus() == MediaMetadata.DownloadStatus.DOWNLOADED) {
                playVideoLocal();
            } else {
                setupStreamingSourceObserver();
                viewModel.fetchStreamingSources(mediaMetadata.getId());
            }
        });
    }

    private void setupStreamingSourceObserver() {
        viewModel.getStreamingSourceLiveData().observe(this, sourceAddress -> {
            playVideoWeb(sourceAddress);
        });
    }

    private void playVideoWeb(String sourceAddress) {
        Uri uri = Uri.parse(
                sourceAddress
                        + mediaMetadata.getFolderName()
                        + "/master.m3u8");

        MediaItem mediaItem = new MediaItem.Builder()
                        .setUri(uri)
                        .setMimeType(MimeTypes.APPLICATION_M3U8)
                        .build();
        player.setMediaItem(mediaItem);

        player.prepare();
        player.setPlayWhenReady(true);
    }

    private void playVideoLocal() {
        Uri videoUri = Uri.parse(
                Resources.getMediaDownloadFolder() +
                        mediaMetadata.getFolderName() +
                        "/master.m3u8");
        MediaItem mediaItem = MediaItem.fromUri(videoUri);
        player.setMediaItem(mediaItem);
        player.prepare();
        player.setPlayWhenReady(true);
    }

}