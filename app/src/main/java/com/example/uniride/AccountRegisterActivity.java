package com.example.uniride;

import android.content.Intent;
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
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
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
    private ProgressBar progressBar;
    private int selectedAvatarResource = R.drawable.a_icon;

    private FirebaseAuth mAuth;
    private FirebaseFirestore db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_account_register);

        mAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();

        initializeViews();
        setupUniversitySpinner();
        setClickListeners();
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

    private void setupUniversitySpinner() {
        ArrayList<LocationModel> allLocations = DataGenerator.loadLocationData();
        List<LocationModel> universities = new ArrayList<>();

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

    private void setClickListeners() {
        selectAvatarButton.setOnClickListener(v -> showAvatarDialog());
        registerButton.setOnClickListener(v -> attemptRegistration());
        loginRedirectText.setOnClickListener(v -> redirectToLogin());
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

            profileImageView.setImageResource(selectedAvatarResource);
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

    private void attemptRegistration() {
        String username = usernameInput.getText().toString().trim();
        String email = emailInput.getText().toString().trim();
        String phone = phoneInput.getText().toString().trim();
        LocationModel selectedUniversity = (LocationModel) universitySpinner.getSelectedItem();
        String password = passwordInput.getText().toString();
        String confirmPassword = confirmPasswordInput.getText().toString();

        if (!validateInputs(username, email, phone, password, confirmPassword)) {
            return;
        }

        progressBar.setVisibility(View.VISIBLE);
        performRegistration(username, email, phone, selectedUniversity.getLocationID(), password);
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
                                     int universityID, String password) {
        mAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, task -> {
                    if (task.isSuccessful()) {
                        FirebaseUser user = mAuth.getCurrentUser();
                        createUserInFirestore(user.getUid(), username, email, phone, universityID);
                    } else {
                        progressBar.setVisibility(View.GONE);
                        String errorMessage = task.getException() != null ?
                                task.getException().getMessage() : "Registration failed";
                        Toast.makeText(AccountRegisterActivity.this,
                                "Registration failed: " + errorMessage,
                                Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void createUserInFirestore(String uid, String username, String email,
                                       String phone, int universityID) {
        db.collection(MyFirestoreReferences.USERS_COLLECTION)
                .orderBy("userID", Query.Direction.DESCENDING)
                .limit(1)
                .get()
                .addOnCompleteListener(task -> {
                    int nextUserId = 30001; // Default starting ID

                    if (task.isSuccessful() && !task.getResult().isEmpty()) {
                        Long highestId = task.getResult().getDocuments().get(0).getLong("userID");
                        if (highestId != null) {
                            nextUserId = highestId.intValue() + 1;
                        }
                    }

                    Map<String, Object> user = new HashMap<>();
                    user.put("userID", nextUserId);
                    user.put("pfp", selectedAvatarResource);
                    user.put("name", username);
                    user.put("email", email);
                    user.put("phoneNumber", phone);
                    user.put("universityID", universityID);
                    user.put("isDriver", false);
                    user.put("balance", 0.0);

                    db.collection(MyFirestoreReferences.USERS_COLLECTION)
                            .document(uid)
                            .set(user)
                            .addOnSuccessListener(aVoid -> {
                                progressBar.setVisibility(View.GONE);
                                Toast.makeText(AccountRegisterActivity.this,
                                        "Registration successful!", Toast.LENGTH_SHORT).show();
                                navigateToHome();
                            })
                            .addOnFailureListener(e -> {
                                progressBar.setVisibility(View.GONE);
                                Toast.makeText(AccountRegisterActivity.this,
                                        "Failed to create user profile: " + e.getMessage(),
                                        Toast.LENGTH_SHORT).show();
                                if (mAuth.getCurrentUser() != null) {
                                    mAuth.getCurrentUser().delete();
                                }
                            });
                });
    }

    private void navigateToHome() {
        Intent intent = new Intent(AccountRegisterActivity.this, BookingHomeActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        finish();
    }

    private void redirectToLogin() {
        Intent intent = new Intent(this, AccountLoginActivity.class);
        startActivity(intent);
        finish();
    }
}