package com.example.uniride;

import android.os.Bundle;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.app.DatePickerDialog;
import android.util.Log;
import android.content.Intent;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Comparator;

public class BookingHomeActivity extends BottomNavigationActivity {
    private ArrayList<BookingModel> myBookingData;
    private RecyclerView recyclerView;
    private MyBookingHomeAdapter myHomeAdapter;
    AutoCompleteTextView originInput;
    AutoCompleteTextView destinationInput;
    EditText dateInput;
    Spinner priceInput;
    Button searchBtn;
    private FirebaseAuth mAuth;
    private FirebaseFirestore db;
    private FirebaseUser currentUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.booking_home);

        // Initialize Firebase
        mAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();
        currentUser = mAuth.getCurrentUser();
        myBookingData = new ArrayList<>();

        if (currentUser == null) {
            startActivity(new Intent(this, AccountLoginActivity.class));
            finish();
            return;
        }

        initViews();
        setupAutocompleteFields();
        setupClickListeners();
        loadUserModel(db, mAuth);
    }

    private void initViews() {
        originInput = findViewById(R.id.originInput);
        destinationInput = findViewById(R.id.destinationInput);
        dateInput = findViewById(R.id.dateInput);
        priceInput = findViewById(R.id.priceInput);
        searchBtn = findViewById(R.id.searchBtn);
        recyclerView = findViewById(R.id.myBookingsRecyclerView);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
    }

    private void setupAutocompleteFields() {
        ArrayList<LocationModel> locations = DataGenerator.loadLocationData();
        locations.sort(Comparator.comparing(LocationModel::getName));
        ArrayAdapter<LocationModel> adapter = new ArrayAdapter<>(this, android.R.layout.simple_dropdown_item_1line, locations);

        originInput.setThreshold(0);
        originInput.setAdapter(adapter);
        destinationInput.setThreshold(0);
        destinationInput.setAdapter(adapter);

        dateInput.setOnClickListener(v -> showDatePicker());
        originInput.setOnClickListener(v -> originInput.showDropDown());
        destinationInput.setOnClickListener(v -> destinationInput.showDropDown());

        String[] priceRanges = {"Less than 250", "Less than 500", "Less than 1000", "Any"};
        ArrayAdapter<String> priceAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, priceRanges);
        priceAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        priceInput.setAdapter(priceAdapter);
    }

    private void setupClickListeners() {
        searchBtn.setOnClickListener(view -> {
            if (isAnyFieldEmpty()) {
                Toast.makeText(BookingHomeActivity.this, "Please fill in all fields", Toast.LENGTH_SHORT).show();
            } else {
                Intent i = new Intent(BookingHomeActivity.this, BookingSearchActivity.class);
                i.putExtra("originInput", originInput.getText().toString());
                i.putExtra("destinationInput", destinationInput.getText().toString());
                i.putExtra("dateInput", dateInput.getText().toString());
                i.putExtra("priceInput", priceInput.getSelectedItem().toString());
                i.putExtra("myBookingData", myBookingData);
                i.putExtra("locations", DataGenerator.loadLocationData());
                i.putExtra("currentUserID", currentUser.getUid());
                startActivity(i);
            }
        });
    }

    private void showDatePicker() {
        Calendar calendar = Calendar.getInstance();
        DatePickerDialog datePickerDialog = new DatePickerDialog(
                BookingHomeActivity.this,
                R.style.CustomDatePickerDialog,
                (view, year, month, dayOfMonth) -> {
                    String selectedDate = year + "-" + (month + 1) + "-" + dayOfMonth;
                    dateInput.setText(selectedDate);
                },
                calendar.get(Calendar.YEAR),
                calendar.get(Calendar.MONTH),
                calendar.get(Calendar.DAY_OF_MONTH)
        );
        datePickerDialog.show();
    }

    private void loadUserModel(FirebaseFirestore db, FirebaseAuth auth) {
        String uid = auth.getCurrentUser().getUid();

        db.collection(MyFirestoreReferences.USERS_COLLECTION)
                .document(uid)
                .get()
                .addOnSuccessListener(documentSnapshot -> {
                    if (documentSnapshot.exists()) {
                        UserModel userModel = UserModel.fromMap(documentSnapshot.getData());
                        userModel.populateObjects(db, populatedUser -> {
                            loadBookingData(db, populatedUser);
                        });
                    } else {
                        Toast.makeText(this, "User document not found.", Toast.LENGTH_SHORT).show();
                    }
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(this, "Failed to load user data: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                });
    }

    private void loadBookingData(FirebaseFirestore db, UserModel userModel) {
        int userID = userModel.getUserID();
        db.collection(MyFirestoreReferences.BOOKINGS_COLLECTION)
                .orderBy("date", Query.Direction.DESCENDING)
                .whereEqualTo(MyFirestoreReferences.Bookings.PASSENGER_ID, userID)
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    myBookingData.clear();
                    final int[] completedBookings = {0};

                    if (queryDocumentSnapshots.isEmpty()) {
                        return;
                    }

                    for (DocumentSnapshot document : queryDocumentSnapshots.getDocuments()) {
                        BookingModel booking = BookingModel.fromMap(document.getData());

                        booking.populateObjects(db, populatedBooking -> {
                            completedBookings[0]++;

                            if (completedBookings[0] == queryDocumentSnapshots.size()) {
                                myHomeAdapter = new MyBookingHomeAdapter(myBookingData, BookingHomeActivity.this);
                                recyclerView.setAdapter(myHomeAdapter);
                                myHomeAdapter.notifyDataSetChanged();
                            }
                        });

                        myBookingData.add(booking);
                    }
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(BookingHomeActivity.this,
                            "Error loading bookings: " + e.getMessage(),
                            Toast.LENGTH_SHORT).show();
                });
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (currentUser != null) {
            loadUserModel(db, mAuth);
        }
    }

    public void createRide(View view) {
        db = FirebaseFirestore.getInstance();
        currentUser = FirebaseAuth.getInstance().getCurrentUser();

        if (currentUser != null) {
            db.collection(MyFirestoreReferences.USERS_COLLECTION)
                    .document(currentUser.getUid())
                    .get()
                    .addOnSuccessListener(documentSnapshot -> {
                        if (documentSnapshot.exists()) {
                            boolean isDriver = documentSnapshot.getBoolean("isDriver") != null &&
                                    documentSnapshot.getBoolean("isDriver");
                            if (!isDriver) {
                                Intent intent = new Intent(this, DriverRegistrationPromptActivity.class);
                                startActivity(intent);
                                finish();
                                return;
                            }
                            Intent i = new Intent(BookingHomeActivity.this, RideCreate.class);
                        startActivity(i);

                        }
                    })
                    .addOnFailureListener(e -> {
                        Toast.makeText(this, "Error checking driver status", Toast.LENGTH_SHORT).show();
                        finish();
                    });
        } else {
            Intent i = new Intent(BookingHomeActivity.this, RideCreate.class);
            startActivity(i);
            finish();
        }

    }

    private boolean isAnyFieldEmpty() {
        return originInput.getText().toString().trim().isEmpty() ||
                destinationInput.getText().toString().trim().isEmpty() ||
                dateInput.getText().toString().trim().isEmpty() ||
                priceInput.getSelectedItem().toString().trim().isEmpty();
    }

    @Override
    protected int getSelectedItemId() {
        return R.id.home;
    }
}