package com.example.uniride;

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
import java.util.ArrayList;

public class MyBookingSearchAdapter extends RecyclerView.Adapter<MyBookingSearchAdapter.ViewHolder> {

    ArrayList<BookingModel> myBookingData;
    Context context;

    public MyBookingSearchAdapter(ArrayList<BookingModel> myBookingData, BookingSearchActivity activity) {
        this.myBookingData = myBookingData;
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
        final BookingModel myBookingDataList = myBookingData.get(position);
        holder.carImage2.setImageResource(myBookingDataList.getRide().getDriver().getCar().getCarImage());
        holder.locationTv2.setText(myBookingDataList.getRide().getFrom() + " to " + myBookingDataList.getRide().getTo());
        holder.timeTv2.setText(myBookingDataList.getRide().getDepartureTime() + " to " + myBookingDataList.getRide().getArrivalTime());
        holder.capacityTv2.setText(myBookingDataList.getRide().getAvailableSeats() + " seats available");
        holder.priceTv2.setText("P" + myBookingDataList.getRide().getPrice());

        Log.d("BookingData", "Booking: " + myBookingDataList.getRide().getFrom() + " to " + myBookingDataList.getRide().getTo());

        holder.bookBtn2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(context, BookingSearchDetailsActivity.class);
                i.putExtra("myBookingData", myBookingData);
                i.putExtra("selectedBooking", myBookingDataList);
                context.startActivity(i);
            }
        });
    }

    @Override
    public int getItemCount() {
        return myBookingData.size();
    }
}
