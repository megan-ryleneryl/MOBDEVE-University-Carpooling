package com.example.uniride;

public class BookingModel {
    private int bookingID;
    private RideModel ride;
    private UserModel user;
    private String paymentStatus; // paid, unpaid
    private String bookingStatus; // pending, completed

    public BookingModel (int bookingID, RideModel ride, UserModel user, String paymentStatus, String bookingStatus) {
        this.bookingID = bookingID;
        this.ride = ride;
        this.user = user;
        this.paymentStatus = paymentStatus;
        this.bookingStatus = bookingStatus;
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

    public String getPaymentStatus() {
        return paymentStatus;
    }

    public String getBookingStatus() {
        return bookingStatus;
    }
}
