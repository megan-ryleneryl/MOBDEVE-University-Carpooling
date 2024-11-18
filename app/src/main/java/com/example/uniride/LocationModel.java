package com.example.uniride;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

public class LocationModel implements Serializable {
    private int locationID;
    private String name;
    private boolean isUniversity;

    // Default constructor needed for Firebase
    public LocationModel() {}

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

    public boolean getIsUniversity() {
        return isUniversity;
    }

    // Setters
    public void setName(String name) {
        this.name = name;
    }

    public void setUniversity(boolean university) {
        isUniversity = university;
    }

    // Helper method to convert to Firebase document
    public Map<String, Object> toMap() {
        Map<String, Object> map = new HashMap<>();
        map.put("locationID", locationID);
        map.put("name", name);
        map.put("isUniversity", isUniversity);
        return map;
    }

    // Static method to create from Firebase document
    public static LocationModel fromMap(Map<String, Object> map) {
        return new LocationModel(
                ((Long) map.get("locationID")).intValue(),
                (String) map.get("name"),
                (boolean) map.get("isUniversity")
        );
    }

    // ToString helper for UI display (e.g., spinners)
    @Override
    public String toString() {
        return name;
    }
}