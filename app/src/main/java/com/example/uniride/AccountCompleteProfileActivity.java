package com.example.uniride;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.Toast;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import de.hdodenhof.circleimageview.CircleImageView;

public class AccountCompleteProfileActivity extends AppCompatActivity {
    private CircleImageView profileImageView;
    private Button selectAvatarButton;
    private EditText usernameInput;
    private EditText phoneInput;
    private Spinner universitySpinner;
    private Button completeButton;
    private int selectedAvatarResource = R.drawable.a_icon;

    private FirebaseAuth mAuth;
    private FirebaseFirestore db;
    private FirebaseUser currentUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_account_complete_profile);

        mAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();
        currentUser = mAuth.getCurrentUser();

        if (currentUser == null) {
            startActivity(new Intent(this, AccountLoginActivity.class));
            finish();
            return;
        }

        initializeViews();
        setupUniversitySpinner();

        if (currentUser.getDisplayName() != null) {
            usernameInput.setText(currentUser.getDisplayName());
        }

        if (currentUser.getPhoneNumber() != null) {
            phoneInput.setText(currentUser.getPhoneNumber());
        }

        selectAvatarButton.setOnClickListener(v -> showAvatarDialog());
        completeButton.setOnClickListener(v -> attemptProfileCompletion());
    }

    private void initializeViews() {
        profileImageView = findViewById(R.id.profileImageView);
        selectAvatarButton = findViewById(R.id.selectAvatarButton);
        usernameInput = findViewById(R.id.inputUsername);
        phoneInput = findViewById(R.id.inputPhone);
        universitySpinner = findViewById(R.id.universitySpinner);
        completeButton = findViewById(R.id.btnComplete);
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

    private void attemptProfileCompletion() {
        String username = usernameInput.getText().toString().trim();
        String phone = phoneInput.getText().toString().trim();
        LocationModel selectedUniversity = (LocationModel) universitySpinner.getSelectedItem();

        if (!validateInputs(username, phone)) {
            return;
        }

        createUserInFirestore(username, phone, selectedUniversity.getLocationID());
    }

    private boolean validateInputs(String username, String phone) {
        if (TextUtils.isEmpty(username)) {
            usernameInput.setError("Username is required");
            usernameInput.requestFocus();
            return false;
        }

        if (TextUtils.isEmpty(phone) || phone.length() < 11) {
            phoneInput.setError("Valid 11-digit phone number is required");
            phoneInput.requestFocus();
            return false;
        }

        return true;
    }

    private void createUserInFirestore(String username, String phone, int universityId) {
        db.collection(MyFirestoreReferences.USERS_COLLECTION)
                .orderBy("userID", Query.Direction.DESCENDING)
                .limit(1)
                .get()
                .addOnCompleteListener(task -> {
                    int nextUserId = 30001;

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
                    user.put("email", currentUser.getEmail());
                    user.put("phoneNumber", phone);
                    user.put("universityID", universityId);
                    user.put("isDriver", false);
                    user.put("carID", 0);
                    user.put("balance", 0.0);

                    db.collection(MyFirestoreReferences.USERS_COLLECTION)
                            .document(currentUser.getUid())
                            .set(user)
                            .addOnSuccessListener(aVoid -> {
                                Toast.makeText(AccountCompleteProfileActivity.this,
                                        "Profile completed successfully!", Toast.LENGTH_SHORT).show();
                                navigateToHome();
                            })
                            .addOnFailureListener(e -> {
                                Toast.makeText(AccountCompleteProfileActivity.this,
                                        "Error creating profile: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                            });
                });
    }

    private void navigateToHome() {
        Intent intent = new Intent(AccountCompleteProfileActivity.this, AccountDetailsActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        finish();
    }

    @Override
    public void onBackPressed() {
        new AlertDialog.Builder(this)
                .setTitle("Cancel Registration")
                .setMessage("If you go back, your account will not be created. Are you sure?")
                .setPositiveButton("Yes", (dialog, which) -> {
                    String uid = currentUser.getUid();

                    db.collection(MyFirestoreReferences.USERS_COLLECTION)
                            .document(uid)
                            .delete()
                            .addOnCompleteListener(task -> {
                                if (currentUser != null) {
                                    currentUser.delete().addOnCompleteListener(deleteTask -> {
                                        mAuth.signOut();
                                        GoogleSignInClient googleSignInClient = GoogleSignIn.getClient(this,
                                                new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                                                        .requestIdToken(getString(R.string.default_web_client_id))
                                                        .requestEmail()
                                                        .build());
                                        googleSignInClient.signOut().addOnCompleteListener(signOutTask -> {
                                            startActivity(new Intent(AccountCompleteProfileActivity.this, AccountLoginActivity.class));
                                            finish();
                                            super.onBackPressed();
                                        });
                                    });
                                }
                            });
                })
                .setNegativeButton("No", null)
                .show();
    }
}