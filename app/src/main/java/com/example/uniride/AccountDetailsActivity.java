package com.example.uniride;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.InputType;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
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
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.Map;

import de.hdodenhof.circleimageview.CircleImageView;

/**
 * Activity that displays and manages user account details, including profile information,
 * car details for drivers, and balance management functionality
 * Extends BottomNavigationActivity for consistent navigation across the app
 */
public class AccountDetailsActivity extends BottomNavigationActivity {
    // UI Elements for profile information
    private CircleImageView profileImage;
    private TextView nameText, emailText, phoneText, universityText, accountStatusText;
    private TextView carDetailsLabel, carDetailsText, balanceText;
    private LinearLayout balanceContainer;
    private Button editProfileButton, logoutButton, deleteAccountButton;
    private Button withdrawButton, depositButton;

    // Cards for conditional content display
    private CardView carDetailsCard, balanceCard;

    // Firebase and data instances
    private FirebaseAuth mAuth;
    private FirebaseFirestore db;
    private FirebaseUser currentUser;
    private UserModel userModel;

    private AlertDialog loadingDialog;

    /**
     * Initializes activity, sets up Firebase instances and UI components
     * Redirects to login if user is not authenticated
     */
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

    /**
     * Reloads user data when activity resumes
     * Ensures displayed information is current
     */
    @Override
    protected void onResume() {
        super.onResume();
        loadUserData(); // Reload data when returning to this activity
    }

    @Override
    protected int getSelectedItemId() {
        return R.id.account;
    }

    /**
     * Initializes all view elements from layout resources
     * Binds UI components to their respective variables
     */
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

    /**
     * Loads user data from Firestore and updates UI
     * Handles both basic user info and related object population
     */
    private void loadUserData() {
        db.collection(MyFirestoreReferences.USERS_COLLECTION)
                .document(currentUser.getUid())
                .get()
                .addOnSuccessListener(documentSnapshot -> {
                    if (documentSnapshot.exists()) {
                        userModel = UserModel.fromMap(documentSnapshot.getData());
                        // This immediately updates basic info that doesn't need population
                        nameText.setText(userModel.getName());
                        emailText.setText(userModel.getEmail());
                        phoneText.setText(userModel.getPhoneNumber());
                        profileImage.setImageResource(userModel.getPfp());

                        // Now populate related objects
                        userModel.populateObjects(db, this::onUserPopulateComplete);
                    }
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(this, "Error loading user data: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                });
    }

    /**
     * Callback for when user object population is complete
     * Updates UI elements with populated user data
     * @param user The fully populated UserModel object
     */

    private void onUserPopulateComplete(UserModel user) {
        // Update UI elements that depend on populated objects
        accountStatusText.setText(user.isDriver() ? "Driver" : "Passenger");

        if (user.getUniversity() != null) {
            universityText.setText(user.getUniversity().getName());
        }

        if (user.isDriver()) {
            balanceCard.setVisibility(View.VISIBLE);
            balanceText.setText(String.format("₱ %.2f", user.getBalance()));

            if (user.getCar() != null) {
                carDetailsCard.setVisibility(View.VISIBLE);
                String carDetails = String.format("%s %s\nPlate Number: %s",
                        user.getCar().getMake(),
                        user.getCar().getModel(),
                        user.getCar().getPlateNumber());
                carDetailsText.setText(carDetails);
            }
        } else {
            balanceCard.setVisibility(View.GONE);
            carDetailsCard.setVisibility(View.GONE);
        }
    }

    /**
     * Displays dialog for depositing funds
     * Includes quick amount options and input validation
     */
    private void showDepositDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        LayoutInflater inflater = getLayoutInflater();
        View dialogView = inflater.inflate(R.layout.dialog_deposit, null);
        builder.setView(dialogView);

        AlertDialog dialog = builder.create();

        // Get references to dialog views
        TextInputEditText amountInput = dialogView.findViewById(R.id.amountInput);
        Button amount20Btn = dialogView.findViewById(R.id.amount20);
        Button amount50Btn = dialogView.findViewById(R.id.amount50);
        Button amount100Btn = dialogView.findViewById(R.id.amount100);
        Button cancelBtn = dialogView.findViewById(R.id.cancelButton);
        Button depositBtn = dialogView.findViewById(R.id.depositButton);

        // Quick amount buttons
        amount20Btn.setOnClickListener(v -> amountInput.setText("20"));
        amount50Btn.setOnClickListener(v -> amountInput.setText("50"));
        amount100Btn.setOnClickListener(v -> amountInput.setText("100"));

        // Cancel button
        cancelBtn.setOnClickListener(v -> dialog.dismiss());

        // Deposit button
        depositBtn.setOnClickListener(v -> {
            String amountStr = amountInput.getText().toString();
            if (!amountStr.isEmpty()) {
                try {
                    double amount = Double.parseDouble(amountStr);
                    if (amount <= 0) {
                        amountInput.setError("Please enter a valid amount");
                        return;
                    }
                    processDeposit(amount);
                    dialog.dismiss();
                } catch (NumberFormatException e) {
                    amountInput.setError("Please enter a valid number");
                }
            } else {
                amountInput.setError("Amount is required");
            }
        });

        dialog.show();
    }

    /**
     * Displays dialog for withdrawing funds
     * Includes balance check and confirmation
     */
    private void showWithdrawDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        LayoutInflater inflater = getLayoutInflater();
        View dialogView = inflater.inflate(R.layout.dialog_withdraw, null);
        builder.setView(dialogView);

        AlertDialog dialog = builder.create();

        // Get references to dialog views
        TextView currentBalanceText = dialogView.findViewById(R.id.currentBalanceText);
        TextInputEditText amountInput = dialogView.findViewById(R.id.amountInput);
        Button amount20Btn = dialogView.findViewById(R.id.amount20);
        Button amount50Btn = dialogView.findViewById(R.id.amount50);
        Button amount100Btn = dialogView.findViewById(R.id.amount100);
        Button cancelBtn = dialogView.findViewById(R.id.cancelButton);
        Button withdrawBtn = dialogView.findViewById(R.id.withdrawButton);

        // Set current balance
        currentBalanceText.setText(String.format("Current Balance: ₱%.2f", userModel.getBalance()));

        // Quick amount buttons - disable if balance is insufficient
        amount20Btn.setEnabled(userModel.getBalance() >= 20);
        amount50Btn.setEnabled(userModel.getBalance() >= 50);
        amount100Btn.setEnabled(userModel.getBalance() >= 100);

        amount20Btn.setOnClickListener(v -> amountInput.setText("20"));
        amount50Btn.setOnClickListener(v -> amountInput.setText("50"));
        amount100Btn.setOnClickListener(v -> amountInput.setText("100"));

        // Cancel button
        cancelBtn.setOnClickListener(v -> dialog.dismiss());

        // Withdraw button
        withdrawBtn.setOnClickListener(v -> {
            String amountStr = amountInput.getText().toString();
            if (!amountStr.isEmpty()) {
                try {
                    double amount = Double.parseDouble(amountStr);
                    if (amount <= 0) {
                        amountInput.setError("Please enter a valid amount");
                        return;
                    }
                    if (amount > userModel.getBalance()) {
                        amountInput.setError("Amount exceeds available balance");
                        return;
                    }
                    // Show confirmation dialog
                    new AlertDialog.Builder(this)
                            .setTitle("Confirm Withdrawal")
                            .setMessage("Are you sure you want to withdraw ₱" + String.format("%.2f", amount) + "?")
                            .setPositiveButton("Withdraw", (confirmDialog, which) -> {
                                processWithdraw(amount);
                                dialog.dismiss();
                            })
                            .setNegativeButton("Cancel", null)
                            .show();
                } catch (NumberFormatException e) {
                    amountInput.setError("Please enter a valid number");
                }
            } else {
                amountInput.setError("Amount is required");
            }
        });

        // Add text change listener to validate amount in real time
        amountInput.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (!TextUtils.isEmpty(s)) {
                    try {
                        double amount = Double.parseDouble(s.toString());
                        if (amount > userModel.getBalance()) {
                            amountInput.setError("Amount exceeds available balance");
                            withdrawBtn.setEnabled(false);
                        } else {
                            amountInput.setError(null);
                            withdrawBtn.setEnabled(true);
                        }
                    } catch (NumberFormatException e) {
                        amountInput.setError("Please enter a valid number");
                        withdrawBtn.setEnabled(false);
                    }
                }
            }

            @Override
            public void afterTextChanged(Editable s) {}
        });

        dialog.show();
    }

    /**
     * Processes deposit transaction in Firestore
     * Updates local balance and UI
     * @param amount Amount to deposit
     */
    private void processDeposit(double amount) {
        // Show loading state
        showLoadingDialog("Processing deposit...");

        // Update local model
        double newBalance = userModel.getBalance() + amount;
        userModel.setBalance(newBalance);

        // Update Firestore
        db.collection(MyFirestoreReferences.USERS_COLLECTION)
                .document(currentUser.getUid())
                .update("balance", newBalance)
                .addOnSuccessListener(aVoid -> {
                    hideLoadingDialog();
                    // Update UI
                    balanceText.setText(String.format("₱ %.2f", newBalance));
                    showSuccessDialog("Successfully deposited ₱" + String.format("%.2f", amount));
                })
                .addOnFailureListener(e -> {
                    hideLoadingDialog();
                    // Revert local change on failure
                    userModel.setBalance(userModel.getBalance() - amount);
                    showErrorDialog("Failed to process deposit: " + e.getMessage());
                });
    }

    /**
     * Processes withdrawal transaction in Firestore
     * Updates local balance and UI
     * @param amount Amount to withdraw
     */
    private void processWithdraw(double amount) {
        // Show loading state
        showLoadingDialog("Processing withdrawal...");

        // Update local model
        double newBalance = userModel.getBalance() - amount;
        userModel.setBalance(newBalance);

        // Update Firestore
        db.collection(MyFirestoreReferences.USERS_COLLECTION)
                .document(currentUser.getUid())
                .update("balance", newBalance)
                .addOnSuccessListener(aVoid -> {
                    hideLoadingDialog();
                    // Update UI
                    balanceText.setText(String.format("₱ %.2f", newBalance));
                    showSuccessDialog("Successfully withdrew ₱" + String.format("%.2f", amount));
                })
                .addOnFailureListener(e -> {
                    hideLoadingDialog();
                    // Revert local change on failure
                    userModel.setBalance(userModel.getBalance() + amount);
                    showErrorDialog("Failed to process withdrawal: " + e.getMessage());
                });
    }



    /**
     * Shows loading dialog during async operations
     * @param message Message to display in loading dialog
     */
    private void showLoadingDialog(String message) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        View dialogView = getLayoutInflater().inflate(R.layout.dialog_loading, null);
        TextView messageText = dialogView.findViewById(R.id.messageText);
        messageText.setText(message);
        builder.setView(dialogView);
        builder.setCancelable(false);
        loadingDialog = builder.create();
        loadingDialog.show();
    }

    /**
     * Hides loading dialog when operation completes
     */
    private void hideLoadingDialog() {
        if (loadingDialog != null && loadingDialog.isShowing()) {
            loadingDialog.dismiss();
        }
    }

    /**
     * Shows success dialog with custom message
     * @param message Success message to display
     */
    private void showSuccessDialog(String message) {
        new AlertDialog.Builder(this)
                .setTitle("Success")
                .setMessage(message)
                .setPositiveButton("OK", null)
                .setIcon(R.drawable.ic_acceptedbookings)
                .show();
    }

    /**
     * Shows error dialog with custom message
     * @param message Error message to display
     */
    private void showErrorDialog(String message) {
        new AlertDialog.Builder(this)
                .setTitle("Error")
                .setMessage(message)
                .setPositiveButton("OK", null)
                .setIcon(android.R.drawable.ic_dialog_alert)
                .show();
    }

    /**
     * Sets up click listeners for all interactive elements
     * Handles navigation, logout, and account management actions
     */
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

        withdrawButton.setOnClickListener(v -> showWithdrawDialog());
        depositButton.setOnClickListener(v -> showDepositDialog());
    }

    /**
     * Shows confirmation dialog before account deletion
     */
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

    /**
     * Handles complete account deletion process
     * Removes user data from Firestore and authentication
     */
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