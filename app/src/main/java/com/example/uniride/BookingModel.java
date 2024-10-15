package com.example.uniride;

public class BookingModel {
    private int bookingID;
    private RideModel ride;
    private UserModel user;
    private boolean isPaymentComplete = false;
    private boolean isBookingDone = false;

    public BookingModel (int bookingID, RideModel ride, UserModel user) {
        this.bookingID = bookingID;
        this.ride = ride;
        this.user = user;
    }

    // Getters
    public int getBookingID() {
        return bookingID;
    }

    public RideModel getRide() {
        return ride;
    }

    public UserModel getUser() {
        return user;
    }

    public boolean getPaymentStatus() {
        return isPaymentComplete;
    }

    public boolean getBookingStatus() {
        return isBookingDone;
    }
}
