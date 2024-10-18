package com.example.uniride;

import java.io.Serializable;
import java.util.Date;
import java.util.Objects;

public class BookingModel implements Serializable {
    private int bookingID;
    private RideModel ride; // TODO: Change to rideID for mco3, same for passenger
    private UserModel passenger;
    private Date date;
    private boolean isPaymentComplete = false;
    private boolean isBookingDone = false;

    public BookingModel() {

    }

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

    // Helper method for comparing BookingModels to one another
    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;

        BookingModel that = (BookingModel) obj;
        return bookingID == that.bookingID;
    }

    @Override
    public int hashCode() {
        return Objects.hash(bookingID); // Ensure you use a unique field like `id`
    }
}
