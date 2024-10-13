package com.example.uniride;

public class CarModel {
    private String make;
    private String model;
    private String plateNumber;
    private int year;

    public CarModel(String make, String model, String plateNumber, int year) {
        this.make = make;
        this.model = model;
        this.plateNumber = plateNumber;
        this.year = year;
    }

    // Getters and setters
    public String getMake() { return make; }
    public void setMake(String make) { this.make = make; }

    public String getModel() { return model; }
    public void setModel(String model) { this.model = model; }

    public String getPlateNumber() { return plateNumber; }
    public void setPlateNumber(String plateNumber) { this.plateNumber = plateNumber; }

    public int getYear() { return year; }
    public void setYear(int year) { this.year = year; }
}