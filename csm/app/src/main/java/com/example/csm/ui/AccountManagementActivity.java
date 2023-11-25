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

        new AsyncPopulateLists(this).execute();
        //new AsyncPopulateSubscriberList(this).execute();
    }

    private class AsyncPopulateLists extends AsyncTask<Void, Void, List[]> {

        Activity context;

        public AsyncPopulateLists(Activity context) {
            this.context = context;
        }

        @Override
        protected List[] doInBackground(Void... params) {
            try {
                List<User> admins = UserDAO.getAdminsInfo();
                List<User> subs = UserDAO.getSubscribersInfo();

                return new List[]{admins, subs};
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        }

        @Override
        protected void onPostExecute(List[] userLists) {
            UserListAdapter adapter = new UserListAdapter(context, userLists[0]);
            ListView adminsListView = findViewById(R.id.listAdmins);
            adminsListView.setAdapter(adapter);

            adapter = new UserListAdapter(context, userLists[1]);
            ListView subsListView = findViewById(R.id.listSubscribers);
            subsListView.setAdapter(adapter);
        }
    }

}