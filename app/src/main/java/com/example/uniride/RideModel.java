package com.example.uniride;

import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.DocumentSnapshot;
import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

public class RideModel implements Serializable {
    private int rideID;
    private int driverID;        // Changed from UserModel driver
    private int fromLocationID;  // Changed from LocationModel from
    private int toLocationID;    // Changed from LocationModel to
    private String type;
    private String departureTime;
    private String arrivalTime;
    private int availableSeats;
    private int totalSeats;
    private double price;
    private boolean isActive;

    // Transient fields - not stored in Firebase
    private transient UserModel driverObj;
    private transient LocationModel fromLocationObj;
    private transient LocationModel toLocationObj;

    // Default constructor for Firebase
    public RideModel() {}

    public RideModel(int rideID, int driverID, int fromLocationID, int toLocationID,
                     String type, String departureTime, String arrivalTime,
                     int availableSeats, int totalSeats, double price, boolean isActive) {
        this.rideID = rideID;
        this.driverID = driverID;
        this.fromLocationID = fromLocationID;
        this.toLocationID = toLocationID;
        this.type = type;
        this.departureTime = departureTime;
        this.arrivalTime = arrivalTime;
        this.availableSeats = availableSeats;
        this.totalSeats = totalSeats;
        this.price = price;
        this.isActive = isActive;
    }

    // Method to populate related objects
    public void populateObjects(FirebaseFirestore db, OnPopulateCompleteListener listener) {
        final int[] completedQueries = {0};
        final int totalQueries = 3;

        // Get driver data
        db.collection(MyFirestoreReferences.USERS_COLLECTION)
                .whereEqualTo("userID", driverID)
                .get()
                .addOnSuccessListener(querySnapshot -> {
                    if (!querySnapshot.isEmpty()) {
                        DocumentSnapshot doc = querySnapshot.getDocuments().get(0);
                        this.driverObj = doc.toObject(UserModel.class);
                    }
                    completedQueries[0]++;
                    checkCompletion(completedQueries[0], totalQueries, listener);
                });

        // Get from location data
        db.collection(MyFirestoreReferences.LOCATIONS_COLLECTION)
                .whereEqualTo("locationID", fromLocationID)
                .get()
                .addOnSuccessListener(querySnapshot -> {
                    if (!querySnapshot.isEmpty()) {
                        DocumentSnapshot doc = querySnapshot.getDocuments().get(0);
                        this.fromLocationObj = doc.toObject(LocationModel.class);
                    }
                    completedQueries[0]++;
                    checkCompletion(completedQueries[0], totalQueries, listener);
                });

        // Get to location data
        db.collection(MyFirestoreReferences.LOCATIONS_COLLECTION)
                .whereEqualTo("locationID", toLocationID)
                .get()
                .addOnSuccessListener(querySnapshot -> {
                    if (!querySnapshot.isEmpty()) {
                        DocumentSnapshot doc = querySnapshot.getDocuments().get(0);
                        this.toLocationObj = doc.toObject(LocationModel.class);
                    }
                    completedQueries[0]++;
                    checkCompletion(completedQueries[0], totalQueries, listener);
                });
    }

    private void checkCompletion(int completed, int total, OnPopulateCompleteListener listener) {
        if (completed == total && listener != null) {
            listener.onPopulateComplete(this);
        }
    }

    // Interface for completion callback
    public interface OnPopulateCompleteListener {
        void onPopulateComplete(RideModel ride);
    }

    // Getters for IDs
    public int getRideID() { return rideID; }
    public int getDriverID() { return driverID; }
    public int getFromLocationID() { return fromLocationID; }
    public int getToLocationID() { return toLocationID; }

    // Getters for objects
    public UserModel getDriver() { return driverObj; }
    public LocationModel getFrom() { return fromLocationObj; }
    public LocationModel getTo() { return toLocationObj; }

    // Other getters
    public String getType() { return type; }
    public String getDepartureTime() { return departureTime; }
    public String getArrivalTime() { return arrivalTime; }
    public int getAvailableSeats() { return availableSeats; }
    public int getTotalSeats() { return totalSeats; }
    public double getPrice() { return price; }
    public boolean isActive() { return isActive; }

    // Setters
    public void setAvailableSeats(int amountReduced) {
        this.availableSeats -= amountReduced;
    }
    public void setIsActive(boolean isActive) { this.isActive = isActive; }

    // Helper method to convert to Firebase document
    public Map<String, Object> toMap() {
        Map<String, Object> map = new HashMap<>();
        map.put("rideID", rideID);
        map.put("driverID", driverID);
        map.put("fromLocationID", fromLocationID);
        map.put("toLocationID", toLocationID);
        map.put("type", type);
        map.put("departureTime", departureTime);
        map.put("arrivalTime", arrivalTime);
        map.put("availableSeats", availableSeats);
        map.put("totalSeats", totalSeats);
        map.put("price", price);
        map.put("isActive", isActive);
        return map;
    }

    // Static method to create from Firebase document
    public static RideModel fromMap(Map<String, Object> map) {
        return new RideModel(
                ((Long) map.get("rideID")).intValue(),
                ((Long) map.get("driverID")).intValue(),
                ((Long) map.get("fromLocationID")).intValue(),
                ((Long) map.get("toLocationID")).intValue(),
                (String) map.get("type"),
                (String) map.get("departureTime"),
                (String) map.get("arrivalTime"),
                ((Long) map.get("availableSeats")).intValue(),
                ((Long) map.get("totalSeats")).intValue(),
                (double) map.get("price"),
                (boolean) map.get("isActive")
        );
    }
}