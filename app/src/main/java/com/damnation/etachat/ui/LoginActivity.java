package com.damnation.etachat.ui;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import com.damnation.etachat.R;
import com.damnation.etachat.http.HTTPClient;
import com.damnation.etachat.http.CallBacks.LoginCallback;
import com.damnation.etachat.token.Token;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.textfield.TextInputLayout;

import java.util.HashMap;

import static com.damnation.etachat.ui.RegisterActivity.getTextWatcher;

public class LoginActivity extends AppCompatActivity {

    private TextInputLayout textUsernameLayout;
    private TextInputLayout textPasswordLayout;
    private Button loginButton;
    private Button registerButton;
    private ProgressBar progressBar;
    private Preferences preferences;
    private HTTPClient httpClient;
    private Token token;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Intent intent = getIntent();
        String message = intent.getStringExtra("snackbar_message");
        if(message != null) {
            showSnackbar(message);
        }

        httpClient = HTTPClient.INSTANCE;
        preferences = new Preferences(this);
        token = Token.INSTANCE;
        if(preferences.isLoggedIn()) {
            String tokenVal = preferences.getToken();
            String id = preferences.getId();
            token.setToken(tokenVal, id);
            startMainActivity();
        }

        setContentView(R.layout.activity_login);

        textUsernameLayout = findViewById(R.id.username);
        textPasswordLayout = findViewById(R.id.password);
        loginButton = findViewById(R.id.login);
        registerButton = findViewById(R.id.signUpButton);
        progressBar = findViewById(R.id.loading);
        loginButton.setOnClickListener(v -> onLoginClick());
        registerButton.setOnClickListener(v -> startRegisterActivity());

        textUsernameLayout.getEditText().addTextChangedListener(createTextWatcher(textUsernameLayout));
        textPasswordLayout.getEditText().addTextChangedListener(createTextWatcher(textPasswordLayout));
    }

    private TextWatcher createTextWatcher(TextInputLayout textInputLayout) {
        return getTextWatcher(textInputLayout);
    }

    private void initialLoginState() {
        textUsernameLayout.setEnabled(true);
        textPasswordLayout.setEnabled(true);
        registerButton.setEnabled(true);
        loginButton.setVisibility(View.VISIBLE);
        progressBar.setVisibility(View.INVISIBLE);
    }

    private void onLoginClick() {
        String username = textUsernameLayout.getEditText().getText().toString();
        String password = textPasswordLayout.getEditText().getText().toString();
        if(username.isEmpty()) {
            textUsernameLayout.setError("Username must not be empty");
        } else if(password.isEmpty()) {
            textPasswordLayout.setError("Password must not be empty");
        } else {
            performLogin(username, password);
        }
    }

    private void showErrorDialog(String message) {
        new AlertDialog.Builder(this)
                .setTitle("Login Failed")
                .setMessage(message)
                .setPositiveButton("OK", (dialog, which) -> dialog.dismiss())
                .show();
        initialLoginState();
    }

    private void showSnackbar(String message) {
        View rootView = findViewById(android.R.id.content);
        Snackbar snackbar = Snackbar.make(rootView, message, Snackbar.LENGTH_INDEFINITE);
        snackbar.setActionTextColor(getResources().getColor(R.color.cyan_500));
        snackbar.setAction("Close", v -> snackbar.dismiss());
        snackbar.show();
    }

    private void showLoginSnackbar() {
        View rootView = findViewById(android.R.id.content);
        Snackbar snackbar = Snackbar.make(rootView, "Login Successful", Snackbar.LENGTH_INDEFINITE);
        snackbar.setActionTextColor(getResources().getColor(R.color.cyan_500));
        snackbar.setAction("Close", v -> {
            snackbar.dismiss();
            initialLoginState();
        });
        snackbar.show();
    }

    private void performLogin(String username, String password) {
        textUsernameLayout.setEnabled(false);
        textPasswordLayout.setEnabled(false);
        registerButton.setEnabled(false);
        loginButton.setVisibility(View.INVISIBLE);
        progressBar.setVisibility(View.VISIBLE);
        httpClient.login(new LoginCallback() {
            @Override
            public void onSuccess(HashMap<String, String> map) {
                runOnUiThread(() -> {
                    String id = map.get("id");
                    String tokenVal = map.get("token");
                    token.setToken(tokenVal, id);
                    preferences.setLoggedIn(true, tokenVal, id);
                    showLoginSnackbar();
                    startMainActivity();
                });
            }

            @Override
            public void onError(String message) {
                runOnUiThread(() -> showErrorDialog(message));
            }
        }, username, password);
    }

    private void startRegisterActivity() {
        Intent intent = new Intent(this, RegisterActivity.class);
        startActivity(intent);
    }

    private void startMainActivity() {
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
        finish();
    }
}
