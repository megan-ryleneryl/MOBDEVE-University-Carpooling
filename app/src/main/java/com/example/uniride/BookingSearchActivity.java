package com.example.uniride;

import android.app.DatePickerDialog;
import android.app.ProgressDialog;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Toast;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Comparator;

public class BookingSearchActivity extends BottomNavigationActivity {
    private ArrayList<BookingModel> myBookingData;
    private ArrayList<BookingModel> searchResults;
    private MyBookingSearchAdapter mySearchAdapter;
    private RecyclerView recyclerView;
    private FirebaseFirestore db;
    private ProgressDialog progressDialog;
    private ArrayList<LocationModel> locations;
    private AutoCompleteTextView originInput2;
    private AutoCompleteTextView destinationInput2;
    private EditText dateInput2;
    private AutoCompleteTextView passengerInput2;
    private Button searchBtn2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.booking_search);

        db = FirebaseFirestore.getInstance();
        setupProgressDialog();

        // Initialize data
        myBookingData = (ArrayList<BookingModel>) getIntent().getSerializableExtra("myBookingData");
        locations = (ArrayList<LocationModel>) getIntent().getSerializableExtra("locations");
        locations.sort(Comparator.comparing(LocationModel::getName));
        String origin = getIntent().getStringExtra("originInput");
        String destination = getIntent().getStringExtra("destinationInput");
        String date = getIntent().getStringExtra("dateInput");
        int passengers = getIntent().getIntExtra("passengerInput", 1);
        Integer[] numPassengers = { 1, 2, 3, 4, 5, 6 };
        searchResults = new ArrayList<>();

        // Setup RecyclerView
        recyclerView = findViewById(R.id.searchResultsRecyclerView);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        // Initialize adapter with empty results
        mySearchAdapter = new MyBookingSearchAdapter(searchResults, this);
        recyclerView.setAdapter(mySearchAdapter);

        initViews();
        searchForBookings(origin, destination, date, passengers);
    }

    private void setupProgressDialog() {
        progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Searching for rides...");
        progressDialog.setCancelable(false);
    }

    private void initViews() {
        Integer[] numPassengers = {1, 2, 3, 4, 5, 6};

        // Declarations
        originInput2 = findViewById(R.id.originInput2);
        destinationInput2 = findViewById(R.id.destinationInput2);
        dateInput2 = findViewById(R.id.dateInput2);
        passengerInput2 = findViewById(R.id.passengerInput2);
        searchBtn2 = findViewById(R.id.searchBtn2);

        // Declare autocomplete fields
        ArrayAdapter<LocationModel> adapter = new ArrayAdapter<>(this, android.R.layout.simple_dropdown_item_1line, locations);
        originInput2.setThreshold(1);
        originInput2.setAdapter(adapter);
        destinationInput2.setThreshold(1);
        destinationInput2.setAdapter(adapter);
        ArrayAdapter<Integer> passengerAdapter = new ArrayAdapter<>(this, android.R.layout.simple_dropdown_item_1line, numPassengers);
        passengerInput2.setThreshold(0);
        passengerInput2.setAdapter(passengerAdapter);

        // Open date selector when clicked
        dateInput2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Get the current date as default in the DatePicker
                final Calendar calendar = Calendar.getInstance();
                int year = calendar.get(Calendar.YEAR);
                int month = calendar.get(Calendar.MONTH);
                int day = calendar.get(Calendar.DAY_OF_MONTH);

                // Create a DatePickerDialog
                DatePickerDialog datePickerDialog = new DatePickerDialog(BookingSearchActivity.this, R.style.CustomDatePickerDialog,
                        new DatePickerDialog.OnDateSetListener() {
                            @Override
                            public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                                // month is 0-indexed, so add 1 for display
                                String selectedDate = dayOfMonth + "/" + (month + 1) + "/" + year;
                                dateInput2.setText(selectedDate);
                            }
                        }, year, month, day);

                datePickerDialog.show();
            }
        });

        originInput2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                originInput2.showDropDown();
            }
        });

        destinationInput2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                destinationInput2.showDropDown();
            }
        });

        passengerInput2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                passengerInput2.showDropDown();
            }
        });

        searchBtn2.setOnClickListener(view -> {
            if(isAnyFieldEmpty()) {
                Toast.makeText(BookingSearchActivity.this, "Please fill in all fields", Toast.LENGTH_SHORT).show();
            } else {
                String originInput = originInput2.getText().toString();
                String destinationInput = destinationInput2.getText().toString();
                String dateInput = dateInput2.getText().toString();
                int passengerInput = Integer.parseInt(passengerInput2.getText().toString());

                searchForBookings(originInput, destinationInput, dateInput, passengerInput);
            }
        });
    }

    private void searchForBookings(String originName, String destinationName, String date, int passengers) {
        progressDialog.show();
        searchResults.clear();

        LocationModel originLocation = findLocationByName(originName);
        LocationModel destinationLocation = findLocationByName(destinationName);

//        Log.d("SearchDebug", "Origin ID: " + (originLocation != null ? originLocation.getLocationID() : "null"));
//        Log.d("SearchDebug", "Destination ID: " + (destinationLocation != null ? destinationLocation.getLocationID() : "null"));
//        Log.d("SearchDebug", "Date: " + date);
//        Log.d("SearchDebug", "Passengers: " + passengers);

        if (originLocation == null || destinationLocation == null) {
            progressDialog.dismiss();
            Toast.makeText(this, "Invalid location selected", Toast.LENGTH_SHORT).show();
            return;
        }

        // Add timeout handler
        new android.os.Handler().postDelayed(() -> {
            if (progressDialog.isShowing()) {
                progressDialog.dismiss();
//                Log.d("SearchDebug", "Search timed out");
                Toast.makeText(BookingSearchActivity.this, "Search timed out. Please try again.", Toast.LENGTH_SHORT).show();
            }
        }, 15000); // 15 second timeout

        db.collection("rides")
                .whereEqualTo("fromLocationID", originLocation.getLocationID())
                .whereEqualTo("toLocationID", destinationLocation.getLocationID())
                .whereGreaterThanOrEqualTo("availableSeats", passengers)
                .whereEqualTo("isActive", true)
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
//                    Log.d("SearchDebug", "Query successful. Found " + queryDocumentSnapshots.size() + " documents");

                    if (queryDocumentSnapshots.isEmpty()) {
                        progressDialog.dismiss();
                        Toast.makeText(this, "No rides found matching your criteria", Toast.LENGTH_SHORT).show();
                        return;
                    }

                    int totalDocuments = queryDocumentSnapshots.size();
                    final int[] processedDocuments = {0};

                    for (DocumentSnapshot document : queryDocumentSnapshots.getDocuments()) {
//                        Log.d("SearchDebug", "Processing document: " + document.getId());
                        RideModel ride = RideModel.fromMap(document.getData());

                        // Check if already booked
                        boolean isAlreadyBooked = myBookingData.stream()
                                .anyMatch(booking -> booking.getRideID() == ride.getRideID());

                        if (!isAlreadyBooked) {
                            BookingModel searchResult = new BookingModel();
                            searchResult.setRideID(ride.getRideID());
                            searchResult.setDate(date);
                            searchResults.add(searchResult);

                            searchResult.populateObjects(db, populatedBooking -> {
                                processedDocuments[0]++;
//                                Log.d("SearchDebug", "Processed " + processedDocuments[0] + " of " + totalDocuments);

                                if (processedDocuments[0] == totalDocuments) {
                                    progressDialog.dismiss();
                                    if (!searchResults.isEmpty()) {
                                        mySearchAdapter.notifyDataSetChanged();
                                    } else {
                                        Toast.makeText(BookingSearchActivity.this,
                                                "No available rides found",
                                                Toast.LENGTH_SHORT).show();
                                    }
                                }
                            });
                        } else {
                            processedDocuments[0]++;
                        }
                    }
                })
                .addOnFailureListener(e -> {
                    Log.e("SearchDebug", "Search failed", e);
                    progressDialog.dismiss();
                    Toast.makeText(BookingSearchActivity.this,
                            "Error searching for rides: " + e.getMessage(),
                            Toast.LENGTH_SHORT).show();
                })
                .addOnCompleteListener(task -> {
                    Log.d("SearchDebug", "Search completed. Success: " + task.isSuccessful());
                    // Ensure progress dialog is dismissed
                    if (progressDialog.isShowing()) {
                        progressDialog.dismiss();
                    }
                });
    }

    private LocationModel findLocationByName(String locationName) {
        for (LocationModel location : locations) {
            if (location.getName().equals(locationName)) {
                return location;
            }
        }
        return null;
    }

    // Helper to check if any EditText is empty
    private boolean isAnyFieldEmpty() {
        return originInput2.getText().toString().trim().isEmpty() ||
                destinationInput2.getText().toString().trim().isEmpty() ||
                dateInput2.getText().toString().trim().isEmpty() ||
                passengerInput2.getText().toString().trim().isEmpty();
    }

    @Override
    protected int getSelectedItemId() {
        return R.id.home;
    }
}