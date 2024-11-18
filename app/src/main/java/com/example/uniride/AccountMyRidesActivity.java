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

        // Initialize RecyclerView
        recyclerView = findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        rideList = new ArrayList<>();
        adapter = new MyRidesAdapter(this, rideList);
        recyclerView.setAdapter(adapter);

        // Load ride data
        loadRides();
    }

    private void loadRides() {
        db.collection(MyFirestoreReferences.RIDES_COLLECTION)
                .get()
                .addOnSuccessListener(querySnapshot -> {
                    rideList.clear();
                    for (QueryDocumentSnapshot document : querySnapshot) {
                        RideModel ride = document.toObject(RideModel.class);
                        ride.populateObjects(db, this::onRidePopulateComplete);
                        rideList.add(ride);
                    }
                    adapter.notifyDataSetChanged();
                })
                .addOnFailureListener(e -> Log.e("AccountMyRidesActivity", "Error loading rides", e));
    }

    private void onRidePopulateComplete(RideModel ride) {
        // Ride object is now fully populated
        adapter.notifyDataSetChanged();
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