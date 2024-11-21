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

import com.google.firebase.firestore.FirebaseFirestore;

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
    Button previousBtn;
    Button nextBtn;
    int currentIndex = 0;
    private FirebaseFirestore db;
    private ArrayList<BookingModel> populatedBookings;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.booking_home_details);

        db = FirebaseFirestore.getInstance();

        // Retrieve Intent data
        ArrayList<BookingModel> myBookingData = (ArrayList<BookingModel>) getIntent().getSerializableExtra("myBookingData");
        BookingModel selectedBooking = (BookingModel) getIntent().getSerializableExtra("selectedBooking");

        if (myBookingData != null && selectedBooking != null) {
            currentIndex = myBookingData.indexOf(selectedBooking);
            populatedBookings = new ArrayList<>(myBookingData.size());

            // Initialize the populated bookings array with nulls
            for (int i = 0; i < myBookingData.size(); i++) {
                populatedBookings.add(null);
            }

            initializeViews();
            populateAllBookings(myBookingData);

            previousBtn.setOnClickListener(v -> {
                if (currentIndex > 0) {
                    currentIndex--;
                    BookingModel booking = populatedBookings.get(currentIndex);
                    if (booking != null) {
                        displayBookingData(booking);
                    } else {
                        Toast.makeText(BookingHomeDetailsActivity.this, "Loading booking data...", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Toast.makeText(BookingHomeDetailsActivity.this, "No previous booking", Toast.LENGTH_SHORT).show();
                }
            });

            nextBtn.setOnClickListener(v -> {
                if (currentIndex < populatedBookings.size() - 1) {
                    currentIndex++;
                    BookingModel booking = populatedBookings.get(currentIndex);
                    if (booking != null) {
                        displayBookingData(booking);
                    } else {
                        Toast.makeText(BookingHomeDetailsActivity.this, "Loading booking data...", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Toast.makeText(BookingHomeDetailsActivity.this, "No more bookings", Toast.LENGTH_SHORT).show();
                }
            });
        }
    }

    private void populateAllBookings(ArrayList<BookingModel> bookings) {
        for (int i = 0; i < bookings.size(); i++) {
            final int index = i;
            BookingModel booking = bookings.get(i);

            booking.populateObjects(db, populatedBooking -> {
                runOnUiThread(() -> {
                    populatedBookings.set(index, populatedBooking);

                    // If this is the current booking, display it
                    if (index == currentIndex) {
                        displayBookingData(populatedBooking);
                    }

                    Log.d("Booking Population", "Populated booking " + index + ": " + populatedBooking.toString());
                });
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
        previousBtn = findViewById(R.id.previousBtn);
        nextBtn = findViewById(R.id.nextBtn);
    }

    // Helper method to display booking data for the current booking
    private void displayBookingData(BookingModel booking) {
        RideModel ride = booking.getRide();

        if (ride == null) {
            Log.e("BookingHomeDetails", "Ride is null for booking: " + booking.toString());
            return;
        }

        String fromLocation = ride.getFrom() != null ? ride.getFrom().getName() : "";
        String toLocation = ride.getTo() != null ? ride.getTo().getName() : "";

        priceTv.setText("P" + ride.getPrice());
        departureTimeTv.setText(ride.getDepartureTime());
        arrivalTimeTv.setText(ride.getArrivalTime());
        departureLocTv.setText(fromLocation);
        arrivalLocTv.setText(toLocation);
        capacityTv.setText(ride.getAvailableSeats() + " seats available");

        if (ride.getDriver() != null) {
            if (ride.getDriver().getCar() != null) {
                carImage.setImageResource(ride.getDriver().getCar().getCarImage());
                carModelTv.setText(ride.getDriver().getCar().getModel() + " " + ride.getDriver().getCar().getMake());
            }
            userNameTv.setText(ride.getDriver().getName());
            // ratingTv.setText("★ " + ride.getDriver().getRating());
        }

        if (booking.getPassenger() != null) {
            userImage.setImageResource(booking.getPassenger().getPfp());
        }
    }

    @Override
    protected int getSelectedItemId() {
        return R.id.home;
    }
}