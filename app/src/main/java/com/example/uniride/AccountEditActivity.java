package com.example.uniride;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;

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
        // For this example, we'll use the first user from DataGenerator
        currentUser = DataGenerator.loadUserData().get(0);

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
        // Validate input
        if (!validateInput()) {
            return;
        }

        // Update user model
        currentUser.setName(nameInput.getText().toString());
        currentUser.setEmail(emailInput.getText().toString());
        currentUser.setPhoneNumber(phoneInput.getText().toString());
        currentUser.setUniversity(universityInput.getText().toString());

        if (currentUser.isDriver()) {
            CarModel car = currentUser.getCar();
            if (car == null) {
                car = new CarModel(0, 0, "", "", "");
                currentUser.setCar(car);
            }
            car.setMake(carMakeInput.getText().toString());
            car.setModel(carModelInput.getText().toString());
            car.setPlateNumber(plateNumberInput.getText().toString());
        }

        // TODO: Save changes to your data source (e.g., local database, API)

        // Return to the previous activity
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