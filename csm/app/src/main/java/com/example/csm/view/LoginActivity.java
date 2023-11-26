package com.example.csm.view;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.JsonObjectRequest;
import com.example.csm.R;
import com.example.csm.util.NetworkUtil;

import org.json.JSONException;
import org.json.JSONObject;


public class LoginActivity extends AppCompatActivity {

    TextView messageBoard;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        messageBoard = (TextView) findViewById(R.id.textMessage);
    }

    public void login() {
        Intent newIntent = new Intent(this, MainMenuActivity.class);
        this.startActivity(newIntent);
    }

    // TODO: use DAO
    public void onClickButtonLogin(View view) {
        messageBoard.setText("");

        RequestQueue queue = null;
        try {
            queue = NetworkUtil.getInstance().getRequestQueue();
        } catch (Exception e) {
            messageBoard.setText("Error on the http request module");
            return;
        }

        // TODO: put these addresses somewhere else
        String url = "http://192.168.1.68:8080/api/auth/adminLogin";

        JSONObject jsonBody = new JSONObject();
        String username = ((EditText)findViewById(R.id.editTextUsername)).getText().toString();
        String password = ((EditText)findViewById(R.id.editTextPassword)).getText().toString();
        try {
            jsonBody.put("username", username);
            jsonBody.put("password", password);
        } catch (JSONException e) {
            messageBoard.setText("Error on the json body building");
            return;
        }

        // Request a string response from the provided URL.
        JsonObjectRequest request = new JsonObjectRequest(Request.Method.POST, url, jsonBody,
                response -> {
                    Intent newIntent = new Intent(this, MainMenuActivity.class);
                    this.startActivity(newIntent);
                },
                error -> {
                    String errorMessage = NetworkUtil.defaultErrorHandling(error);
                    messageBoard.setText(errorMessage);
                }
        );

        // Add the request to the RequestQueue.
        queue.add(request);
    }
}