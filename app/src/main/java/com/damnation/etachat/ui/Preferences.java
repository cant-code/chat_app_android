package com.damnation.etachat.ui;

import android.content.Context;
import android.content.SharedPreferences;

public class Preferences {
    public static final String KEY_LOGIN_STATE = "key_login_state";

    private SharedPreferences preferences;

    Preferences(Context context) {
        preferences = context.getSharedPreferences("eta-chat", Context.MODE_PRIVATE);
    }

    public boolean isLoggedIn() {
        return preferences.getBoolean(KEY_LOGIN_STATE, false);
    }

    public void setLoggedIn(boolean loggedIn) {
        preferences.edit().putBoolean(KEY_LOGIN_STATE, loggedIn).apply();
    }
}
