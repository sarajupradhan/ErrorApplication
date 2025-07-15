package com.zebra.pttproservice.task;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.Query;

public interface TaskTrackerApi {
    @GET("ems/v1/api/task-tracker/tasks")
    Call<JsonObject> getTasks(
            @Header("API-Key") String apiKey,
            @Header("customerId") String customerId,
            @Query("storename") String storename
    );
}