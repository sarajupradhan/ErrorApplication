package com.example.errorapplication.task;

import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.*;
import androidx.appcompat.app.AppCompatActivity;

import com.example.errorapplication.R;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.*;
import retrofit2.converter.gson.GsonConverterFactory;

public class TaskTrackerActivity extends AppCompatActivity {

    private EditText etStoreName, etAssignedTo;
    private Button btnFetch;
    private TextView tvResult;

    private TaskTrackerApi api;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_task_tracker);

        etStoreName = findViewById(R.id.etStoreName);
        etAssignedTo = findViewById(R.id.etAssignedTo);
        btnFetch = findViewById(R.id.btnFetch);
        tvResult = findViewById(R.id.tvResult);

        HttpLoggingInterceptor logging = new HttpLoggingInterceptor();
        logging.setLevel(HttpLoggingInterceptor.Level.BODY);

        OkHttpClient client = new OkHttpClient.Builder()
                .addInterceptor(logging)
                .build();

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("https://wfc-las-sit.pttpro.zebra.com:6443/")
                .client(client)
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        api = retrofit.create(TaskTrackerApi.class);

        btnFetch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String storename = etStoreName.getText().toString().trim();
                // assignedTo is present in UI but not used in API as per requirements
                if (TextUtils.isEmpty(storename)) {
                    etStoreName.setError("Enter storename");
                    return;
                }
                fetchTasks(storename);
            }
        });
    }

        private void fetchTasks(String storename) {
        tvResult.setText("Loading...");
        Call<JsonArray> call = api.getTasks(
                "TaskTracker_Automation",
                "TaskTracker_Automation",
                storename
        );
        call.enqueue(new Callback<JsonArray>() {
            @Override
            public void onResponse(Call<JsonArray> call, Response<JsonArray> response) {
                if (response.isSuccessful() && response.body() != null) {
                    tvResult.setText(response.body().toString());
                } else {
                    String errorMsg = "Error: " + response.code() + " " + response.message();
                    try {
                        errorMsg += "\n" + response.errorBody().string();
                    } catch (Exception ignored) {}
                    tvResult.setText(errorMsg);
                }
            }

            @Override
            public void onFailure(Call<JsonArray> call, Throwable t) {
                Log.e("TaskTrackerActivity","onFailure",t);
                tvResult.setText("Failed: " + t.getMessage());
            }
        });
    }
}
