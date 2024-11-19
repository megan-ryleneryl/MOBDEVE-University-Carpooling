package com.example.uniride;

import android.os.Bundle;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.app.DatePickerDialog;
import android.util.Log;
import android.widget.DatePicker;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

//import com.google.firebase.firestore.CollectionReference;
//import com.google.firebase.firestore.FirebaseFirestore;
//import com.google.android.gms.tasks.OnCompleteListener;
//import com.google.firebase.firestore.QuerySnapshot;
//import androidx.annotation.NonNull;
//import com.google.android.gms.tasks.Task;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.Calendar;

public class BookingHomeActivity extends BottomNavigationActivity {

    private ArrayList<BookingModel> myBookingData;
    private BookingModel bookingModel;
    AutoCompleteTextView originInput;
    AutoCompleteTextView destinationInput;
    MyBookingHomeAdapter myHomeAdapter;
    EditText dateInput;
    AutoCompleteTextView passengerInput;
    Button searchBtn;
    private FirebaseAuth mAuth;
    private FirebaseFirestore db;
    private FirebaseUser currentUser;
    private UserModel userModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.booking_home);

        mAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();
        currentUser = mAuth.getCurrentUser();
        myBookingData = new ArrayList<>();

        if (currentUser == null) {
            startActivity(new Intent(this, AccountLoginActivity.class));
            finish();
            return;
        }

        // Load data
        ArrayList<LocationModel> locations = DataGenerator.loadLocationData();
        Integer[] numPassengers = {1, 2, 3, 4, 5, 6};
        loadUserModel(db, mAuth);

        // Declarations
        originInput = findViewById(R.id.originInput);
        destinationInput = findViewById(R.id.destinationInput);
        dateInput = findViewById(R.id.dateInput);
        passengerInput = findViewById(R.id.passengerInput);
        searchBtn = findViewById(R.id.searchBtn);

        // Declare autocomplete fields
        ArrayAdapter<LocationModel> adapter = new ArrayAdapter<>(this, android.R.layout.simple_dropdown_item_1line, locations);
        originInput.setThreshold(0);
        originInput.setAdapter(adapter);
        destinationInput.setThreshold(0);
        destinationInput.setAdapter(adapter);
        ArrayAdapter<Integer> passengerAdapter = new ArrayAdapter<>(this, android.R.layout.simple_dropdown_item_1line, numPassengers);
        passengerInput.setThreshold(0);
        passengerInput.setAdapter(passengerAdapter);

        // Open date selector when clicked
        dateInput.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Get the current date as default in the DatePicker
                final Calendar calendar = Calendar.getInstance();
                int year = calendar.get(Calendar.YEAR);
                int month = calendar.get(Calendar.MONTH);
                int day = calendar.get(Calendar.DAY_OF_MONTH);

                // Create a DatePickerDialog
                DatePickerDialog datePickerDialog = new DatePickerDialog(BookingHomeActivity.this, R.style.CustomDatePickerDialog,
                        new DatePickerDialog.OnDateSetListener() {
                            @Override
                            public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                                // month is 0-indexed, so add 1 for display
                                String selectedDate = dayOfMonth + "/" + (month + 1) + "/" + year;
                                dateInput.setText(selectedDate);
                            }
                        }, year, month, day);

                datePickerDialog.show();
            }
        });

        // Set onclicklisteners for all autocompletetextview fields so they dropdown on click
        originInput.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                originInput.showDropDown();
            }
        });
        destinationInput.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                destinationInput.showDropDown();
            }
        });
        passengerInput.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                passengerInput.showDropDown();
            }
        });

        // Initialize intent for next activity (search)
        searchBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (isAnyFieldEmpty()) {
                    Toast.makeText(BookingHomeActivity.this, "Please fill in all fields", Toast.LENGTH_SHORT).show();
                } else {
                    Intent i = new Intent(BookingHomeActivity.this, BookingSearchActivity.class);
                    i.putExtra("originInput", originInput.getText().toString());
                    i.putExtra("destinationInput", destinationInput.getText().toString());
                    i.putExtra("dateInput", dateInput.getText().toString());
                    i.putExtra("passengerInput", Integer.parseInt(passengerInput.getText().toString()));
                    i.putExtra("myBookingData", myBookingData);
                    i.putExtra("locations", locations);
                    startActivity(i);
                }
            }
        });
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
                .whereEqualTo(MyFirestoreReferences.Bookings.PASSENGER_ID, userID)
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    myBookingData.clear();
                    final int[] completedBookings = {0};

                    for (DocumentSnapshot document : queryDocumentSnapshots.getDocuments()) {
                        BookingModel booking = BookingModel.fromMap(document.getData());

                        // Populate this booking's objects
                        booking.populateObjects(db, populatedBooking -> {
                            completedBookings[0]++;

                            // When all bookings are populated, set the adapter
                            if (completedBookings[0] == queryDocumentSnapshots.size()) {
                                RecyclerView recyclerView = findViewById(R.id.myBookingsRecyclerView);
                                recyclerView.setHasFixedSize(true);
                                recyclerView.setLayoutManager(new LinearLayoutManager(this));
                                myHomeAdapter = new MyBookingHomeAdapter(myBookingData, BookingHomeActivity.this);
                                recyclerView.setAdapter(myHomeAdapter);
                                myHomeAdapter.notifyDataSetChanged();
                            }
                        });

                        myBookingData.add(booking);
                    }

                    Log.e("Booking data", myBookingData.toString());
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
        loadUserModel(db, mAuth); // Reload data when returning to this activity
    }

    // Helper to create a ride
    public void createRide(View view) {
        Intent i = new Intent(BookingHomeActivity.this, RideCreate.class);
        startActivity(i);
    }

    // Helper to check if any EditText is empty
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