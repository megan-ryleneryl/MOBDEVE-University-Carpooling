package com.example.uniride;

import android.content.Context;

import com.google.firebase.firestore.FirebaseFirestore;
import com.google.android.gms.tasks.Tasks;
import java.util.ArrayList;
import java.util.concurrent.CountDownLatch;

public class FirebaseMigrationHelper {
    private FirebaseFirestore db;

    public FirebaseMigrationHelper() {
        db = FirebaseFirestore.getInstance();
    }

    public void migrateAllData() {
        // Create a latch to wait for all operations to complete
        CountDownLatch latch = new CountDownLatch(6); // One for each collection

        // Migrate locations first since they're referenced by other entities
        ArrayList<LocationModel> locations = DataGenerator.loadLocationData();
        for (LocationModel location : locations) {
            db.collection(MyFirestoreReferences.LOCATIONS_COLLECTION)
                    .add(location.toMap())
                    .addOnCompleteListener(task -> {
                        if (task.isSuccessful()) {
                            System.out.println("Location added: " + location.getName());
                        } else {
                            System.err.println("Error adding location: " + location.getName());
                        }
                    });
        }
        latch.countDown();



        // Migrate rides
        ArrayList<RideModel> rides = DataGenerator.loadRideData();
        for (RideModel ride : rides) {
            db.collection(MyFirestoreReferences.RIDES_COLLECTION)
                    .add(ride.toMap())
                    .addOnCompleteListener(task -> {
                        if (task.isSuccessful()) {
                            System.out.println("Ride added: " + ride.getRideID());
                        } else {
                            System.err.println("Error adding ride: " + ride.getRideID());
                        }
                    });
        }
        latch.countDown();

        // Migrate bookings
//        ArrayList<BookingModel> bookings = DataGenerator.loadBookingData();
//        for (BookingModel booking : bookings) {
//            db.collection(MyFirestoreReferences.BOOKINGS_COLLECTION)
//                    .add(booking.toMap())
//                    .addOnCompleteListener(task -> {
//                        if (task.isSuccessful()) {
//                            System.out.println("Booking added: " + booking.getBookingID());
//                        } else {
//                            System.err.println("Error adding booking: " + booking.getBookingID());
//                        }
//                    });
//        }
//        latch.countDown();

        // Wait for all operations to complete
        try {
            latch.await();
            System.out.println("All data migrated successfully!");
        } catch (InterruptedException e) {
            System.err.println("Migration interrupted: " + e.getMessage());
        }
    }

    // Helper method to call from an Activity
    public static void migrateData(Context context) {
        FirebaseMigrationHelper helper = new FirebaseMigrationHelper();
        helper.migrateAllData();
    }
}