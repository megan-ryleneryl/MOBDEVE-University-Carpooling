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
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.GoogleAuthProvider;

import com.facebook.AccessToken;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.login.LoginManager;
import com.facebook.login.LoginResult;
import com.facebook.FacebookSdk;
import com.google.firebase.auth.FacebookAuthProvider;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

public class AccountLoginActivity extends AppCompatActivity {
    // Fields for UI elements
    private EditText emailInput;
    private EditText passwordInput;
    private Button loginButton;
    private TextView signupText;
    private TextView forgotPasswordText;
    private Button btnGoogle;
    private Button btnFacebook;
    private ProgressDialog progressDialog;

    // Firebase related fields
    private FirebaseAuth mAuth;
    private FirebaseFirestore db;
    private GoogleSignInClient mGoogleSignInClient;
    private CallbackManager mCallbackManager;
    private static final int RC_SIGN_IN = 9001;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_account_login);

        // Initialize Firebase instances
        mAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();

        // Initialize Facebook SDK
        FacebookSdk.sdkInitialize(getApplicationContext());
        mCallbackManager = CallbackManager.Factory.create();

        // Configure Google Sign In
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build();
        mGoogleSignInClient = GoogleSignIn.getClient(this, gso);

        initializeViews();
        setupClickListeners();
        setupFacebookCallback();
    }

    private void initializeViews() {
        emailInput = findViewById(R.id.inputEmail);
        passwordInput = findViewById(R.id.inputPassword);
        loginButton = findViewById(R.id.btnlogin);
        signupText = findViewById(R.id.textViewSignUp);
        forgotPasswordText = findViewById(R.id.forgotPassword);
        btnGoogle = findViewById(R.id.btnGoogle);
        btnFacebook = findViewById(R.id.btnFacebook);

        progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Please wait...");
        progressDialog.setCancelable(false);
    }

    private void setupClickListeners() {
        loginButton.setOnClickListener(v -> attemptLogin());
        signupText.setOnClickListener(v -> redirectToSignup());
        forgotPasswordText.setOnClickListener(v -> handleForgotPassword());
        btnGoogle.setOnClickListener(v -> signInWithGoogle());
        btnFacebook.setOnClickListener(v -> signInWithFacebook());
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
                    public void onError(FacebookException exception) {
                        Toast.makeText(AccountLoginActivity.this,
                                "Facebook login error: " + exception.getMessage(),
                                Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void attemptLogin() {
        // Reset errors
        emailInput.setError(null);
        passwordInput.setError(null);

        String email = emailInput.getText().toString().trim();
        String password = passwordInput.getText().toString().trim();

        if (!validateLoginInput(email, password)) {
            return;
        }

        progressDialog.show();
        mAuth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, task -> {
                    progressDialog.dismiss();
                    if (task.isSuccessful()) {
                        FirebaseUser user = mAuth.getCurrentUser();
                        checkUserInFirestore(user.getUid());
                    } else {
                        String errorMessage = task.getException() != null ?
                                task.getException().getMessage() : "Authentication failed";
                        Toast.makeText(AccountLoginActivity.this,
                                "Login failed: " + errorMessage, Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private boolean validateLoginInput(String email, String password) {
        boolean valid = true;

        if (TextUtils.isEmpty(email)) {
            emailInput.setError("Required field");
            valid = false;
        } else if (!android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            emailInput.setError("Enter a valid email address");
            valid = false;
        }

        if (TextUtils.isEmpty(password)) {
            passwordInput.setError("Required field");
            valid = false;
        } else if (password.length() < 6) {
            passwordInput.setError("Password must be at least 6 characters");
            valid = false;
        }

        return valid;
    }

    private void handleForgotPassword() {
        String email = emailInput.getText().toString().trim();

        if (TextUtils.isEmpty(email)) {
            emailInput.setError("Please enter your email first");
            emailInput.requestFocus();
            return;
        }

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
                                "Failed to send reset email: " + task.getException().getMessage(),
                                Toast.LENGTH_LONG).show();
                    }
                });
    }

    private void signInWithGoogle() {
        Intent signInIntent = mGoogleSignInClient.getSignInIntent();
        startActivityForResult(signInIntent, RC_SIGN_IN);
    }

    private void signInWithFacebook() {
        LoginManager.getInstance().logInWithReadPermissions(this,
                Arrays.asList("email", "public_profile"));
    }

    private void handleFacebookAccessToken(AccessToken token) {
        progressDialog.setMessage("Logging in with Facebook...");
        progressDialog.show();

        AuthCredential credential = FacebookAuthProvider.getCredential(token.getToken());
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, task -> {
                    progressDialog.dismiss();
                    if (task.isSuccessful()) {
                        FirebaseUser user = mAuth.getCurrentUser();
                        checkUserInFirestore(user.getUid());
                    } else {
                        Toast.makeText(AccountLoginActivity.this,
                                "Facebook authentication failed: " + task.getException().getMessage(),
                                Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void firebaseAuthWithGoogle(String idToken) {
        progressDialog.setMessage("Logging in with Google...");
        progressDialog.show();

        AuthCredential credential = GoogleAuthProvider.getCredential(idToken, null);
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, task -> {
                    progressDialog.dismiss();
                    if (task.isSuccessful()) {
                        FirebaseUser user = mAuth.getCurrentUser();
                        checkUserInFirestore(user.getUid());
                    } else {
                        Toast.makeText(AccountLoginActivity.this,
                                "Google authentication failed: " + task.getException().getMessage(),
                                Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void checkUserInFirestore(String uid) {
        progressDialog.setMessage("Checking user data...");
        progressDialog.show();

        db.collection(MyFirestoreReferences.USERS_COLLECTION)
                .document(uid)
                .get()
                .addOnSuccessListener(documentSnapshot -> {
                    progressDialog.dismiss();
                    if (documentSnapshot.exists()) {
                        // User exists in Firestore, proceed to home
                        navigateToHome();
                    } else {
                        // New social login user, needs to complete profile
                        Intent intent = new Intent(AccountLoginActivity.this,
                                AccountCompleteProfileActivity.class);
                        startActivity(intent);
                        finish();
                    }
                })
                .addOnFailureListener(e -> {
                    progressDialog.dismiss();
                    Toast.makeText(AccountLoginActivity.this,
                            "Error checking user data: " + e.getMessage(),
                            Toast.LENGTH_SHORT).show();
                    mAuth.signOut();
                });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        // Pass the activity result back to the Facebook SDK
        mCallbackManager.onActivityResult(requestCode, resultCode, data);

        // Result returned from launching the Intent from GoogleSignInApi.getSignInIntent(...);
        if (requestCode == RC_SIGN_IN) {
            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
            try {
                // Google Sign In was successful, authenticate with Firebase
                GoogleSignInAccount account = task.getResult(ApiException.class);
                firebaseAuthWithGoogle(account.getIdToken());
            } catch (ApiException e) {
                // Google Sign In failed
                Toast.makeText(this, "Google sign in failed: " + e.getStatusCode(),
                        Toast.LENGTH_SHORT).show();
            }
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        // Check if user is signed in and update UI accordingly
        FirebaseUser currentUser = mAuth.getCurrentUser();
        if (currentUser != null) {
            checkUserInFirestore(currentUser.getUid());
        }
    }

    private void navigateToHome() {
        Intent intent = new Intent(AccountLoginActivity.this, BookingHomeActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        finish();
    }

    private void redirectToSignup() {
        startActivity(new Intent(AccountLoginActivity.this, AccountRegisterActivity.class));
        finish();
    }
}