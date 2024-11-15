package com.example.uniride;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class AccountMyRidesActivity extends BottomNavigationActivity {
    private RecyclerView recyclerView;
    private MyRidesAdapter adapter;
    private List<RideModel> rideList;
    private FirebaseFirestore db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_account_my_rides);

        // Initialize Firestore
        db = FirebaseFirestore.getInstance();

        // Initialize RecyclerView first
        recyclerView = findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        rideList = new ArrayList<>();
        adapter = new MyRidesAdapter(this, rideList);
        recyclerView.setAdapter(adapter);

        // Then load data
        loadRides();
    }

    private void loadRides() {
        Log.d("AccountMyRidesActivity", "Starting to load rides...");
        db.collection(MyFirestoreReferences.RIDES_COLLECTION)
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    rideList.clear();
                    for (QueryDocumentSnapshot document : queryDocumentSnapshots) {
                        try {
                            // Create LocationModel objects first
                            Map<String, Object> fromData = (Map<String, Object>) document.get("from");
                            Map<String, Object> toData = (Map<String, Object>) document.get("to");

                            LocationModel from = new LocationModel(
                                    ((Long) fromData.get("locationID")).intValue(),
                                    (String) fromData.get("name"),
                                    (boolean) fromData.get("isUniversity")
                            );

                            LocationModel to = new LocationModel(
                                    ((Long) toData.get("locationID")).intValue(),
                                    (String) toData.get("name"),
                                    (boolean) fromData.get("isUniversity")
                            );

                            // Then create RideModel
                            RideModel ride = new RideModel(
                                    document.getLong("rideID").intValue(),
                                    null, // We'll skip driver for now
                                    from,
                                    to,
                                    document.getString("type"),
                                    document.getString("departureTime"),
                                    document.getString("arrivalTime"),
                                    document.getLong("availableSeats").intValue(),
                                    document.getLong("totalSeats").intValue(),
                                    document.getDouble("price"),
                                    document.getBoolean("isActive")
                            );
                            rideList.add(ride);
                        } catch (Exception e) {
                            Log.e("AccountMyRidesActivity", "Error parsing document: " + e.getMessage());
                        }
                    }
                    adapter.notifyDataSetChanged();
                })
                .addOnFailureListener(e -> Log.e("AccountMyRidesActivity", "Error loading rides", e));
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

    private UserModel convertMapToUserModel(Map<String, Object> map) {
        return new UserModel(
                ((Long) map.get("userID")).intValue(),
                ((Long) map.get("pfp")).intValue(),
                (String) map.get("name"),
                (String) map.get("email"),
                (String) map.get("phoneNumber"),
                (String) map.get("university"),
                (Boolean) map.get("isDriver")
        );
    }

    private LocationModel convertMapToLocationModel(Map<String, Object> map) {
        return new LocationModel(
                ((Long) map.get("locationID")).intValue(),
                (String) map.get("name"),
                (boolean) map.get("isUniversity")
        );
    }


}