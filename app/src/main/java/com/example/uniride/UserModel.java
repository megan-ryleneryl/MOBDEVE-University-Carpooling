package com.example.uniride;

import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.DocumentSnapshot;
import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

public class UserModel implements Serializable {
    private int userID;
    private int pfp;
    private String name;
    private String email;
    private String phoneNumber;
    private int universityID;
    private boolean isDriver;
    private int carID;
    private double balance;
    private String licenseNumber;
    private String licenseExpiry;

    // Transient fields for object references - not stored in Firebase
    private transient LocationModel universityObj;
    private transient CarModel carObj;

    // Default constructor needed for Firebase
    public UserModel() {}

    // Constructor for passengers
    public UserModel(int userID, int pfp, String name, String email, String phoneNumber, int universityID) {
        this.userID = userID;
        this.pfp = pfp;
        this.name = name;
        this.email = email;
        this.phoneNumber = phoneNumber;
        this.universityID = universityID;
        this.isDriver = false;
        this.balance = 0.0;
    }

    // Constructor for drivers
    public UserModel(int userID, int pfp, String name, String email, String phoneNumber,
                     int universityID, int carID, String licenseNumber, String licenseExpiry) {
        this(userID, pfp, name, email, phoneNumber, universityID);
        this.isDriver = true;
        this.carID = carID;
        this.licenseNumber = licenseNumber;
        this.licenseExpiry = licenseExpiry;
    }

    // Method to populate related objects
    public void populateObjects(FirebaseFirestore db, OnPopulateCompleteListener listener) {
        final int[] completedQueries = {0};
        final int totalQueries = isDriver ? 2 : 1;

        // Get university data
        db.collection(MyFirestoreReferences.LOCATIONS_COLLECTION)
                .whereEqualTo("locationID", universityID)
                .get()
                .addOnSuccessListener(querySnapshot -> {
                    if (!querySnapshot.isEmpty()) {
                        DocumentSnapshot doc = querySnapshot.getDocuments().get(0);
                        this.universityObj = doc.toObject(LocationModel.class);
                    }
                    completedQueries[0]++;
                    checkCompletion(completedQueries[0], totalQueries, listener);
                });

        // Get car data if user is driver
        if (isDriver && carID != 0) {
            db.collection(MyFirestoreReferences.CARS_COLLECTION)
                    .whereEqualTo("carID", carID)
                    .get()
                    .addOnSuccessListener(querySnapshot -> {
                        if (!querySnapshot.isEmpty()) {
                            DocumentSnapshot doc = querySnapshot.getDocuments().get(0);
                            this.carObj = doc.toObject(CarModel.class);
                        }
                        completedQueries[0]++;
                        checkCompletion(completedQueries[0], totalQueries, listener);
                    });
        }
    }

    private void checkCompletion(int completed, int total, OnPopulateCompleteListener listener) {
        if (completed == total && listener != null) {
            listener.onPopulateComplete(this);
        }
    }

    // Interface for completion callback
    public interface OnPopulateCompleteListener {
        void onPopulateComplete(UserModel user);
    }

    // Standard getters
    public int getUserID() { return userID; }
    public int getPfp() { return pfp; }
    public String getName() { return name; }
    public String getEmail() { return email; }
    public String getPhoneNumber() { return phoneNumber; }
    public int getUniversityID() { return universityID; }
    public boolean isDriver() { return isDriver; }
    public int getCarID() { return carID; }
    public double getBalance() { return balance; }
    public String getLicenseNumber() { return licenseNumber; }
    public String getLicenseExpiry() { return licenseExpiry; }

    // Object getters
    public LocationModel getUniversity() { return universityObj; }
    public CarModel getCar() { return carObj; }

    // Setters
    public void setPfp(int pfp) { this.pfp = pfp; }
    public void setName(String name) { this.name = name; }
    public void setEmail(String email) { this.email = email; }
    public void setPhoneNumber(String phoneNumber) { this.phoneNumber = phoneNumber; }
    public void setUniversityID(int universityID) {
        this.universityID = universityID;
        this.universityObj = null;  // Clear cached object
    }
    public void setDriver(boolean driver) { isDriver = driver; }
    public void setCarID(int carID) {
        this.carID = carID;
        this.carObj = null;  // Clear cached object
    }
    public void setBalance(double balance) { this.balance = balance; }
    public void setLicenseNumber(String licenseNumber) { this.licenseNumber = licenseNumber; }
    public void setLicenseExpiry(String licenseExpiry) { this.licenseExpiry = licenseExpiry; }

    // Helper method to convert to Firebase document
    public Map<String, Object> toMap() {
        Map<String, Object> map = new HashMap<>();
        map.put("userID", userID);
        map.put("pfp", pfp);
        map.put("name", name);
        map.put("email", email);
        map.put("phoneNumber", phoneNumber);
        map.put("universityID", universityID);
        map.put("isDriver", isDriver);
        map.put("carID", carID);
        map.put("balance", balance);
        return map;
    }

    public static UserModel fromMap(Map<String, Object> map) {
        UserModel user = new UserModel();
        user.userID = ((Long) map.get("userID")).intValue();
        user.pfp = ((Long) map.get("pfp")).intValue();
        user.name = (String) map.get("name");
        user.email = (String) map.get("email");
        user.phoneNumber = (String) map.get("phoneNumber");
        user.universityID = ((Long) map.get("universityID")).intValue();
        user.isDriver = (boolean) map.get("isDriver");
        if (map.get("carID") != null) {
            user.carID = ((Long) map.get("carID")).intValue();
        }
        if (map.get("balance") != null) {
            user.balance = ((Long) map.get("balance")).doubleValue();
        }
        // Add these lines to load license details
        user.licenseNumber = (String) map.get("licenseNumber");
        user.licenseExpiry = (String) map.get("licenseExpiry");
        return user;
    }
}