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
    public static final String LOGIN = "/login";

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
                .url(BASE_URL + USERS + LOGIN)
                .build();
        executor.execute(() -> {
            try {
                Response response = client.newCall(request).execute();
                ResponseBody responseBody = response.body();
                int code = response.code();
                Type type = new TypeToken<HashMap<String, String>>(){}.getType();
                assert responseBody != null;
                String json = responseBody.string();
                System.out.println(json);
                System.out.println(code);
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
}
