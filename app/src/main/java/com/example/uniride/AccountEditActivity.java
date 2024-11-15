package com.example.uniride;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

import de.hdodenhof.circleimageview.CircleImageView;

public class AccountEditActivity extends AppCompatActivity {

    private CircleImageView profileImage;
    private TextInputEditText nameInput, emailInput, phoneInput, universityInput;
    private TextInputEditText carMakeInput, carModelInput, plateNumberInput;
    private LinearLayout carDetailsContainer;
    private Button saveChangesButton, cancelButton;

    private FirebaseAuth mAuth;
    private FirebaseFirestore db;
    private FirebaseUser currentUser;
    private boolean isDriver = false;

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
        loadUserData();
        setListeners();
    }

    private void initViews() {
        profileImage = findViewById(R.id.profile_image);
        nameInput = findViewById(R.id.name_input);
        emailInput = findViewById(R.id.email_input);
        phoneInput = findViewById(R.id.phone_input);
        universityInput = findViewById(R.id.university_input);
        carDetailsContainer = findViewById(R.id.car_details_container);
        carMakeInput = findViewById(R.id.car_make_input);
        carModelInput = findViewById(R.id.car_model_input);
        plateNumberInput = findViewById(R.id.plate_number_input);
        saveChangesButton = findViewById(R.id.save_changes_button);
        cancelButton = findViewById(R.id.cancel_button);
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
                        universityInput.setText(documentSnapshot.getString("university"));

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
        updates.put("university", universityInput.getText().toString().trim());

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

        if (TextUtils.isEmpty(universityInput.getText())) {
            universityInput.setError("University is required");
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