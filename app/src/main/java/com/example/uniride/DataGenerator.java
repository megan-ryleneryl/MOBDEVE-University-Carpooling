package com.example.uniride;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

public class DataGenerator {
    // Location data
    public static final LocationModel location1 = new LocationModel(10001, "DLSU");
    public static final LocationModel location2 = new LocationModel(10002, "ADMU");
    public static final LocationModel location3 = new LocationModel(10003, "Taguig");
    public static final LocationModel location4 = new LocationModel(10004, "Binondo");
    public static final LocationModel location5 = new LocationModel(10005, "Marikina");
    public static final LocationModel location6 = new LocationModel(10006, "Quezon City");
    public static final LocationModel location7 = new LocationModel(10007, "Makati");
    public static final LocationModel location8 = new LocationModel(10008, "Pasig");
    public static final LocationModel location9 = new LocationModel(10009, "Manila");
    public static final LocationModel location10 = new LocationModel(10010, "Caloocan");
    public static final LocationModel location11 = new LocationModel(10011, "Las Piñas");
    public static final LocationModel location12 = new LocationModel(10012, "Parañaque");
    public static final LocationModel location13 = new LocationModel(10013, "Malabon");
    public static final LocationModel location14 = new LocationModel(10014, "Navotas");
    public static final LocationModel location15 = new LocationModel(10015, "Valenzuela");
    public static final LocationModel location16 = new LocationModel(10016, "Mandaluyong");
    public static final LocationModel location17 = new LocationModel(10017, "San Juan");
    public static final LocationModel location18 = new LocationModel(10018, "Pateros");
    public static final LocationModel location19 = new LocationModel(10019, "Bacoor");
    public static final LocationModel location20 = new LocationModel(10020, "Imus");
    public static final LocationModel location21 = new LocationModel(10021, "Dasmariñas");
    public static final LocationModel location22 = new LocationModel(10022, "Cavite City");
    public static final LocationModel location23 = new LocationModel(10023, "Biñan");
    public static final LocationModel location24 = new LocationModel(10024, "Santa Rosa");
    public static final LocationModel location25 = new LocationModel(10025, "San Pedro");
    public static final LocationModel location26 = new LocationModel(10026, "Antipolo");
    public static final LocationModel location27 = new LocationModel(10027, "Montalban");
    public static final LocationModel location28 = new LocationModel(10028, "Taytay");
    public static final LocationModel location29 = new LocationModel(10029, "Cainta");
    public static final LocationModel location30 = new LocationModel(10030, "UST");
    public static final LocationModel location31 = new LocationModel(10031, "UP");

    // Car data
    public static final CarModel car1 = new CarModel(20001, R.drawable.sedan, "Toyota", "Corolla", "ABC 123");
    public static final CarModel car2 = new CarModel(20002, R.drawable.hatchback, "Honda", "Civic", "XYZ 789");

    // User data
    public static final UserModel user1 = new UserModel(30001, R.drawable.default_profile_image, "John Doe", "john@example.com", "+639123456789", "DLSU", true, car1);
    public static final UserModel user2 = new UserModel(30002, R.drawable.default_profile_image, "Jane Smith", "jane@example.com", "+639987654321", "ADMU", false);
    public static final UserModel user3 = new UserModel(30003, R.drawable.default_profile_image, "Alice Johnson", "alice@example.com", "+639111222333", "DLSU", true, car2);

    // Ride data
    public static final RideModel ride1 = new RideModel(40001, user1, location3, location1, "toUniversity", "08:00 AM", "09:00 AM", 3, 4, 150.0, true);
    public static final RideModel ride2 = new RideModel(40002, user3, location2, location5, "fromUniversity", "05:00 PM", "06:30 PM", 2, 3, 200.0, true);
    public static final RideModel ride3 = new RideModel(40003, user1, location1, location4, "fromUniversity", "04:30 PM", "05:45 PM", 1, 4, 180.0, true);

    // Booking data
    public static final BookingModel booking1 = new BookingModel(50001, ride1, user1, getDate(2024, 10, 11));
    public static final BookingModel booking2 = new BookingModel(50002, ride2, user2, getDate(2024, 10, 12));
    public static final BookingModel booking3 = new BookingModel(50003, ride3, user3, getDate(2024, 10, 13));

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
        data.add(location7);
        data.add(location8);
        data.add(location9);
        data.add(location10);
        data.add(location11);
        data.add(location12);
        data.add(location13);
        data.add(location14);
        data.add(location15);
        data.add(location16);
        data.add(location17);
        data.add(location18);
        data.add(location19);
        data.add(location20);
        data.add(location21);
        data.add(location22);
        data.add(location23);
        data.add(location24);
        data.add(location25);
        data.add(location26);
        data.add(location27);
        data.add(location28);
        data.add(location29);
        data.add(location30);
        data.add(location31);

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

        data.add(booking1);
        data.add(booking2);
        data.add(booking3);

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

    // Helper method to get a java.util.Date object
    private static Date getDate(int year, int month, int day) {
        Calendar calendar = Calendar.getInstance();
        calendar.set(year, month - 1, day); // Month is 0-indexed
        return calendar.getTime();
    }
}
