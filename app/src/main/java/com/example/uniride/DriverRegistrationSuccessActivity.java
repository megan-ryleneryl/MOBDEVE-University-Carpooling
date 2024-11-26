package com.example.uniride;

import android.content.Intent;
import android.os.Bundle;
import android.text.InputType;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AlertDialog;

import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.DocumentReference;

/**
 * Activity to display if the Driver Registration was successful, prompting next steps to user
 */
public class DriverRegistrationSuccessActivity extends BottomNavigationActivity {
    private TextView balanceText;
    private Button depositButton;
    private Button createRideButton;
    private Button laterButton;
    private FirebaseAuth mAuth;
    private FirebaseFirestore db;
    private FirebaseUser currentUser;
    private double currentBalance = 0.0;
    private static final double MIN_BALANCE = 20.0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_driver_registration_success);

        // Initialize Firebase
        mAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();
        currentUser = mAuth.getCurrentUser();

        if (currentUser == null) {
            startActivity(new Intent(this, AccountLoginActivity.class));
            finish();
            return;
        }

        initializeViews();
        setupListeners();
        loadUserBalance();
    }

    private void initializeViews() {
        balanceText = findViewById(R.id.balanceText);
        depositButton = findViewById(R.id.depositButton);
        createRideButton = findViewById(R.id.createRideButton);
        laterButton = findViewById(R.id.laterButton);
    }

    private void setupListeners() {
        depositButton.setOnClickListener(v -> showDepositDialog());

        createRideButton.setOnClickListener(v -> {
            Intent intent = new Intent(this, RideCreate.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
        });

        laterButton.setOnClickListener(v -> {
            Intent intent = new Intent(this, DriverHubActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
        });
    }

    private void loadUserBalance() {
        DocumentReference userRef = db.collection(MyFirestoreReferences.USERS_COLLECTION)
                .document(currentUser.getUid());

        userRef.get().addOnSuccessListener(documentSnapshot -> {
            if (documentSnapshot.exists()) {
                Double balance = documentSnapshot.getDouble("balance");
                currentBalance = balance != null ? balance : 0.0;
                updateBalanceDisplay();
                createRideButton.setEnabled(currentBalance >= MIN_BALANCE);
            }
        });
    }

    private void showDepositDialog() {
        // Create dialog
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        View dialogView = getLayoutInflater().inflate(R.layout.dialog_deposit, null);
        builder.setView(dialogView);
        AlertDialog dialog = builder.create();

        // Get views
        TextInputEditText amountInput = dialogView.findViewById(R.id.amountInput);
        Button amount20Btn = dialogView.findViewById(R.id.amount20);
        Button amount50Btn = dialogView.findViewById(R.id.amount50);
        Button amount100Btn = dialogView.findViewById(R.id.amount100);
        Button cancelBtn = dialogView.findViewById(R.id.cancelButton);
        Button depositBtn = dialogView.findViewById(R.id.depositButton);

        // Quick amount button listeners
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
                    if (amount > 0) {
                        processDeposit(amount);
                        dialog.dismiss();
                    } else {
                        amountInput.setError("Please enter a valid amount");
                    }
                } catch (NumberFormatException e) {
                    amountInput.setError("Please enter a valid number");
                }
            } else {
                amountInput.setError("Amount is required");
            }
        });

        // Show dialog
        dialog.show();

        // Make sure dialog can't be dismissed by clicking outside
        dialog.setCanceledOnTouchOutside(false);
    }

    private void processDeposit(double amount) {
        DocumentReference userRef = db.collection(MyFirestoreReferences.USERS_COLLECTION)
                .document(currentUser.getUid());

        userRef.get().addOnSuccessListener(documentSnapshot -> {
            if (documentSnapshot.exists()) {
                Double currentBalance = documentSnapshot.getDouble("balance");
                double newBalance = (currentBalance != null ? currentBalance : 0.0) + amount;

                userRef.update("balance", newBalance)
                        .addOnSuccessListener(aVoid -> {
                            this.currentBalance = newBalance;
                            updateBalanceDisplay();
                            createRideButton.setEnabled(newBalance >= MIN_BALANCE);
                            Toast.makeText(this, "Successfully deposited ₱" + amount, Toast.LENGTH_SHORT).show();
                        })
                        .addOnFailureListener(e -> Toast.makeText(this,
                                "Error processing deposit: " + e.getMessage(), Toast.LENGTH_SHORT).show());
            }
        });
    }

    private void updateBalanceDisplay() {
        balanceText.setText(String.format("Current Balance: ₱%.2f", currentBalance));
    }

    @Override
    protected int getSelectedItemId() {
        return R.id.driver;
    }
}