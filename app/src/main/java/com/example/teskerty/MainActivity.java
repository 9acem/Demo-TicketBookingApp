package com.example.teskerty;

import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity {
    private EditText emailField, passwordField;
    private Button loginButton, signupButton;
    private sqlite databaseHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Initialize views
        emailField = findViewById(R.id.emailField);
        passwordField = findViewById(R.id.passwordField);
        loginButton = findViewById(R.id.loginButton);
        signupButton = findViewById(R.id.signupButton);

        // Initialize database helper
        databaseHelper = new sqlite(this);

        // Insert default admin if not already present
        addDefaultAdmin();

        // Set up login button click listener
        loginButton.setOnClickListener(v -> {
            String email = emailField.getText().toString().trim();
            String password = passwordField.getText().toString().trim();

            // Validate email and password
            if (email.isEmpty() || password.isEmpty()) {
                Toast.makeText(this, "Please enter both email and password", Toast.LENGTH_SHORT).show();
            } else {
                // Get user role and user ID based on email and password
                String role = databaseHelper.getUserRole(email, password);

                if (role != null) {
                    // Save the logged-in user's email in shared preferences
                    SharedPreferences sharedPref = getSharedPreferences("UserPrefs", Context.MODE_PRIVATE);
                    SharedPreferences.Editor editor = sharedPref.edit();
                    editor.putString("loggedInUserEmail", email);
                    editor.apply();

                    // Redirect user based on role
                    Intent intent;
                    if (role.equals("admin_role")) {
                        intent = new Intent(MainActivity.this, AdminDashbord.class); // Admin activity
                    } else {
                        intent = new Intent(MainActivity.this, UserDashboard.class); // User activity
                    }
                    startActivity(intent);
                    finish();
                } else {
                    Toast.makeText(this, "Invalid email or password", Toast.LENGTH_SHORT).show();
                }
            }
        });

        // Set up sign-up button click listener
        signupButton.setOnClickListener(view -> {
            Intent intent = new Intent(MainActivity.this, SignUp.class);
            startActivity(intent);
        });
    }

    /**
     * Adds a default admin account if it doesn't already exist.
     */
    private void addDefaultAdmin() {
        String adminEmail = "admin@gmail.com";
        String adminPassword = "admin123";
        String adminRole = "admin_role";

        // Check if admin already exists
        if (databaseHelper.getUserRole(adminEmail, adminPassword) == null) {
            // Insert admin account
            ContentValues adminValues = new ContentValues();
            adminValues.put("name", "Admin");
            adminValues.put("last_name", "Default");
            adminValues.put("email", adminEmail);
            adminValues.put("password", adminPassword);
            adminValues.put("role", adminRole);

            databaseHelper.getWritableDatabase().insert("users", null, adminValues);
            Toast.makeText(this, "Default admin added", Toast.LENGTH_SHORT).show();
        }
    }
}