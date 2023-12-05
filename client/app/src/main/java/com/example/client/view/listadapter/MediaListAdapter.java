package com.example.client.view.listadapter;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.example.client.R;
import com.example.client.model.MediaMetadata;
import com.example.client.model.User;
import com.example.client.view.VideoPlayerActivity;

import java.util.List;

public class MediaListAdapter extends ArrayAdapter<MediaMetadata> {

    private final Context mContext;
    private final List<MediaMetadata> mData;

    private LinearLayout cardLayout;
    private TextView titleTextView;
    private TextView descriptionTextView;

    public MediaListAdapter(@NonNull Context context, List<MediaMetadata> data) {
        super(context, R.layout.activity_media_list_item, data);
        this.mContext = context;
        this.mData = data;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        if (convertView == null) {
            LayoutInflater inflater = LayoutInflater.from(mContext);
            convertView = inflater.inflate(R.layout.activity_media_list_item, parent, false);
        }

        cardLayout = convertView.findViewById(R.id.layoutMediaCard);
        titleTextView = convertView.findViewById(R.id.textFieldTitle);
        descriptionTextView = convertView.findViewById(R.id.textFieldDescription);

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
                newIntent.putExtra("mediaId", mData.get(position).getId());
                mContext.startActivity(newIntent);
            }
        });
    }

}
