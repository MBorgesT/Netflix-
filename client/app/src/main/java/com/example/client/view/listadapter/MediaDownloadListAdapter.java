package com.example.client.view.listadapter;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.example.client.R;
import com.example.client.model.MediaMetadata;

import java.util.List;

public class MediaDownloadListAdapter extends ArrayAdapter<MediaMetadata> {

    public interface OnMediaDownloadCallListener {
        void onMediaDownloadCall(int mediaId, String folderName);
    }

    public interface OnMediaDeleteListener {
        void onMediaDelete(int mediaId, String folderName);
    }

    private final Context mContext;
    private final List<MediaMetadata> mData;

    private Button downloadInteractionButton;
    private OnMediaDownloadCallListener onMediaDownloadListener;
    private OnMediaDeleteListener onMediaDeleteListener;

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
            convertView = inflater.inflate(R.layout.activity_media_download_list_item, parent, false);
        }

        setupFields(position, convertView);
        setupInteractionClickListener(position);

        return convertView;
    }

    public void setOnMediaDownloadListener(OnMediaDownloadCallListener onMediaDownloadListener) {
        this.onMediaDownloadListener = onMediaDownloadListener;
    }

    public void setOnMediaDeleteListener(OnMediaDeleteListener onMediaDeleteListener) {
        this.onMediaDeleteListener = onMediaDeleteListener;
    }

    private void setupFields(int position, View convertView) {
        downloadInteractionButton = convertView.findViewById(R.id.buttonDownloadInteraction);
        TextView titleTextView = convertView.findViewById(R.id.textFieldTitle);
        TextView descriptionTextView = convertView.findViewById(R.id.textFieldDescription);
        TextView downloadStatusTextView = convertView.findViewById(R.id.textFieldDownloadStatus);

        titleTextView.setText(mData.get(position).getTitle());
        descriptionTextView.setText(mData.get(position).getDescription());

        String buttonText = "";
        switch(mData.get(position).getDownloadStatus()) {
            case DOWNLOADED:
                downloadStatusTextView.setText("DOWNLOADED");
                downloadStatusTextView.setTextColor(Color.GREEN);
                downloadStatusTextView.setVisibility(View.VISIBLE);
                buttonText = "De";
                break;
            case DOWNLOADING:
                // TODO: solve this making more than this button invisible
                downloadStatusTextView.setText("DOWNLOADING");
                downloadStatusTextView.setTextColor(Color.YELLOW);
                downloadInteractionButton.setVisibility(View.INVISIBLE);
                break;
            case NOT_DOWNLOADED:
                downloadStatusTextView.setText("NOT DOWNLOADED");
                downloadStatusTextView.setTextColor(Color.RED);
                downloadStatusTextView.setVisibility(View.VISIBLE);
                buttonText = "Dw";
                break;
        }
        downloadInteractionButton.setText(buttonText);
    }

    private void setupInteractionClickListener(int position) {
        MediaMetadata media = mData.get(position);
        downloadInteractionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                switch (media.getDownloadStatus()) {
                    case NOT_DOWNLOADED:
                        onMediaDownloadListener.onMediaDownloadCall(media.getId(), media.getFolderName());
                        break;
                    case DOWNLOADED:
                        onMediaDeleteListener.onMediaDelete(media.getId(), media.getFolderName());
                        break;
                }
            }
        });
    }

}

