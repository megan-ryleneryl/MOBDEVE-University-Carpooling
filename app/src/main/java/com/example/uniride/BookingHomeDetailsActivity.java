package com.example.uniride;

import android.content.Context;
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

public class BookingHomeDetailsActivity extends AppCompatActivity {
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
    Button previousBtn;
    Button nextBtn;
    Context context;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.booking_home_details);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        // Retrieve Intent data
        ArrayList<BookingModel> myBookingData = (ArrayList<BookingModel>) getIntent().getSerializableExtra("myBookingData");
        BookingModel selectedBooking = (BookingModel) getIntent().getSerializableExtra("selectedBooking");

        if (myBookingData != null && selectedBooking != null) {
            TextView priceTv = findViewById(R.id.priceTv);
            TextView departureTimeTv = findViewById(R.id.departureTimeTv);
            TextView arrivalTimeTv = findViewById(R.id.arrivalTimeTv);
            TextView departureLocTv = findViewById(R.id.departureLocTv);
            TextView arrivalLocTv = findViewById(R.id.arrivalLocTv);
            ImageView carImage = findViewById(R.id.carImage);
            TextView capacityTv = findViewById(R.id.capacityTv);
            TextView carModelTv = findViewById(R.id.carModelTv);
            ImageView userImage = findViewById(R.id.userImage);
            TextView userNameTv = findViewById(R.id.userNameTv);
            TextView ratingTv = findViewById(R.id.ratingTv);
            Button previousBtn = findViewById(R.id.previousBtn);
            Button nextBtn = findViewById(R.id.nextBtn);

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
            ratingTv.setText("" + selectedBooking.getRide().getDriver().getRating());

            previousBtn.setOnClickListener(v -> {
                // Previous button click
                Toast.makeText(context, "It worked", Toast.LENGTH_SHORT).show();
            });

            nextBtn.setOnClickListener(v -> {
                // Next button click
                Toast.makeText(context, "It worked", Toast.LENGTH_SHORT).show();
            });
        }
    }
}