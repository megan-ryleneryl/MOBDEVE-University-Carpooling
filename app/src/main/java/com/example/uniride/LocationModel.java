package com.example.uniride;

import java.io.Serializable;

public class LocationModel implements Serializable {
    private int locationID;
    private String name;
    private boolean isUniversity = false;

    public LocationModel() {

    }

    public LocationModel(int locationID, String name, boolean isUniversity) {
        this.locationID = locationID;
        this.name = name;
        this.isUniversity = isUniversity;
    }

    // Getters
    public int getLocationID() {
        return locationID;
    }
    public String getName() {
        return name;
    }
    public boolean getIsUniversity() {return isUniversity;}

    // Setters


    public void setName(String name) {
        this.name = name;
    }

    public void setUniversity(boolean university) {
        isUniversity = university;
    }

    // ToString helper
    @Override
    public String toString() {
        return name;
    }
}
