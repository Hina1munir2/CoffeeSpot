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

public class ForgotPasswordActivity extends AppCompatActivity {
    private EditText emailEditText;
    private Button resetButton;
    private TextView backToLoginText, developerText;
    private FirebaseRepository firebaseRepository;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forget_password);

        firebaseRepository = new FirebaseRepository();

        emailEditText = findViewById(R.id.emailEditText);
        resetButton = findViewById(R.id.resetButton);
        backToLoginText = findViewById(R.id.backToLoginText);
        developerText = findViewById(R.id.developerText);

        developerText.setText("Hina Munir");

        resetButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String email = emailEditText.getText().toString().trim();

                if (TextUtils.isEmpty(email)) {
                    emailEditText.setError("Email required");
                    return;
                }

                resetButton.setEnabled(false);
                resetButton.setText("Sending...");

                firebaseRepository.sendPasswordResetEmail(email, new FirebaseRepository.AuthCallback() {
                    @Override
                    public void onSuccess(String message) {
                        Toast.makeText(ForgotPasswordActivity.this, "Reset email sent", Toast.LENGTH_SHORT).show();
                        Intent intent = new Intent(ForgotPasswordActivity.this, ResetPasswordActivity.class);
                        startActivity(intent);
                        finish();
                    }

                    @Override
                    public void onFailure(String error) {
                        resetButton.setEnabled(true);
                        resetButton.setText("Send Reset Link");
                        Toast.makeText(ForgotPasswordActivity.this, "Failed: " + error, Toast.LENGTH_SHORT).show();
                    }
                });
            }
        });

        backToLoginText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(ForgotPasswordActivity.this, LoginActivity.class);
                startActivity(intent);
                finish();
            }
        });
    }
}