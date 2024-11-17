package com.example.uniride;

import android.os.Bundle;
import android.content.Intent;
import androidx.activity.EdgeToEdge;
import com.google.android.material.card.MaterialCardView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;

import android.widget.TextView;
import android.widget.Toast;

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

        // Check if user is a driver
        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
        if (currentUser != null) {
            FirebaseFirestore.getInstance()
                    .collection(MyFirestoreReferences.USERS_COLLECTION)
                    .document(currentUser.getUid())
                    .get()
                    .addOnSuccessListener(documentSnapshot -> {
                        if (documentSnapshot.exists()) {
                            boolean isDriver = documentSnapshot.getBoolean("isDriver") != null &&
                                    documentSnapshot.getBoolean("isDriver");
                            if (!isDriver) {
                                // User is not a driver, redirect to registration prompt
                                Intent intent = new Intent(this, DriverRegistrationPromptActivity.class);
                                startActivity(intent);
                                finish();
                                return;
                            }
                        }
                        // User is a driver, continue with normal Driver Hub setup
                        EdgeToEdge.enable(this);
                        setContentView(R.layout.activity_driver_hub);
                        initializeViews();
                        setupClickListeners();
                        updateDashboardCounts(2, 3);
                    })
                    .addOnFailureListener(e -> {
                        Toast.makeText(this, "Error checking driver status", Toast.LENGTH_SHORT).show();
                        finish();
                    });
        } else {
            // No user logged in, redirect to login
            Intent intent = new Intent(this, AccountLoginActivity.class);
            startActivity(intent);
            finish();
        }
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