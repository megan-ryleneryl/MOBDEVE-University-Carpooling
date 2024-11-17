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

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.Map;

import de.hdodenhof.circleimageview.CircleImageView;

public class AccountDetailsActivity extends BottomNavigationActivity {

    private CircleImageView profileImage;
    private TextView nameText, emailText, phoneText, universityText, accountStatusText;
    private TextView carDetailsLabel, carDetailsText, balanceText;
    private LinearLayout accountActionsContainer, balanceContainer;
    private Button editProfileButton, logoutButton, deleteAccountButton;
    private Button withdrawButton, depositButton;

    private FirebaseAuth mAuth;
    private FirebaseFirestore db;
    private FirebaseUser currentUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_account_details);

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

    @Override
    protected int getSelectedItemId() {
        return R.id.account;
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

    private void loadUserData() {
        db.collection(MyFirestoreReferences.USERS_COLLECTION)
                .document(currentUser.getUid())
                .get()
                .addOnSuccessListener(documentSnapshot -> {
                    if (documentSnapshot.exists()) {
                        // Set user data
                        nameText.setText(documentSnapshot.getString("name"));
                        emailText.setText(documentSnapshot.getString("email"));
                        phoneText.setText(documentSnapshot.getString("phoneNumber"));
                        universityText.setText(documentSnapshot.getString("university"));
                        accountStatusText.setText(documentSnapshot.getBoolean("isDriver") ? "Driver" : "Passenger");

                        // Set profile image
                        Integer pfpResource = documentSnapshot.getLong("pfp") != null ?
                                documentSnapshot.getLong("pfp").intValue() : R.drawable.default_profile_image;
                        profileImage.setImageResource(pfpResource);

                        boolean isDriver = documentSnapshot.getBoolean("isDriver") != null &&
                                documentSnapshot.getBoolean("isDriver");

                        // Handle driver-specific UI
                        if (isDriver) {
                            balanceContainer.setVisibility(View.VISIBLE);
                            Double balance = documentSnapshot.getDouble("balance");
                            balanceText.setText(String.format("P %.2f", balance != null ? balance : 0.0));

                            // Load car details if available
                            Object carData = documentSnapshot.get("car");
                            if (carData != null) {
                                carDetailsLabel.setVisibility(View.VISIBLE);
                                carDetailsText.setVisibility(View.VISIBLE);
                                // Assuming car data structure matches CarModel
                                String carDetails = String.format("%s %s\nPlate Number: %s",
                                        ((Map<String, Object>) carData).get("make"),
                                        ((Map<String, Object>) carData).get("model"),
                                        ((Map<String, Object>) carData).get("plateNumber"));
                                carDetailsText.setText(carDetails);
                            }
                        } else {
                            balanceContainer.setVisibility(View.GONE);
                            carDetailsLabel.setVisibility(View.GONE);
                            carDetailsText.setVisibility(View.GONE);
                        }
                    }
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(this, "Error loading user data: " + e.getMessage(),
                            Toast.LENGTH_SHORT).show();
                });
    }

    private void setListeners() {
        editProfileButton.setOnClickListener(v -> {
            Intent intent = new Intent(AccountDetailsActivity.this, AccountEditActivity.class);
            startActivity(intent);
        });

        logoutButton.setOnClickListener(v -> {
            // Sign out from both Firebase and Google
            mAuth.signOut();
            GoogleSignInClient googleSignInClient = GoogleSignIn.getClient(this,
                    new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                            .requestIdToken(getString(R.string.default_web_client_id))
                            .requestEmail()
                            .build());
            googleSignInClient.signOut().addOnCompleteListener(task -> {
                Intent intent = new Intent(AccountDetailsActivity.this, AccountLoginActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(intent);
            });
        });

        deleteAccountButton.setOnClickListener(v -> {
            showDeleteAccountConfirmationDialog();
        });

        withdrawButton.setOnClickListener(v -> {
            // TODO: Implement withdraw functionality
            Toast.makeText(this, "Withdraw functionality coming soon", Toast.LENGTH_SHORT).show();
        });

        depositButton.setOnClickListener(v -> {
            // TODO: Implement deposit functionality
            Toast.makeText(this, "Deposit functionality coming soon", Toast.LENGTH_SHORT).show();
        });
    }

    private void showDeleteAccountConfirmationDialog() {
        new AlertDialog.Builder(this)
                .setTitle("Delete Account")
                .setMessage("Are you sure you want to delete your account? This action cannot be undone.")
                .setPositiveButton("Delete", (dialog, which) -> {
                    deleteAccount();
                })
                .setNegativeButton("Cancel", null)
                .setIcon(android.R.drawable.ic_dialog_alert)
                .show();
    }

    private void deleteAccount() {
        // Delete from Firestore first
        db.collection(MyFirestoreReferences.USERS_COLLECTION)
                .document(currentUser.getUid())
                .delete()
                .addOnSuccessListener(aVoid -> {
                    // Then delete the Firebase Auth account
                    currentUser.delete()
                            .addOnSuccessListener(aVoid1 -> {
                                // Sign out from both Firebase and Google
                                mAuth.signOut();
                                GoogleSignInClient googleSignInClient = GoogleSignIn.getClient(this,
                                        new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                                                .requestIdToken(getString(R.string.default_web_client_id))
                                                .requestEmail()
                                                .build());
                                googleSignInClient.signOut().addOnCompleteListener(task -> {
                                    Toast.makeText(this, "Account deleted successfully", Toast.LENGTH_SHORT).show();
                                    Intent intent = new Intent(AccountDetailsActivity.this, AccountLoginActivity.class);
                                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                    startActivity(intent);
                                });
                            })
                            .addOnFailureListener(e -> {
                                Toast.makeText(this, "Error deleting authentication: " + e.getMessage(),
                                        Toast.LENGTH_SHORT).show();
                            });
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(this, "Error deleting user data: " + e.getMessage(),
                            Toast.LENGTH_SHORT).show();
                });
    }
}