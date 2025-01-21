package com.example.teskerty;

import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

public class UserProfil extends Base22Activity {

    private TextView tvName, tvLastName, tvEmail, tvPassword;
    private sqlite dbHelper;
    private String loggedInUserEmail;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_profil);
        setupD(R.layout.activity_user_profil);

        // Initialize views
        tvName = findViewById(R.id.tv_name);
        tvLastName = findViewById(R.id.tv_last_name);
        tvEmail = findViewById(R.id.tv_email);
        tvPassword = findViewById(R.id.tv_password);
        Button btnUpdate = findViewById(R.id.btn_update);
        Button btnDelete = findViewById(R.id.btn_delete);

        // Initialize database helper
        dbHelper = new sqlite(this);

        // Retrieve the logged-in user's email from shared preferences
        SharedPreferences sharedPref = getSharedPreferences("UserPrefs", Context.MODE_PRIVATE);
        loggedInUserEmail = sharedPref.getString("loggedInUserEmail", null);

        if (loggedInUserEmail != null) {
            // Load user data
            loadUserData(loggedInUserEmail);
        } else {
            // Handle the case where no user is logged in
            // For example, redirect to the login screen
        }

        // Set up button click listeners
        btnUpdate.setOnClickListener(v -> showUpdateDialog());
        btnDelete.setOnClickListener(v -> deleteUser(loggedInUserEmail));
    }

    private void loadUserData(String email) {
        User user = dbHelper.getUserByEmail(email);
        if (user != null) {
            tvName.setText("Name: " + user.getName());
            tvLastName.setText("Last Name: " + user.getLastName());
            tvEmail.setText("Email: " + user.getEmail());
            tvPassword.setText("Password: " + user.getPassword());
        }
    }

    private void showUpdateDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        View dialogView = getLayoutInflater().inflate(R.layout.dialog_update, null);
        builder.setView(dialogView);

        EditText etUpdateName = dialogView.findViewById(R.id.et_update_name);
        EditText etUpdateLastName = dialogView.findViewById(R.id.et_update_last_name);
        EditText etUpdateEmail = dialogView.findViewById(R.id.et_update_email);
        EditText etUpdatePassword = dialogView.findViewById(R.id.et_update_password);
        Button btnUpdateConfirm = dialogView.findViewById(R.id.btn_update_confirm);

        User user = dbHelper.getUserByEmail(loggedInUserEmail);
        if (user != null) {
            etUpdateName.setText(user.getName());
            etUpdateLastName.setText(user.getLastName());
            etUpdateEmail.setText(user.getEmail());
            etUpdatePassword.setText(user.getPassword());
        }

        AlertDialog alertDialog = builder.create();

        btnUpdateConfirm.setOnClickListener(v -> {
            String newName = etUpdateName.getText().toString().trim();
            String newLastName = etUpdateLastName.getText().toString().trim();
            String newEmail = etUpdateEmail.getText().toString().trim();
            String newPassword = etUpdatePassword.getText().toString().trim();

            if (!newName.isEmpty() && !newLastName.isEmpty() && !newEmail.isEmpty() && !newPassword.isEmpty()) {
                user.setName(newName);
                user.setLastName(newLastName);
                user.setEmail(newEmail);
                user.setPassword(newPassword);

                dbHelper.updateUser(user);
                loggedInUserEmail = newEmail; // Update the email in case it was changed
                SharedPreferences sharedPref = getSharedPreferences("UserPrefs", Context.MODE_PRIVATE);
                SharedPreferences.Editor editor = sharedPref.edit();
                editor.putString("loggedInUserEmail", newEmail);
                editor.apply();

                loadUserData(newEmail);
                alertDialog.dismiss();
                Toast.makeText(UserProfil.this, "Profile updated successfully", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(UserProfil.this, "Please fill out all fields", Toast.LENGTH_SHORT).show();
            }
        });

        alertDialog.show();
    }

    private void deleteUser(String email) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage("Are you sure you want to delete your profile?")
                .setPositiveButton("Yes", (dialog, id) -> {
                    dbHelper.deleteUser(email);

                    SharedPreferences sharedPref = getSharedPreferences("UserPrefs", Context.MODE_PRIVATE);
                    SharedPreferences.Editor editor = sharedPref.edit();
                    editor.remove("loggedInUserEmail");
                    editor.apply();

                    Toast.makeText(UserProfil.this, "Profile deleted successfully", Toast.LENGTH_SHORT).show();
                    finish(); // Close the activity
                })
                .setNegativeButton("No", (dialog, id) -> {
                    // User cancelled the dialog
                });
        builder.create().show();
    }
}