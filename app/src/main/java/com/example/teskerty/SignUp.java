package com.example.teskerty;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioGroup;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;

public class SignUp extends AppCompatActivity {
    private EditText nameField, lastNameField, emailField, passwordField;
    private RadioGroup roleRadioGroup;
    private Button submitButton;
    private sqlite databaseHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);

        // Initialize views
        nameField = findViewById(R.id.firstNameField);
        lastNameField = findViewById(R.id.lastNameField);
        emailField = findViewById(R.id.emailField);
        passwordField = findViewById(R.id.passwordField);

        submitButton = findViewById(R.id.submitButton);
        databaseHelper = new sqlite(this);

        // Handle submit button click
        submitButton.setOnClickListener(v -> {
            String name = nameField.getText().toString();
            String lastName = lastNameField.getText().toString();
            String email = emailField.getText().toString();
            String password = passwordField.getText().toString();

            // All new users are assigned "user_role" by default
            String role = "user_role";

            if (name.isEmpty() || lastName.isEmpty() || email.isEmpty() || password.isEmpty()) {
                Toast.makeText(this, "Please fill all fields", Toast.LENGTH_SHORT).show();
            } else {
                boolean isInserted = databaseHelper.insertUser(name, lastName, email, password, role);

                if (isInserted) {
                    Toast.makeText(this, "Account Created Successfully!", Toast.LENGTH_SHORT).show();
                    Intent intent = new Intent(SignUp.this, MainActivity.class);
                    startActivity(intent);
                    finish();
                } else {
                    Toast.makeText(this, "Error creating account", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }
}