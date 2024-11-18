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
import androidx.cardview.widget.CardView;

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
    private LinearLayout balanceContainer;
    private Button editProfileButton, logoutButton, deleteAccountButton;
    private Button withdrawButton, depositButton;

    private CardView carDetailsCard, balanceCard;

    private FirebaseAuth mAuth;
    private FirebaseFirestore db;
    private FirebaseUser currentUser;
    private UserModel userModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_account_details);

        mAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();
        currentUser = mAuth.getCurrentUser();

        if (currentUser == null) {
            startActivity(new Intent(this, AccountLoginActivity.class));
            finish();
            return;
        }

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

        carDetailsCard = findViewById(R.id.car_details_card);
        carDetailsText = findViewById(R.id.car_details_text);

        balanceCard = findViewById(R.id.balance_card);
        balanceText = findViewById(R.id.balance_text);
        withdrawButton = findViewById(R.id.withdraw_button);
        depositButton = findViewById(R.id.deposit_button);

        editProfileButton = findViewById(R.id.edit_profile_button);
        logoutButton = findViewById(R.id.logout_button);
        deleteAccountButton = findViewById(R.id.delete_account_button);
    }

    private void loadUserData() {
        db.collection(MyFirestoreReferences.USERS_COLLECTION)
                .document(currentUser.getUid())
                .get()
                .addOnSuccessListener(documentSnapshot -> {
                    if (documentSnapshot.exists()) {
                        userModel = documentSnapshot.toObject(UserModel.class);
                        userModel.populateObjects(db, this::onUserPopulateComplete);
                    }
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(this, "Error loading user data: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                });
    }

    private void onUserPopulateComplete(UserModel user) {
        nameText.setText(user.getName());
        emailText.setText(user.getEmail());
        phoneText.setText(user.getPhoneNumber());

        db.collection(MyFirestoreReferences.LOCATIONS_COLLECTION)
                .whereEqualTo("locationID", user.getUniversityID())
                .get()
                .addOnSuccessListener(querySnapshot -> {
                    if (!querySnapshot.isEmpty()) {
                        LocationModel university = querySnapshot.getDocuments().get(0).toObject(LocationModel.class);
                        universityText.setText(university.getName());
                    } else {
                        universityText.setText("");
                    }
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(this, "Error loading university data: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                });

        accountStatusText.setText(user.isDriver() ? "Driver" : "Passenger");
        profileImage.setImageResource(user.getPfp());

        if (user.isDriver()) {
            balanceCard.setVisibility(View.VISIBLE);
            balanceText.setText(String.format("P %.2f", user.getBalance()));

            if (user.getCarID() != 0) {
                carDetailsCard.setVisibility(View.VISIBLE);
                db.collection(MyFirestoreReferences.CARS_COLLECTION)
                        .whereEqualTo("carID", user.getCarID())
                        .get()
                        .addOnSuccessListener(querySnapshot -> {
                            if (!querySnapshot.isEmpty()) {
                                CarModel car = querySnapshot.getDocuments().get(0).toObject(CarModel.class);
                                String carDetails = String.format("%s %s\nPlate Number: %s", car.getMake(), car.getModel(), car.getPlateNumber());
                                carDetailsText.setText(carDetails);
                            }
                        })
                        .addOnFailureListener(e -> {
                            Toast.makeText(this, "Error loading car data: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                        });
            }
        } else {
            balanceCard.setVisibility(View.GONE);
            carDetailsCard.setVisibility(View.GONE);
        }
    }
    private void setListeners() {
        editProfileButton.setOnClickListener(v -> {
            Intent intent = new Intent(AccountDetailsActivity.this, AccountEditActivity.class);
            startActivity(intent);
        });

        logoutButton.setOnClickListener(v -> {
            mAuth.signOut();
            GoogleSignIn.getClient(this, new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                            .requestIdToken(getString(R.string.default_web_client_id))
                            .requestEmail()
                            .build())
                    .signOut()
                    .addOnCompleteListener(task -> {
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
        db.collection(MyFirestoreReferences.USERS_COLLECTION)
                .document(currentUser.getUid())
                .delete()
                .addOnSuccessListener(aVoid -> {
                    currentUser.delete()
                            .addOnSuccessListener(aVoid1 -> {
                                mAuth.signOut();
                                GoogleSignIn.getClient(this, new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                                                .requestIdToken(getString(R.string.default_web_client_id))
                                                .requestEmail()
                                                .build())
                                        .signOut()
                                        .addOnCompleteListener(task -> {
                                            Toast.makeText(this, "Account deleted successfully", Toast.LENGTH_SHORT).show();
                                            Intent intent = new Intent(AccountDetailsActivity.this, AccountLoginActivity.class);
                                            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                            startActivity(intent);
                                        });
                            })
                            .addOnFailureListener(e -> {
                                Toast.makeText(this, "Error deleting authentication: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                            });
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(this, "Error deleting user data: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                });
    }
}