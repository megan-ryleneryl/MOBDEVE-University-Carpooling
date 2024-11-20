package com.example.uniride;

import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.DocumentSnapshot;
import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class BookingModel implements Serializable {
    private int bookingID;
    private int rideID;
    private int passengerID;
    private String date;
    private boolean isPaymentComplete = false;
    private boolean isBookingDone = false;

    // Transient fields - not stored in Firebase
    private transient RideModel rideObj;
    private transient UserModel passengerObj;

    // Default constructor for Firebase
    public BookingModel() {}

    public BookingModel(int bookingID, int rideID, int passengerID, String date, boolean isPaymentComplete, boolean isBookingDone) {
        this.bookingID = bookingID;
        this.rideID = rideID;
        this.passengerID = passengerID;
        this.date = date;
        this.isPaymentComplete = isPaymentComplete;
        this.isBookingDone = isBookingDone;
    }

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
                        if (this.rideObj != null) {
                            this.rideObj.populateObjects(db, ride -> {
                                completedQueries[0]++;
                                checkCompletion(completedQueries[0], totalQueries, listener);
                            });
                        }
                    }
                });

        // Get passenger data
        db.collection(MyFirestoreReferences.USERS_COLLECTION)
                .whereEqualTo("userID", passengerID)
                .get()
                .addOnSuccessListener(querySnapshot -> {
                    if (!querySnapshot.isEmpty()) {
                        DocumentSnapshot doc = querySnapshot.getDocuments().get(0);
                        this.passengerObj = UserModel.fromMap(doc.getData());
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

    public interface OnPopulateCompleteListener {
        void onPopulateComplete(BookingModel booking);
    }

    // Getters
    public int getBookingID() { return bookingID; }
    public int getRideID() { return rideID; }
    public int getPassengerID() { return passengerID; }
    public String getDate() { return date; }
    public boolean isPaymentComplete() { return isPaymentComplete; }
    public boolean isBookingDone() { return isBookingDone; }
    public RideModel getRide() { return rideObj; }
    public UserModel getPassenger() { return passengerObj; }

    // Setters
    public void setBookingID(int bookingID) { this.bookingID = bookingID; }
    public void setRideID(int rideID) { this.rideID = rideID; }
    public void setPassengerID(int passengerID) { this.passengerID = passengerID; }
    public void setDate(String date) { this.date = date; }
    public void setPaymentComplete(boolean paymentComplete) { this.isPaymentComplete = paymentComplete; }
    public void setBookingDone(boolean bookingDone) { this.isBookingDone = bookingDone; }

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
        booking.date = (String) map.get("date");
        booking.isPaymentComplete = (boolean) map.get("isPaymentComplete");
        booking.isBookingDone = (boolean) map.get("isBookingDone");
        return booking;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        BookingModel that = (BookingModel) obj;
        return Objects.equals(bookingID, that.bookingID);
    }

    @Override
    public int hashCode() {
        return Objects.hash(bookingID);
    }

    @Override
    public String toString() {
        return "BookingModel{" +
                "bookingID=" + bookingID +
                ", rideID=" + rideID +
                ", passengerID=" + passengerID +
                ", date=" + date +
                ", isPaymentComplete=" + isPaymentComplete +
                ", isBookingDone=" + isBookingDone +
                '}';
    }
}