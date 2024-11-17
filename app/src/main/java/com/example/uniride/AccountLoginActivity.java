package com.example.uniride;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.GoogleAuthProvider;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.Query;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import com.facebook.AccessToken;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.login.LoginManager;
import com.facebook.login.LoginResult;
import com.google.firebase.auth.FacebookAuthProvider;


public class AccountLoginActivity extends AppCompatActivity {
    // Existing fields
    private EditText emailInput;
    private EditText passwordInput;
    private Button loginButton;
    private TextView signupText;
    private FirebaseAuth mAuth;
    private FirebaseFirestore db;

    // Google Sign In fields
    private static final int RC_SIGN_IN = 9001;
    private GoogleSignInClient mGoogleSignInClient;
    private Button btnGoogle;

    // Facebook fields
    private CallbackManager mCallbackManager;
    private Button btnFacebook;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_account_login);

        // Initialize Firebase Auth and Firestore
        mAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();

        // Initialize Google Sign In
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build();
        mGoogleSignInClient = GoogleSignIn.getClient(this, gso);

        // Initialize Facebook Login
        mCallbackManager = CallbackManager.Factory.create();

        // Initialize views
        emailInput = findViewById(R.id.inputEmail);
        passwordInput = findViewById(R.id.inputPassword);
        loginButton = findViewById(R.id.btnlogin);
        signupText = findViewById(R.id.textViewSignUp);
        btnGoogle = findViewById(R.id.btnGoogle);
        btnFacebook = findViewById(R.id.btnFacebook);

        // Set click listeners
        loginButton.setOnClickListener(v -> attemptLogin());
        signupText.setOnClickListener(v -> signup(v));
        btnGoogle.setOnClickListener(v -> signInWithGoogle());
        btnFacebook.setOnClickListener(v -> signInWithFacebook());

        setupForgotPassword();
        setupFacebookCallback();
    }

    @Override
    public void onStart() {
        super.onStart();
        // Check if user is signed in and update UI accordingly
        FirebaseUser currentUser = mAuth.getCurrentUser();
        if(currentUser != null){
            navigateToHome();
        }
    }

    private void setupFacebookCallback() {
        LoginManager.getInstance().registerCallback(mCallbackManager,
                new FacebookCallback<LoginResult>() {
                    @Override
                    public void onSuccess(LoginResult loginResult) {
                        handleFacebookAccessToken(loginResult.getAccessToken());
                    }

                    @Override
                    public void onCancel() {
                        Toast.makeText(AccountLoginActivity.this,
                                "Facebook login cancelled", Toast.LENGTH_SHORT).show();
                    }

                    @Override
                    public void onError(FacebookException error) {
                        Toast.makeText(AccountLoginActivity.this,
                                "Facebook login failed: " + error.getMessage(),
                                Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void signInWithFacebook() {
        LoginManager.getInstance().logInWithReadPermissions(this,
                Arrays.asList("email", "public_profile"));
    }

    private void handleFacebookAccessToken(AccessToken token) {
        AuthCredential credential = FacebookAuthProvider.getCredential(token.getToken());
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, task -> {
                    if (task.isSuccessful()) {
                        FirebaseUser user = mAuth.getCurrentUser();
                        checkUserInFirestore(user);
                    } else {
                        Toast.makeText(AccountLoginActivity.this,
                                "Authentication failed: " + task.getException().getMessage(),
                                Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void checkUserInFirestore(FirebaseUser user) {
        db.collection(MyFirestoreReferences.USERS_COLLECTION)
                .document(user.getUid())
                .get()
                .addOnSuccessListener(documentSnapshot -> {
                    if (documentSnapshot.exists()) {
                        navigateToHome();
                    } else {
                        createFacebookUserInFirestore(user);
                    }
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(AccountLoginActivity.this,
                            "Error checking user data: " + e.getMessage(),
                            Toast.LENGTH_SHORT).show();
                    mAuth.signOut();
                });
    }

    private void createFacebookUserInFirestore(FirebaseUser user) {
        db.collection(MyFirestoreReferences.USERS_COLLECTION)
                .orderBy("userID", Query.Direction.DESCENDING)
                .limit(1)
                .get()
                .addOnCompleteListener(task -> {
                    int nextUserId = 30001;

                    if (task.isSuccessful() && !task.getResult().isEmpty()) {
                        DocumentSnapshot document = task.getResult().getDocuments().get(0);
                        Long highestId = document.getLong("userID");
                        if (highestId != null) {
                            nextUserId = highestId.intValue() + 1;
                        }
                    }

                    Map<String, Object> userData = new HashMap<>();
                    userData.put("userID", nextUserId);
                    userData.put("name", user.getDisplayName());
                    userData.put("email", user.getEmail());
                    userData.put("phoneNumber", "");
                    userData.put("university", "");
                    userData.put("isDriver", false);
                    userData.put("balance", 0.0);
                    userData.put("pfp", R.drawable.default_profile_image);

                    db.collection(MyFirestoreReferences.USERS_COLLECTION)
                            .document(user.getUid())
                            .set(userData)
                            .addOnSuccessListener(aVoid -> {
                                Intent intent = new Intent(AccountLoginActivity.this,
                                        AccountCompleteProfileActivity.class);
                                startActivity(intent);
                                finish();
                            })
                            .addOnFailureListener(e -> {
                                Toast.makeText(AccountLoginActivity.this,
                                        "Error creating user profile: " + e.getMessage(),
                                        Toast.LENGTH_SHORT).show();
                                mAuth.signOut();
                            });
                });
    }

    private void attemptLogin() {
        // Reset errors
        emailInput.setError(null);
        passwordInput.setError(null);

        // Get values
        String email = emailInput.getText().toString().trim();
        String password = passwordInput.getText().toString().trim();

        // Validate input
        boolean cancel = false;
        View focusView = null;

        // Check for a valid password
        if (TextUtils.isEmpty(password)) {
            passwordInput.setError("Password is required");
            focusView = passwordInput;
            cancel = true;
        } else if (password.length() < 6) {
            passwordInput.setError("Password must be at least 6 characters");
            focusView = passwordInput;
            cancel = true;
        }

        // Check for a valid email address
        if (TextUtils.isEmpty(email)) {
            emailInput.setError("Email is required");
            focusView = emailInput;
            cancel = true;
        } else if (!email.contains("@")) {
            emailInput.setError("Enter a valid email address");
            focusView = emailInput;
            cancel = true;
        }

        if (cancel) {
            // There was an error; don't attempt login and focus the first form field with an error
            focusView.requestFocus();
        } else {
            // Show a progress spinner, and perform the login attempt
            performLogin(email, password);
        }
    }

    private void performLogin(String email, String password) {
        mAuth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, task -> {
                    if (task.isSuccessful()) {
                        // Sign in success, update UI with the signed-in user's information
                        FirebaseUser user = mAuth.getCurrentUser();

                        // Check if user exists in Firestore
                        db.collection(MyFirestoreReferences.USERS_COLLECTION)
                                .document(user.getUid())
                                .get()
                                .addOnSuccessListener(documentSnapshot -> {
                                    if (documentSnapshot.exists()) {
                                        // User exists in Firestore, proceed to home
                                        navigateToHome();
                                    } else {
                                        // This shouldn't normally happen - user exists in Auth but not in Firestore
                                        Toast.makeText(AccountLoginActivity.this,
                                                "Error: User data not found",
                                                Toast.LENGTH_SHORT).show();
                                        mAuth.signOut();
                                    }
                                })
                                .addOnFailureListener(e -> {
                                    Toast.makeText(AccountLoginActivity.this,
                                            "Error checking user data: " + e.getMessage(),
                                            Toast.LENGTH_SHORT).show();
                                    mAuth.signOut();
                                });
                    } else {
                        // If sign in fails, display a message to the user
                        String errorMessage = task.getException() != null ?
                                task.getException().getMessage() :
                                "Authentication failed";
                        Toast.makeText(AccountLoginActivity.this,
                                "Login failed: " + errorMessage,
                                Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void navigateToHome() {
        Intent intent = new Intent(AccountLoginActivity.this, BookingHomeActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        finish();
    }

    public void signup(View view) {
        Intent intent = new Intent(this, AccountRegisterActivity.class);
        startActivity(intent);
        finish();
    }

    private void setupForgotPassword() {
        TextView forgotPasswordText = findViewById(R.id.forgotPassword);
        forgotPasswordText.setOnClickListener(v -> {
            String email = emailInput.getText().toString().trim();

            if (TextUtils.isEmpty(email)) {
                emailInput.setError("Please enter your email first");
                emailInput.requestFocus();
                return;
            }

            // Show a progress dialog
            ProgressDialog progressDialog = new ProgressDialog(this);
            progressDialog.setMessage("Sending reset email...");
            progressDialog.show();

            mAuth.sendPasswordResetEmail(email)
                    .addOnCompleteListener(task -> {
                        progressDialog.dismiss();
                        if (task.isSuccessful()) {
                            Toast.makeText(AccountLoginActivity.this,
                                    "Password reset email sent. Please check your inbox.",
                                    Toast.LENGTH_LONG).show();
                        } else {
                            Toast.makeText(AccountLoginActivity.this,
                                    "Failed to send reset email. " + task.getException().getMessage(),
                                    Toast.LENGTH_LONG).show();
                        }
                    });
        });
    }

    // Add these new methods for Google Sign In
    private void signInWithGoogle() {
        Intent signInIntent = mGoogleSignInClient.getSignInIntent();
        startActivityForResult(signInIntent, RC_SIGN_IN);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        // Handle Facebook login result
        mCallbackManager.onActivityResult(requestCode, resultCode, data);

        // Handle Google login result
        if (requestCode == RC_SIGN_IN) {
            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
            try {
                GoogleSignInAccount account = task.getResult(ApiException.class);
                firebaseAuthWithGoogle(account.getIdToken());
            } catch (ApiException e) {
                Toast.makeText(this, "Google sign in failed: " + e.getMessage(),
                        Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void firebaseAuthWithGoogle(String idToken) {
        AuthCredential credential = GoogleAuthProvider.getCredential(idToken, null);
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, task -> {
                    if (task.isSuccessful()) {
                        FirebaseUser user = mAuth.getCurrentUser();

                        // Check if user exists in Firestore
                        db.collection(MyFirestoreReferences.USERS_COLLECTION)
                                .document(user.getUid())
                                .get()
                                .addOnSuccessListener(documentSnapshot -> {
                                    if (documentSnapshot.exists()) {
                                        // User exists, proceed to home
                                        navigateToHome();
                                    } else {
                                        // Create new user in Firestore
                                        createGoogleUserInFirestore(user);
                                    }
                                })
                                .addOnFailureListener(e -> {
                                    Toast.makeText(AccountLoginActivity.this,
                                            "Error checking user data: " + e.getMessage(),
                                            Toast.LENGTH_SHORT).show();
                                    mAuth.signOut();
                                });
                    } else {
                        Toast.makeText(AccountLoginActivity.this,
                                "Authentication failed: " + task.getException().getMessage(),
                                Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void createGoogleUserInFirestore(FirebaseUser user) {
        // First query to get the highest existing userID
        db.collection(MyFirestoreReferences.USERS_COLLECTION)
                .orderBy("userID", Query.Direction.DESCENDING)
                .limit(1)
                .get()
                .addOnCompleteListener(task -> {
                    int nextUserId = 30001; // Default starting ID

                    if (task.isSuccessful() && !task.getResult().isEmpty()) {
                        DocumentSnapshot document = task.getResult().getDocuments().get(0);
                        Long highestId = document.getLong("userID");
                        if (highestId != null) {
                            nextUserId = highestId.intValue() + 1;
                        }
                    }

                    Map<String, Object> userData = new HashMap<>();
                    userData.put("userID", nextUserId);
                    userData.put("name", user.getDisplayName());
                    userData.put("email", user.getEmail());
                    userData.put("phoneNumber", user.getPhoneNumber() != null ?
                            user.getPhoneNumber() : "");
                    userData.put("university", "");
                    userData.put("isDriver", false);
                    userData.put("balance", 0.0);
                    userData.put("pfp", R.drawable.default_profile_image);

                    db.collection(MyFirestoreReferences.USERS_COLLECTION)
                            .document(user.getUid())
                            .set(userData)
                            .addOnSuccessListener(aVoid -> {
                                Intent intent = new Intent(AccountLoginActivity.this, AccountCompleteProfileActivity.class);
                                startActivity(intent);
                                finish();
                            })
                            .addOnFailureListener(e -> {
                                Toast.makeText(AccountLoginActivity.this,
                                        "Error creating user profile: " + e.getMessage(),
                                        Toast.LENGTH_SHORT).show();
                                mAuth.signOut();
                            });
                });
    }

    private void signOut() {
        // Sign out from Firebase
        mAuth.signOut();

        // Sign out from Google
        if (mGoogleSignInClient != null) {
            mGoogleSignInClient.signOut().addOnCompleteListener(this, task -> {
                // Google Sign out complete
            });
        }
    }
}