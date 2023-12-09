package com.example.client.view;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;
import androidx.media3.common.MediaItem;
import androidx.media3.common.MimeTypes;
import androidx.media3.common.util.UnstableApi;
import androidx.media3.common.util.Util;
import androidx.media3.datasource.DataSource;
import androidx.media3.datasource.DefaultDataSourceFactory;
import androidx.media3.exoplayer.ExoPlayer;
import androidx.media3.exoplayer.hls.HlsMediaSource;
import androidx.media3.exoplayer.source.MediaSource;
import androidx.media3.exoplayer.trackselection.AdaptiveTrackSelection;
import androidx.media3.exoplayer.trackselection.DefaultTrackSelector;
import androidx.media3.exoplayer.trackselection.MappingTrackSelector;
import androidx.media3.exoplayer.trackselection.TrackSelector;
import androidx.media3.ui.PlayerView;

import android.content.pm.ActivityInfo;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.widget.Toast;

import com.example.client.R;
import com.example.client.model.MediaMetadata;
import com.example.client.util.Resources;
import com.example.client.viewmodel.MainMenuViewModel;
import com.example.client.viewmodel.VideoPlayerViewModel;

public class VideoPlayerActivity extends AppCompatActivity {

    private VideoPlayerViewModel viewModel;

    private ExoPlayer player;
    private PlayerView playerView;
    private MediaMetadata mediaMetadata;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_video_player);

        ViewModelProvider viewModelProvider = new ViewModelProvider(this);
        viewModel = viewModelProvider.get(VideoPlayerViewModel.class);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            mediaMetadata = getIntent().getSerializableExtra("mediaMetadata", MediaMetadata.class);
        }

        setupVideoPlayer();
        setupToast();
        if (mediaMetadata.getDownloadStatus() == MediaMetadata.DownloadStatus.DOWNLOADED) {
            playVideoLocal();
        } else {
            playVideoWeb();
//            setupMediaFetchObserver();
        }

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
        //TrackSelector trackSelector = new DefaultTrackSelector(this);

        player = new ExoPlayer.Builder(this)
//                .setTrackSelector(trackSelector)
                .build();

        playerView = findViewById(R.id.playerView);
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
            playVideoWeb();
        });
    }

    private void playVideoWeb() {
        Uri uri = Uri.parse(
                Resources.backendResourcesUrl
                        + mediaMetadata.getFolderName()
                        + "/master.m3u8");

//        DefaultDataSourceFactory dataSourceFactory = new DefaultDataSourceFactory(this,
//                Util.getUserAgent(this, "YourApp"));
//        MediaSource mediaSource = new HlsMediaSource.Factory(dataSourceFactory)
//                .createMediaSource(MediaItem.fromUri(uri));
//        player.setMediaSource(mediaSource);

        MediaItem mediaItem = new MediaItem.Builder()
                        .setUri(uri)
                        .setMimeType(MimeTypes.APPLICATION_M3U8)
                        .build();
        player.setMediaItem(mediaItem);

        player.prepare();
//        player.play();
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