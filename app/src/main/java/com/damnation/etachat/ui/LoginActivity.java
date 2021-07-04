package com.damnation.etachat.ui;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import com.damnation.etachat.R;
import com.google.android.material.textfield.TextInputLayout;

public class LoginActivity extends AppCompatActivity {

    private TextInputLayout textUsernameLayout;
    private TextInputLayout textPasswordLayout;
    private Button loginButton;
    private ProgressBar progressBar;
    private Preferences preferences;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        preferences = new Preferences(this);
        if(preferences.isLoggedIn()) {
            showLoginDialog();
        }

        setContentView(R.layout.activity_login);

        textUsernameLayout = findViewById(R.id.username);
        textPasswordLayout = findViewById(R.id.password);
        loginButton = findViewById(R.id.login);
        progressBar = findViewById(R.id.loading);
        loginButton.setOnClickListener(v -> onLoginClick());

        textUsernameLayout.getEditText().addTextChangedListener(createTextWatcher(textUsernameLayout));
        textPasswordLayout.getEditText().addTextChangedListener(createTextWatcher(textPasswordLayout));
    }

    private TextWatcher createTextWatcher(TextInputLayout textInputLayout) {
        return new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                textInputLayout.setError(null);
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        };
    }

    private void onLoginClick() {
        String username = textUsernameLayout.getEditText().getText().toString();
        String password = textPasswordLayout.getEditText().getText().toString();
        if(username.isEmpty()) {
            textUsernameLayout.setError("Username must not be empty");
        } else if(password.isEmpty()) {
            textPasswordLayout.setError("Password must not be empty");
        } else if(!username.equals("admin") && !password.equals("admin")) {
            showErrorDialog();
        } else {
            performLogin();
        }
    }

    private void showErrorDialog() {
        new AlertDialog.Builder(this)
                .setTitle("Login Failed")
                .setMessage("Username or Password is invalid. Please try again")
                .setPositiveButton("OK", (dialog, which) -> dialog.dismiss())
                .show();
    }

    private void showLoginDialog() {
        new AlertDialog.Builder(this)
                .setTitle("Login Successful")
                .setMessage("Logged In successfully")
                .setPositiveButton("OK", (dialog, which) -> dialog.dismiss())
                .show();
    }

    private void performLogin() {
        preferences.setLoggedIn(true);
        textUsernameLayout.setEnabled(false);
        textPasswordLayout.setEnabled(false);
        loginButton.setVisibility(View.INVISIBLE);
        progressBar.setVisibility(View.VISIBLE);

        Handler handler = new Handler();
        handler.postDelayed(() -> {
            showLoginDialog();
//            finish();
        }, 2000);
    }
}
