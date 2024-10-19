package com.example.uniride;

import android.os.Bundle;
import android.content.Intent;
import androidx.activity.EdgeToEdge;
import com.google.android.material.card.MaterialCardView;
import android.widget.TextView;

public class DriverHubActivity extends BottomNavigationActivity {
    private TextView newRequestsCount;
    private TextView todayBookingsCount;
    private MaterialCardView manageRidesButton;
    private MaterialCardView bookingRequestsButton;
    private MaterialCardView acceptedBookingsButton;
    private MaterialCardView todayScheduleButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_driver_hub);

        // Initialize views
        initializeViews();
        setupClickListeners();

        // TODO: Update these counts from your backend
        updateDashboardCounts(2, 3);
    }

    private void initializeViews() {
        newRequestsCount = findViewById(R.id.newRequestsCount);
        todayBookingsCount = findViewById(R.id.todayBookingsCount);
        manageRidesButton = findViewById(R.id.manageRidesButton);
        bookingRequestsButton = findViewById(R.id.bookingRequestsButton);
        acceptedBookingsButton = findViewById(R.id.acceptedBookingsButton);
        todayScheduleButton = findViewById(R.id.todayScheduleButton);
    }

    @Override
    protected int getSelectedItemId() {
        return R.id.driver;
    }

    private void setupClickListeners() {
        manageRidesButton.setOnClickListener(v -> {
            Intent i = new Intent(this, AccountMyRidesActivity.class);
            startActivity(i);
        });

        bookingRequestsButton.setOnClickListener(v -> {
            Intent i = new Intent(this, HomeBookingActivity.class);
            i.putExtra("bookingTypePassed", "requests");
            startActivity(i);
        });

        acceptedBookingsButton.setOnClickListener(v -> {
            Intent i = new Intent(this, HomeBookingActivity.class);
            i.putExtra("bookingTypePassed", "accepted");
            startActivity(i);
        });

        todayScheduleButton.setOnClickListener(v -> {
            Intent i = new Intent(this, HomeBookingActivity.class);
            i.putExtra("bookingTypePassed", "scheduled");
            startActivity(i);
        });
    }

    private void updateDashboardCounts(int newRequests, int todayBookings) {
        newRequestsCount.setText(String.valueOf(newRequests));
        todayBookingsCount.setText(String.valueOf(todayBookings));
    }
}