package com.example.uniride;

import android.content.Intent;
import android.content.Context;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;
import de.hdodenhof.circleimageview.CircleImageView;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

public class HomeBookingActivity extends BottomNavigationActivity {

    TextView titleText;
    String bookingType;

    private RecyclerView recyclerView;
    private MyHomeBookingAdapter adapter;
    private ArrayList<BookingModel> bookingData;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_home_booking);

        titleText = findViewById(R.id.titleText);

        Intent i = getIntent();
        bookingType = i.getStringExtra("bookingTypePassed");

        if (bookingType.equals("scheduled")) {
            titleText.setText("Bookings Scheduled Today");
        } else if (bookingType.equals("requests")) {
            titleText.setText("Pending Booking Requests");
        } else if (bookingType.equals("accepted")) {
            titleText.setText("Other Accepted Bookings");
        }

        recyclerView = findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        adapter = new MyHomeBookingAdapter(bookingData, bookingType, this);
        recyclerView.setAdapter(adapter);
    }

    @Override
    protected int getSelectedItemId() {
        return R.id.driver;
    }
}