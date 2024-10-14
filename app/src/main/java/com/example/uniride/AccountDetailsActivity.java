package com.example.uniride;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import de.hdodenhof.circleimageview.CircleImageView;

public class AccountDetailsActivity extends AppCompatActivity {

    private CircleImageView profileImage;
    private TextView nameText, emailText, phoneText, universityText, accountStatusText;
    private TextView carDetailsLabel, carDetailsText, balanceText;
    private LinearLayout accountActionsContainer, balanceContainer;
    private Button editProfileButton, logoutButton, deleteAccountButton;
    private Button withdrawButton, depositButton;
    private UserModel currentUser;
    private boolean isOwnProfile;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_account_details);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        initViews();
        createDummyUser();
        loadUserData();
        setListeners();
    }

    private void initViews() {
        profileImage = findViewById(R.id.profile_image);
        nameText = findViewById(R.id.name_text);
        emailText = findViewById(R.id.email_text);
        phoneText = findViewById(R.id.phone_text);
        universityText = findViewById(R.id.university_text);
        accountStatusText = findViewById(R.id.account_status_text);
        carDetailsLabel = findViewById(R.id.car_details_label);
        carDetailsText = findViewById(R.id.car_details_text);

        accountActionsContainer = findViewById(R.id.account_actions_container);
        balanceContainer = findViewById(R.id.balance_container);
        balanceText = findViewById(R.id.balance_text);
        editProfileButton = findViewById(R.id.edit_profile_button);
        logoutButton = findViewById(R.id.logout_button);
        deleteAccountButton = findViewById(R.id.delete_account_button);
        withdrawButton = findViewById(R.id.withdraw_button);
        depositButton = findViewById(R.id.deposit_button);
    }

    private void createDummyUser() {
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

        isOwnProfile = true;
    }

    private void loadUserData() {
        if (currentUser != null) {
            nameText.setText(currentUser.getName());
            emailText.setText(currentUser.getEmail());
            phoneText.setText(currentUser.getPhoneNumber());
            universityText.setText(currentUser.getUniversity());
            accountStatusText.setText(currentUser.getAccountStatus());

            if (currentUser.isDriver() && currentUser.getCar() != null) {
                CarModel car = currentUser.getCar();
                String carDetails = String.format("%s %s\nPlate Number: %s\nYear: %d",
                        car.getMake(), car.getModel(), car.getPlateNumber(), car.getYear());
                carDetailsText.setText(carDetails);
                carDetailsText.setVisibility(View.VISIBLE);
            } else {
                carDetailsText.setVisibility(View.GONE);
                carDetailsLabel.setVisibility(View.GONE);
            }

            if (isOwnProfile) {
                accountActionsContainer.setVisibility(View.VISIBLE);
                if (currentUser.isDriver()) {
                    balanceContainer.setVisibility(View.VISIBLE);
                    balanceText.setText(String.format("P %.2f", currentUser.getBalance()));
                } else {
                    balanceContainer.setVisibility(View.GONE);
                }
            } else {
                accountActionsContainer.setVisibility(View.GONE);
            }

            // TODO: Load profile image
            // profileImage.setImageResource(R.drawable.default_profile_image);
        }


    }

    private void setListeners() {
        editProfileButton.setOnClickListener(v -> {
            Intent intent = new Intent(AccountDetailsActivity.this, AccountEditActivity.class);
            startActivity(intent);
        });

        logoutButton.setOnClickListener(v -> {
            // TODO: Implement logout functionality
        });

        deleteAccountButton.setOnClickListener(v -> {
            showDeleteAccountConfirmationDialog();
        });

        withdrawButton.setOnClickListener(v -> {
            // TODO: Implement withdraw balance functionality
        });

        depositButton.setOnClickListener(v -> {
            // TODO: Implement deposit balance functionality
        });
    }

    private void showDeleteAccountConfirmationDialog() {
        new AlertDialog.Builder(this)
                .setTitle("Delete Account")
                .setMessage("Are you sure you want to delete your account? This action cannot be undone.")
                .setPositiveButton("Delete", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        // Perform the delete account action
                        deleteAccount();
                    }
                })
                .setNegativeButton("Cancel", null)
                .setIcon(android.R.drawable.ic_dialog_alert)
                .show();
    }

    private void deleteAccount() {
        // TODO: Implement the actual account deletion logic
        // This might include:
        // 1. Calling an API to delete the account from the server
        // 2. Clearing local data related to the account
        // 3. Logging the user out
        // 4. Navigating to the login or welcome screen

        // For now, we'll just show a toast message
        Toast.makeText(this, "Account deleted", Toast.LENGTH_SHORT).show();
        finish(); // Close the activity
    }
}