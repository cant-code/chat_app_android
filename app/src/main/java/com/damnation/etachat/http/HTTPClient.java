package com.damnation.etachat.http;

import android.util.Log;
import androidx.annotation.NonNull;
import com.damnation.etachat.http.CallBacks.LoginCallback;
import com.damnation.etachat.http.CallBacks.RegisterCallback;
import com.damnation.etachat.model.Group;
import com.damnation.etachat.model.Messages;
import com.damnation.etachat.model.MessagesDeserializer;
import com.damnation.etachat.model.User;
import com.damnation.etachat.token.Token;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import okhttp3.*;
import org.jetbrains.annotations.Nullable;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

public class HTTPClient {

    public static final HTTPClient INSTANCE = new HTTPClient();
    public static final MediaType JSON = MediaType.get("application/json; charset=utf-8");
    public static final String BASE_URL = "https://etachat.herokuapp.com/api";
    public static final String USERS = "/users";
    public static final String GROUPS = "/group";
    public static final String MESSAGES = "/messages";

    private final OkHttpClient client;
    private final Gson gson;
    private final Executor executor;
    private final Token token;
    static Type type = new TypeToken<HashMap<String, String>>() {}.getType();

    private HTTPClient() {
        client = new OkHttpClient();
        gson = new Gson();
        executor = Executors.newFixedThreadPool(4);
        token = Token.INSTANCE;
    }

    public void sendMessage(RegisterCallback callback, @NonNull String msg, @NonNull String dest) {
        HashMap<String, String> data = new HashMap<>();
        data.put("data", msg);
        String post = gson.toJson(data);
        RequestBody body = RequestBody.create(JSON, post);
        String url = BASE_URL + MESSAGES;
        if (dest.equals("global")) url += "/global/";
        else data.put("to", dest);
        Request request = new Request.Builder()
                .post(body)
                .url(url)
                .addHeader("Authorization", token.getToken())
                .build();
        executor.execute(() -> {
            try {
                Response response = client.newCall(request).execute();
                int code = response.code();
                if (code == 404 || code == 500) {
                    Log.e("SendMessageHttp", "Error sending message");
                    callback.onError("Error sending message");
                    return;
                }
                callback.onSuccess();
            } catch (Exception e) {
                Log.e("SendMessageHttp", "Error sending message", e);
                callback.onError("Error sending message");
            }
        });
    }

    public List<Messages> getGlobalMessages() {
        Request request = new Request.Builder()
                .get()
                .url(BASE_URL + MESSAGES + "/global/")
                .addHeader("Authorization", token.getToken())
                .build();
        return getMessages(request);
    }

    @Nullable
    private List<Messages> getMessages(Request request) {
        try {
            Response response = client.newCall(request).execute();
            ResponseBody responseBody = response.body();
            if (responseBody != null) {
                String json = responseBody.string();
                GsonBuilder gsonBuilder = new GsonBuilder();
                gsonBuilder.registerTypeAdapter(Messages.class, new MessagesDeserializer());
                Gson customGson = gsonBuilder.create();
                Type type = new TypeToken<ArrayList<Messages>>() {}.getType();
                List<Messages> messagesList = customGson.fromJson(json, type);
                if (messagesList.size() > 0) {
                    return messagesList;
                }
            }
        } catch (Exception e) {
            Log.e("MessagesHttp", "Error loading messages", e);
        }
        return null;
    }

    public List<Messages> loadMessages(String id) {
        Request request = new Request.Builder()
                .get()
                .url(BASE_URL + MESSAGES + "/convos/query?userId=" + id)
                .addHeader("Authorization", token.getToken())
                .build();
        return getMessages(request);
    }

    public Group addOrJoinGroup(String name, String type) {
        HashMap<String, String> data = new HashMap<>();
        data.put("name", name);
        data.put("type", type);
        String post = gson.toJson(data);
        RequestBody body = RequestBody.create(JSON, post);
        Request request = new Request.Builder()
                .post(body)
                .url(BASE_URL + GROUPS + "/room")
                .addHeader("Authorization", token.getToken())
                .build();
        try {
            Response response = client.newCall(request).execute();
            ResponseBody responseBody = response.body();
            int code = response.code();
            assert responseBody != null;
            String json = responseBody.string();
            if (code == 404 || code == 400) {
                Log.e("GroupAddHttp", "Error joining or creating group");
                return null;
            }
            Group group = gson.fromJson(json, Group.class);
            if (group != null) {
                return group;
            }
        } catch (Exception e) {
            Log.e("GroupAddHttp", "Error joining or creating group", e);
        }
        return null;
    }

    public List<Group> loadGroup() {
        Request request = new Request.Builder()
                .get()
                .url(BASE_URL + GROUPS)
                .addHeader("Authorization", token.getToken())
                .build();
        try {
            Response response = client.newCall(request).execute();
            ResponseBody responseBody = response.body();
            if (responseBody != null) {
                String json = responseBody.string();
                Type type = new TypeToken<ArrayList<Group>>() {}.getType();
                List<Group> groupData = gson.fromJson(json, type);
                if (groupData != null) {
                    return groupData;
                }
            }
        } catch (Exception e) {
            Log.e("GroupHttp", "Error loading groups", e);
        }
        return null;
    }

    public List<User> loadUsers() {
        Request request = new Request.Builder()
                .get()
                .url(BASE_URL + USERS)
                .addHeader("Authorization", token.getToken())
                .build();
        try {
            Response response = client.newCall(request).execute();
            ResponseBody responseBody = response.body();
            if (responseBody != null) {
                String json = responseBody.string();
                Type type = new TypeToken<ArrayList<User>>() {}.getType();
                List<User> userData = gson.fromJson(json, type);
                if (userData != null) {
                    return userData;
                }
            }
        } catch (Exception e) {
            Log.e("UserHttp", "Error Loading Users", e);
        }
        return null;
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
                assert responseBody != null;
                String json = responseBody.string();
                HashMap<String, String> resp = gson.fromJson(json, type);
                if (code == 404 || code == 400) {
                    callback.onError(resp.get("Error"));
                    return;
                }
                callback.onSuccess(resp);
            } catch (Exception e) {
                Log.e("t", "t", e);
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
                assert responseBody != null;
                String json = responseBody.string();
                HashMap<String, String> resp = gson.fromJson(json, type);
                if (code == 404 || code == 400) {
                    callback.onError(resp.get("error"));
                    return;
                }
                callback.onSuccess();
            } catch (Exception e) {
                callback.onError("An Error Occurred");
            }
        });
    }
}
