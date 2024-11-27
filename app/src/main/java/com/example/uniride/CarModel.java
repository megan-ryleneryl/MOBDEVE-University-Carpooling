package com.example.uniride;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

public class CarModel implements Serializable {
    private int carID;
    private int carImage;
    private String make;
    private String model;
    private String plateNumber;
    private int seatingCapacity;  // Added field for seating capacity

    // int hatchbackResourceId = 2131230920;
    // int sedanResourceId = 2131231041;
    // int suvResourceId = 2131231044;
    // int vanResourceId = 2131231053;
    // int mpvResourceId = 2131230989;

    // Default constructor needed for Firebase
    public CarModel() {}

    public CarModel(int carID, int carImage, String make, String model, String plateNumber, int seatingCapacity) {
        this.carID = carID;
        this.carImage = carImage;
        this.make = make;
        this.model = model;
        this.plateNumber = plateNumber;
        this.seatingCapacity = seatingCapacity;
    }

    // Getters
    public int getCarID() { return carID; }
    public int getCarImage() { return carImage; }
    public String getMake() { return make; }
    public String getModel() { return model; }
    public String getPlateNumber() { return plateNumber; }
    public int getSeatingCapacity() { return seatingCapacity; }

    // Setters
    public void setCarID(int carID) { this.carID = carID; }
    public void setCarImage(int carImage) { this.carImage = carImage; }
    public void setMake(String make) { this.make = make; }
    public void setModel(String model) { this.model = model; }
    public void setPlateNumber(String plateNumber) { this.plateNumber = plateNumber; }
    public void setSeatingCapacity(int seatingCapacity) { this.seatingCapacity = seatingCapacity; }

    // Helper method to convert to Firebase document
    public Map<String, Object> toMap() {
        Map<String, Object> map = new HashMap<>();
        map.put("carID", carID);
        map.put("carImage", carImage);
        map.put("make", make);
        map.put("model", model);
        map.put("plateNumber", plateNumber);
        map.put("seatingCapacity", seatingCapacity);
        return map;
    }

    // Static method to create from Firebase document
    public static CarModel fromMap(Map<String, Object> map) {
        return new CarModel(
                ((Long) map.get("carID")).intValue(),
                ((Long) map.get("carImage")).intValue(),
                (String) map.get("make"),
                (String) map.get("model"),
                (String) map.get("plateNumber"),
                ((Long) map.get("seatingCapacity")).intValue()
        );
    }
}