package com.example.uniride;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import androidx.appcompat.app.AppCompatActivity;
import com.google.android.material.bottomnavigation.BottomNavigationView;

public abstract class BottomNavigationActivity extends AppCompatActivity {
    protected BottomNavigationView bottomNavigationView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        super.setContentView(R.layout.activity_bottom_navigation);
        bottomNavigationView = findViewById(R.id.bottomNavigationView);
        setupBottomNavigation();
    }

    @Override
    public void setContentView(int layoutResID) {
        FrameLayout contentFrame = findViewById(R.id.content_frame);
        LayoutInflater.from(this).inflate(layoutResID, contentFrame, true);
    }

    private void setupBottomNavigation() {
        bottomNavigationView.setSelectedItemId(getSelectedItemId());

        bottomNavigationView.setOnItemSelectedListener(item -> {
            int itemId = item.getItemId();

            // Start the appropriate activity based on selection
            if (itemId == R.id.home) {
                //IF HOME IS CLICKED, CLEAR ALL STACKS
                Intent i = new Intent(this, BookingHomeActivity.class);
                i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(i);
//                overridePendingTransition(0, 0);
                return true;
            } else if (itemId == R.id.driver) {
                Intent i = new Intent(this, DriverHubActivity.class);
                i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(i);
//                overridePendingTransition(0, 0);
                return true;
            } else if (itemId == R.id.passenger) {
                Intent i = new Intent(this, PassengerHubActivity.class);
                i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(i);
//                overridePendingTransition(0, 0);
                return true;
            } else if (itemId == R.id.account) {
                Intent i = new Intent(this, AccountDetailsActivity.class);
                i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(i);
//                overridePendingTransition(0, 0);
                return true;
            }
            return false;
        });
    }

    // Disable the back button animation
    @Override
    public void onBackPressed() {
        super.onBackPressed();
        overridePendingTransition(0, 0);
    }

    // This method should return the ID of the currently selected item
    protected abstract int getSelectedItemId();
}