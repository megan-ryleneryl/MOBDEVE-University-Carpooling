package com.example.uniride;

import android.app.TimePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;

public class RideEdit extends AppCompatActivity {

    private AutoCompleteTextView fromLocationInput, toLocationInput;
    private Spinner spinnerTotalSeats;
    private Button btnDepartureTime, btnArrivalTime, btnSubmit;
    private EditText etPrice;

    private FirebaseFirestore db;

    private String rideID; // To identify the ride being edited
    private String departureTime = "";
    private String arrivalTime = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ride_edit);

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

        // Retrieve and populate ride data
        Intent intent = getIntent();
        if (intent != null) {
            rideID = intent.getStringExtra("rideID");
            populateFields(intent);
        }

        // Set time pickers
        btnDepartureTime.setOnClickListener(v -> showTimePicker((time) -> {
            departureTime = time;
            btnDepartureTime.setText(time);
        }));

        btnArrivalTime.setOnClickListener(v -> showTimePicker((time) -> {
            arrivalTime = time;
            btnArrivalTime.setText(time);
        }));

        // Update button logic
        btnSubmit.setText("Update Ride");
        btnSubmit.setOnClickListener(v -> updateRide());
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
        String[] totalSeats = {"1", "2", "3", "4", "5"};
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

    private void populateFields(Intent intent) {
        String fromLocation = intent.getStringExtra("fromLocation");
        String toLocation = intent.getStringExtra("toLocation");
        String totalSeats = intent.getStringExtra("totalSeats");
        String price = intent.getStringExtra("price");
        departureTime = intent.getStringExtra("departureTime");
        arrivalTime = intent.getStringExtra("arrivalTime");

        fromLocationInput.setText(fromLocation);
        toLocationInput.setText(toLocation);
        etPrice.setText(price);
        btnDepartureTime.setText(departureTime);
        btnArrivalTime.setText(arrivalTime);

        // Select the spinner value
        ArrayAdapter<String> adapter = (ArrayAdapter<String>) spinnerTotalSeats.getAdapter();
        int spinnerPosition = adapter.getPosition(totalSeats);
        spinnerTotalSeats.setSelection(spinnerPosition);
    }

    private void updateRide() {
        String fromLocation = fromLocationInput.getText().toString();
        String toLocation = toLocationInput.getText().toString();
        String totalSeatsStr = spinnerTotalSeats.getSelectedItem().toString();
        String priceStr = etPrice.getText().toString().trim();

        if (TextUtils.isEmpty(fromLocation) || TextUtils.isEmpty(toLocation)
                || TextUtils.isEmpty(departureTime) || TextUtils.isEmpty(arrivalTime)
                || TextUtils.isEmpty(totalSeatsStr) || TextUtils.isEmpty(priceStr)) {
            Toast.makeText(this, "Please fill all fields", Toast.LENGTH_SHORT).show();
            return;
        }

        double price = Double.parseDouble(priceStr);

        // Create a map for the updated ride data
        Map<String, Object> updatedRide = new HashMap<>();
        updatedRide.put("fromLocation", fromLocation);
        updatedRide.put("toLocation", toLocation);
        updatedRide.put("totalSeats", Integer.parseInt(totalSeatsStr));
        updatedRide.put("price", price);
        updatedRide.put("departureTime", departureTime);
        updatedRide.put("arrivalTime", arrivalTime);

        // Update the ride in Firestore
        db.collection("rides").document(rideID)
                .update(updatedRide)
                .addOnSuccessListener(aVoid -> {
                    Toast.makeText(RideEdit.this, "Ride updated successfully!", Toast.LENGTH_SHORT).show();
                    finish();
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(RideEdit.this, "Error: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                });
    }

    interface TimePickerCallback {
        void onTimeSelected(String time);
    }
}
