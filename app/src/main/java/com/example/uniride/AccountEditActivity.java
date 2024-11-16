package com.example.uniride;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import de.hdodenhof.circleimageview.CircleImageView;

public class AccountEditActivity extends AppCompatActivity {

    private CircleImageView profileImage;
    private TextInputEditText nameInput, emailInput, phoneInput;
    private Spinner universitySpinner;
    private TextInputEditText carMakeInput, carModelInput, plateNumberInput;
    private LinearLayout carDetailsContainer;
    private Button saveChangesButton, cancelButton;

    private FirebaseAuth mAuth;
    private FirebaseFirestore db;
    private FirebaseUser currentUser;
    private boolean isDriver = false;
    private String currentUniversity;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_account_edit);

        // Initialize Firebase
        mAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();
        currentUser = mAuth.getCurrentUser();

        if (currentUser == null) {
            // User not logged in, redirect to login
            startActivity(new Intent(this, AccountLoginActivity.class));
            finish();
            return;
        }

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        initViews();
        setupUniversitySpinner();
        loadUserData();
        setListeners();
    }

    private void initViews() {
        profileImage = findViewById(R.id.profile_image);
        nameInput = findViewById(R.id.name_input);
        emailInput = findViewById(R.id.email_input);
        phoneInput = findViewById(R.id.phone_input);
        universitySpinner = findViewById(R.id.university_spinner); // Update layout to use Spinner
        carDetailsContainer = findViewById(R.id.car_details_container);
        carMakeInput = findViewById(R.id.car_make_input);
        carModelInput = findViewById(R.id.car_model_input);
        plateNumberInput = findViewById(R.id.plate_number_input);
        saveChangesButton = findViewById(R.id.save_changes_button);
        cancelButton = findViewById(R.id.cancel_button);
    }

    private void setupUniversitySpinner() {
        ArrayList<LocationModel> allLocations = DataGenerator.loadLocationData();
        List<LocationModel> universities = new ArrayList<>();

        // Filter for universities only
        for (LocationModel location : allLocations) {
            if (location.getIsUniversity()) {
                universities.add(location);
            }
        }

        ArrayAdapter<LocationModel> adapter = new ArrayAdapter<>(
                this,
                android.R.layout.simple_spinner_item,
                universities
        );
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        universitySpinner.setAdapter(adapter);
    }

    private void loadUserData() {
        db.collection(MyFirestoreReferences.USERS_COLLECTION)
                .document(currentUser.getUid())
                .get()
                .addOnSuccessListener(documentSnapshot -> {
                    if (documentSnapshot.exists()) {
                        // Set user data
                        nameInput.setText(documentSnapshot.getString("name"));
                        emailInput.setText(documentSnapshot.getString("email"));
                        phoneInput.setText(documentSnapshot.getString("phoneNumber"));
                        currentUniversity = documentSnapshot.getString("university");

                        // Set selected university in spinner
                        ArrayAdapter<LocationModel> adapter = (ArrayAdapter<LocationModel>) universitySpinner.getAdapter();
                        for (int i = 0; i < adapter.getCount(); i++) {
                            LocationModel location = adapter.getItem(i);
                            if (location != null && location.getName().equals(currentUniversity)) {
                                universitySpinner.setSelection(i);
                                break;
                            }
                        }

                        // Set profile image
                        Integer pfpResource = documentSnapshot.getLong("pfp") != null ?
                                documentSnapshot.getLong("pfp").intValue() : R.drawable.default_profile_image;
                        profileImage.setImageResource(pfpResource);

                        // Handle driver-specific data
                        isDriver = documentSnapshot.getBoolean("isDriver") != null &&
                                documentSnapshot.getBoolean("isDriver");

                        if (isDriver) {
                            carDetailsContainer.setVisibility(View.VISIBLE);
                            Object carData = documentSnapshot.get("car");
                            if (carData != null) {
                                Map<String, Object> carMap = (Map<String, Object>) carData;
                                carMakeInput.setText((String) carMap.get("make"));
                                carModelInput.setText((String) carMap.get("model"));
                                plateNumberInput.setText((String) carMap.get("plateNumber"));
                            }
                        } else {
                            carDetailsContainer.setVisibility(View.GONE);
                        }
                    }
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(this, "Error loading user data: " + e.getMessage(),
                            Toast.LENGTH_SHORT).show();
                });
    }

    private void setListeners() {
        saveChangesButton.setOnClickListener(v -> saveChanges());
        cancelButton.setOnClickListener(v -> finish());
    }

    private void saveChanges() {
        if (!validateInput()) {
            return;
        }

        Map<String, Object> updates = new HashMap<>();
        updates.put("name", nameInput.getText().toString().trim());
        updates.put("phoneNumber", phoneInput.getText().toString().trim());

        // Get selected university from spinner
        LocationModel selectedUniversity = (LocationModel) universitySpinner.getSelectedItem();
        updates.put("university", selectedUniversity.getName());

        // If user is a driver, update car details
        if (isDriver) {
            Map<String, Object> carData = new HashMap<>();
            carData.put("make", carMakeInput.getText().toString().trim());
            carData.put("model", carModelInput.getText().toString().trim());
            carData.put("plateNumber", plateNumberInput.getText().toString().trim());
            updates.put("car", carData);
        }

        // Update Firestore document
        db.collection(MyFirestoreReferences.USERS_COLLECTION)
                .document(currentUser.getUid())
                .update(updates)
                .addOnSuccessListener(aVoid -> {
                    Toast.makeText(this, "Profile updated successfully", Toast.LENGTH_SHORT).show();

                    // Refresh AccountDetailsActivity by restarting it
                    Intent intent = new Intent(this, AccountDetailsActivity.class);
                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    startActivity(intent);
                    finish();
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(this, "Error updating profile: " + e.getMessage(),
                            Toast.LENGTH_SHORT).show();
                });
    }

    private boolean validateInput() {
        if (TextUtils.isEmpty(nameInput.getText())) {
            nameInput.setError("Name is required");
            return false;
        }

        if (TextUtils.isEmpty(phoneInput.getText())) {
            phoneInput.setError("Phone number is required");
            return false;
        }

        if (universitySpinner.getSelectedItem() == null) {
            Toast.makeText(this, "Please select a university", Toast.LENGTH_SHORT).show();
            return false;
        }

        if (isDriver) {
            if (TextUtils.isEmpty(carMakeInput.getText())) {
                carMakeInput.setError("Car make is required");
                return false;
            }
            if (TextUtils.isEmpty(carModelInput.getText())) {
                carModelInput.setError("Car model is required");
                return false;
            }
            if (TextUtils.isEmpty(plateNumberInput.getText())) {
                plateNumberInput.setError("Plate number is required");
                return false;
            }
        }

        return true;
    }
}