package com.example.uniride;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import java.util.ArrayList;
import java.util.List;

public class AccountMyRidesActivity extends BottomNavigationActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_account_my_rides);

        RecyclerView recyclerView = findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        List<RideModel> rideList = DataGenerator.loadRideData();

        MyRidesAdapter adapter = new MyRidesAdapter(AccountMyRidesActivity.this, rideList);
        recyclerView.setAdapter(adapter);
    }

    @Override
    protected int getSelectedItemId() {
        return R.id.driver;
    }

    public void postRide(View v) {
        Intent intent = new Intent(this, RideCreate.class);
        startActivity(intent);
    }

    public void editRide(View v) {
        Intent intent = new Intent(this, RideEdit.class);
        startActivity(intent);
    }

}