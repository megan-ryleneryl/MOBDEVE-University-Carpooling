package com.example.uniride;

import java.io.Serializable;

public class LocationModel implements Serializable {
    private int locationID;
    private String name;

    public LocationModel(int locationID, String name) {
        this.locationID = locationID;
        this.name = name;
    }

    // Getters
    public int getLocationID() {
        return locationID;
    }
    public String getName() {
        return name;
    }
}
