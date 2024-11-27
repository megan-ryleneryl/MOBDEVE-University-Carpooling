package com.example.uniride;

import static android.content.Intent.getIntent;

import android.content.Context;
import android.content.Intent;
import android.util.Log;
import java.util.Random;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import java.util.ArrayList;

public class MyBookingSearchAdapter extends RecyclerView.Adapter<MyBookingSearchAdapter.ViewHolder> {
    ArrayList<BookingModel> myBookingData;
    Context context;
    private String currentUserID;
    private ArrayList<BookingModel> searchResults;

    public MyBookingSearchAdapter(ArrayList<BookingModel> searchResults, String currentUserID, BookingSearchActivity activity) {
        this.searchResults = searchResults;
        this.currentUserID = currentUserID;
        this.context = activity;
    }

    public class ViewHolder extends RecyclerView.ViewHolder{
        ImageView carImage2;
        TextView locationTv2;
        TextView timeTv2;
        TextView capacityTv2;
        TextView priceTv2;
        Button bookBtn2;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            carImage2 = itemView.findViewById(R.id.carImage2);
            locationTv2 = itemView.findViewById(R.id.locationTv2);
            timeTv2 = itemView.findViewById(R.id.timeTv2);
            capacityTv2 = itemView.findViewById(R.id.capacityTv);
            priceTv2 = itemView.findViewById(R.id.priceTv2);
            bookBtn2 = itemView.findViewById(R.id.bookBtn);
        }
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());
        View view = layoutInflater.inflate(R.layout.booking_search_item,parent,false);
        ViewHolder viewHolder = new ViewHolder(view);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        if(searchResults != null) {
            final BookingModel searchResult = searchResults.get(position);
            RideModel ride = searchResult.getRide();

            if (ride != null) {
                if (ride.getDriver() != null && ride.getDriver().getCar() != null) {
                    holder.carImage2.setImageResource(ride.getDriver().getCar().getCarImage());
                }

                if (ride.getFrom() != null && ride.getTo() != null) {
                    holder.locationTv2.setText(ride.getFrom().getName() + " to " + ride.getTo().getName());
                }

                float rating = getRating();

                holder.timeTv2.setText(ride.getDepartureTime() + " to " + ride.getArrivalTime());
                holder.capacityTv2.setText(String.valueOf("★ " + rating));
                holder.priceTv2.setText("P" + ride.getPrice());

                holder.bookBtn2.setOnClickListener(v -> {
                    Intent i = new Intent(context, BookingSearchDetailsActivity.class);
                    i.putExtra("selectedBooking", searchResult);
                    i.putExtra("currentUserID", currentUserID);
                    i.putExtra("rating", rating);
                    context.startActivity(i);
                });
            }
        }
    }

    private float getRating() {
        Random random = new Random();
        float value = 3.5f + (random.nextFloat() * (5.0f - 3.5f));
        return Math.round(value * 100f) / 100f;
    }

    @Override
    public int getItemCount() {
        return searchResults != null ? searchResults.size() : 0;
    }
}
