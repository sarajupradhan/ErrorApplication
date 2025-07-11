package com.example.errorapplication;

import android.app.Application;
import android.util.Log;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.UUID;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.FormBody;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;


public class ErrorApplication extends Application
{

    @Override
    public void onCreate() {
        super.onCreate();

        Thread.setDefaultUncaughtExceptionHandler(new Thread.UncaughtExceptionHandler() {
            @Override
            public void uncaughtException(Thread thread, Throwable throwable) {
                // Capture the stack trace
                String stackTrace = Log.getStackTraceString(throwable);

                // Post the stack trace to your API
                postStackTraceToApi(stackTrace);

                // Optionally: kill the app or restart it
            }
        });
    }

    public static void postStackTraceToApi(String stackTrace) {
        // Implement your network request here
        // Use a library like Retrofit, OkHttp, or HttpURLConnection for the network request

        // Example using OkHttp:
        OkHttpClient client = new OkHttpClient();
// {
//    "id": "f2e1b2c7-6c7e-4c7b-9e1a-1a2b3c4d5e6f",
//    "name": "NullPointerException in simulateNullPointerException",
//    "log": "java.lang.NullPointerException: Attempt to invoke virtual method 'int java.lang.String.length()' on a null object reference\n\tat com.example.errorapplication.MainActivity.simulateNullPointerException(MainActivity.java:92)\n\tat com.example.errorapplication.MainActivity.lambda$initializeButtons$1$com-example-errorapplication-MainActivity(MainActivity.java:52)",
//    "file": "MainActivity.java",
//    "line": 92,
//    "cause": "A String object is null and its length() method is being called without a null check.",
//    "fix": "Ensure the String variable is initialized before calling length(). Add a null check before accessing the method. Example:\n\nString str = ...;\nif (str != null) {\n    int len = str.length();\n} else {\n    // handle null case\n}",
//    "exception": "NullPointerException"
//  },
        MediaType JSON = MediaType.get("application/json; charset=utf-8");
        JSONObject json = new JSONObject();
        try {
            json.put("package", "com.example.errorapplication");
            json.put("id", UUID.randomUUID().toString());
            json.put("stack_trace", stackTrace);
        } catch (JSONException e) {
            throw new RuntimeException(e);
        }

        RequestBody body = RequestBody.create(json.toString(), JSON);
        Request request = new Request.Builder()
                .url("http://192.168.29.104:5000/upload_logs")
                .post(body)
                .build();

        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                Log.d("Exception",e.getMessage());
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                Log.d("onResponse","response");
            }
        });
    }
}
