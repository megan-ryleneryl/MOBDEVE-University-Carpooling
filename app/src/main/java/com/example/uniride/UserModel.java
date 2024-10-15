package com.example.uniride;

public class UserModel {
    private int userID;
    private String name;
    private String email;
    private String phoneNumber;
    private String university;
    private String accountStatus;
    private String userType; // "Driver" or "Passenger"
    private CarModel car; // Only for drivers
    private double balance = 0; // Only for drivers

    public UserModel(int userID, String name, String email, String phoneNumber, String university, String accountStatus, String userType) {
        this.userID = userID;
        this.name = name;
        this.email = email;
        this.phoneNumber = phoneNumber;
        this.university = university;
        this.accountStatus = accountStatus;
        this.userType = userType;
    }

    // Getters
    public int getUserID() { return userID; }
    public String getName() { return name; }
    public String getEmail() { return email; }
    public String getPhoneNumber() { return phoneNumber; }
    public String getUniversity() { return university; }
    public String getAccountStatus() { return accountStatus; }
    public String getUserType() { return userType; }
    public CarModel getCar() { return car; }
    public double getBalance() { return balance; }

    // Setters
    public void setName(String name) { this.name = name; }
    public void setEmail(String email) { this.email = email; }
    public void setPhoneNumber(String phoneNumber) { this.phoneNumber = phoneNumber; }
    public void setUniversity(String university) { this.university = university; }
    public void setAccountStatus(String accountStatus) { this.accountStatus = accountStatus; }
    public void setUserType(String userType) { this.userType = userType; }
    public void setCar(CarModel car) { this.car = car; }
    public void setBalance(double balance) { this.balance = balance; }

    public boolean isDriver() {
        return "Driver".equals(userType);
    }
}