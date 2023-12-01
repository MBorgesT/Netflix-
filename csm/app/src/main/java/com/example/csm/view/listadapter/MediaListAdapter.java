package com.example.csm.view.listadapter;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.example.csm.R;
import com.example.csm.model.MediaMetadata;
import com.example.csm.view.UserFormActivity;

import java.util.List;

public class MediaListAdapter extends ArrayAdapter<MediaMetadata> {

    public interface OnMediaDeleteListener {
        void onDeleteMedia(int mediaId);
    }

    private OnMediaDeleteListener deleteListener;

    private Context mContext;
    private List<MediaMetadata> mData;

    public MediaListAdapter(@NonNull Context context, List<MediaMetadata> data) {
        super(context, R.layout.activity_user_list_item, data);
        this.mContext = context;
        this.mData = data;
    }

    // ============================== PUBLIC ==============================

    public void setOnMediaDeleteListener(OnMediaDeleteListener deleteListener) {
        this.deleteListener = deleteListener;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        if (convertView == null) {
            LayoutInflater inflater = LayoutInflater.from(mContext);
            convertView = inflater.inflate(R.layout.activity_media_list_item, parent, false);
        }

        TextView titleTextView = convertView.findViewById(R.id.textFieldTitle);
        titleTextView.setText(mData.get(position).getTitle());

        TextView statusTextView = convertView.findViewById(R.id.textFieldStatus);
        statusTextView.setText(mData.get(position).getUploadStatus().toString());
        switch (mData.get(position).getUploadStatus()) {
            case FINISHED:
                statusTextView.setTextColor(Color.GREEN);
                break;
            case PROCESSING:
                statusTextView.setTextColor(Color.YELLOW);
                break;
            case ERROR:
                statusTextView.setTextColor(Color.RED);
                break;
        }

        setupDeleteClickListener(position, convertView);
        setupUpdateClickListener(position, convertView);

        return convertView;
    }

    // ============================== PRIVATE ==============================

    private void setupDeleteClickListener(int position, View convertView) {
        Button deleteButton = convertView.findViewById(R.id.buttonDelete);
        deleteButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                deleteListener.onDeleteMedia(mData.get(position).getId());
            }
        });
    }

    private void setupUpdateClickListener(int position, View convertView) {
        Button updateButton = convertView.findViewById(R.id.buttonEdit);
        updateButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent newIntent = new Intent(mContext, UserFormActivity.class);
                newIntent.putExtra("functionality", UserFormActivity.Functionality.UPDATE);
                newIntent.putExtra("user", mData.get(position));
                mContext.startActivity(newIntent);
            }
        });
    }
}
