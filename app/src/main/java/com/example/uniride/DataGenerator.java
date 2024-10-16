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
//    public static final CarModel car1 = new CarModel();
//    public static final CarModel car2 = new CarModel();
//
//    public static final UserModel user1 = new UserModel();
//    public static final UserModel user2 = new UserModel();
//
//    public static final RideModel ride1 = new RideModel();
//    public static final RideModel ride2 = new RideModel();
//    public static final RideModel ride3 = new RideModel();
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
        ArrayList<CarModel> data = new ArrayList<CarModel>();

//        data.add(car1);
//        data.add(car2);

        return data;
    }

    public static ArrayList<UserModel> loadUserData() {
        ArrayList<UserModel> data = new ArrayList<UserModel>();

//        data.add(user1);
//        data.add(user2);

        return data;
    }

    public static ArrayList<RideModel> loadRideData() {
        ArrayList<RideModel> data = new ArrayList<RideModel>();

//        data.add(ride1);
//        data.add(ride2);
//        data.add(ride3);

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
