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
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.Calendar;
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

    private TextInputEditText licenseNumberInput;
    private TextInputEditText licenseExpiryInput;
    private TextInputEditText seatingCapacityInput;

    private FirebaseAuth mAuth;
    private FirebaseFirestore db;
    private FirebaseUser currentUser;
    private boolean isDriver = false;
    private String currentUniversity;
    private int selectedAvatarResource;

    private UserModel userModel;
    private int carId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_account_edit);

        mAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();
        currentUser = mAuth.getCurrentUser();

        if (currentUser == null) {
            startActivity(new Intent(this, AccountLoginActivity.class));
            finish();
            return;
        }

        initViews();
        setupUniversityDropdown();
        setupCarTypeSpinnerForEdit();
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
        licenseNumberInput = findViewById(R.id.license_number_input);
        licenseExpiryInput = findViewById(R.id.license_expiry_input);
        seatingCapacityInput = findViewById(R.id.seating_capacity_input);

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

    private void setupCarTypeSpinnerForEdit() {
        Spinner carTypeSpinner = findViewById(R.id.carTypeSpinner);
        ImageView carPreviewImage = findViewById(R.id.carPreviewImage);

        // Setup adapter
        ArrayAdapter<String> adapter = new ArrayAdapter<>(
                this,
                android.R.layout.simple_spinner_item,
                CarTypeUtils.getCarTypes()
        );
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        carTypeSpinner.setAdapter(adapter);

        // Handle spinner selection changes
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

    // Update loadUserData() to include car type loading
    private void loadUserData() {
        db.collection(MyFirestoreReferences.USERS_COLLECTION)
                .document(currentUser.getUid())
                .get()
                .addOnSuccessListener(documentSnapshot -> {
                    if (documentSnapshot.exists()) {
                        userModel = UserModel.fromMap(documentSnapshot.getData());

                        // Basic info
                        nameInput.setText(userModel.getName());
                        phoneInput.setText(userModel.getPhoneNumber());
                        profileImage.setImageResource(userModel.getPfp());
                        isDriver = userModel.isDriver();
                        carId = userModel.getCarID();

                        // Setup university data
                        db.collection(MyFirestoreReferences.LOCATIONS_COLLECTION)
                                .whereEqualTo("locationID", userModel.getUniversityID())
                                .get()
                                .addOnSuccessListener(querySnapshot -> {
                                    if (!querySnapshot.isEmpty()) {
                                        LocationModel university = LocationModel.fromMap(
                                                querySnapshot.getDocuments().get(0).getData());
                                        universityInput.setText(university.getName(), false);
                                    }
                                });

                        // Load driver-specific data
                        if (isDriver) {
                            carDetailsContainer.setVisibility(View.VISIBLE);
                            licenseNumberInput.setText(userModel.getLicenseNumber());
                            licenseExpiryInput.setText(userModel.getLicenseExpiry());

                            // Load car data if available
                            if (carId != 0) {
                                db.collection(MyFirestoreReferences.CARS_COLLECTION)
                                        .whereEqualTo("carID", carId)
                                        .get()
                                        .addOnSuccessListener(querySnapshot -> {
                                            if (!querySnapshot.isEmpty()) {
                                                CarModel car = CarModel.fromMap(
                                                        querySnapshot.getDocuments().get(0).getData());

                                                carMakeInput.setText(car.getMake());
                                                carModelInput.setText(car.getModel());
                                                plateNumberInput.setText(car.getPlateNumber());
                                                seatingCapacityInput.setText(
                                                        String.valueOf(car.getSeatingCapacity()));

                                                // Set car type spinner and preview image
                                                Spinner carTypeSpinner = findViewById(R.id.carTypeSpinner);
                                                ImageView carPreviewImage = findViewById(R.id.carPreviewImage);

                                                String carType = CarTypeUtils.getTypeFromResource(
                                                        car.getCarImage());
                                                String[] carTypes = CarTypeUtils.getCarTypes();
                                                for (int i = 0; i < carTypes.length; i++) {
                                                    if (carTypes[i].equals(carType)) {
                                                        carTypeSpinner.setSelection(i);
                                                        break;
                                                    }
                                                }

                                                // Set preview image
                                                carPreviewImage.setImageResource(car.getCarImage());
                                            }
                                        });
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

        // Only update pfp if it was changed
        if (selectedAvatarResource != 0) {
            updates.put("pfp", selectedAvatarResource);
        }

        if (isDriver) {
            // Add license updates
            updates.put("licenseNumber", licenseNumberInput.getText().toString().trim());
            updates.put("licenseExpiry", licenseExpiryInput.getText().toString().trim());

            // Update car data in its own collection
            Map<String, Object> carUpdates = new HashMap<>();
            carUpdates.put("make", carMakeInput.getText().toString().trim());
            carUpdates.put("model", carModelInput.getText().toString().trim());
            carUpdates.put("plateNumber", plateNumberInput.getText().toString().trim());
            carUpdates.put("seatingCapacity",
                    Integer.parseInt(seatingCapacityInput.getText().toString().trim()));
            String selectedCarType = ((Spinner)findViewById(R.id.carTypeSpinner))
                    .getSelectedItem().toString();
            int carImageResource = CarTypeUtils.getCarImageResource(selectedCarType);
            carUpdates.put("carImage", carImageResource);

            db.collection(MyFirestoreReferences.CARS_COLLECTION)
                    .whereEqualTo("carID", carId)
                    .get()
                    .addOnSuccessListener(querySnapshot -> {
                        if (!querySnapshot.isEmpty()) {
                            querySnapshot.getDocuments().get(0).getReference()
                                    .update(carUpdates);
                        }
                    });
        }

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

        // Add date picker for license expiry
        licenseExpiryInput.setOnClickListener(v -> showDatePicker());

        // Add text watcher for seating capacity
        seatingCapacityInput.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (!TextUtils.isEmpty(s)) {
                    try {
                        int capacity = Integer.parseInt(s.toString());
                        if (capacity < 1 || capacity > 6) {
                            seatingCapacityInput.setError("Seating capacity must be between 1 and 6");
                        } else {
                            seatingCapacityInput.setError(null);
                        }
                    } catch (NumberFormatException e) {
                        seatingCapacityInput.setError("Please enter a valid number");
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
                R.style.CustomDatePickerDialog,
                (view, selectedYear, selectedMonth, selectedDay) -> {
                    // Format the date as needed
                    String date = String.format("%02d/%02d/%d", selectedDay, selectedMonth + 1, selectedYear);
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
            if (TextUtils.isEmpty(seatingCapacityInput.getText())) {
                seatingCapacityInput.setError("Seating capacity is required");
                return false;
            }

            try {
                int capacity = Integer.parseInt(seatingCapacityInput.getText().toString().trim());
                if (capacity < 1 || capacity > 6) {
                    seatingCapacityInput.setError("Seating capacity must be between 1 and 6");
                    return false;
                }
            } catch (NumberFormatException e) {
                seatingCapacityInput.setError("Please enter a valid number");
                return false;
            }
        }

        return true;
    }
}