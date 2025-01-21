package com.example.teskerty;

import android.app.AlertDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Base64;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.Toast;


import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.ArrayList;

public class UserDashboard extends Base22Activity {

    private ListView listView;
    private TAdapter adapter;
    private ArrayList<Ticket> ticketList;
    private sqlite dbHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_dashboard);
        setupD(R.layout.activity_user_dashboard);

        listView = findViewById(R.id.List); // Ensure this ID matches your layout
        dbHelper = new sqlite(this);

        loadTickets();
    }

    private void loadTickets() {
        ticketList = dbHelper.getAllTickets();
        // Pass 'true' to the adapter to indicate user view
        adapter = new TAdapter(this, ticketList, true);
        listView.setAdapter(adapter);
    }
}

