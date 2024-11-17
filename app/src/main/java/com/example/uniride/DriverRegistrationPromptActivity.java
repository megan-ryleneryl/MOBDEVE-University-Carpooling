package com.example.uniride;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;

public class DriverRegistrationPromptActivity extends BottomNavigationActivity {
    private Button registerAsDriverButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_driver_registration_prompt);

        registerAsDriverButton = findViewById(R.id.registerAsDriverButton);
        registerAsDriverButton.setOnClickListener(v -> {
            // TODO: Navigate to driver registration form
            Toast.makeText(this, "Driver registration coming soon!", Toast.LENGTH_SHORT).show();
        });
    }

    @Override
    protected int getSelectedItemId() {
        return R.id.driver;
    }
}