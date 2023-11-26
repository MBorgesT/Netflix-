package com.example.csm.view;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.example.csm.R;
import com.example.csm.model.User;

import java.util.List;

public class UserListAdapter extends ArrayAdapter<User> {

    private Context mContext;
    private List<User> mData;

    public UserListAdapter(Context context, List<User> data) {
        super(context, R.layout.activity_user_list_item, data);
        this.mContext = context;
        this.mData = data;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        if (convertView == null) {
            LayoutInflater inflater = LayoutInflater.from(mContext);
            convertView = inflater.inflate(R.layout.activity_user_list_item, parent, false);
        }

        convertView.setId(mData.get(position).getId());

        TextView textView = convertView.findViewById(R.id.textFieldUsername);
        textView.setText(mData.get(position).getUsername());

        return convertView;
    }
}