package com.example.csm.view;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;

import android.os.Bundle;
import android.widget.ListView;

import com.example.csm.R;
import com.example.csm.viewmodel.AccountManagementViewModel;

public class AccountManagementActivity extends AppCompatActivity {

    private AccountManagementViewModel viewModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_account_management);

        ViewModelProvider viewModelProvider = new ViewModelProvider(this);
        viewModel = viewModelProvider.get(AccountManagementViewModel.class);

        populateLists();
    }

    private void populateLists() {
        viewModel.fetchData();

        viewModel.getAdmins().observe(this, adminsList -> {
            UserListAdapter adapter = new UserListAdapter(this, adminsList);
            ListView adminsListView = findViewById(R.id.listAdmins);
            adminsListView.setAdapter(adapter);
        });
        viewModel.getSubscribers().observe(this, subsList -> {
            UserListAdapter adapter = new UserListAdapter(this, subsList);
            ListView subsListView = findViewById(R.id.listSubscribers);
            subsListView.setAdapter(adapter);
        });
    }

//    private class AsyncPopulateLists extends AsyncTask<Void, Void, List[]> {
//
//        Activity context;
//
//        public AsyncPopulateLists(Activity context) {
//            this.context = context;
//        }
//
//        @Override
//        protected List[] doInBackground(Void... params) {
//            try {
//                List<User> admins = UserHandler.getAdminsInfo();
//                List<User> subs = UserHandler.getSubscribersInfo();
//
//                return new List[]{admins, subs};
//            } catch (Exception e) {
//                throw new RuntimeException(e);
//            }
//        }
//
//        @Override
//        protected void onPostExecute(List[] userLists) {
//            UserListAdapter adapter = new UserListAdapter(context, userLists[0]);
//            ListView adminsListView = findViewById(R.id.listAdmins);
//            adminsListView.setAdapter(adapter);
//
//            adapter = new UserListAdapter(context, userLists[1]);
//            ListView subsListView = findViewById(R.id.listSubscribers);
//            subsListView.setAdapter(adapter);
//        }
//    }

}