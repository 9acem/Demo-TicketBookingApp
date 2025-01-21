package com.example.teskerty;

import android.app.AlertDialog;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;

public class UserAdapter extends BaseAdapter {
    private Context context;
    private ArrayList<User> userList;
    private sqlite dbHelper;

    public UserAdapter(Context context, ArrayList<User> userList, sqlite dbHelper) {
        this.context = context;
        this.userList = userList;
        this.dbHelper = dbHelper;
    }

    @Override
    public int getCount() {
        return userList.size();
    }

    @Override
    public Object getItem(int position) {
        return userList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            convertView = LayoutInflater.from(context).inflate(R.layout.list_item_user, parent, false);
        }

        User user = userList.get(position);

        TextView nameTextView = convertView.findViewById(R.id.nameTextView);
        TextView lastNameTextView = convertView.findViewById(R.id.lastNameTextView);
        TextView emailTextView = convertView.findViewById(R.id.emailTextView);
        Button updateButton = convertView.findViewById(R.id.updateButton);
        Button deleteButton = convertView.findViewById(R.id.deleteButton);

        nameTextView.setText("First Name: " + user.getName());
        lastNameTextView.setText("Last Name: " + user.getLastName());
        emailTextView.setText("Email: " + user.getEmail());


        deleteButton.setOnClickListener(v -> {
            dbHelper.deleteUser(user.getEmail());
            userList.remove(position);
            notifyDataSetChanged();
            Toast.makeText(context, "User Deleted", Toast.LENGTH_SHORT).show();
        });

        updateButton.setOnClickListener(v -> showUpdateDialog(user));

        return convertView;
    }

    private void showUpdateDialog(User user) {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        LayoutInflater inflater = LayoutInflater.from(context);
        View dialogView = inflater.inflate(R.layout.dialog_update_user, null);
        builder.setView(dialogView);

        EditText etUpdateFirstName = dialogView.findViewById(R.id.et_update_first_name);
        EditText etUpdateLastName = dialogView.findViewById(R.id.et_update_last_name);
        EditText etUpdateEmail = dialogView.findViewById(R.id.et_update_email);
        Button btnUpdateConfirm = dialogView.findViewById(R.id.btn_update_confirm);

        etUpdateFirstName.setText(user.getName());
        etUpdateLastName.setText(user.getLastName());
        etUpdateEmail.setText(user.getEmail());

        AlertDialog alertDialog = builder.create();

        btnUpdateConfirm.setOnClickListener(v -> {
            String newFirstName = etUpdateFirstName.getText().toString();
            String newLastName = etUpdateLastName.getText().toString();
            String newEmail = etUpdateEmail.getText().toString();

            if (!newFirstName.isEmpty() && !newLastName.isEmpty() && !newEmail.isEmpty()) {
                user.setName(newFirstName);
                user.setLastName(newLastName);
                user.setEmail(newEmail);
                dbHelper.updateUser(user);
                notifyDataSetChanged();
                alertDialog.dismiss();
                Toast.makeText(context, "User updated", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(context, "Please fill out all fields", Toast.LENGTH_SHORT).show();
            }
        });

        alertDialog.show();
    }
}