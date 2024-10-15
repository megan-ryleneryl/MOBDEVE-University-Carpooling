package com.example.uniride;

public class LocationModel {
    private int locationID;
    private String name;

    public LocationModel(int locationID, String name) {
        this.locationID = locationID;
        this.name = name;
    }

    // Getter
    public int getLocationID() {
        return locationID;
    }
    public String getName() {
        return name;
    }
}
