package com.example.uniride;

import android.content.Context;
import android.content.Intent;
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

import com.google.firebase.firestore.FirebaseFirestore;

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
    private FirebaseFirestore db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.booking_search_details);

        db = FirebaseFirestore.getInstance();

        // Retrieve Intent data
        BookingModel selectedBooking = (BookingModel) getIntent().getSerializableExtra("selectedBooking");

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
        ratingTv = findViewById(R.id.ratingTv);
        cancelBtn = findViewById(R.id.cancelBtn);
        confirmBtn = findViewById(R.id.confirmBtn);

        Log.d("CodeDebug", selectedBooking.toString());
        RideModel ride = selectedBooking.getRide();
        Log.d("CodeDebug", String.valueOf(ride != null));

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

//        confirmBtn.setOnClickListener(v -> {
//            // 1. Prepare booking data
//            String bookingId = db.collection(MyFirestoreReferences.BOOKINGS_COLLECTION).document().getId();
//
//            // Create a booking document
//            BookingModel bookingToSave = new BookingModel(
//                    bookingId,
//                    selectedBooking.getRide().getId(), // rideID
//                    getCurrentUserId(), // passengerID (need to implement method to get current user's ID)
//                    getCurrentDate(), // date
//                    false, // isPaymentComplete
//                    false  // isBookingDone
//            );
//
//            // 2. Save to Firestore
//            db.collection(MyFirestoreReferences.BOOKINGS_COLLECTION)
//                    .document(bookingId)
//                    .set(bookingToSave)
//                    .addOnSuccessListener(documentReference -> {
//                        // 3. Pass data to confirmation screen
//                        Intent i = new Intent(BookingSearchDetailsActivity.this, BookingConfirmActivity.class);
//                        i.putExtra("selectedBooking", selectedBooking);
//                        startActivity(i);
//                    })
//                    .addOnFailureListener(e -> {
//                        Toast.makeText(this, "Booking failed: " + e.getMessage(), Toast.LENGTH_SHORT).show();
//                    });
//        });
    }
}