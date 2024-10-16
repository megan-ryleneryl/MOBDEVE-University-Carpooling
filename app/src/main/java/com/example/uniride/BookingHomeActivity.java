package com.example.uniride;

import android.os.Bundle;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import java.util.ArrayList;

public class BookingHomeActivity extends AppCompatActivity {

    private ArrayList<BookingModel> myBookingData;
    AutoCompleteTextView originInput;
    AutoCompleteTextView destinationInput;
    EditText dateInput;
    EditText passengerInput;
    Button searchBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.booking_home);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        // Connect the recyclerview
        RecyclerView recyclerView = findViewById(R.id.myBookingsRecyclerView);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        // Load data
        ArrayList<BookingModel> myBookingData = DataGenerator.loadBookingData();
        ArrayList<LocationModel> locations = DataGenerator.loadLocationData();

        // Set Adapter
        MyBookingHomeAdapter myHomeAdapter = new MyBookingHomeAdapter(myBookingData, BookingHomeActivity.this);
        recyclerView.setAdapter(myHomeAdapter);

        // Declarations
        originInput = findViewById(R.id.originInput);
        destinationInput = findViewById(R.id.destinationInput);
        dateInput = findViewById(R.id.dateInput);
        passengerInput = findViewById(R.id.passengerInput);
        searchBtn = findViewById(R.id.searchBtn);

        // Declare autocomplete fields
        ArrayAdapter<LocationModel> adapter = new ArrayAdapter<>(this, android.R.layout.simple_dropdown_item_1line, locations);
        originInput.setThreshold(1);
        originInput.setAdapter(adapter);
        destinationInput.setThreshold(1);
        destinationInput.setAdapter(adapter);
    }
}