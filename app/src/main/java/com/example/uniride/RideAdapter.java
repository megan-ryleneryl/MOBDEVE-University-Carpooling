package com.example.uniride;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import java.util.List;

public class RideAdapter extends RecyclerView.Adapter<RideAdapter.RideViewHolder> {
    private List<RideData> rideList;

    public RideAdapter(List<RideData> rideList) {
        this.rideList = rideList;
    }

    public static class RideViewHolder extends RecyclerView.ViewHolder {
        TextView rideId, from, to, type, departure, availableSeats, price, status;
        Button editButton, deactivateButton;

        public RideViewHolder(@NonNull View itemView) {
            super(itemView);
            rideId = itemView.findViewById(R.id.textRideId);
            from = itemView.findViewById(R.id.textFrom);
            to = itemView.findViewById(R.id.textTo);
            type = itemView.findViewById(R.id.textType);
            departure = itemView.findViewById(R.id.textDeparture);
            availableSeats = itemView.findViewById(R.id.textAvailableSeats);
            price = itemView.findViewById(R.id.textPrice);
            status = itemView.findViewById(R.id.textStatus);
            editButton = itemView.findViewById(R.id.buttonEdit);
            deactivateButton = itemView.findViewById(R.id.buttonDeactivate);
        }
    }

    @NonNull
    @Override
    public RideViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.ride_item, parent, false);
        return new RideViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull RideViewHolder holder, int position) {
        RideData currentItem = rideList.get(position);
        holder.rideId.setText("Ride ID: " + currentItem.getRideId());
        holder.from.setText("From: " + currentItem.getFrom());
        holder.to.setText("to " + currentItem.getTo());
        holder.type.setText("Type: " + currentItem.getType());
        holder.departure.setText("Departure: " + currentItem.getDeparture());
        holder.availableSeats.setText("Available Seats: " + currentItem.getAvailableSeats());
        holder.price.setText("Price: " + currentItem.getPrice());
        holder.status.setText("Status: " + currentItem.getStatus());

        // Hide deactivate button if status is inactive
        if (currentItem.getStatus().toLowerCase().equals("inactive")) {
            holder.deactivateButton.setVisibility(View.GONE);
        } else {
            holder.deactivateButton.setVisibility(View.VISIBLE);
        }

        // Set click listeners for buttons
        holder.editButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // TODO: Implement edit functionality
            }
        });
        holder.deactivateButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // TODO: Implement deactivate functionality
            }
        });
    }

    @Override
    public int getItemCount() {
        return rideList.size();
    }
}
