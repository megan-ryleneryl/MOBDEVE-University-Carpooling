package com.example.uniride;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.text.SimpleDateFormat;
import java.util.ArrayList;

public class MyBookingHomeAdapter extends RecyclerView.Adapter<MyBookingHomeAdapter.ViewHolder> {

    ArrayList<BookingModel> myBookingData;
    Context context;

    public MyBookingHomeAdapter(ArrayList<BookingModel> myBookingData, BookingHomeActivity activity) {
        this.myBookingData = myBookingData;
        this.context = activity;
    }

    public class ViewHolder extends RecyclerView.ViewHolder{
        ImageView carImage;
        TextView locationTv;
        TextView timeTv;
        TextView dateTv;
        TextView priceTv;
        Button detailsBtn;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            carImage = itemView.findViewById(R.id.carImage);
            locationTv = itemView.findViewById(R.id.locationTv);
            timeTv = itemView.findViewById(R.id.timeTv);
            dateTv = itemView.findViewById(R.id.dateTv);
            priceTv = itemView.findViewById(R.id.priceTv);
            detailsBtn = itemView.findViewById(R.id.detailsBtn);
        }
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());
        View view = layoutInflater.inflate(R.layout.booking_home_item,parent,false);
        ViewHolder viewHolder = new ViewHolder(view);
        return viewHolder;
    }

    @SuppressLint("ResourceType")
    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        final BookingModel booking = myBookingData.get(position);

        if (booking != null && booking.getRide() != null) {
            RideModel ride = booking.getRide();

            Log.d("Car image", context.getResources().getResourceName(2131231041));

            // Set car image
            if (ride.getDriver() != null && ride.getDriver().getCar() != null) {
                holder.carImage.setImageResource(ride.getDriver().getCar().getCarImage());
                Log.d("Car image", String.valueOf(ride.getDriver().getCar().getCarImage()));
            }

            // Set locations
            String fromLocation = ride.getFrom() != null ? ride.getFrom().getName() : "";
            String toLocation = ride.getTo() != null ? ride.getTo().getName() : "";
            holder.locationTv.setText(fromLocation + " to " + toLocation);

            // Set time
            holder.timeTv.setText(ride.getDepartureTime() + " to " + ride.getArrivalTime());

            // Set date
            holder.dateTv.setText(booking.getDate().replace("\"", ""));

            // Set price
            holder.priceTv.setText("P" + ride.getPrice());

            holder.detailsBtn.setOnClickListener(v -> {
                Intent intent = new Intent(context, BookingHomeDetailsActivity.class);
                intent.putExtra("selectedBooking", booking);
                intent.putExtra("myBookingData", myBookingData);
                context.startActivity(intent);
            });
        }
    }

    @Override
    public int getItemCount() {
        return myBookingData.size();
    }
}
