package com.example.uniride;

import java.io.Serializable;
import java.util.Date;

public class BookingModel implements Serializable {
    private int bookingID;
    private RideModel ride;
    private UserModel passenger;
    private Date date;
    private boolean isPaymentComplete = false;
    private boolean isBookingDone = false;

    public BookingModel (int bookingID, RideModel ride, UserModel passenger, Date date) {
        this.bookingID = bookingID;
        this.ride = ride;
        this.passenger = passenger;
        this.date = date;
    }

    // Getters
    public int getBookingID() {
        return bookingID;
    }
    public RideModel getRide() {
        return ride;
    }
    public UserModel getPassenger() {
        return passenger;
    }
    public Date getDate() {
        return date;
    }
    public boolean getPaymentComplete() {
        return isPaymentComplete;
    }
    public boolean getBookingDone() {
        return isBookingDone;
    }
    
    // Setters
    public void getPaymentComplete(boolean paymentComplete) {
        isPaymentComplete = paymentComplete;
    }
    public void setBookingDone(boolean bookingDone) {
        isBookingDone = bookingDone;
    }
}
