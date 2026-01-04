package com.example.coffee.activities;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import com.example.coffee.R;
import com.example.coffee.data.FirebaseRepository;
import com.example.coffee.local.LocalDatabaseHelper;

public class LoginActivity extends AppCompatActivity {
    private EditText emailEditText, passwordEditText;
    private Button loginButton;
    private ImageView googleLoginButton, facebookLoginButton, twitterLoginButton;
    private TextView signupText, forgotPasswordText, developerText;
    private FirebaseRepository firebaseRepository;
    private LocalDatabaseHelper localDatabaseHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        firebaseRepository = new FirebaseRepository();
        localDatabaseHelper = new LocalDatabaseHelper(this);

        emailEditText = findViewById(R.id.emailEditText);
        passwordEditText = findViewById(R.id.passwordEditText);
        loginButton = findViewById(R.id.loginButton);
        googleLoginButton = findViewById(R.id.googleLoginButton);
        facebookLoginButton = findViewById(R.id.facebookLoginButton);
        twitterLoginButton = findViewById(R.id.twitterLoginButton);
        signupText = findViewById(R.id.signupText);
        forgotPasswordText = findViewById(R.id.forgotPasswordText);
        developerText = findViewById(R.id.developerText);

        developerText.setText("Hina Munir");

        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                performEmailLogin();
            }
        });

        googleLoginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                firebaseRepository.signInWithGoogle(LoginActivity.this, new FirebaseRepository.AuthCallback() {
                    @Override
                    public void onSuccess(String userId) {
                        handleSocialLoginSuccess(userId, "Google");
                    }

                    @Override
                    public void onFailure(String error) {
                        Toast.makeText(LoginActivity.this, "Google login failed: " + error, Toast.LENGTH_SHORT).show();
                    }
                });
            }
        });

        facebookLoginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                firebaseRepository.signInWithFacebook(LoginActivity.this, new FirebaseRepository.AuthCallback() {
                    @Override
                    public void onSuccess(String userId) {
                        handleSocialLoginSuccess(userId, "Facebook");
                    }

                    @Override
                    public void onFailure(String error) {
                        Toast.makeText(LoginActivity.this, "Facebook login failed: " + error, Toast.LENGTH_SHORT).show();
                    }
                });
            }
        });

        twitterLoginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                firebaseRepository.signInWithTwitter(LoginActivity.this, new FirebaseRepository.AuthCallback() {
                    @Override
                    public void onSuccess(String userId) {
                        handleSocialLoginSuccess(userId, "Twitter");
                    }

                    @Override
                    public void onFailure(String error) {
                        Toast.makeText(LoginActivity.this, "Twitter login failed: " + error, Toast.LENGTH_SHORT).show();
                    }
                });
            }
        });

        signupText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(LoginActivity.this, SignupActivity.class);
                startActivity(intent);
            }
        });

        forgotPasswordText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(LoginActivity.this, ForgotPasswordActivity.class);
                startActivity(intent);
            }
        });
    }

    private void performEmailLogin() {
        String email = emailEditText.getText().toString().trim();
        String password = passwordEditText.getText().toString().trim();

        if (TextUtils.isEmpty(email)) {
            emailEditText.setError("Email required");
            return;
        }

        if (TextUtils.isEmpty(password)) {
            passwordEditText.setError("Password required");
            return;
        }

        loginButton.setEnabled(false);
        loginButton.setText("Logging in...");

        firebaseRepository.loginUser(email, password, new FirebaseRepository.AuthCallback() {
            @Override
            public void onSuccess(String userId) {
                localDatabaseHelper.saveUserSession(email, userId);
                Toast.makeText(LoginActivity.this, "Login successful", Toast.LENGTH_SHORT).show();
                navigateToHome();
            }

            @Override
            public void onFailure(String error) {
                loginButton.setEnabled(true);
                loginButton.setText("Login");
                Toast.makeText(LoginActivity.this, "Login failed: " + error, Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void handleSocialLoginSuccess(String userId, String provider) {
        localDatabaseHelper.saveUserSession(provider + "_user", userId);
        Toast.makeText(LoginActivity.this, provider + " login successful", Toast.LENGTH_SHORT).show();
        navigateToHome();
    }

    private void navigateToHome() {
        Intent intent = new Intent(LoginActivity.this, HomeActivity.class);
        startActivity(intent);
        finish();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        firebaseRepository.onActivityResult(requestCode, resultCode, data);
    }
}