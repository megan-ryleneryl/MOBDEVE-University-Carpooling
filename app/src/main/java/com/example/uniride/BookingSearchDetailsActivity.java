package com.example.uniride;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import java.util.ArrayList;

public class BookingSearchDetailsActivity extends AppCompatActivity {
    TextView priceTv;
    TextView departureTimeTv;
    TextView arrivalTimeTv;
    TextView departureLocTv;
    TextView arrivalLocTv;
    ImageView carImage;
    TextView capacityTv;
    TextView carModelTv;
    ImageView userImage;
    TextView userNameTv;
    TextView ratingTv;
    Button cancelBtn;
    Button confirmBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.booking_search_details);

        // Retrieve Intent data
        BookingModel selectedBooking = (BookingModel) getIntent().getSerializableExtra("selectedBooking");

        if (selectedBooking != null) {
            priceTv = findViewById(R.id.priceTv);
            departureTimeTv = findViewById(R.id.departureTimeTv);
            arrivalTimeTv = findViewById(R.id.arrivalTimeTv);
            departureLocTv = findViewById(R.id.departureLocTv);
            arrivalLocTv = findViewById(R.id.arrivalLocTv);
            carImage = findViewById(R.id.carImage);
            capacityTv = findViewById(R.id.capacityTv);
            carModelTv = findViewById(R.id.carModelTv);
            userImage = findViewById(R.id.userImage);
            userNameTv = findViewById(R.id.userNameTv);
            ratingTv = findViewById(R.id.ratingTv);
            cancelBtn = findViewById(R.id.cancelBtn);
            confirmBtn = findViewById(R.id.confirmBtn);

            // get the needed data from within selectedBooking
            priceTv.setText("P" + selectedBooking.getRide().getPrice());
            departureTimeTv.setText(selectedBooking.getRide().getDepartureTime());
            arrivalTimeTv.setText(selectedBooking.getRide().getArrivalTime());
            departureLocTv.setText(selectedBooking.getRide().getFrom().getName());
            arrivalLocTv.setText(selectedBooking.getRide().getTo().getName());
            carImage.setImageResource(selectedBooking.getRide().getDriver().getCar().getCarImage());
            capacityTv.setText(selectedBooking.getRide().getAvailableSeats() + " seats available");
            carModelTv.setText(selectedBooking.getRide().getDriver().getCar().getModel() + " " + selectedBooking.getRide().getDriver().getCar().getMake());
            userImage.setImageResource(selectedBooking.getPassenger().getPfp());
            userNameTv.setText(selectedBooking.getRide().getDriver().getName());
            //ratingTv.setText("★ " + selectedBooking.getRide().getDriver().getRating());

            cancelBtn.setOnClickListener(v -> {
                finish();
            });

            confirmBtn.setOnClickListener(v -> {
                Intent i = new Intent(BookingSearchDetailsActivity.this, BookingConfirmActivity.class);
                i.putExtra("selectedBooking", selectedBooking);
                startActivity(i);
            });
        }
    }
}