package com.example.uniride;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.google.android.material.textfield.TextInputEditText;

import de.hdodenhof.circleimageview.CircleImageView;

public class AccountEditActivity extends AppCompatActivity {

    private CircleImageView profileImage;
    private TextInputEditText nameInput, emailInput, phoneInput, universityInput;
    private TextInputEditText carMakeInput, carModelInput, plateNumberInput;
    private LinearLayout carDetailsContainer;
    private Button saveChangesButton, cancelButton;
    private UserModel currentUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_account_edit);

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
        Intent i = getIntent();
        currentUser = (UserModel) i.getSerializableExtra("currentUser");

        nameInput.setText(currentUser.getName());
        emailInput.setText(currentUser.getEmail());
        phoneInput.setText(currentUser.getPhoneNumber());
        universityInput.setText(currentUser.getUniversity());

        if (currentUser.isDriver() && currentUser.getCar() != null) {
            carDetailsContainer.setVisibility(View.VISIBLE);
            CarModel car = currentUser.getCar();
            carMakeInput.setText(car.getMake());
            carModelInput.setText(car.getModel());
            plateNumberInput.setText(car.getPlateNumber());
        } else {
            carDetailsContainer.setVisibility(View.GONE);
        }

        profileImage.setImageResource(currentUser.getPfp());
    }

    private void setListeners() {
        saveChangesButton.setOnClickListener(v -> saveChanges());
        cancelButton.setOnClickListener(v -> finish());
    }

    private void saveChanges() {
        if (!validateInput()) {
            Toast.makeText(this, "Please fill in all required fields", Toast.LENGTH_SHORT).show();
            return;
        }
        Toast.makeText(this, "Changes saved", Toast.LENGTH_SHORT).show();
        finish();
    }

    private boolean validateInput() {
        // TODO: Implement input validation
        // Check if required fields are not empty
        // Validate email format
        // Validate phone number format
        return true;
    }
}