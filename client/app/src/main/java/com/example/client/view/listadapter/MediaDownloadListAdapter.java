package com.example.client.view.listadapter;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.example.client.R;
import com.example.client.model.MediaMetadata;
import com.example.client.view.VideoPlayerActivity;

import java.util.List;

public class MediaDownloadListAdapter extends ArrayAdapter<MediaMetadata> {

    private final Context mContext;
    private final List<MediaMetadata> mData;

    private Button downloadInteractionButton;

    public MediaDownloadListAdapter(@NonNull Context context, List<MediaMetadata> data) {
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

        downloadInteractionButton = convertView.findViewById(R.id.buttonDownloadInteraction);
        TextView titleTextView = convertView.findViewById(R.id.textFieldTitle);
        TextView descriptionTextView = convertView.findViewById(R.id.textFieldDescription);

        titleTextView.setText(mData.get(position).getTitle());
        descriptionTextView.setText(mData.get(position).getDescription());
        String buttonText = "";
        switch(mData.get(position).getDownloadStatus()) {
            case DOWNLOADED:
            case DOWNLOADING:
                buttonText = "De";
                break;
            case NOT_DOWNLOADED:
                buttonText = "Dw";
                break;
        }
        downloadInteractionButton.setText(buttonText);

        setupInteractionClickListener(position, convertView);

        return convertView;
    }

    private void setupInteractionClickListener(int position, View convertView) {
        downloadInteractionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

            }
        });
    }

}

