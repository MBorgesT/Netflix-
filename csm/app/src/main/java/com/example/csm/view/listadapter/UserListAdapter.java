package com.example.csm.view.listadapter;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.example.csm.R;
import com.example.csm.model.User;
import com.example.csm.view.UserFormActivity;

import java.util.List;

public class UserListAdapter extends ArrayAdapter<User> {

    public interface OnUserDeleteListener {
        void onDeleteUser(int userId);
    }

    private OnUserDeleteListener deleteListener;

    private Context mContext;
    private List<User> mData;

    public UserListAdapter(Context context, List<User> data) {
        super(context, R.layout.activity_user_list_item, data);
        this.mContext = context;
        this.mData = data;
    }

    // ============================== PUBLIC ==============================

    public void setOnUserDeleteListener(OnUserDeleteListener listener) {
        this.deleteListener = listener;
    }
    
    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        if (convertView == null) {
            LayoutInflater inflater = LayoutInflater.from(mContext);
            convertView = inflater.inflate(R.layout.activity_user_list_item, parent, false);
        }

        TextView textView = convertView.findViewById(R.id.textFieldUsername);
        textView.setText(mData.get(position).getUsername());

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
                deleteListener.onDeleteUser(mData.get(position).getId());
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