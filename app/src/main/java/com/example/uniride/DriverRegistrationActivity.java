package com.example.uniride;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;

import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;

public class DriverRegistrationActivity extends BottomNavigationActivity {
    private TextInputEditText carMakeInput;
    private TextInputEditText carModelInput;
    private TextInputEditText plateNumberInput;
    private TextInputEditText licenseNumberInput;
    private TextInputEditText licenseExpiryInput;
    private CheckBox termsCheckbox;
    private Button submitButton;

    private FirebaseAuth mAuth;
    private FirebaseFirestore db;
    private FirebaseUser currentUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_driver_registration);

        // Initialize Firebase
        mAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();
        currentUser = mAuth.getCurrentUser();

        if (currentUser == null) {
            startActivity(new Intent(this, AccountLoginActivity.class));
            finish();
            return;
        }

        initializeViews();
        setupListeners();
    }

    private void initializeViews() {
        carMakeInput = findViewById(R.id.carMakeInput);
        carModelInput = findViewById(R.id.carModelInput);
        plateNumberInput = findViewById(R.id.plateNumberInput);
        licenseNumberInput = findViewById(R.id.licenseNumberInput);
        licenseExpiryInput = findViewById(R.id.licenseExpiryInput);
        termsCheckbox = findViewById(R.id.termsCheckbox);
        submitButton = findViewById(R.id.submitButton);
    }

    private void setupListeners() {
        // Setup date picker for license expiry
        licenseExpiryInput.setOnClickListener(v -> showDatePicker());

        // Enable/disable submit button based on terms checkbox
        termsCheckbox.setOnCheckedChangeListener((buttonView, isChecked) ->
                submitButton.setEnabled(isChecked));

        // Submit button click handler
        submitButton.setOnClickListener(v -> {
            if (validateInputs()) {
                submitRegistration();
            }
        });
    }

    private void showDatePicker() {
        Calendar calendar = Calendar.getInstance();
        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH);
        int day = calendar.get(Calendar.DAY_OF_MONTH);

        DatePickerDialog datePickerDialog = new DatePickerDialog(
                this,
                (view, selectedYear, selectedMonth, selectedDay) -> {
                    String date = selectedDay + "/" + (selectedMonth + 1) + "/" + selectedYear;
                    licenseExpiryInput.setText(date);
                },
                year,
                month,
                day
        );

        // Set minimum date to today
        datePickerDialog.getDatePicker().setMinDate(System.currentTimeMillis());
        datePickerDialog.show();
    }

    private boolean validateInputs() {
        boolean isValid = true;

        if (TextUtils.isEmpty(carMakeInput.getText())) {
            carMakeInput.setError("Car make is required");
            isValid = false;
        }

        if (TextUtils.isEmpty(carModelInput.getText())) {
            carModelInput.setError("Car model is required");
            isValid = false;
        }

        if (TextUtils.isEmpty(plateNumberInput.getText())) {
            plateNumberInput.setError("Plate number is required");
            isValid = false;
        }

        if (TextUtils.isEmpty(licenseNumberInput.getText())) {
            licenseNumberInput.setError("License number is required");
            isValid = false;
        }

        if (TextUtils.isEmpty(licenseExpiryInput.getText())) {
            licenseExpiryInput.setError("License expiry date is required");
            isValid = false;
        }

        return isValid;
    }

    private void submitRegistration() {
        // First, create the car document
        Map<String, Object> carData = new HashMap<>();
        carData.put("make", carMakeInput.getText().toString().trim());
        carData.put("model", carModelInput.getText().toString().trim());
        carData.put("plateNumber", plateNumberInput.getText().toString().trim());

        // Query to get the next available carID
        db.collection(MyFirestoreReferences.CARS_COLLECTION)
                .orderBy("carID", Query.Direction.DESCENDING)
                .limit(1)
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    int nextCarId = 20001; // Default starting ID

                    if (!queryDocumentSnapshots.isEmpty()) {
                        Long highestId = queryDocumentSnapshots.getDocuments()
                                .get(0).getLong("carID");
                        if (highestId != null) {
                            nextCarId = highestId.intValue() + 1;
                        }
                    }

                    carData.put("carID", nextCarId);
                    carData.put("carImage", R.drawable.sedan); // Default car image

                    // Save car data and update user
                    db.collection(MyFirestoreReferences.CARS_COLLECTION)
                            .add(carData)
                            .addOnSuccessListener(documentReference -> {
                                // Update the user document to mark as driver and add car reference
                                Map<String, Object> updates = new HashMap<>();
                                updates.put("isDriver", true);
                                updates.put("car", carData);
                                updates.put("licenseNumber", licenseNumberInput.getText().toString().trim());
                                updates.put("licenseExpiry", licenseExpiryInput.getText().toString().trim());

                                db.collection(MyFirestoreReferences.USERS_COLLECTION)
                                        .document(currentUser.getUid())
                                        .update(updates)
                                        .addOnSuccessListener(aVoid -> {
                                            Toast.makeText(DriverRegistrationActivity.this,
                                                    "Registration successful!", Toast.LENGTH_SHORT).show();
                                            // Navigate to Driver Hub
                                            Intent intent = new Intent(DriverRegistrationActivity.this,
                                                    DriverHubActivity.class);
                                            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK |
                                                    Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                            startActivity(intent);
                                            finish();
                                        })
                                        .addOnFailureListener(e -> {
                                            Toast.makeText(DriverRegistrationActivity.this,
                                                    "Error updating user data: " + e.getMessage(),
                                                    Toast.LENGTH_SHORT).show();
                                        });
                            })
                            .addOnFailureListener(e -> {
                                Toast.makeText(DriverRegistrationActivity.this,
                                        "Error saving car data: " + e.getMessage(),
                                        Toast.LENGTH_SHORT).show();
                            });
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(DriverRegistrationActivity.this,
                            "Error generating car ID: " + e.getMessage(),
                            Toast.LENGTH_SHORT).show();
                });
    }

    @Override
    protected int getSelectedItemId() {
        return R.id.driver;
    }
}