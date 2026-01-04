package com.example.coffee.activities;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import com.example.coffee.R;
import com.example.coffee.data.FirebaseRepository;
import com.example.coffee.local.LocalDatabaseHelper;

public class SignupActivity extends AppCompatActivity {
    private EditText nameEditText, emailEditText, passwordEditText, confirmPasswordEditText;
    private Button signupButton, googleSignupButton, facebookSignupButton, twitterSignupButton;
    private TextView loginText, developerText;
    private FirebaseRepository firebaseRepository;
    private LocalDatabaseHelper localDatabaseHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);

        firebaseRepository = new FirebaseRepository();
        localDatabaseHelper = new LocalDatabaseHelper(this);

        nameEditText = findViewById(R.id.nameEditText);
        emailEditText = findViewById(R.id.emailEditText);
        passwordEditText = findViewById(R.id.passwordEditText);
        confirmPasswordEditText = findViewById(R.id.confirmPasswordEditText);
        signupButton = findViewById(R.id.signupButton);
        googleSignupButton = findViewById(R.id.googleSignupButton);
        facebookSignupButton = findViewById(R.id.facebookSignupButton);
        twitterSignupButton = findViewById(R.id.twitterSignupButton);
        loginText = findViewById(R.id.loginText);
        developerText = findViewById(R.id.developerText);

        developerText.setText("Hina Munir");

        signupButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                performEmailSignup();
            }
        });

        googleSignupButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                firebaseRepository.signInWithGoogle(SignupActivity.this, new FirebaseRepository.AuthCallback() {
                    @Override
                    public void onSuccess(String userId) {
                        handleSocialSignupSuccess(userId, "Google");
                    }

                    @Override
                    public void onFailure(String error) {
                        Toast.makeText(SignupActivity.this, "Google signup failed: " + error, Toast.LENGTH_SHORT).show();
                    }
                });
            }
        });

        facebookSignupButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                firebaseRepository.signInWithFacebook(SignupActivity.this, new FirebaseRepository.AuthCallback() {
                    @Override
                    public void onSuccess(String userId) {
                        handleSocialSignupSuccess(userId, "Facebook");
                    }

                    @Override
                    public void onFailure(String error) {
                        Toast.makeText(SignupActivity.this, "Facebook signup failed: " + error, Toast.LENGTH_SHORT).show();
                    }
                });
            }
        });

        twitterSignupButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                firebaseRepository.signInWithTwitter(SignupActivity.this, new FirebaseRepository.AuthCallback() {
                    @Override
                    public void onSuccess(String userId) {
                        handleSocialSignupSuccess(userId, "Twitter");
                    }

                    @Override
                    public void onFailure(String error) {
                        Toast.makeText(SignupActivity.this, "Twitter signup failed: " + error, Toast.LENGTH_SHORT).show();
                    }
                });
            }
        });

        loginText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(SignupActivity.this, LoginActivity.class);
                startActivity(intent);
                finish();
            }
        });
    }

    private void performEmailSignup() {
        String name = nameEditText.getText().toString().trim();
        String email = emailEditText.getText().toString().trim();
        String password = passwordEditText.getText().toString().trim();
        String confirmPassword = confirmPasswordEditText.getText().toString().trim();

        if (TextUtils.isEmpty(name)) {
            nameEditText.setError("Name required");
            return;
        }

        if (TextUtils.isEmpty(email)) {
            emailEditText.setError("Email required");
            return;
        }

        if (TextUtils.isEmpty(password)) {
            passwordEditText.setError("Password required");
            return;
        }

        if (password.length() < 6) {
            passwordEditText.setError("Password must be at least 6 characters");
            return;
        }

        if (!password.equals(confirmPassword)) {
            confirmPasswordEditText.setError("Passwords don't match");
            return;
        }

        signupButton.setEnabled(false);
        signupButton.setText("Creating Account...");

        firebaseRepository.registerUser(name, email, password, new FirebaseRepository.AuthCallback() {
            @Override
            public void onSuccess(String userId) {
                localDatabaseHelper.saveUserSession(email, userId);
                localDatabaseHelper.saveUserProfile(name, email);
                Toast.makeText(SignupActivity.this, "Registration successful", Toast.LENGTH_SHORT).show();
                navigateToHome();
            }

            @Override
            public void onFailure(String error) {
                signupButton.setEnabled(true);
                signupButton.setText("Sign Up");
                Toast.makeText(SignupActivity.this, "Registration failed: " + error, Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void handleSocialSignupSuccess(String userId, String provider) {
        String email = provider.toLowerCase() + "_user@social.com";
        localDatabaseHelper.saveUserSession(email, userId);
        localDatabaseHelper.saveUserProfile(provider + " User", email);
        Toast.makeText(SignupActivity.this, provider + " signup successful", Toast.LENGTH_SHORT).show();
        navigateToHome();
    }

    private void navigateToHome() {
        Intent intent = new Intent(SignupActivity.this, HomeActivity.class);
        startActivity(intent);
        finish();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        firebaseRepository.onActivityResult(requestCode, resultCode, data);
    }
}