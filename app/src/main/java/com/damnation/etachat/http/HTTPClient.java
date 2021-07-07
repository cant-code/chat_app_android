package com.damnation.etachat.http;

import android.util.Log;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import okhttp3.*;

import java.io.IOException;
import java.lang.reflect.Type;
import java.util.HashMap;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

public class HTTPClient {

    public static final HTTPClient INSTANCE = new HTTPClient();
    public static final MediaType JSON = MediaType.get("application/json; charset=utf-8");
    public static final String BASE_URL = "https://etachat.herokuapp.com/api";
    public static final String USERS = "/users";

    private OkHttpClient client;
    private Gson gson;
    private Executor executor;

    private HTTPClient() {
        client = new OkHttpClient();
        gson = new Gson();
        executor = Executors.newFixedThreadPool(4);
    }

    public void login(LoginCallback callback, String username, String password) {
        HashMap<String, String> data = new HashMap<>();
        data.put("username", username);
        data.put("password", password);
        String post = gson.toJson(data);
        RequestBody body = RequestBody.create(JSON, post);
        Request request = new Request.Builder()
                .post(body)
                .url(BASE_URL + USERS + "/login")
                .build();
        executor.execute(() -> {
            try {
                Response response = client.newCall(request).execute();
                ResponseBody responseBody = response.body();
                int code = response.code();
                Type type = new TypeToken<HashMap<String, String>>(){}.getType();
                assert responseBody != null;
                String json = responseBody.string();
                HashMap<String, String> resp = gson.fromJson(json, type);
                if(code == 404 || code == 400) {
                    callback.onError(resp.get("Error"));
                    return;
                }
                callback.onSuccess(resp);
            } catch (IOException e) {
                callback.onError("An Error Occurred");
            }
        });
    }

    public void register(RegisterCallback callback, String username, String email, String password) {
        HashMap<String, String> data = new HashMap<>();
        data.put("email", email);
        data.put("username", username);
        data.put("password", password);
        data.put("password2", password);
        String post = gson.toJson(data);
        RequestBody body = RequestBody.create(JSON, post);
        Request request = new Request.Builder()
                .post(body)
                .url(BASE_URL + USERS + "/register")
                .build();
        executor.execute(() -> {
            try {
                Response response = client.newCall(request).execute();
                ResponseBody responseBody = response.body();
                int code = response.code();
                Type type = new TypeToken<HashMap<String, String>>(){}.getType();
                assert responseBody != null;
                String json = responseBody.string();
                HashMap<String, String> resp = gson.fromJson(json, type);
                System.out.println(resp);
                if(code == 404 || code == 400) {
                    callback.onError(resp.get("error"));
                    return;
                }
                callback.onSuccess();
            } catch (IOException e) {
                callback.onError("An Error Occurred");
            }
        });
    }
}
