package com.example.uniride;

import android.content.Context;
import android.os.Bundle;
import android.util.Log;
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

public class BookingHomeDetailsActivity extends BottomNavigationActivity {
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
    int currentIndex = 0;

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
            currentIndex = myBookingData.indexOf(selectedBooking);
            initializeViews();
            displayBookingData(myBookingData.get(currentIndex));

            previousBtn.setOnClickListener(v -> {
                if (currentIndex > 0) {
                    currentIndex--;
                    displayBookingData(myBookingData.get(currentIndex));
                } else {
                    Toast.makeText(BookingHomeDetailsActivity.this, "No previous booking", Toast.LENGTH_SHORT).show();
                }
            });

            nextBtn.setOnClickListener(v -> {
                if (currentIndex < myBookingData.size() - 1) {
                    currentIndex++;
                    displayBookingData(myBookingData.get(currentIndex));
                } else {
                    Toast.makeText(BookingHomeDetailsActivity.this, "No more bookings", Toast.LENGTH_SHORT).show();
                }
            });
        }
    }

    // Helper method to initialize views
    private void initializeViews() {
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
        previousBtn = findViewById(R.id.previousBtn);
        nextBtn = findViewById(R.id.nextBtn);
    }

    // Helper method to display booking data for the current booking
    private void displayBookingData(BookingModel booking) {
        priceTv.setText("P" + booking.getRide().getPrice());
        departureTimeTv.setText(booking.getRide().getDepartureTime());
        arrivalTimeTv.setText(booking.getRide().getArrivalTime());
        departureLocTv.setText(booking.getRide().getFrom().getName());
        arrivalLocTv.setText(booking.getRide().getTo().getName());
        carImage.setImageResource(booking.getRide().getDriver().getCar().getCarImage());
        capacityTv.setText(booking.getRide().getAvailableSeats() + " seats available");
        carModelTv.setText(booking.getRide().getDriver().getCar().getModel() + " " + booking.getRide().getDriver().getCar().getMake());
        userImage.setImageResource(booking.getPassenger().getPfp());
        userNameTv.setText(booking.getRide().getDriver().getName());
        ratingTv.setText("★ " + booking.getRide().getDriver().getRating());
    }

    @Override
    protected int getSelectedItemId() {
        return R.id.home;
    }
}