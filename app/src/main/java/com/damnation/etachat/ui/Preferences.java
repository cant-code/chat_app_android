package com.damnation.etachat.ui;

import android.content.Context;
import android.content.SharedPreferences;

public class Preferences {
    public static final String KEY_LOGIN_STATE = "key_login_state";
    public static final String TOKEN = "Token";
    public static final String ID = "id";

    private SharedPreferences preferences;

    Preferences(Context context) {
        preferences = context.getSharedPreferences("eta-chat", Context.MODE_PRIVATE);
    }

    public boolean isLoggedIn() {
        return preferences.getBoolean(KEY_LOGIN_STATE, false);
    }

    public String getToken() { return preferences.getString(TOKEN, ""); }

    public String getId() { return preferences.getString(ID, ""); }

    public void setLoggedIn(boolean loggedIn, String token, String id) {
        preferences.edit().putString(TOKEN, token).apply();
        preferences.edit().putString(ID, id).apply();
        preferences.edit().putBoolean(KEY_LOGIN_STATE, loggedIn).apply();
    }

    public void logout() {
        preferences.edit().clear().apply();
    }
}
