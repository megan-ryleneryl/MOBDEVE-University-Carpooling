package com.example.uniride;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.Spinner;
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
    private TextInputEditText seatingCapacityInput;

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
        setupCarTypeSpinner();
    }

    private void initializeViews() {
        carMakeInput = findViewById(R.id.carMakeInput);
        carModelInput = findViewById(R.id.carModelInput);
        plateNumberInput = findViewById(R.id.plateNumberInput);
        licenseNumberInput = findViewById(R.id.licenseNumberInput);
        licenseExpiryInput = findViewById(R.id.licenseExpiryInput);
        termsCheckbox = findViewById(R.id.termsCheckbox);
        submitButton = findViewById(R.id.submitButton);
        seatingCapacityInput = findViewById(R.id.seatingCapacityInput);
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

        seatingCapacityInput.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (TextUtils.isEmpty(s.toString().trim())) {
                    seatingCapacityInput.setError("Seating capacity is required");
                } else {
                    int capacity = Integer.parseInt(s.toString().trim());
                    if (capacity < 1 || capacity > 8) {
                        seatingCapacityInput.setError("Seating capacity must be between 1 and 6");
                    } else {
                        seatingCapacityInput.setError(null);
                    }
                }
            }

            @Override
            public void afterTextChanged(Editable s) {}
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

    private void setupCarTypeSpinner() {
        Spinner carTypeSpinner = findViewById(R.id.carTypeSpinner);
        ImageView carPreviewImage = findViewById(R.id.carPreviewImage);

        ArrayAdapter<String> adapter = new ArrayAdapter<>(
                this,
                android.R.layout.simple_spinner_item,
                CarTypeUtils.getCarTypes()
        );
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        carTypeSpinner.setAdapter(adapter);

        carTypeSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String selectedType = parent.getItemAtPosition(position).toString();
                carPreviewImage.setImageResource(CarTypeUtils.getCarImageResource(selectedType));
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {}
        });
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

        if (TextUtils.isEmpty(seatingCapacityInput.getText())) {
            seatingCapacityInput.setError("Seating capacity is required");
            isValid = false;
        }

        int capacity = Integer.parseInt(seatingCapacityInput.getText().toString().trim());
        if (capacity < 1 || capacity > 8) {
            seatingCapacityInput.setError("Seating capacity must be between 1 and 8");
            isValid = false;
        }

        return isValid;
    }

    private void submitRegistration() {
        // Create the car document
        CarModel car = new CarModel();
        car.setMake(carMakeInput.getText().toString().trim());
        car.setModel(carModelInput.getText().toString().trim());
        car.setPlateNumber(plateNumberInput.getText().toString().trim());
        car.setSeatingCapacity(Integer.parseInt(seatingCapacityInput.getText().toString().trim()));

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

                    car.setCarID(nextCarId);

                    String selectedCarType = ((Spinner)findViewById(R.id.carTypeSpinner))
                            .getSelectedItem().toString();
                    int carImageResource = CarTypeUtils.getCarImageResource(selectedCarType);
                    car.setCarImage(carImageResource);

                    // Save the car data
                    db.collection(MyFirestoreReferences.CARS_COLLECTION)
                            .add(car.toMap())
                            .addOnSuccessListener(documentReference -> {
                                // Update the user document to mark as driver and add car reference
                                Map<String, Object> updates = new HashMap<>();
                                updates.put("isDriver", true);
                                updates.put("carID", car.getCarID());
                                updates.put("licenseNumber", licenseNumberInput.getText().toString().trim());
                                updates.put("licenseExpiry", licenseExpiryInput.getText().toString().trim());

                                db.collection(MyFirestoreReferences.USERS_COLLECTION)
                                        .document(currentUser.getUid())
                                        .update(updates)
                                        .addOnSuccessListener(aVoid -> {
                                            Toast.makeText(DriverRegistrationActivity.this,
                                                    "Registration successful!", Toast.LENGTH_SHORT).show();
                                            // Navigate to Success page
                                            Intent intent = new Intent(DriverRegistrationActivity.this,
                                                    DriverRegistrationSuccessActivity.class);
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