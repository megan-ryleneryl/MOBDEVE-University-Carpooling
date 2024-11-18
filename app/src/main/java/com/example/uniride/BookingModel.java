package com.example.uniride;

import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.DocumentSnapshot;
import java.io.Serializable;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class BookingModel implements Serializable {
    private int bookingID;
    private int rideID;          // Changed from RideModel ride
    private int passengerID;     // Changed from UserModel passenger
    private Date date;
    private boolean isPaymentComplete = false;
    private boolean isBookingDone = false;

    // Transient fields - not stored in Firebase
    private transient RideModel rideObj;
    private transient UserModel passengerObj;

    // Default constructor for Firebase
    public BookingModel() {}

    public BookingModel(int bookingID, int rideID, int passengerID, Date date) {
        this.bookingID = bookingID;
        this.rideID = rideID;
        this.passengerID = passengerID;
        this.date = date;
    }

    // Method to populate related objects
    public void populateObjects(FirebaseFirestore db, OnPopulateCompleteListener listener) {
        final int[] completedQueries = {0};
        final int totalQueries = 2;

        // Get ride data
        db.collection(MyFirestoreReferences.RIDES_COLLECTION)
                .whereEqualTo("rideID", rideID)
                .get()
                .addOnSuccessListener(querySnapshot -> {
                    if (!querySnapshot.isEmpty()) {
                        DocumentSnapshot doc = querySnapshot.getDocuments().get(0);
                        this.rideObj = RideModel.fromMap(doc.getData());
                        // Populate ride's related objects
                        this.rideObj.populateObjects(db, ride -> {
                            completedQueries[0]++;
                            checkCompletion(completedQueries[0], totalQueries, listener);
                        });
                    } else {
                        completedQueries[0]++;
                        checkCompletion(completedQueries[0], totalQueries, listener);
                    }
                })
                .addOnFailureListener(e -> {
                    completedQueries[0]++;
                    checkCompletion(completedQueries[0], totalQueries, listener);
                });

        // Get passenger data
        db.collection(MyFirestoreReferences.USERS_COLLECTION)
                .whereEqualTo("userID", passengerID)
                .get()
                .addOnSuccessListener(querySnapshot -> {
                    if (!querySnapshot.isEmpty()) {
                        DocumentSnapshot doc = querySnapshot.getDocuments().get(0);
                        this.passengerObj = UserModel.fromMap(doc.getData());
                        // Populate passenger's related objects
                        this.passengerObj.populateObjects(db, user -> {
                            completedQueries[0]++;
                            checkCompletion(completedQueries[0], totalQueries, listener);
                        });
                    } else {
                        completedQueries[0]++;
                        checkCompletion(completedQueries[0], totalQueries, listener);
                    }
                })
                .addOnFailureListener(e -> {
                    completedQueries[0]++;
                    checkCompletion(completedQueries[0], totalQueries, listener);
                });
    }

    private void checkCompletion(int completed, int total, OnPopulateCompleteListener listener) {
        if (completed == total && listener != null) {
            listener.onPopulateComplete(this);
        }
    }

    public interface OnPopulateCompleteListener {
        void onPopulateComplete(BookingModel booking);
    }

    // Getters for IDs
    public int getBookingID() { return bookingID; }
    public int getRideID() { return rideID; }
    public int getPassengerID() { return passengerID; }

    // Getters for objects
    public RideModel getRide() {
        return rideObj;
    }

    public UserModel getPassenger() {
        return passengerObj;
    }

    // Other getters
    public Date getDate() { return date; }
    public boolean getPaymentComplete() { return isPaymentComplete; }
    public boolean getBookingDone() { return isBookingDone; }

    // Setters
    public void setPaymentComplete(boolean paymentComplete) {
        isPaymentComplete = paymentComplete;
    }

    public void setBookingDone(boolean bookingDone) {
        isBookingDone = bookingDone;
    }

    // Helper method to convert to Firebase document
    public Map<String, Object> toMap() {
        Map<String, Object> map = new HashMap<>();
        map.put("bookingID", bookingID);
        map.put("rideID", rideID);
        map.put("passengerID", passengerID);
        map.put("date", date);
        map.put("isPaymentComplete", isPaymentComplete);
        map.put("isBookingDone", isBookingDone);
        return map;
    }

    // Static method to create from Firebase document
    public static BookingModel fromMap(Map<String, Object> map) {
        BookingModel booking = new BookingModel();
        booking.bookingID = ((Long) map.get("bookingID")).intValue();
        booking.rideID = ((Long) map.get("rideID")).intValue();
        booking.passengerID = ((Long) map.get("passengerID")).intValue();
        booking.date = (Date) map.get("date");
        booking.isPaymentComplete = (boolean) map.get("isPaymentComplete");
        booking.isBookingDone = (boolean) map.get("isBookingDone");
        return booking;
    }

    // Helper method for comparing BookingModels
    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;

        BookingModel that = (BookingModel) obj;
        return bookingID == that.bookingID;
    }

    @Override
    public int hashCode() {
        return Objects.hash(bookingID);
    }
}