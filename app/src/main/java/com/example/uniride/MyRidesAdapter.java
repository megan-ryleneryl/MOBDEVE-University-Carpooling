package com.example.uniride;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.firestore.FirebaseFirestore;

import java.util.List;

public class MyRidesAdapter extends RecyclerView.Adapter<MyRidesAdapter.RideViewHolder> {
    private List<RideModel> rideList;
    private Context context;

    public MyRidesAdapter(Context context, List<RideModel> rideList) {
        this.context = context;
        this.rideList = rideList;
    }

    public static class RideViewHolder extends RecyclerView.ViewHolder {
        TextView rideId, from, to, type, departure, arrival, availableSeats, totalSeats, price, status;
        Button editButton, deactivateButton;

        public RideViewHolder(@NonNull View itemView) {
            super(itemView);
            rideId = itemView.findViewById(R.id.textRideId);
            from = itemView.findViewById(R.id.textFrom);
            to = itemView.findViewById(R.id.textTo);
            type = itemView.findViewById(R.id.textType);
            departure = itemView.findViewById(R.id.textDeparture);
            arrival = itemView.findViewById(R.id.textArrival);
            availableSeats = itemView.findViewById(R.id.textAvailableSeats);
            totalSeats = itemView.findViewById(R.id.textTotalSeats);
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
        RideModel currentRide = rideList.get(position);

        holder.rideId.setText("Ride ID: " + currentRide.getRideID());

        if (currentRide.getFrom() != null) {
            holder.from.setText("From: " + currentRide.getFrom().getName());
        }

        if (currentRide.getTo() != null) {
            holder.to.setText("To: " + currentRide.getTo().getName());
        }

        holder.type.setText("Type: " + currentRide.getType());
        holder.departure.setText("Departure: " + currentRide.getDepartureTime());
        holder.arrival.setText("Arrival: " + currentRide.getArrivalTime());
        holder.availableSeats.setText("Available Seats: " + currentRide.getAvailableSeats());
        holder.totalSeats.setText("Total Seats: " + currentRide.getTotalSeats());
        holder.price.setText(String.format("Price: ₱%.2f", currentRide.getPrice()));
        holder.status.setText("Status: " + (currentRide.isActive() ? "Active" : "Inactive"));

        // Hide deactivate button if already inactive
        if (!currentRide.isActive()) {
            holder.deactivateButton.setVisibility(View.GONE);
        } else {
            holder.deactivateButton.setVisibility(View.VISIBLE);
        }

        // Button click listeners
        holder.editButton.setOnClickListener(v -> {
            Intent intent = new Intent(context, RideEdit.class);
            // Add ride details to intent
            intent.putExtra("rideID", currentRide.getRideID());
            context.startActivity(intent);
        });

        holder.deactivateButton.setOnClickListener(v -> {
            // Get reference to the ride document and update its status
            FirebaseFirestore.getInstance()
                    .collection(MyFirestoreReferences.RIDES_COLLECTION)
                    .whereEqualTo("rideID", currentRide.getRideID())
                    .get()
                    .addOnSuccessListener(querySnapshot -> {
                        if (!querySnapshot.isEmpty()) {
                            querySnapshot.getDocuments().get(0).getReference()
                                    .update("isActive", false)
                                    .addOnSuccessListener(aVoid -> {
                                        currentRide.setIsActive(false);
                                        notifyItemChanged(position);
                                    });
                        }
                    });
        });
    }

    @Override
    public int getItemCount() {
        return rideList.size();
    }
}