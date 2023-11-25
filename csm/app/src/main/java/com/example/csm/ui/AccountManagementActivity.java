package com.example.csm.ui;

import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.os.AsyncTask;
import android.os.Bundle;
import android.widget.ListView;

import com.example.csm.R;
import com.example.csm.dao.UserDAO;
import com.example.csm.model.User;

import java.util.List;

public class AccountManagementActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_account_management);

        new AsyncPopulateAdminList(this).execute();
        new AsyncPopulateSubscriberList(this).execute();
    }

    private class AsyncPopulateAdminList extends AsyncTask<Void, Void, List<User>> {

        Activity context;

        public AsyncPopulateAdminList(Activity context) {
            this.context = context;
        }
        @Override
        protected List<User> doInBackground(Void... params) {
            try {
                return UserDAO.getAdminsInfo();
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        }

        @Override
        protected void onPostExecute(List<User> result) {
            UserListAdapter adapter = new UserListAdapter(context, result);
            ListView adminsListView = findViewById(R.id.listAdmins);
            adminsListView.setAdapter(adapter);
        }
    }

    private class AsyncPopulateSubscriberList extends AsyncTask<Void, Void, List<User>> {

        Activity context;

        public AsyncPopulateSubscriberList(Activity context) {
            this.context = context;
        }
        @Override
        protected List<User> doInBackground(Void... params) {
            try {
                return UserDAO.getSubscribersInfo();
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        }

        @Override
        protected void onPostExecute(List<User> result) {
            UserListAdapter adapter = new UserListAdapter(context, result);
            ListView subsListView = findViewById(R.id.listSubscribers);
            subsListView.setAdapter(adapter);
        }
    }


}