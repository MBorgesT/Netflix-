package com.example.client;

import androidx.appcompat.app.AppCompatActivity;

import android.net.Uri;
import android.os.Bundle;

import com.google.android.exoplayer2.ExoPlayer;
import com.google.android.exoplayer2.MediaItem;
import com.google.android.exoplayer2.source.hls.HlsMediaSource;
import com.google.android.exoplayer2.ui.PlayerView;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        ExoPlayer player = new ExoPlayer.Builder(this).build();
        PlayerView playerView = findViewById(R.id.playerView);
        playerView.setPlayer(player);

        Uri uri = Uri.parse("http://10.0.2.2:8080/resources/test/master.m3u8");
        //player.setMediaItem(MediaItem.fromUri("http://127.0.0.1:8080/resources/test/master.m3u8"));
        player.setMediaItem(MediaItem.fromUri(uri));

        player.prepare();
        player.play();
    }
}