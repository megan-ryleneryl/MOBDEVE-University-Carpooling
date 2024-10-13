package com.example.uniride;

import android.os.Bundle;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class BookingHomeDetailsActivity extends AppCompatActivity {

    // declare ui elements

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.booking_home_details);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

//        movieImage = findViewById(R.id.imageview);
//        textViewName = findViewById(R.id.textName);
//        textViewDate = findViewById(R.id.textdate);
//        textViewSummary = findViewById(R.id.textsummary);
//
//        int imageID = getIntent().getIntExtra("image", 0);
//        String movieName = getIntent().getStringExtra("name");
//        String movieDate = getIntent().getStringExtra("date");
//        String movieSummary = getIntent().getStringExtra("summary");
//
//        movieImage.setImageResource(imageID);
//        textViewName.setText(movieName);
//        textViewDate.setText(movieDate);
//        textViewSummary.setText(movieSummary);
    }
}