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

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        final BookingModel myBookingDataList = myBookingData.get(position);
        Log.e("booking at index ", myBookingDataList.toString());
        Log.d("Ride", "Ride Object: " + myBookingDataList.getRide());
        Log.d("Driver", "Driver Object: " + myBookingDataList.getRide().getDriver());
        holder.carImage.setImageResource(myBookingDataList.getRide().getDriver().getCar().getCarImage());
        holder.locationTv.setText(myBookingDataList.getRide().getFrom() + " to " + myBookingDataList.getRide().getTo());
        holder.timeTv.setText(myBookingDataList.getRide().getDepartureTime() + " to " + myBookingDataList.getRide().getArrivalTime());
        holder.dateTv.setText(myBookingDataList.getDate());
        holder.priceTv.setText("P" + myBookingDataList.getRide().getPrice());

        holder.detailsBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(context, BookingHomeDetailsActivity.class);
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
