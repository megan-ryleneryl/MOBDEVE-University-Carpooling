package com.example.uniride;

import java.lang.reflect.Array;
import java.util.ArrayList;

public class DataGenerator {
    public static final LocationModel location1 = new LocationModel(10001, "DLSU");
    public static final LocationModel location2 = new LocationModel(10001, "ADMU");
    public static final LocationModel location3 = new LocationModel(10002, "Taguig");
    public static final LocationModel location4 = new LocationModel(10002, "Binondo");
    public static final LocationModel location5 = new LocationModel(10002, "Marikina");
    public static final LocationModel location6 = new LocationModel(10002, "Quezon City");

    // Need help populating these once models are final!
    // Car data
    public static final CarModel car1 = new CarModel(20001, R.drawable.sedan, "Toyota", "Corolla", "ABC 123");
    public static final CarModel car2 = new CarModel(20002, R.drawable.hatchback, "Honda", "Civic", "XYZ 789");

    // User data
    public static final UserModel user1 = new UserModel(30001, R.drawable.default_profile_image, "John Doe", "john@example.com", "+639123456789", "DLSU", true);
    public static final UserModel user2 = new UserModel(30002, R.drawable.default_profile_image, "Jane Smith", "jane@example.com", "+639987654321", "ADMU", false);
    public static final UserModel user3 = new UserModel(30003, R.drawable.default_profile_image, "Alice Johnson", "alice@example.com", "+639111222333", "DLSU", true);

    // Ride data
    public static final RideModel ride1 = new RideModel(40001, user1, location3, location1, "toUniversity", "08:00 AM", "09:00 AM", 3, 4, 150.0, true);
    public static final RideModel ride2 = new RideModel(40002, user3, location2, location5, "fromUniversity", "05:00 PM", "06:30 PM", 2, 3, 200.0, true);
    public static final RideModel ride3 = new RideModel(40003, user1, location1, location4, "fromUniversity", "04:30 PM", "05:45 PM", 1, 4, 180.0, true);
//
//    public static final BookingModel booking1 = new BookingModel(40001, ride1, user1);
//    public static final BookingModel booking2 = new BookingModel(40002, ride2, user2);

//    public static final ReviewModel review1 = new ReviewModel(60001, user1, user2, ...);

//    public static final MessageModel message1 = new MessageModel(70001, user1, user2, ...);

    // Data-loading functions
    public static ArrayList<LocationModel> loadLocationData() {
        ArrayList<LocationModel> data = new ArrayList<LocationModel>();

        data.add(location1);
        data.add(location2);
        data.add(location3);
        data.add(location4);
        data.add(location5);
        data.add(location6);

        return data;
    }

    public static ArrayList<CarModel> loadCarData() {
        ArrayList<CarModel> data = new ArrayList<>();
        data.add(car1);
        data.add(car2);
        return data;
    }

    public static ArrayList<UserModel> loadUserData() {
        ArrayList<UserModel> data = new ArrayList<>();
        data.add(user1);
        data.add(user2);
        data.add(user3);

        // Set cars for drivers
        user1.setCar(car1);
        user3.setCar(car2);

        // Set initial balance for drivers
        user1.setBalance(500.0);
        user3.setBalance(750.0);

        return data;
    }

    public static ArrayList<RideModel> loadRideData() {
        ArrayList<RideModel> data = new ArrayList<>();
        data.add(ride1);
        data.add(ride2);
        data.add(ride3);
        return data;
    }

    public static ArrayList<BookingModel> loadBookingData() {
        ArrayList<BookingModel> data = new ArrayList<BookingModel>();

//        data.add(booking1);
//        data.add(booking2);

        return data;
    }

    public static ArrayList<ReviewModel> loadReviewData() {
        ArrayList<ReviewModel> data = new ArrayList<ReviewModel>();

//        data.add(review1);

        return data;
    }

    public static ArrayList<MessageModel> loadMessageData() {
        ArrayList<MessageModel> data = new ArrayList<MessageModel>();

//        data.add(message1);

        return data;
    }
}
