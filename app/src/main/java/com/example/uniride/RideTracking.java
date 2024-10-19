package com.example.uniride;

import android.content.Intent;
import android.os.Bundle;

import android.view.View;
import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class RideTracking extends BottomNavigationActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_ride_tracking);

    }

    public void openChatActivity(View view) {
        Intent i = new Intent(this, HomeChatActivity.class);
        startActivity(i);
    }

    public void completeRide(View view) {
        Intent i = new Intent(this, BookingHomeActivity.class);
        startActivity(i);
    }

    @Override
    protected int getSelectedItemId() {
        return R.id.passenger;
    }
}