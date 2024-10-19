package com.example.uniride;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Button;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.recyclerview.widget.RecyclerView;
import android.graphics.drawable.Drawable;
import androidx.core.content.ContextCompat;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashSet;

import de.hdodenhof.circleimageview.CircleImageView;

public class MyHomeBookingAdapter extends RecyclerView.Adapter<MyHomeBookingAdapter.ViewHolder> {

    ArrayList<BookingModel> myBookingData;
    String bookingType; // scheduled/requests/accepted
    HomeBookingActivity activity;


    public MyHomeBookingAdapter(ArrayList<BookingModel> myBookingData, String bookingType, HomeBookingActivity activity) {
        this.myBookingData = myBookingData;
        this.bookingType = bookingType;

        this.activity = activity;
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        CircleImageView pfpImage;
        TextView passengerText;
        TextView bookingIDText;
        TextView dateText;
        TextView statusText;
        Button acceptButton;
        Button rejectButton;
        Button cancelBookingButton;
        Button onTheWayButton;
        Button cancelButton;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            pfpImage = itemView.findViewById(R.id.pfpImage);
            passengerText = itemView.findViewById(R.id.passengerText);
            bookingIDText = itemView.findViewById(R.id.bookingIDText);
            dateText = itemView.findViewById(R.id.dateText);
            statusText = itemView.findViewById(R.id.statusText);
            acceptButton = itemView.findViewById(R.id.acceptButton);
            rejectButton = itemView.findViewById(R.id.rejectButton);
            cancelBookingButton = itemView.findViewById(R.id.cancelBookingButton);
            onTheWayButton = itemView.findViewById(R.id.onTheWayButton);
            cancelButton = itemView.findViewById(R.id.cancelButton);
        }
    }
    @NonNull
    @Override
    public MyHomeBookingAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view;
        LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());

        if (bookingType.equals("accepted")) {
            view = layoutInflater.inflate(R.layout.home_booking_accepted_item, parent, false);
        } else if (bookingType.equals("requests")){
            view = layoutInflater.inflate(R.layout.home_booking_requests_item, parent, false);
        } else {
            view = layoutInflater.inflate(R.layout.home_booking_scheduled_item, parent, false);
        }
        MyHomeBookingAdapter.ViewHolder viewHolder = new MyHomeBookingAdapter.ViewHolder(view);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull MyHomeBookingAdapter.ViewHolder holder, int position) {
        final BookingModel booking = myBookingData.get(position);

        holder.pfpImage.setImageResource(booking.getPassenger().getPfp());
        holder.passengerText.setText(booking.getPassenger().getName());
        holder.bookingIDText.setText(String.valueOf(booking.getBookingID()));

        if (!bookingType.equals("scheduled")) {
            SimpleDateFormat dateFormat = new SimpleDateFormat("MM/dd/yyyy");
            holder.dateText.setText(dateFormat.format(booking.getDate()));
        }
        if (!bookingType.equals("requests")) {
            if (booking.getPaymentComplete()) {
                holder.statusText.setText("Finished");
            } else {
                holder.statusText.setText("Pending");
            }
        }
        setListeners(holder);
    }

    @Override
    public int getItemCount() { return myBookingData.size(); }

    private void setListeners(@NonNull MyHomeBookingAdapter.ViewHolder holder) {
        if (bookingType.equals("requests")) {
            holder.acceptButton.setOnClickListener(v -> {
                showConfirmationDialog("accept");
            });
            holder.rejectButton.setOnClickListener(v -> {
                showConfirmationDialog("reject");
            });
        } else if (bookingType.equals("accepted")) {
            holder.cancelBookingButton.setOnClickListener(v -> {
                showConfirmationDialog("cancelBooking");
            });
        } else if (bookingType.equals("scheduled")) {
            holder.onTheWayButton.setOnClickListener(v -> {
                showConfirmationDialog("onTheWay");
            });
            holder.cancelButton.setOnClickListener(v -> {
                showConfirmationDialog("cancel");
            });
        }
    }

    private void showConfirmationDialog(String code) {
        Drawable icon = activity.getResources().getDrawable(android.R.drawable.ic_dialog_alert, null);
        icon.setTint(ContextCompat.getColor(activity, R.color.primary));
        AlertDialog dialog = null;
        if (code.equals("accept")) {
            dialog = new AlertDialog.Builder(activity)
                .setTitle("Accept Booking Request")
                .setMessage("Are you sure you want to ACCEPT the booking request?")
                .setPositiveButton("Accept", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {

                    }
                })
                .setNegativeButton("Cancel", null)
                .setIcon(icon)
                .show();
        } else if (code.equals("reject")) {
            dialog = new AlertDialog.Builder(activity)
                .setTitle("Reject Booking Request")
                .setMessage("Are you sure you want to REJECT the booking request?")
                .setPositiveButton("Reject", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {

                    }
                })
                .setNegativeButton("Cancel", null)
                .setIcon(icon)
                .show();
        } else if (code.equals("onTheWay")) {
            dialog = new AlertDialog.Builder(activity)
                .setTitle("On The Way!")
                .setMessage("Are you sure you want to START the ride?")
                .setPositiveButton("Proceed", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        // TODO: Move to Ongoing Ride Activity
                        Intent i = new Intent(activity, RideTracking.class);
                        i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                        activity.startActivity(i);
                    }
                })
                .setNegativeButton("Cancel", null)
                .setIcon(icon)
                .show();
        } else if (code.equals("cancelBooking") || code.equals("cancel")) {
            dialog = new AlertDialog.Builder(activity)
                    .setTitle("Cancel Booking")
                    .setMessage("Are you sure you want to CANCEL the booking?")
                    .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {

                        }
                    })
                    .setNegativeButton("No", null)
                    .setIcon(icon)
                    .show();
        }
        dialog.getButton(AlertDialog.BUTTON_NEGATIVE).setTextColor(ContextCompat.getColor(activity, R.color.unselected_color));

// Change the text color of the PositiveButton (optional)
        dialog.getButton(AlertDialog.BUTTON_POSITIVE).setTextColor(ContextCompat.getColor(activity, R.color.unselected_color));
    }
}
