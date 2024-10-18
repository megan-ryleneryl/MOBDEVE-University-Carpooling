package com.example.uniride;

import java.io.Serializable;

public class CarModel implements Serializable {
    private int carID;
    private int carImage;
    private String make;
    private String model;
    private String plateNumber;

    public CarModel() {

    }

    public CarModel(int carID, int carImage, String make, String model, String plateNumber) {
        this.carID = carID;
        this.carImage = carImage;
        this.make = make;
        this.model = model;
        this.plateNumber = plateNumber;
    }

    // Getters and setters
    public int getCarID() { return carID; }
    public void setCarID(int carID) { this.carID = carID; }

    public int getCarImage() { return carImage; }
    public void setCarImage(int carImage) { this.carImage = carImage; }

    public String getMake() { return make; }
    public void setMake(String make) { this.make = make; }

    public String getModel() { return model; }
    public void setModel(String model) { this.model = model; }

    public String getPlateNumber() { return plateNumber; }
    public void setPlateNumber(String plateNumber) { this.plateNumber = plateNumber; }
}