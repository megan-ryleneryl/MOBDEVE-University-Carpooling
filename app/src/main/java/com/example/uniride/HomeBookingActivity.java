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

public class HomeBookingActivity extends AppCompatActivity {

    TextView titleText;
    String bookingType;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_home_booking);

        titleText = findViewById(R.id.titleText);

        Intent i = getIntent();
        bookingType = i.getStringExtra("bookingTypePassed");

        //if (bookingType == "scheduled")
    }
}