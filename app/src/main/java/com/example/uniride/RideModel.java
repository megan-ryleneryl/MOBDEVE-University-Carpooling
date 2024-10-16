package com.example.uniride;

import java.io.Serializable;

public class RideModel implements Serializable {
    private int rideID;
    private UserModel driver;
    private LocationModel from;
    private LocationModel to;
    private String type; // toUniversity, fromUniversity
    private String departureTime;
    private String arrivalTime;
    private int availableSeats;
    private int totalSeats;
    private int price;
    private String status; // active, inactive

    public RideModel(int rideID, UserModel driver, LocationModel from, LocationModel to, String type, String departureTime, String arrivalTime, int availableSeats, int totalSeats, int price, String status) {
        this.rideID = rideID;
        this.driver = driver;
        this.from = from;
        this.to = to;
        this.type = type;
        this.departureTime = departureTime;
        this.arrivalTime = arrivalTime;
        this.availableSeats = availableSeats;
        this.totalSeats = totalSeats;
        this.price = price;
        this.status = status;
    }

    // Getters
    public int getRideID() { return rideID; }
    public UserModel getDriver() { return driver; }
    public LocationModel getFrom() { return from; }
    public LocationModel getTo() { return to; }
    public String getType() { return type; }
    public String getDepartureTime() { return departureTime; }
    public String getArrivalTime() { return arrivalTime; }
    public int getAvailableSeats() { return availableSeats; }
    public int getTotalSeats() { return totalSeats; }
    public int getPrice() { return price; }
    public String getStatus() { return status; }

    // Setters
    public void setAvailableSeats(int amountReduced) {
        this.availableSeats -= amountReduced;
    }
    public void setStatus(String status) {
        this.status = status;
    }
}
