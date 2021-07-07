package com.damnation.etachat.ui;

import android.content.Intent;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import com.damnation.etachat.R;
import com.damnation.etachat.http.HTTPClient;
import com.damnation.etachat.http.RegisterCallback;
import com.google.android.material.textfield.TextInputLayout;
import org.jetbrains.annotations.NotNull;

public class RegisterActivity extends AppCompatActivity {

    private TextInputLayout textEmailLayout;
    private TextInputLayout textUsernameLayout;
    private TextInputLayout textPasswordLayout;
    private TextInputLayout textConfirmPasswordLayout;
    private Button registerButton;
    private Button loginButton;
    private ProgressBar progressBar;
    private HTTPClient httpClient;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        httpClient = HTTPClient.INSTANCE;

        setContentView(R.layout.activity_register);

        textEmailLayout = findViewById(R.id.email);
        textUsernameLayout = findViewById(R.id.username);
        textPasswordLayout = findViewById(R.id.password);
        textConfirmPasswordLayout = findViewById(R.id.confirm_password);
        registerButton = findViewById(R.id.login);
        loginButton = findViewById(R.id.signInButton);
        progressBar = findViewById(R.id.loading);
        loginButton.setOnClickListener(v -> startLoginActivity());
        registerButton.setOnClickListener(v -> onRegisterClick());

        textEmailLayout.getEditText().addTextChangedListener(createTextWatcher(textEmailLayout));
        textUsernameLayout.getEditText().addTextChangedListener(createTextWatcher(textUsernameLayout));
        textPasswordLayout.getEditText().addTextChangedListener(createTextWatcher(textPasswordLayout));
        textConfirmPasswordLayout.getEditText().addTextChangedListener(createTextWatcher(textConfirmPasswordLayout));
    }

    private TextWatcher createTextWatcher(TextInputLayout textInputLayout) {
        return getTextWatcher(textInputLayout);
    }

    private void initialRegisterState() {
        textEmailLayout.setEnabled(true);
        textUsernameLayout.setEnabled(true);
        textPasswordLayout.setEnabled(true);
        textConfirmPasswordLayout.setEnabled(true);
        loginButton.setEnabled(true);
        registerButton.setVisibility(View.VISIBLE);
        progressBar.setVisibility(View.INVISIBLE);
    }

    @NotNull
    static TextWatcher getTextWatcher(TextInputLayout textInputLayout) {
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

    private void onRegisterClick() {
        String email = textEmailLayout.getEditText().getText().toString();
        String username = textUsernameLayout.getEditText().getText().toString();
        String password = textPasswordLayout.getEditText().getText().toString();
        String confirmPassword = textConfirmPasswordLayout.getEditText().getText().toString();
        if (email.isEmpty()) {
            textEmailLayout.setError("Email cannot be empty");
        } else if(!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            textEmailLayout.setError("Invalid Email");
        } else if(username.isEmpty()) {
            textUsernameLayout.setError("Username must not be empty");
        } else if(password.isEmpty()) {
            textPasswordLayout.setError("Password must not be empty");
        } else if(!confirmPassword.equals(password)) {
            textConfirmPasswordLayout.setError("Confirm password does not match with password");
        } else {
            performRegister(username, email, password);
        }
    }

    private void showErrorDialog(String message) {
        new AlertDialog.Builder(this)
                .setTitle("Registration Failed")
                .setMessage(message)
                .setPositiveButton("OK", (dialog, which) -> dialog.dismiss())
                .show();
        initialRegisterState();
    }

    private void performRegister(String username, String email, String password) {
        textEmailLayout.setEnabled(false);
        textUsernameLayout.setEnabled(false);
        textPasswordLayout.setEnabled(false);
        textConfirmPasswordLayout.setEnabled(false);
        loginButton.setEnabled(false);
        registerButton.setVisibility(View.INVISIBLE);
        progressBar.setVisibility(View.VISIBLE);
        httpClient.register(new RegisterCallback() {
            @Override
            public void onSuccess() {
                runOnUiThread(() -> {
                    startLoginActivity();
                    finish();
                });
            }

            @Override
            public void onError(String message) {
                runOnUiThread(() -> showErrorDialog(message));
            }
        }, username, email, password);
    }

    private void startLoginActivity() {
        Intent intent = new Intent(this, LoginActivity.class);
        intent.putExtra("snackbar_message", "Registered Successfully");
        startActivity(intent);
    }
}
