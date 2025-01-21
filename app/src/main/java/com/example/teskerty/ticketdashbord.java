package com.example.teskerty;

import android.app.AlertDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
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

import androidx.appcompat.app.AppCompatActivity;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.ArrayList;

public class ticketdashbord extends BaseActivity {

    private sqlite dbHelper;
    private ListView listView;
    private TicketAdapter adapter;
    private ArrayList<Ticket> ticketList;
    private Button addTicketButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ticketdashbord);
        setupDrawer(R.layout.activity_ticketdashbord);

        listView = findViewById(R.id.ListView);
        addTicketButton = findViewById(R.id.addticket);
        dbHelper = new sqlite(this);

        loadTickets();

        addTicketButton.setOnClickListener(view -> showAddTicketDialog());
    }

    public void loadTickets() {
        ticketList = dbHelper.getAllTickets();
        adapter = new TicketAdapter(this, ticketList);
        listView.setAdapter(adapter);
    }



    public void reloadTickets() {
        loadTickets();  // This method will reload the tickets after update
    }


    private void showAddTicketDialog() {
        LayoutInflater inflater = getLayoutInflater();
        View dialogView = inflater.inflate(R.layout.dialog_add_ticket, null);

        EditText editStadiumName = dialogView.findViewById(R.id.editStadiumName);
        EditText editMatchTime = dialogView.findViewById(R.id.editMatchTime);
        Button btnChooseLeftLogo = dialogView.findViewById(R.id.btnChooseLeftLogo);
        Button btnChooseRightLogo = dialogView.findViewById(R.id.btnChooseRightLogo);
        ImageView leftLogoPreview = dialogView.findViewById(R.id.leftLogoPreview);
        ImageView rightLogoPreview = dialogView.findViewById(R.id.rightLogoPreview);

        final String[] leftLogoBase64 = {""};
        final String[] rightLogoBase64 = {""};

        btnChooseLeftLogo.setOnClickListener(v -> {
            selectImage((bitmap) -> {
                leftLogoPreview.setImageBitmap(bitmap);
                leftLogoBase64[0] = encodeImageToBase64(bitmap);
            });
        });

        btnChooseRightLogo.setOnClickListener(v -> {
            selectImage((bitmap) -> {
                rightLogoPreview.setImageBitmap(bitmap);
                rightLogoBase64[0] = encodeImageToBase64(bitmap);
            });
        });

        new AlertDialog.Builder(this)
                .setTitle("Add Ticket")
                .setView(dialogView)
                .setPositiveButton("Add", (dialog, which) -> {
                    String stadiumName = editStadiumName.getText().toString().trim();
                    String matchTime = editMatchTime.getText().toString().trim();

                    if (stadiumName.isEmpty() || matchTime.isEmpty()) {
                        Toast.makeText(this, "Fields cannot be empty!", Toast.LENGTH_SHORT).show();
                        return;
                    }

                    Ticket newTicket = new Ticket(0, stadiumName, matchTime, leftLogoBase64[0], rightLogoBase64[0]);
                    dbHelper.addTicket(newTicket);
                    Toast.makeText(this, "Ticket Added Successfully", Toast.LENGTH_SHORT).show();

                    loadTickets();
                })
                .setNegativeButton("Cancel", null)
                .show();
    }



    private void selectImage(OnImageSelectedListener listener) {
        Intent intent = new Intent(Intent.ACTION_PICK);
        intent.setType("image/*");
        startActivityForResult(intent, IMAGE_PICK_REQUEST);

        imageSelectedListener = listener;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == IMAGE_PICK_REQUEST && resultCode == RESULT_OK && data != null) {
            Uri imageUri = data.getData();
            try {
                Bitmap bitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(), imageUri);
                if (imageSelectedListener != null) {
                    imageSelectedListener.onImageSelected(bitmap);
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private String encodeImageToBase64(Bitmap bitmap) {
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 50, outputStream);
        byte[] byteArray = outputStream.toByteArray();
        return Base64.encodeToString(byteArray, Base64.DEFAULT);
    }
    private Bitmap decodeBase64ToBitmap(String base64Str) throws IllegalArgumentException {
        byte[] decodedBytes = Base64.decode(base64Str, Base64.DEFAULT);
        return BitmapFactory.decodeByteArray(decodedBytes, 0, decodedBytes.length);
    }


    private interface OnImageSelectedListener {
        void onImageSelected(Bitmap bitmap);
    }

    private static final int IMAGE_PICK_REQUEST = 1;
    private OnImageSelectedListener imageSelectedListener;

}
