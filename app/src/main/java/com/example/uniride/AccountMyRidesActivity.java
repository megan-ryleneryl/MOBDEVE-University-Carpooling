package com.example.uniride;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import java.util.ArrayList;
import java.util.List;

public class AccountMyRidesActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_account_my_rides);

        RecyclerView recyclerView = findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        List<RideModel> rideList = new ArrayList<>();
        rideList.add(new RideModel("30023", "Taguig", "De La Salle University", "toUniversity", "14:21", 2, "P55", "inactive"));
        rideList.add(new RideModel("30022", "De La Salle University", "Taguig", "fromUniversity", "09:36", 1, "P50", "active"));
        rideList.add(new RideModel("30012", "Taguig", "De La Salle University", "toUniversity", "09:30", 2, "P150", "active"));
        rideList.add(new RideModel("30011", "De La Salle University", "Taguig", "fromUniversity", "07:30", 3, "P50", "active"));

        MyRidesAdapter adapter = new MyRidesAdapter(rideList);
        recyclerView.setAdapter(adapter);
    }

    public void postRide (View v) {
        Intent intent = new Intent(this, AccountDetailsActivity.class);
        startActivity(intent);
    }
}