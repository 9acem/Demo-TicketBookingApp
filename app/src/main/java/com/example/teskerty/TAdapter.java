package com.example.teskerty;

import android.app.AlertDialog;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Base64;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;

public class TAdapter extends ArrayAdapter<Ticket> {

    private Context context;
    private ArrayList<Ticket> tickets;
    private boolean isUserView; // Differentiate between User and Admin view

    public TAdapter(Context context, ArrayList<Ticket> tickets, boolean isUserView) {
        super(context, 0, tickets);
        this.context = context;
        this.tickets = tickets;
        this.isUserView = isUserView;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            // Inflate the layout based on the view type (user or admin)
            int layoutId = isUserView ? R.layout.ticket_item_user : R.layout.ticket_item_user; // Adjust admin layout
            convertView = LayoutInflater.from(context).inflate(layoutId, parent, false);
        }
        Button reserveButton = convertView.findViewById(R.id.reserveButton);


        // Bind ticket data
        Ticket ticket = tickets.get(position);

        ImageView leftLogo = convertView.findViewById(R.id.leftLogoImageView);
        ImageView rightLogo = convertView.findViewById(R.id.rightLogoImageView);
        TextView stadiumName = convertView.findViewById(R.id.stadiumName);
        TextView matchTime = convertView.findViewById(R.id.matchTime);

        // Set data for the ticket item
        stadiumName.setText("Stadium Name: " + ticket.getStadiumName());
        matchTime.setText("Match Time: " + ticket.getMatchTime());

        // Decode and set images for logos
        if (ticket.getLeftLogo() != null) {
            Bitmap leftBitmap = decodeBase64ToBitmap(ticket.getLeftLogo());
            leftLogo.setImageBitmap(leftBitmap);
        }
        if (ticket.getRightLogo() != null) {
            Bitmap rightBitmap = decodeBase64ToBitmap(ticket.getRightLogo());
            rightLogo.setImageBitmap(rightBitmap);
        }
        // Set button action
        reserveButton.setOnClickListener(v -> {
            // Generate QR code for the ticket
            String qrCodeData = "Stadium: " + ticket.getStadiumName() + ", Time: " + ticket.getMatchTime();
            Bitmap qrCodeBitmap = QRCodeUtil.generateQRCode(qrCodeData, 300, 300);

            // Show QR code in a new dialog
            if (qrCodeBitmap != null) {
                showQRCodeDialog(qrCodeBitmap);
            }
        });

        return convertView;
    }
    private void showQRCodeDialog(Bitmap qrCodeBitmap) {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle("Your Ticket QR Code");

        // Create ImageView for QR Code
        ImageView imageView = new ImageView(context);
        imageView.setImageBitmap(qrCodeBitmap);
        imageView.setPadding(20, 20, 20, 20);

        builder.setView(imageView);
        builder.setPositiveButton("OK", (dialog, which) -> dialog.dismiss());
        builder.show();
    }

    private Bitmap decodeBase64ToBitmap(String base64Image) {
        byte[] decodedString = Base64.decode(base64Image, Base64.DEFAULT);
        return BitmapFactory.decodeByteArray(decodedString, 0, decodedString.length);
    }
}
