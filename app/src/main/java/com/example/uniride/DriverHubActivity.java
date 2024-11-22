package com.example.uniride;

import android.os.Bundle;
import android.content.Intent;
import androidx.activity.EdgeToEdge;
import com.google.android.material.card.MaterialCardView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.DocumentSnapshot;

import android.widget.TextView;
import android.widget.Toast;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class DriverHubActivity extends BottomNavigationActivity {
    private TextView newRequestsCount;
    private TextView todayBookingsCount;
    private MaterialCardView manageRidesButton;
    private MaterialCardView bookingRequestsButton;
    private MaterialCardView acceptedBookingsButton;
    private MaterialCardView todayScheduleButton;
    private FirebaseFirestore db;
    private FirebaseUser currentUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_driver_hub);

        db = FirebaseFirestore.getInstance();
        currentUser = FirebaseAuth.getInstance().getCurrentUser();

        if (currentUser != null) {
            db.collection(MyFirestoreReferences.USERS_COLLECTION)
                    .document(currentUser.getUid())
                    .get()
                    .addOnSuccessListener(documentSnapshot -> {
                        if (documentSnapshot.exists()) {
                            boolean isDriver = documentSnapshot.getBoolean("isDriver") != null &&
                                    documentSnapshot.getBoolean("isDriver");
                            if (!isDriver) {
                                Intent intent = new Intent(this, DriverRegistrationPromptActivity.class);
                                startActivity(intent);
                                finish();
                                return;
                            }

                            initializeViews();
                            setupClickListeners();
                            updateDashboardStats();
                        }
                    })
                    .addOnFailureListener(e -> {
                        Toast.makeText(this, "Error checking driver status", Toast.LENGTH_SHORT).show();
                        finish();
                    });
        } else {
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

    private void updateDashboardStats() {
        String uid = currentUser.getUid();

        db.collection(MyFirestoreReferences.USERS_COLLECTION)
                .document(uid)
                .get()
                .addOnSuccessListener(userDoc -> {
                    if (userDoc.exists()) {
                        int driverId = ((Long) userDoc.get("userID")).intValue();
                        SimpleDateFormat dateFormat = new SimpleDateFormat("d/M/yyyy");
                        String today = dateFormat.format(new Date());

                        db.collection(MyFirestoreReferences.RIDES_COLLECTION)
                                .whereEqualTo("driverID", driverId)
                                .get()
                                .addOnSuccessListener(ridesSnapshot -> {
                                    List<Integer> rideIds = new ArrayList<>();
                                    for (DocumentSnapshot rideDoc : ridesSnapshot.getDocuments()) {
                                        rideIds.add(((Long) rideDoc.get("rideID")).intValue());
                                    }

                                    if (!rideIds.isEmpty()) {
                                        db.collection(MyFirestoreReferences.BOOKINGS_COLLECTION)
                                                .whereIn("rideID", rideIds)
                                                .get()
                                                .addOnSuccessListener(bookingsSnapshot -> {
                                                    int newRequests = 0;
                                                    int todayBookings = 0;

                                                    for (DocumentSnapshot bookingDoc : bookingsSnapshot.getDocuments()) {
                                                        BookingModel booking = BookingModel.fromMap(bookingDoc.getData());

                                                        if (!booking.isAccepted() && !booking.isPaymentComplete() && !booking.isBookingDone()) {
                                                            newRequests++;
                                                        }

                                                        if (booking.isAccepted() && booking.getDate().equals(today)) {
                                                            todayBookings++;
                                                        }
                                                    }
                                                    updateDashboardCounts(newRequests, todayBookings);
                                                });
                                    } else {
                                        updateDashboardCounts(0, 0);
                                    }
                                });
                    }
                });
    }

    private void updateDashboardCounts(int newRequests, int todayBookings) {
        if (newRequestsCount != null && todayBookingsCount != null) {
            newRequestsCount.setText(String.valueOf(newRequests));
            todayBookingsCount.setText(String.valueOf(todayBookings));
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (currentUser != null) {
            updateDashboardStats();
        }
    }
}