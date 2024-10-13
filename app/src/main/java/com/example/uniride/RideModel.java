package com.example.uniride;

public class RideModel {
    private String rideId;
    private String from;
    private String to;
    private String type;
    private String departure;
    private int availableSeats;
    private String price;
    private String status;

    public RideModel(String rideId, String from, String to, String type, String departure, int availableSeats, String price, String status) {
        this.rideId = rideId;
        this.from = from;
        this.to = to;
        this.type = type;
        this.departure = departure;
        this.availableSeats = availableSeats;
        this.price = price;
        this.status = status;
    }

    // Getters
    public String getRideId() { return rideId; }
    public String getFrom() { return from; }
    public String getTo() { return to; }
    public String getType() { return type; }
    public String getDeparture() { return departure; }
    public int getAvailableSeats() { return availableSeats; }
    public String getPrice() { return price; }
    public String getStatus() { return status; }
}
