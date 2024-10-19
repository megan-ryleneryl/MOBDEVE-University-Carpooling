package com.example.uniride;

import android.content.Intent;
import android.os.Bundle;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.android.material.card.MaterialCardView;

public class PassengerHubActivity extends BottomNavigationActivity {
    private MaterialCardView ongoingRideButton;
    private MaterialCardView chatsButton;
    private MaterialCardView myBookingsButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_passenger_hub);

        initializeViews();
        setupClickListeners();
    }

    private void initializeViews() {
        ongoingRideButton = findViewById(R.id.ongoingRideButton);
        chatsButton = findViewById(R.id.chatsButton);
        myBookingsButton = findViewById(R.id.myBookingsButton);
    }

    @Override
    protected int getSelectedItemId() {
        return R.id.passenger;
    }

    private void setupClickListeners() {
        ongoingRideButton.setOnClickListener(v -> {
            Intent i = new Intent(this, RideTracking.class);
            startActivity(i);
        });

        chatsButton.setOnClickListener(v -> {
            Intent i = new Intent(this, HomeChatActivity.class);
            startActivity(i);
        });

        myBookingsButton.setOnClickListener(v -> {
            Intent i = new Intent(this, BookingHomeActivity.class);
            i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(i);
        });
    }
}