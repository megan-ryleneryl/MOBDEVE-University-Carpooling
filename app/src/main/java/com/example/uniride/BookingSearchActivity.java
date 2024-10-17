package com.example.uniride;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.Calendar;

public class BookingSearchActivity extends BottomNavigationActivity {
    private ArrayList<BookingModel> myBookingData;
    AutoCompleteTextView originInput2;
    AutoCompleteTextView destinationInput2;
    EditText dateInput2;
    AutoCompleteTextView passengerInput2;
    Button searchBtn2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.booking_search);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        // Receive the data from home
        String origin = getIntent().getStringExtra("originInput");
        String destination = getIntent().getStringExtra("destinationInput");
        String date = getIntent().getStringExtra("dateInput");
        int passengers = getIntent().getIntExtra("passengerInput", 1);
        ArrayList<BookingModel> myBookingData = (ArrayList<BookingModel>) getIntent().getSerializableExtra("myBookingData");
        ArrayList<LocationModel> locations = (ArrayList<LocationModel>) getIntent().getSerializableExtra("locations");
        Integer[] numPassengers = { 1, 2, 3, 4, 5, 6 };

        // Connect the recyclerview
        RecyclerView recyclerView = findViewById(R.id.searchResultsRecyclerView);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        // Set Adapter
        MyBookingSearchAdapter mySearchAdapter = new MyBookingSearchAdapter(myBookingData, BookingSearchActivity.this);
        recyclerView.setAdapter(mySearchAdapter);

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

        searchBtn2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(isAnyFieldEmpty()) {
                    Toast.makeText(BookingSearchActivity.this, "Please fill in all fields", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(BookingSearchActivity.this, "Searching database...", Toast.LENGTH_SHORT).show();

                    String originInput = originInput2.getText().toString();
                    String destinationInput = destinationInput2.getText().toString();
                    String dateInput = dateInput2.getText().toString();
                    int passengerInput = Integer.parseInt(passengerInput2.getText().toString());

                    searchForBookings(originInput, destinationInput, dateInput, passengerInput);

                    originInput2.setText("");
                    destinationInput2.setText("");
                    dateInput2.setText("");
                    passengerInput2.setText("");

                    // TODO: Refresh page and load new search results
                }
            }
        });
    }

    private ArrayList<BookingModel> searchForBookings(String origin, String destination, String date, int passengers) {
        // TODO: Implement db search

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