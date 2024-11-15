package com.example.uniride;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import de.hdodenhof.circleimageview.CircleImageView;

public class AccountRegisterActivity extends AppCompatActivity {
    private CircleImageView profileImageView;
    private Button selectAvatarButton;
    private EditText usernameInput;
    private EditText emailInput;
    private EditText phoneInput;
    private Spinner universitySpinner;
    private EditText passwordInput;
    private EditText confirmPasswordInput;
    private Button registerButton;
    private TextView loginRedirectText;
    private Uri selectedImageUri;

    // Add ProgressBar
    private ProgressBar progressBar;
    private int selectedAvatarResource = R.drawable.a_icon; // Default avatar

    private FirebaseAuth mAuth;
    private FirebaseFirestore db;
    private ActivityResultLauncher<String> imagePickerLauncher;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_account_register);

        // Initialize Firebase
        mAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();

        // Initialize views
        initializeViews();
        setupUniversitySpinner();

        // Set click listeners
        selectAvatarButton.setOnClickListener(v -> showAvatarDialog());
        registerButton.setOnClickListener(v -> attemptRegistration());
        loginRedirectText.setOnClickListener(v -> redirectToLogin(v));
    }

    private void initializeViews() {
        profileImageView = findViewById(R.id.profileImageView);
        selectAvatarButton = findViewById(R.id.selectAvatarButton);
        usernameInput = findViewById(R.id.inputUsername);
        emailInput = findViewById(R.id.inputEmail);
        phoneInput = findViewById(R.id.inputPhone);
        universitySpinner = findViewById(R.id.universitySpinner);
        passwordInput = findViewById(R.id.inputPassword);
        confirmPasswordInput = findViewById(R.id.inputConfirmPassword);
        registerButton = findViewById(R.id.btnRegister);
        loginRedirectText = findViewById(R.id.alreadyHaveAccount);
        progressBar = findViewById(R.id.progressBar);
    }

    private void showAvatarDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        View dialogView = getLayoutInflater().inflate(R.layout.dialog_avatar_selection, null);
        builder.setView(dialogView);
        builder.setTitle("Select Avatar");

        AlertDialog dialog = builder.create();

        // Find all avatar options in dialog
        ImageView avatar1 = dialogView.findViewById(R.id.avatar1);
        ImageView avatar2 = dialogView.findViewById(R.id.avatar2);
        ImageView avatar3 = dialogView.findViewById(R.id.avatar3);
        ImageView avatar4 = dialogView.findViewById(R.id.avatar4);
        ImageView avatar5 = dialogView.findViewById(R.id.avatar5);
        ImageView avatar6 = dialogView.findViewById(R.id.avatar6);

        // Set click listeners for all avatars
        avatar1.setOnClickListener(v -> {
            selectedAvatarResource = R.drawable.a_icon;
            profileImageView.setImageResource(selectedAvatarResource);
            dialog.dismiss();
        });

        avatar2.setOnClickListener(v -> {
            selectedAvatarResource = R.drawable.b_icon;
            profileImageView.setImageResource(selectedAvatarResource);
            dialog.dismiss();
        });

        avatar3.setOnClickListener(v -> {
            selectedAvatarResource = R.drawable.c_icon;
            profileImageView.setImageResource(selectedAvatarResource);
            dialog.dismiss();
        });

        avatar4.setOnClickListener(v -> {
            selectedAvatarResource = R.drawable.d_icon;
            profileImageView.setImageResource(selectedAvatarResource);
            dialog.dismiss();
        });

        avatar5.setOnClickListener(v -> {
            selectedAvatarResource = R.drawable.e_icon;
            profileImageView.setImageResource(selectedAvatarResource);
            dialog.dismiss();
        });

        avatar6.setOnClickListener(v -> {
            selectedAvatarResource = R.drawable.f_icon;
            profileImageView.setImageResource(selectedAvatarResource);
            dialog.dismiss();
        });

        dialog.show();
    }

    private void setupUniversitySpinner() {
        ArrayList<LocationModel> allLocations = DataGenerator.loadLocationData();
        List<LocationModel> universities = new ArrayList<>();

        // Filter for universities only
        for (LocationModel location : allLocations) {
            if (location.getIsUniversity()) {
                universities.add(location);
            }
        }

        ArrayAdapter<LocationModel> adapter = new ArrayAdapter<>(
                this,
                android.R.layout.simple_spinner_item,
                universities
        );
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        universitySpinner.setAdapter(adapter);
    }

    private void attemptRegistration() {
        // Reset errors
        usernameInput.setError(null);
        emailInput.setError(null);
        phoneInput.setError(null);
        passwordInput.setError(null);
        confirmPasswordInput.setError(null);

        // Get values
        String username = usernameInput.getText().toString().trim();
        String email = emailInput.getText().toString().trim();
        String phone = phoneInput.getText().toString().trim();
        LocationModel selectedUniversity = (LocationModel) universitySpinner.getSelectedItem();
        String password = passwordInput.getText().toString();
        String confirmPassword = confirmPasswordInput.getText().toString();

        // Validate input
        if (!validateInputs(username, email, phone, password, confirmPassword)) {
            return;
        }

        // Perform registration
        performRegistration(username, email, phone, selectedUniversity.getName(), password);
    }

    private boolean validateInputs(String username, String email, String phone,
                                   String password, String confirmPassword) {
        if (TextUtils.isEmpty(username)) {
            usernameInput.setError("Username is required");
            usernameInput.requestFocus();
            return false;
        }

        if (TextUtils.isEmpty(email) || !email.contains("@")) {
            emailInput.setError("Valid email is required");
            emailInput.requestFocus();
            return false;
        }

        if (TextUtils.isEmpty(phone) || phone.length() < 11) {
            phoneInput.setError("Valid 11-digit phone number is required");
            phoneInput.requestFocus();
            return false;
        }

        if (TextUtils.isEmpty(password) || password.length() < 6) {
            passwordInput.setError("Password must be at least 6 characters");
            passwordInput.requestFocus();
            return false;
        }

        if (!password.equals(confirmPassword)) {
            confirmPasswordInput.setError("Passwords do not match");
            confirmPasswordInput.requestFocus();
            return false;
        }

        return true;
    }

    private void performRegistration(String username, String email, String phone,
                                     String university, String password) {

        // Show progress before starting registration
        progressBar.setVisibility(View.VISIBLE);

        mAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, task -> {
                    progressBar.setVisibility(View.GONE);
                    if (task.isSuccessful()) {
                        FirebaseUser user = mAuth.getCurrentUser();
                        createUserInFirestore(user.getUid(), username, email, phone, university);
                    } else {
                        String errorMessage = task.getException() != null ?
                                task.getException().getMessage() :
                                "Registration failed";
                        Toast.makeText(AccountRegisterActivity.this,
                                "Registration failed: " + errorMessage,
                                Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void createUserInFirestore(String uid, String username, String email, String phone, String university) {
        // First query to get the highest existing userID
        db.collection(MyFirestoreReferences.USERS_COLLECTION)
                .orderBy("userID", Query.Direction.DESCENDING)
                .limit(1)
                .get()
                .addOnCompleteListener(task -> {
                    int nextUserId = 30001; // Default starting ID

                    if (task.isSuccessful() && !task.getResult().isEmpty()) {
                        // Get the highest existing userID and increment
                        DocumentSnapshot document = task.getResult().getDocuments().get(0);
                        Long highestId = document.getLong("userID");
                        if (highestId != null) {
                            nextUserId = highestId.intValue() + 1;
                        }
                    }

                    // Create the user document with the new ID
                    Map<String, Object> user = new HashMap<>();
                    user.put("userID", nextUserId);
                    user.put("pfp", selectedAvatarResource);
                    user.put("name", username);
                    user.put("email", email);
                    user.put("phoneNumber", phone);
                    user.put("university", university);
                    user.put("isDriver", false);
                    user.put("balance", 0.0);

                    db.collection(MyFirestoreReferences.USERS_COLLECTION)
                            .document(uid)
                            .set(user)
                            .addOnSuccessListener(aVoid -> {
                                Toast.makeText(AccountRegisterActivity.this,
                                        "Registration successful!", Toast.LENGTH_SHORT).show();
                                navigateToHome();
                            })
                            .addOnFailureListener(e -> {
                                Toast.makeText(AccountRegisterActivity.this,
                                        "Failed to create user profile: " + e.getMessage(),
                                        Toast.LENGTH_SHORT).show();
                                if (mAuth.getCurrentUser() != null) {
                                    mAuth.getCurrentUser().delete();
                                }
                            });
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(AccountRegisterActivity.this,
                            "Failed to generate user ID: " + e.getMessage(),
                            Toast.LENGTH_SHORT).show();
                    if (mAuth.getCurrentUser() != null) {
                        mAuth.getCurrentUser().delete();
                    }
                });
    }

    private void navigateToHome() {
        Intent intent = new Intent(AccountRegisterActivity.this, BookingHomeActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        finish();
    }

    public void redirectToLogin(View v) {
        Intent intent = new Intent(this, AccountLoginActivity.class);
        startActivity(intent);
        finish();
    }
}