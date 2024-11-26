package com.example.uniride;

import android.app.TimePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;

public class RideCreate extends AppCompatActivity {

    private AutoCompleteTextView fromLocationInput, toLocationInput;
    private Spinner spinnerTotalSeats;
    private Button btnDepartureTime, btnArrivalTime, btnSubmit;
    private EditText etPrice;

    private FirebaseFirestore db;

    private String departureTime = "";
    private String arrivalTime = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ride_create);

        // Initialize views
        fromLocationInput = findViewById(R.id.originInput);
        toLocationInput = findViewById(R.id.destinationInput);
        spinnerTotalSeats = findViewById(R.id.spinner_total_seats);
        btnDepartureTime = findViewById(R.id.btn_departure_time);
        btnArrivalTime = findViewById(R.id.btn_arrival_time);
        btnSubmit = findViewById(R.id.btn_submit);
        etPrice = findViewById(R.id.et_price);

        // Initialize Firebase Firestore
        db = FirebaseFirestore.getInstance();

        // Populate AutoCompleteTextViews with locations
        setupAutoCompleteFields();

        // Populate total seats spinner
        setupTotalSeatsSpinner();

        // Set time pickers
        btnDepartureTime.setOnClickListener(v -> showTimePicker((time) -> {
            departureTime = time;
            btnDepartureTime.setText(time);
        }));

        btnArrivalTime.setOnClickListener(v -> showTimePicker((time) -> {
            arrivalTime = time;
            btnArrivalTime.setText(time);
        }));

        // Submit button
        btnSubmit.setOnClickListener(v -> createRide());
    }

    private void setupAutoCompleteFields() {
        ArrayList<LocationModel> locations = DataGenerator.loadLocationData(); // Replace with your actual data loading method
        ArrayAdapter<LocationModel> adapter = new ArrayAdapter<>(this, android.R.layout.simple_dropdown_item_1line, locations);

        fromLocationInput.setAdapter(adapter);
        toLocationInput.setAdapter(adapter);

        fromLocationInput.setThreshold(0); // Show dropdown immediately
        toLocationInput.setThreshold(0);

        fromLocationInput.setOnClickListener(v -> fromLocationInput.showDropDown());
        toLocationInput.setOnClickListener(v -> toLocationInput.showDropDown());
    }

    private void setupTotalSeatsSpinner() {
        String[] totalSeats = {"1", "2", "3", "4", "5", "6"};
        ArrayAdapter<String> seatsAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_dropdown_item, totalSeats);
        spinnerTotalSeats.setAdapter(seatsAdapter);
    }

    private void showTimePicker(TimePickerCallback callback) {
        Calendar calendar = Calendar.getInstance();
        int hour = calendar.get(Calendar.HOUR_OF_DAY);
        int minute = calendar.get(Calendar.MINUTE);

        TimePickerDialog timePickerDialog = new TimePickerDialog(this, (view, hourOfDay, minuteOfHour) -> {
            String time = String.format("%02d:%02d %s",
                    (hourOfDay % 12 == 0 ? 12 : hourOfDay % 12), // Convert 24-hour to 12-hour
                    minuteOfHour,
                    (hourOfDay >= 12 ? "PM" : "AM"));
            callback.onTimeSelected(time);
        }, hour, minute, false);

        timePickerDialog.show();
    }

    private void createRide() {
        // Get inputs
        String fromLocation = fromLocationInput.getText().toString();
        String toLocation = toLocationInput.getText().toString();
        String totalSeatsStr = spinnerTotalSeats.getSelectedItem().toString();
        String priceStr = etPrice.getText().toString().trim();

        // Validate inputs
        if (TextUtils.isEmpty(fromLocation) || TextUtils.isEmpty(toLocation)
                || TextUtils.isEmpty(departureTime) || TextUtils.isEmpty(arrivalTime)
                || TextUtils.isEmpty(totalSeatsStr) || TextUtils.isEmpty(priceStr)) {
            Toast.makeText(this, "Please fill all fields", Toast.LENGTH_SHORT).show();
            return;
        }

        int totalSeats = Integer.parseInt(totalSeatsStr);
        double price = Double.parseDouble(priceStr);

        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
        if (currentUser == null) {
            Toast.makeText(RideCreate.this, "User not logged in", Toast.LENGTH_SHORT).show();
            return;
        }

        String currentUID = currentUser.getUid();

        // Fetch driverID and location data as before
        DocumentReference userDocRef = FirebaseFirestore.getInstance().collection("users").document(currentUID);
        userDocRef.get().addOnSuccessListener(documentSnapshot -> {
            if (documentSnapshot.exists()) {
                String driverID = String.valueOf(documentSnapshot.getLong("userID"));

                db.collection("locations").whereEqualTo("name", fromLocation).get()
                        .addOnSuccessListener(fromSnapshot -> {
                            if (!fromSnapshot.isEmpty()) {
                                Map<String, Object> fromLocationData = fromSnapshot.getDocuments().get(0).getData();
                                int fromLocationID = ((Long) fromLocationData.get("locationID")).intValue();
                                boolean fromIsUniversity = (boolean) fromLocationData.get("isUniversity");

                                db.collection("locations").whereEqualTo("name", toLocation).get()
                                        .addOnSuccessListener(toSnapshot -> {
                                            if (!toSnapshot.isEmpty()) {
                                                Map<String, Object> toLocationData = toSnapshot.getDocuments().get(0).getData();
                                                int toLocationID = ((Long) toLocationData.get("locationID")).intValue();
                                                boolean toIsUniversity = (boolean) toLocationData.get("isUniversity");

                                                String rideType = "neither";
                                                if (fromIsUniversity && !toIsUniversity) {
                                                    rideType = "fromUniversity";
                                                } else if (!fromIsUniversity && toIsUniversity) {
                                                    rideType = "toUniversity";
                                                }

                                                // Fetch the latest rideID and create the ride
                                                getLatestRideIDAndCreateRide(
                                                        Integer.parseInt(driverID), fromLocationID, toLocationID, rideType,
                                                        departureTime, arrivalTime, totalSeats, price, true
                                                );

                                            } else {
                                                Toast.makeText(RideCreate.this, "To location not found in database!", Toast.LENGTH_SHORT).show();
                                            }
                                        })
                                        .addOnFailureListener(e -> Toast.makeText(RideCreate.this, "Error fetching to location: " + e.getMessage(), Toast.LENGTH_SHORT).show());
                            } else {
                                Toast.makeText(RideCreate.this, "From location not found in database!", Toast.LENGTH_SHORT).show();
                            }
                        })
                        .addOnFailureListener(e -> Toast.makeText(RideCreate.this, "Error fetching from location: " + e.getMessage(), Toast.LENGTH_SHORT).show());
            } else {
                Toast.makeText(RideCreate.this, "Error: User data not found", Toast.LENGTH_SHORT).show();
            }
        }).addOnFailureListener(e -> Toast.makeText(RideCreate.this, "Error fetching user data", Toast.LENGTH_SHORT).show());
    }


    private void getLatestRideIDAndCreateRide(int driverID, int fromLocationID, int toLocationID, String rideType,
                                              String departureTime, String arrivalTime, int totalSeats, double price,
                                              boolean isAvailable) {
        db.collection("rides")
                .orderBy("rideID", com.google.firebase.firestore.Query.Direction.DESCENDING)
                .limit(1)
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    int latestRideID = 0; // Default if no rides exist
                    if (!queryDocumentSnapshots.isEmpty()) {
                        latestRideID = queryDocumentSnapshots.getDocuments()
                                .get(0)
                                .getLong("rideID")
                                .intValue();
                    }

                    // Increment rideID
                    int newRideID = latestRideID + 1;

                    // Create a new RideModel with the new rideID
                    RideModel ride = new RideModel(
                            newRideID, driverID, fromLocationID, toLocationID, rideType,
                            departureTime, arrivalTime, totalSeats, totalSeats, price, isAvailable
                    );

                    // Convert ride to map and save to Firestore
                    saveRideToFirestore(ride.toMap());
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(RideCreate.this, "Error fetching latest ride ID: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                });
    }


    private void saveRideToFirestore(Map<String, Object> rideMap) {
        db.collection("rides")
                .add(rideMap)
                .addOnSuccessListener(documentReference -> {
                    Toast.makeText(RideCreate.this, "Ride created successfully!", Toast.LENGTH_SHORT).show();
                    finish(); // Close activity
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(RideCreate.this, "Error: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                });
    }

    public void checkRides(View view) {
        Intent i = new Intent(this, DriverHubActivity.class);
        i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(i);
    }




    interface TimePickerCallback {
        void onTimeSelected(String time);
    }
}
