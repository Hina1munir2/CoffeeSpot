package com.example.coffee.activities;

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

public class ResetPasswordActivity extends AppCompatActivity {
    private EditText newPasswordEditText, confirmPasswordEditText;
    private Button resetButton;
    private TextView developerText;
    private FirebaseRepository firebaseRepository;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reset_password);

        firebaseRepository = new FirebaseRepository();

        newPasswordEditText = findViewById(R.id.newPasswordEditText);
        confirmPasswordEditText = findViewById(R.id.confirmPasswordEditText);
        resetButton = findViewById(R.id.resetButton);
        developerText = findViewById(R.id.developerText);

        developerText.setText("Hina Munir");

        resetButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String newPassword = newPasswordEditText.getText().toString().trim();
                String confirmPassword = confirmPasswordEditText.getText().toString().trim();

                if (TextUtils.isEmpty(newPassword)) {
                    newPasswordEditText.setError("New password required");
                    return;
                }

                if (newPassword.length() < 6) {
                    newPasswordEditText.setError("Password must be at least 6 characters");
                    return;
                }

                if (!newPassword.equals(confirmPassword)) {
                    confirmPasswordEditText.setError("Passwords don't match");
                    return;
                }

                resetButton.setEnabled(false);
                resetButton.setText("Resetting...");

                firebaseRepository.resetPassword(newPassword, new FirebaseRepository.AuthCallback() {
                    @Override
                    public void onSuccess(String message) {
                        Toast.makeText(ResetPasswordActivity.this, "Password reset successful", Toast.LENGTH_SHORT).show();
                        finish();
                    }

                    @Override
                    public void onFailure(String error) {
                        resetButton.setEnabled(true);
                        resetButton.setText("Reset Password");
                        Toast.makeText(ResetPasswordActivity.this, "Reset failed: " + error, Toast.LENGTH_SHORT).show();
                    }
                });
            }
        });
    }
}