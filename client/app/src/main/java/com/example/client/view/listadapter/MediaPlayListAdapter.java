package com.example.client.view.listadapter;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.example.client.R;
import com.example.client.model.MediaMetadata;
import com.example.client.view.VideoPlayerActivity;

import java.util.List;

public class MediaPlayListAdapter extends ArrayAdapter<MediaMetadata> {

    private final Context mContext;
    private final List<MediaMetadata> mData;

    private RelativeLayout cardLayout;

    public MediaPlayListAdapter(@NonNull Context context, List<MediaMetadata> data) {
        super(context, R.layout.activity_media_play_list_item, data);
        this.mContext = context;
        this.mData = data;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        if (convertView == null) {
            LayoutInflater inflater = LayoutInflater.from(mContext);
            convertView = inflater.inflate(R.layout.activity_media_play_list_item, parent, false);
        }

        cardLayout = convertView.findViewById(R.id.layoutMediaCard);
        TextView titleTextView = convertView.findViewById(R.id.textFieldTitle);
        TextView descriptionTextView = convertView.findViewById(R.id.textFieldDescription);
        TextView downloadedTextView = convertView.findViewById(R.id.textFieldDownloaded);

        if (mData.get(position).getDownloadStatus() == MediaMetadata.DownloadStatus.DOWNLOADED) {
            downloadedTextView.setVisibility(View.VISIBLE);
        } else {
            downloadedTextView.setVisibility(View.GONE);
        }

        titleTextView.setText(mData.get(position).getTitle());
        descriptionTextView.setText(mData.get(position).getDescription());

        setupCardClickListener(position, convertView);

        return convertView;
    }

    private void setupCardClickListener(int position, View convertView) {
        cardLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent newIntent = new Intent(mContext, VideoPlayerActivity.class);
                MediaMetadata chosen = mData.get(position);
                newIntent.putExtra("mediaId", chosen.getId());
                mContext.startActivity(newIntent);
            }
        });
    }

}
