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
    private TextInputEditText carMakeInput, carModelInput, plateNumberInput, carYearInput;
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
        carYearInput = findViewById(R.id.car_year_input);
        saveChangesButton = findViewById(R.id.save_changes_button);
        cancelButton = findViewById(R.id.cancel_button);
    }

    private void loadUserData() {
        // TODO: Load actual user data from your data source
        currentUser = new UserModel(
                "Luke Aniago",
                "luke_marion_aniago@dlsu.edu.ph",
                "09565482850",
                "De La Salle University",
                "Verified Driver",
                "Driver"
        );

        if (currentUser.isDriver()) {
            CarModel car = new CarModel("Mitsubishi", "EVO", "NDE1923", 2022);
            currentUser.setCar(car);
        }

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
            carYearInput.setText(String.valueOf(car.getYear()));
        } else {
            carDetailsContainer.setVisibility(View.GONE);
        }

        // TODO: Load profile image
        // profileImage.setImageResource(R.drawable.default_profile_image);
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
//                car = new CarModel();
//                currentUser.setCar(car);
            }
            car.setMake(carMakeInput.getText().toString());
            car.setModel(carModelInput.getText().toString());
            car.setPlateNumber(plateNumberInput.getText().toString());
            car.setYear(Integer.parseInt(carYearInput.getText().toString()));
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
        // Validate car year (if applicable)
        return true;
    }
}