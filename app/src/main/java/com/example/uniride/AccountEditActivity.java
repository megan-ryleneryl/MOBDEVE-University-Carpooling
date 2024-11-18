package com.example.uniride;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

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
    private Button selectAvatarButton;
    private TextInputEditText nameInput, phoneInput;
    private AutoCompleteTextView universityInput;
    private TextInputEditText carMakeInput, carModelInput, plateNumberInput;
    private LinearLayout carDetailsContainer;
    private Button saveChangesButton, cancelButton;

    private FirebaseAuth mAuth;
    private FirebaseFirestore db;
    private FirebaseUser currentUser;
    private boolean isDriver = false;
    private String currentUniversity;
    private int selectedAvatarResource;

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

        initViews();
        setupUniversityDropdown();
        loadUserData();
        setListeners();
    }

    private void initViews() {
        profileImage = findViewById(R.id.profile_image);
        selectAvatarButton = findViewById(R.id.select_avatar_button);
        nameInput = findViewById(R.id.name_input);
        phoneInput = findViewById(R.id.phone_input);
        universityInput = findViewById(R.id.university_input);

        // Car details views - this should be a LinearLayout
        carDetailsContainer = findViewById(R.id.car_details_container);
        carMakeInput = findViewById(R.id.car_make_input);
        carModelInput = findViewById(R.id.car_model_input);
        plateNumberInput = findViewById(R.id.plate_number_input);

        // Action buttons
        saveChangesButton = findViewById(R.id.save_changes_button);
        cancelButton = findViewById(R.id.cancel_button);
    }

    private void setupUniversityDropdown() {
        ArrayList<LocationModel> allLocations = DataGenerator.loadLocationData();
        List<LocationModel> universities = new ArrayList<>();

        for (LocationModel location : allLocations) {
            if (location.getIsUniversity()) {
                universities.add(location);
            }
        }

        ArrayAdapter<LocationModel> adapter = new ArrayAdapter<>(
                this,
                android.R.layout.simple_dropdown_item_1line,
                universities
        );
        universityInput.setAdapter(adapter);
        universityInput.setOnClickListener(v -> universityInput.showDropDown());
    }

    private void loadUserData() {
        db.collection(MyFirestoreReferences.USERS_COLLECTION)
                .document(currentUser.getUid())
                .get()
                .addOnSuccessListener(documentSnapshot -> {
                    if (documentSnapshot.exists()) {
                        // Set user data
                        nameInput.setText(documentSnapshot.getString("name"));
                        phoneInput.setText(documentSnapshot.getString("phoneNumber"));
                        int currentUniversityID = documentSnapshot.getLong("universityID").intValue();
                        LocationModel currentUniversity = getUniversityById(currentUniversityID);
                        universityInput.setText(currentUniversity.getName(), false);

                        // Set profile image
                        selectedAvatarResource = documentSnapshot.getLong("pfp") != null ?
                                documentSnapshot.getLong("pfp").intValue() : R.drawable.default_profile_image;
                        profileImage.setImageResource(selectedAvatarResource);

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

    private void saveChanges() {
        if (!validateInput()) {
            return;
        }

        Map<String, Object> updates = new HashMap<>();
        updates.put("name", nameInput.getText().toString().trim());
        updates.put("phoneNumber", phoneInput.getText().toString().trim());
        updates.put("universityID", getUniversityIdByName(universityInput.getText().toString().trim()));
        updates.put("pfp", selectedAvatarResource);

        // If user is a driver, update car details
        if (isDriver) {
            Map<String, Object> carData = new HashMap<>();
            carData.put("make", carMakeInput.getText().toString().trim());
            carData.put("model", carModelInput.getText().toString().trim());
            carData.put("plateNumber", plateNumberInput.getText().toString().trim());
            updates.put("car", carData);
        }

        db.collection(MyFirestoreReferences.USERS_COLLECTION)
                .document(currentUser.getUid())
                .update(updates)
                .addOnSuccessListener(aVoid -> {
                    Toast.makeText(this, "Profile updated successfully", Toast.LENGTH_SHORT).show();
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

    private LocationModel getUniversityById(int id) {
        for (LocationModel location : DataGenerator.loadLocationData()) {
            if (location.getLocationID() == id) {
                return location;
            }
        }
        return null;
    }

    private int getUniversityIdByName(String name) {
        for (LocationModel location : DataGenerator.loadLocationData()) {
            if (location.getName().equals(name)) {
                return location.getLocationID();
            }
        }
        return -1;
    }

    private void setListeners() {
        selectAvatarButton.setOnClickListener(v -> showAvatarDialog());
        saveChangesButton.setOnClickListener(v -> saveChanges());
        cancelButton.setOnClickListener(v -> finish());
    }

    private void showAvatarDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        View dialogView = getLayoutInflater().inflate(R.layout.dialog_avatar_selection, null);
        builder.setView(dialogView);
        builder.setTitle("Select Avatar");

        AlertDialog dialog = builder.create();

        ImageView avatar1 = dialogView.findViewById(R.id.avatar1);
        ImageView avatar2 = dialogView.findViewById(R.id.avatar2);
        ImageView avatar3 = dialogView.findViewById(R.id.avatar3);
        ImageView avatar4 = dialogView.findViewById(R.id.avatar4);
        ImageView avatar5 = dialogView.findViewById(R.id.avatar5);
        ImageView avatar6 = dialogView.findViewById(R.id.avatar6);

        View.OnClickListener avatarClickListener = v -> {
            int id = v.getId();
            if (id == R.id.avatar1) selectedAvatarResource = R.drawable.a_icon;
            else if (id == R.id.avatar2) selectedAvatarResource = R.drawable.b_icon;
            else if (id == R.id.avatar3) selectedAvatarResource = R.drawable.c_icon;
            else if (id == R.id.avatar4) selectedAvatarResource = R.drawable.d_icon;
            else if (id == R.id.avatar5) selectedAvatarResource = R.drawable.e_icon;
            else if (id == R.id.avatar6) selectedAvatarResource = R.drawable.f_icon;

            profileImage.setImageResource(selectedAvatarResource);
            dialog.dismiss();
        };

        avatar1.setOnClickListener(avatarClickListener);
        avatar2.setOnClickListener(avatarClickListener);
        avatar3.setOnClickListener(avatarClickListener);
        avatar4.setOnClickListener(avatarClickListener);
        avatar5.setOnClickListener(avatarClickListener);
        avatar6.setOnClickListener(avatarClickListener);

        dialog.show();
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