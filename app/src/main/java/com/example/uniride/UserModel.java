package com.example.uniride;

import java.io.Serializable;

public class UserModel implements Serializable {
    private int userID;
    private int pfp;
    private String name;
    private String email;
    private String phoneNumber;
    private String university;
    private boolean isDriver; // true if they are verified as a driver, false if passenger
    private CarModel car; // only for drivers
    private double balance = 0; // Only for drivers

    public UserModel(int userID, int pfp, String name, String email, String phoneNumber, String university, boolean isDriver) {
        this.userID = userID;
        this.pfp = pfp;
        this.name = name;
        this.email = email;
        this.phoneNumber = phoneNumber;
        this.university = university;
        this.isDriver = isDriver;
    }

    public UserModel(int userID, int pfp, String name, String email, String phoneNumber, String university, boolean isDriver, CarModel car) {
        this.userID = userID;
        this.pfp = pfp;
        this.name = name;
        this.email = email;
        this.phoneNumber = phoneNumber;
        this.university = university;
        this.isDriver = isDriver;
        this.car = car;
    }

    // Getters
    public int getUserID() { return userID; }
    public int getPfp() { return pfp; }
    public String getName() { return name; }
    public String getEmail() { return email; }
    public String getPhoneNumber() { return phoneNumber; }
    public String getUniversity() { return university; }
    public boolean isDriver() { return isDriver; }
    public CarModel getCar() { return car; }
    public double getBalance() { return balance; }

    // Setters
    public void setPfp(int pfp) { this.pfp = pfp; }
    public void setName(String name) { this.name = name; }
    public void setEmail(String email) { this.email = email; }
    public void setPhoneNumber(String phoneNumber) { this.phoneNumber = phoneNumber; }
    public void setUniversity(String university) { this.university = university; }
    public void setDriver(boolean driver) { isDriver = driver; }
    public void setCar(CarModel car) { this.car = car; }
    public void setBalance(double balance) { this.balance = balance; } //imo we can use amountIncrease and then this.balance += amountIncrease


    public int getRating() {
        int rating = 0;

        // for every review, if uid matches this users id, add to arraylist
        // average the arraylist

        return rating;
    }
}