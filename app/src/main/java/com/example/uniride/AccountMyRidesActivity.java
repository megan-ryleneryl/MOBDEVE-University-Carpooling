package com.example.uniride;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Activity to display a list of user's rides.
 */
public class AccountMyRidesActivity extends BottomNavigationActivity {
    private RecyclerView recyclerView;
    private MyRidesAdapter adapter;
    private List<RideModel> rideList;
    private FirebaseFirestore db;
    private FirebaseAuth mAuth;
    private FirebaseUser currentUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_account_my_rides);

        // Initialize Firebase instances
        db = FirebaseFirestore.getInstance();
        mAuth = FirebaseAuth.getInstance();
        currentUser = mAuth.getCurrentUser();

        if (currentUser == null) {
            startActivity(new Intent(this, AccountLoginActivity.class));
            finish();
            return;
        }

        // Initialize RecyclerView
        recyclerView = findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        rideList = new ArrayList<>();
        adapter = new MyRidesAdapter(this, rideList);
        recyclerView.setAdapter(adapter);

        // Load user's rides
        loadUserRides();
    }

    private void loadUserRides() {
        // First get the user's ID from their document
        db.collection(MyFirestoreReferences.USERS_COLLECTION)
                .document(currentUser.getUid())
                .get()
                .addOnSuccessListener(documentSnapshot -> {
                    if (documentSnapshot.exists()) {
                        int userId = ((Long) documentSnapshot.get("userID")).intValue();

                        // Query rides with this driver ID
                        db.collection(MyFirestoreReferences.RIDES_COLLECTION)
                                .whereEqualTo("driverID", userId)
                                .get()
                                .addOnSuccessListener(querySnapshot -> {
                                    rideList.clear();
                                    final int[] completedRides = {0};
                                    int totalRides = querySnapshot.size();

                                    if (totalRides == 0) {
                                        adapter.notifyDataSetChanged();
                                        return;
                                    }

                                    for (QueryDocumentSnapshot document : querySnapshot) {
                                        RideModel ride = RideModel.fromMap(document.getData());

                                        // Populate the ride's related objects
                                        ride.populateObjects(db, populatedRide -> {
                                            completedRides[0]++;

                                            // When all rides are populated, sort and notify adapter
                                            if (completedRides[0] == totalRides) {
                                                // Sort the list by rideID
                                                Collections.sort(rideList, (r1, r2) ->
                                                        Integer.compare(r1.getRideID(), r2.getRideID()));
                                                adapter.notifyDataSetChanged();
                                            }
                                        });

                                        rideList.add(ride);
                                    }
                                })
                                .addOnFailureListener(e -> {
                                    Toast.makeText(AccountMyRidesActivity.this,
                                            "Error loading rides: " + e.getMessage(),
                                            Toast.LENGTH_SHORT).show();
                                });
                    }
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(AccountMyRidesActivity.this,
                            "Error loading user data: " + e.getMessage(),
                            Toast.LENGTH_SHORT).show();
                });
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (currentUser != null) {
            loadUserRides(); // Reload rides when returning to activity
        }
    }

    @Override
    protected int getSelectedItemId() {
        return R.id.driver;
    }

    public void postRide(View v) {
        Intent intent = new Intent(this, RideCreate.class);
        startActivity(intent);
    }
}