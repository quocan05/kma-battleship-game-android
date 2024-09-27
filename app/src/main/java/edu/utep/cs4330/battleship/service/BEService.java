package edu.utep.cs4330.battleship.service;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.concurrent.TimeUnit;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class BEService {
    private static BEService instance;
    private OkHttpClient client;
    private BEService() {
        client = new OkHttpClient.Builder()
                .connectTimeout(30, TimeUnit.SECONDS)
                .readTimeout(30, TimeUnit.SECONDS)
                .writeTimeout(30, TimeUnit.SECONDS)
                .build();

    }

    public static BEService getInstance(){
        if(instance==null){
            instance =  new BEService();
        }
        return instance;
    }

    public void makeApiCall(String httpMethod,String apiUrl) {

// Create JSON for the request body
        JSONObject loginJson = new JSONObject();
        try {
            loginJson.put("username", "hoang");
            loginJson.put("password", "123456");
        } catch (JSONException e) {
            e.printStackTrace();
        }

        RequestBody body = RequestBody.create( MediaType.get("application/json; charset=utf-8"),loginJson.toString());

// Create the request
        Request request = new Request.Builder()
                .url("http://13.212.249.56:8080/api/user/login")
                .post(body)
                .build();

// Asynchronous call to avoid blocking the main thread
        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                // Handle failure, e.g., network issues
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                if (response.isSuccessful()) {
                    String responseData = response.body().string();
                    // Handle successful login (e.g., extract token)
                } else {
                    // Handle invalid credentials or errors
                }
            }
        });
    }

    public OkHttpClient getClient() {
        return client;
    }
}
