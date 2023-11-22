package com.example.client;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.net.Network;
import android.net.Uri;
import android.os.Bundle;

import com.example.client.R;
import com.example.client.network.NetworkUtil;
import com.example.client.ui.LoginActivity;
import com.google.android.exoplayer2.ExoPlayer;
import com.google.android.exoplayer2.MediaItem;
import com.google.android.exoplayer2.source.hls.HlsMediaSource;
import com.google.android.exoplayer2.ui.PlayerView;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        NetworkUtil.getInstance().initRequestQueue(this);

        Intent newIntent = new Intent(this, LoginActivity.class);
        this.startActivity(newIntent);
    }
}