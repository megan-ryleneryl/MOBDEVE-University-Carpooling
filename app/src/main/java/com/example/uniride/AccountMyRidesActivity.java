package com.example.uniride;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.os.Bundle;
import java.util.ArrayList;
import java.util.List;

public class AccountMyRidesActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_account_my_rides);

        RecyclerView recyclerView = findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        List<RideData> rideList = new ArrayList<>();
        rideList.add(new RideData("30023", "Taguig", "De La Salle University", "toUniversity", "14:21", 2, "P55", "inactive"));
        rideList.add(new RideData("30022", "De La Salle University", "Taguig", "fromUniversity", "09:36", 1, "P50", "active"));
        rideList.add(new RideData("30012", "Taguig", "De La Salle University", "toUniversity", "09:30", 2, "P150", "active"));
        rideList.add(new RideData("30011", "De La Salle University", "Taguig", "fromUniversity", "07:30", 3, "P50", "active"));

        RideAdapter adapter = new RideAdapter(rideList);
        recyclerView.setAdapter(adapter);
    }
}