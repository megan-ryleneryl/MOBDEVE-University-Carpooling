package com.example.uniride;

import android.os.Bundle;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;

public class NoBookingsActivity extends BottomNavigationActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_no_bookings); // Show a simple "No bookings" layout

    }

    @Override
    protected int getSelectedItemId() {
        return 0;
    }

}
