package com.example.uniride;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Button;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

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
    Context context;


    public MyHomeBookingAdapter(ArrayList<BookingModel> myBookingData, String bookingType, HomeBookingActivity activity) {
        this.myBookingData = myBookingData;
        this.bookingType = bookingType;

        this.context = activity;
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
                holder.statusText.setText("finished");
            } else {
                holder.statusText.setText("pending");
            }
        }
    }

    @Override
    public int getItemCount() { return myBookingData.size(); }
}
