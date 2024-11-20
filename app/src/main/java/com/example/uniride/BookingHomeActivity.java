package com.example.uniride;

import android.os.Bundle;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.app.DatePickerDialog;
import android.app.ProgressDialog;
import android.util.Log;
import android.content.Intent;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
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
    AutoCompleteTextView passengerInput;
    Button searchBtn;
    private FirebaseAuth mAuth;
    private FirebaseFirestore db;
    private FirebaseUser currentUser;
    private ProgressDialog progressDialog;

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
        setupProgressDialog();
        setupAutocompleteFields();
        setupClickListeners();
        loadUserModel(db, mAuth);
    }

    private void initViews() {
        originInput = findViewById(R.id.originInput);
        destinationInput = findViewById(R.id.destinationInput);
        dateInput = findViewById(R.id.dateInput);
        passengerInput = findViewById(R.id.passengerInput);
        searchBtn = findViewById(R.id.searchBtn);
        recyclerView = findViewById(R.id.myBookingsRecyclerView);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
    }

    private void setupProgressDialog() {
        progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Loading bookings...");
        progressDialog.setCancelable(false);
    }

    private void setupAutocompleteFields() {
        ArrayList<LocationModel> locations = DataGenerator.loadLocationData();
        locations.sort(Comparator.comparing(LocationModel::getName));
        Integer[] numPassengers = {1, 2, 3, 4, 5, 6};

        ArrayAdapter<LocationModel> adapter = new ArrayAdapter<>(this, android.R.layout.simple_dropdown_item_1line, locations);
        ArrayAdapter<Integer> passengerAdapter = new ArrayAdapter<>(this, android.R.layout.simple_dropdown_item_1line, numPassengers);

        originInput.setThreshold(0);
        originInput.setAdapter(adapter);
        destinationInput.setThreshold(0);
        destinationInput.setAdapter(adapter);
        passengerInput.setThreshold(0);
        passengerInput.setAdapter(passengerAdapter);

        dateInput.setOnClickListener(v -> showDatePicker());
        originInput.setOnClickListener(v -> originInput.showDropDown());
        destinationInput.setOnClickListener(v -> destinationInput.showDropDown());
        passengerInput.setOnClickListener(v -> passengerInput.showDropDown());
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
                i.putExtra("passengerInput", Integer.parseInt(passengerInput.getText().toString()));
                i.putExtra("myBookingData", myBookingData);
                i.putExtra("locations", DataGenerator.loadLocationData());
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
        progressDialog.show();
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
                        progressDialog.dismiss();
                        Toast.makeText(this, "User document not found.", Toast.LENGTH_SHORT).show();
                    }
                })
                .addOnFailureListener(e -> {
                    progressDialog.dismiss();
                    Toast.makeText(this, "Failed to load user data: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                });
    }

    private void loadBookingData(FirebaseFirestore db, UserModel userModel) {
        int userID = userModel.getUserID();
        db.collection(MyFirestoreReferences.BOOKINGS_COLLECTION)
                .whereEqualTo(MyFirestoreReferences.Bookings.PASSENGER_ID, userID)
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    myBookingData.clear();
                    final int[] completedBookings = {0};

                    if (queryDocumentSnapshots.isEmpty()) {
                        progressDialog.dismiss();
                        return;
                    }

                    for (DocumentSnapshot document : queryDocumentSnapshots.getDocuments()) {
                        BookingModel booking = BookingModel.fromMap(document.getData());

                        booking.populateObjects(db, populatedBooking -> {
                            completedBookings[0]++;

                            if (completedBookings[0] == queryDocumentSnapshots.size()) {
                                progressDialog.dismiss();
                                myHomeAdapter = new MyBookingHomeAdapter(myBookingData, BookingHomeActivity.this);
                                recyclerView.setAdapter(myHomeAdapter);
                                myHomeAdapter.notifyDataSetChanged();
                            }
                        });

                        myBookingData.add(booking);
                    }
                })
                .addOnFailureListener(e -> {
                    progressDialog.dismiss();
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
        Intent i = new Intent(BookingHomeActivity.this, RideCreate.class);
        startActivity(i);
    }

    private boolean isAnyFieldEmpty() {
        return originInput.getText().toString().trim().isEmpty() ||
                destinationInput.getText().toString().trim().isEmpty() ||
                dateInput.getText().toString().trim().isEmpty() ||
                passengerInput.getText().toString().trim().isEmpty();
    }

    @Override
    protected int getSelectedItemId() {
        return R.id.home;
    }
}