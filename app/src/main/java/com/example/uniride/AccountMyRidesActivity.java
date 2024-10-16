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
        // Example data (you should replace this with actual data from your database)
//        LocationModel taguig = new LocationModel("Taguig");
//        LocationModel dlsu = new LocationModel("De La Salle University");
//        rideList.add(new RideModel(30023, taguig, dlsu, "toUniversity", "14:21", "15:00", 2, 4, 55, "inactive"));
//        rideList.add(new RideModel(30022, dlsu, taguig, "fromUniversity", "09:36", "10:15", 1, 4, 50, "active"));
//        rideList.add(new RideModel(30012, taguig, dlsu, "toUniversity", "09:30", "10:00", 2, 4, 150, "active"));
//        rideList.add(new RideModel(30011, dlsu, taguig, "fromUniversity", "07:30", "08:00", 3, 4, 50, "active"));

        MyRidesAdapter adapter = new MyRidesAdapter(rideList);
        recyclerView.setAdapter(adapter);
    }

    public void postRide(View v) {
        Intent intent = new Intent(this, AccountDetailsActivity.class);
        startActivity(intent);
    }
}