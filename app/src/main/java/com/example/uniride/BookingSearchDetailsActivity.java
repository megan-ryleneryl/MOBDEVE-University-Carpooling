package com.example.uniride;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

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
    Button cancelBtn;
    Button confirmBtn;
    private FirebaseFirestore db;
    private BookingModel selectedBooking;
    private String currentUserID;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.booking_search_details);

        db = FirebaseFirestore.getInstance();

        // Retrieve Intent data
        selectedBooking = (BookingModel) getIntent().getSerializableExtra("selectedBooking");
        currentUserID = getIntent().getStringExtra("currentUserID");

        if (selectedBooking != null) {
            selectedBooking.populateObjects(db, populatedBooking -> {
                runOnUiThread(() -> {
                    initViews(populatedBooking);
                });
            });
        }
    }

    private void initViews(BookingModel selectedBooking) {
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
        cancelBtn = findViewById(R.id.cancelBtn);
        confirmBtn = findViewById(R.id.confirmBtn);

        RideModel ride = selectedBooking.getRide();

        // get the needed data from within selectedBooking
        priceTv.setText("P" + ride.getPrice());
        departureTimeTv.setText(ride.getDepartureTime());
        arrivalTimeTv.setText(ride.getArrivalTime());
        departureLocTv.setText(ride.getFrom().getName());
        arrivalLocTv.setText(ride.getTo().getName());
        carImage.setImageResource(ride.getDriver().getCar().getCarImage());
        capacityTv.setText(ride.getAvailableSeats() + " seats available");
        carModelTv.setText(ride.getDriver().getCar().getModel() + " " + ride.getDriver().getCar().getMake());
        userImage.setImageResource(ride.getDriver().getPfp());
        userNameTv.setText(ride.getDriver().getName());
        //ratingTv.setText("★ " + selectedBooking.getRide().getDriver().getRating());

        cancelBtn.setOnClickListener(v -> {
            finish();
        });

        confirmBtn.setOnClickListener(v -> {
            // Find the next available bookingID
            db.collection(MyFirestoreReferences.BOOKINGS_COLLECTION)
                    .orderBy("bookingID", Query.Direction.DESCENDING)
                    .limit(1)
                    .get()
                    .addOnSuccessListener(querySnapshot -> {
                        int nextBookingID;
                        if (!querySnapshot.isEmpty()) {
                            DocumentSnapshot lastDoc = querySnapshot.getDocuments().get(0);
                            int lastBookingID = ((Long) lastDoc.get("bookingID")).intValue();
                            nextBookingID = lastBookingID + 1;
                        } else {
                            nextBookingID = 50001; // Starting bookingID if no bookings exist
                        }

                        // Find the current user's userID
                        db.collection(MyFirestoreReferences.USERS_COLLECTION)
                                .whereEqualTo("email", FirebaseAuth.getInstance().getCurrentUser().getEmail())
                                .get()
                                .addOnSuccessListener(userSnapshot -> {
                                    if (!userSnapshot.isEmpty()) {
                                        DocumentSnapshot userDoc = userSnapshot.getDocuments().get(0);
                                        int userID = ((Long) userDoc.get("userID")).intValue();

                                        // Create and save the booking
                                        BookingModel bookingToSave = new BookingModel(
                                                nextBookingID,
                                                selectedBooking.getRide().getRideID(),
                                                userID,
                                                selectedBooking.getDate()
                                        );

                                        // Generate a unique document ID
                                        String documentId = db.collection(MyFirestoreReferences.BOOKINGS_COLLECTION).document().getId();

                                        // Save the booking
                                        db.collection(MyFirestoreReferences.BOOKINGS_COLLECTION)
                                                .document(documentId)
                                                .set(bookingToSave.toMap())
                                                .addOnFailureListener(e -> {
                                                    Toast.makeText(BookingSearchDetailsActivity.this,
                                                            "Booking failed: " + e.getMessage(),
                                                            Toast.LENGTH_SHORT).show();
                                                });

                                        // Navigate to confirmation
                                        Intent i = new Intent(BookingSearchDetailsActivity.this, BookingConfirmActivity.class);
                                        i.putExtra("bookingToSave", bookingToSave);
//                                      Log.d("CodeDebug", bookingToSave.toString());
                                        startActivity(i);

//                                                // TODO: @Ken, this is the nested lambda function to reduce the available seats
//                                                .addOnSuccessListener(documentReference -> {
//                                                    // Reduce available seats
//                                                    RideModel selectedRide = selectedBooking.getRide();
//                                                    selectedRide.setAvailableSeats(1);
//
//                                                    // Update the ride in Firestore
//                                                    db.collection(MyFirestoreReferences.RIDES_COLLECTION)
//                                                            .whereEqualTo("rideID", selectedRide.getRideID())
//                                                            .get()
//                                                            .addOnSuccessListener(rideSnapshot -> {
//                                                                if (!rideSnapshot.isEmpty()) {
//                                                                    DocumentSnapshot rideDoc = rideSnapshot.getDocuments().get(0);
//                                                                    rideDoc.getReference().update("availableSeats", selectedRide.getAvailableSeats());
//                                                                }
//                                                            });
//                                                })
//
                                    }
                                });
                    });
        });
    }
}