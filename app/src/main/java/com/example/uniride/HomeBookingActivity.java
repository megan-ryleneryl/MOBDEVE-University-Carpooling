package com.example.uniride;

import android.content.Intent;
import android.content.Context;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import de.hdodenhof.circleimageview.CircleImageView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.List;

public class HomeBookingActivity extends BottomNavigationActivity {

    TextView titleText;
    String bookingType;

    private RecyclerView recyclerView;
    private MyHomeBookingAdapter adapter;
    private List<BookingModel> bookingList;
    private FirebaseFirestore db;
    private FirebaseAuth mAuth;
    private FirebaseUser currentUser;

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

        db = FirebaseFirestore.getInstance();
        mAuth = FirebaseAuth.getInstance();
        currentUser = mAuth.getCurrentUser();

        if (currentUser == null) {
            startActivity(new Intent(this, AccountLoginActivity.class));
            finish();
            return;
        }

        recyclerView = findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        bookingList = new ArrayList<>();
        adapter = new MyHomeBookingAdapter(bookingList, bookingType, this);
        recyclerView.setAdapter(adapter);

        loadDriverBookings();
    }

    private void loadDriverBookings() {
        // Step 1: Get user ID from Firestore
        db.collection(MyFirestoreReferences.USERS_COLLECTION)
                .document(currentUser.getUid())
                .get()
                .addOnSuccessListener(documentSnapshot -> {
                    if (documentSnapshot.exists()) {
                        int userId = ((Long) documentSnapshot.get("userID")).intValue();

                        // Step 2: Query rides for the driver
                        db.collection(MyFirestoreReferences.RIDES_COLLECTION)
                                .whereEqualTo("driverID", userId)
                                .get()
                                .addOnSuccessListener(ridesSnapshot -> {
                                    List<String> rideIds = new ArrayList<>();
                                    for (QueryDocumentSnapshot rideDoc : ridesSnapshot) {
                                        String rideId = rideDoc.getString("ride_id");
                                        rideIds.add(rideId);
                                    }

                                    if (!rideIds.isEmpty()) {
                                        fetchBookingsForRideIds(rideIds);
                                    } else {
                                        bookingList.clear();
                                        adapter.notifyDataSetChanged();
                                        Toast.makeText(HomeBookingActivity.this,
                                                "No rides found for this driver.",
                                                Toast.LENGTH_SHORT).show();
                                    }
                                })
                                .addOnFailureListener(e -> {
                                    Toast.makeText(HomeBookingActivity.this,
                                            "Error fetching rides: " + e.getMessage(),
                                            Toast.LENGTH_SHORT).show();
                                });
                    }
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(HomeBookingActivity.this,
                            "Error fetching user data: " + e.getMessage(),
                            Toast.LENGTH_SHORT).show();
                });
    }

    private void fetchBookingsForRideIds(List<String> rideIds) {
        List<List<String>> chunks = chunkList(rideIds, 10);

        bookingList.clear();

        for (List<String> chunk : chunks) {
            db.collection(MyFirestoreReferences.BOOKINGS_COLLECTION)
                    .whereIn("ride_id", chunk)
                    .get()
                    .addOnSuccessListener(bookingsSnapshot -> {
                        for (QueryDocumentSnapshot bookingDoc : bookingsSnapshot) {
                            BookingModel booking = BookingModel.fromMap(bookingDoc.getData());
                            bookingList.add(booking);
                        }

                        // Sort by rideID after adding all data
                        //Collections.sort(bookingList, (b1, b2) -> b1.getDate().compareTo(b2.getDate()));
                        adapter.notifyDataSetChanged();
                    })
                    .addOnFailureListener(e -> {
                        Toast.makeText(HomeBookingActivity.this,
                                "Error fetching bookings: " + e.getMessage(),
                                Toast.LENGTH_SHORT).show();
                    });
        }
    }

    private List<List<String>> chunkList(List<String> list, int chunkSize) {
        List<List<String>> chunks = new ArrayList<>();
        for (int i = 0; i < list.size(); i += chunkSize) {
            chunks.add(list.subList(i, Math.min(list.size(), i + chunkSize)));
        }
        return chunks;
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (currentUser != null) {
            loadDriverBookings();
        }
    }

    @Override
    protected int getSelectedItemId() {
        return R.id.driver;
    }
}