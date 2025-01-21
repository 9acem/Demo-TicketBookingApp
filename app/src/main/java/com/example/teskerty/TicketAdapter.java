package com.example.teskerty;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Base64;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.io.ByteArrayOutputStream;
import java.util.List;

public class TicketAdapter extends BaseAdapter {
    private final Context context;
    private final List<Ticket> tickets;
    private final sqlite dbHelper;

    public TicketAdapter(Context context, List<Ticket> tickets) {
        this.context = context;
        this.tickets = tickets;
        this.dbHelper = new sqlite(context); // Initialize dbHelper
    }

    @Override
    public int getCount() {
        return tickets.size();
    }

    @Override
    public Object getItem(int position) {
        return tickets.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @SuppressLint("SetTextI18n")
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            convertView = LayoutInflater.from(context).inflate(R.layout.ticket_item, parent, false);
        }

        // Get the ticket for the current position
        Ticket ticket = tickets.get(position);

        // Set the stadium name and match time
        TextView stadiumNameTextView = convertView.findViewById(R.id.stadiumName);
        TextView matchTimeTextView = convertView.findViewById(R.id.matchTime);
        stadiumNameTextView.setText("Stadium : " + ticket.getStadiumName());
        matchTimeTextView.setText("Match Time : " + ticket.getMatchTime());

        // Set the left and right logo images
        ImageView leftLogoImageView = convertView.findViewById(R.id.leftLogoImageView);
        ImageView rightLogoImageView = convertView.findViewById(R.id.rightLogoImageView);
        leftLogoImageView.setImageBitmap(decodeBase64ToBitmap(ticket.getLeftLogo()));
        rightLogoImageView.setImageBitmap(decodeBase64ToBitmap(ticket.getRightLogo()));

        // Remove button to delete the ticket
        Button removeButton = convertView.findViewById(R.id.removeButton);
        removeButton.setOnClickListener(v -> {
            dbHelper.deleteTicket(ticket.getId());
            tickets.remove(position);
            notifyDataSetChanged();
            Toast.makeText(context, "Ticket removed successfully", Toast.LENGTH_SHORT).show();
        });

        // Reserve button to show the QR code
        Button reserveButton = convertView.findViewById(R.id.reserveButton);
        reserveButton.setOnClickListener(v -> {
            String qrCodeData = "Stadium: " + ticket.getStadiumName() + ", Time: " + ticket.getMatchTime();
            Bitmap qrCodeBitmap = QRCodeUtil.generateQRCode(qrCodeData, 300, 300);
            if (qrCodeBitmap != null) {
                showQRCodeDialog(qrCodeBitmap);
            }
        });

        // Update button to update the ticket
        Button updateButton = convertView.findViewById(R.id.updateButton);
        updateButton.setOnClickListener(v -> showUpdateTicketDialog(ticket));

        return convertView;
    }

    private void showUpdateTicketDialog(Ticket ticket) {
        LayoutInflater inflater = LayoutInflater.from(context);
        View dialogView = inflater.inflate(R.layout.dialog_update_ticket, null);

        EditText editStadiumName = dialogView.findViewById(R.id.editStadiumName);
        EditText editMatchTime = dialogView.findViewById(R.id.editMatchTime);
        ImageView leftLogoPreview = dialogView.findViewById(R.id.leftLogoPreview);
        ImageView rightLogoPreview = dialogView.findViewById(R.id.rightLogoPreview);


        final String[] leftLogoBase64 = {ticket.getLeftLogo()};
        final String[] rightLogoBase64 = {ticket.getRightLogo()};

        editStadiumName.setText(ticket.getStadiumName());
        editMatchTime.setText(ticket.getMatchTime());
        leftLogoPreview.setImageBitmap(decodeBase64ToBitmap(ticket.getLeftLogo()));
        rightLogoPreview.setImageBitmap(decodeBase64ToBitmap(ticket.getRightLogo()));



        new AlertDialog.Builder(context)
                .setTitle("Update Ticket")
                .setView(dialogView)
                .setPositiveButton("Update", (dialog, which) -> {
                    String stadiumName = editStadiumName.getText().toString().trim();
                    String matchTime = editMatchTime.getText().toString().trim();

                    if (stadiumName.isEmpty() || matchTime.isEmpty()) {
                        Toast.makeText(context, "Fields cannot be empty!", Toast.LENGTH_SHORT).show();
                        return;
                    }

                    ticket.setStadiumName(stadiumName);
                    ticket.setMatchTime(matchTime);
                    ticket.setLeftLogo(leftLogoBase64[0]);
                    ticket.setRightLogo(rightLogoBase64[0]);

                    dbHelper.updateTicket(ticket);
                    Toast.makeText(context, "Ticket updated successfully", Toast.LENGTH_SHORT).show();
                    notifyDataSetChanged();
                })
                .setNegativeButton("Cancel", null)
                .show();
    }

    private void showQRCodeDialog(Bitmap qrCodeBitmap) {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle("Your Ticket QR Code");

        ImageView imageView = new ImageView(context);
        imageView.setImageBitmap(qrCodeBitmap);
        imageView.setPadding(20, 20, 20, 20);

        builder.setView(imageView);
        builder.setPositiveButton("OK", (dialog, which) -> dialog.dismiss());
        builder.show();
    }

    private Bitmap decodeBase64ToBitmap(String base64String) {
        byte[] decodedBytes = Base64.decode(base64String, Base64.DEFAULT);
        return BitmapFactory.decodeByteArray(decodedBytes, 0, decodedBytes.length);
    }

    private String encodeImageToBase64(Bitmap bitmap) {
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.PNG, 100, byteArrayOutputStream);
        byte[] byteArray = byteArrayOutputStream.toByteArray();
        return Base64.encodeToString(byteArray, Base64.DEFAULT);
    }

    private void selectImage(OnImageSelectedListener listener) {
        // Implement this method to handle image selection from the gallery or camera
        // and call listener.onImageSelected(bitmap) with the selected bitmap
    }

    interface OnImageSelectedListener {
        void onImageSelected(Bitmap bitmap);
    }
}