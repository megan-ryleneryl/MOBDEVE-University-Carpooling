package com.example.uniride;

import android.content.Context;
import android.content.Intent;
import android.view.View;
import android.os.Bundle;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.EditText;
import de.hdodenhof.circleimageview.CircleImageView;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

public class HomeReviewsActivity extends AppCompatActivity {

    ImageButton[] stars = new ImageButton[5];
    EditText commentInput;
    Button submitButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_home_reviews);

        stars[0] = findViewById(R.id.star1);
        stars[1] = findViewById(R.id.star2);
        stars[2] = findViewById(R.id.star3);
        stars[3] = findViewById(R.id.star4);
        stars[4] = findViewById(R.id.star5);
        commentInput = findViewById(R.id.commentInput);
        submitButton = findViewById(R.id.submitButton);

        // Set click listeners
        stars[0].setOnClickListener(v -> setRating(1));
        stars[1].setOnClickListener(v -> setRating(2));
        stars[2].setOnClickListener(v -> setRating(3));
        stars[3].setOnClickListener(v -> setRating(4));
        stars[4].setOnClickListener(v -> setRating(5));
        submitButton.setOnClickListener(v -> submitButtonPressed());
    }

    public void setRating(int rating) {
        for (int i = 0; i < stars.length; i++) {
            if (i < rating) {
                stars[i].setImageResource(android.R.drawable.star_big_on); // Filled star
            } else {
                stars[i].setImageResource(android.R.drawable.star_big_off); // Empty star
            }
        }
    }

    public void submitButtonPressed() {
        finish();
    }


}