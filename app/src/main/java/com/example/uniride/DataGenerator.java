package com.example.uniride;

import java.util.ArrayList;
import java.util.Date;
import java.util.Calendar;

public class DataGenerator {
    // Location data (universities and common locations)
    public static final LocationModel location1 = new LocationModel(10001, "DLSU", true);
    public static final LocationModel location2 = new LocationModel(10002, "ADMU", true);
    public static final LocationModel location3 = new LocationModel(10003, "Taguig", false);
    public static final LocationModel location4 = new LocationModel(10004, "Binondo", false);
    public static final LocationModel location5 = new LocationModel(10005, "Marikina", false);
    public static final LocationModel location6 = new LocationModel(10006, "Quezon City", false);
    public static final LocationModel location7 = new LocationModel(10007, "Makati", false);
    public static final LocationModel location8 = new LocationModel(10008, "Pasig", false);
    public static final LocationModel location9 = new LocationModel(10009, "Manila", false);
    public static final LocationModel location10 = new LocationModel(10010, "Caloocan", false);
    public static final LocationModel location11 = new LocationModel(10011, "Las Piñas", false);
    public static final LocationModel location12 = new LocationModel(10012, "Parañaque", false);
    public static final LocationModel location13 = new LocationModel(10013, "Malabon", false);
    public static final LocationModel location14 = new LocationModel(10014, "Navotas", false);
    public static final LocationModel location15 = new LocationModel(10015, "Valenzuela", false);
    public static final LocationModel location16 = new LocationModel(10016, "Mandaluyong", false);
    public static final LocationModel location17 = new LocationModel(10017, "San Juan", false);
    public static final LocationModel location18 = new LocationModel(10018, "Pateros", false);
    public static final LocationModel location19 = new LocationModel(10019, "Bacoor", false);
    public static final LocationModel location20 = new LocationModel(10020, "Imus", false);
    public static final LocationModel location21 = new LocationModel(10021, "Dasmariñas", false);
    public static final LocationModel location22 = new LocationModel(10022, "Cavite City", false);
    public static final LocationModel location23 = new LocationModel(10023, "Biñan", false);
    public static final LocationModel location24 = new LocationModel(10024, "Santa Rosa", false);
    public static final LocationModel location25 = new LocationModel(10025, "San Pedro", false);
    public static final LocationModel location26 = new LocationModel(10026, "Antipolo", false);
    public static final LocationModel location27 = new LocationModel(10027, "Montalban", false);
    public static final LocationModel location28 = new LocationModel(10028, "Taytay", false);
    public static final LocationModel location29 = new LocationModel(10029, "Cainta", false);
    public static final LocationModel location30 = new LocationModel(10030, "UST", true);
    public static final LocationModel location31 = new LocationModel(10031, "UP", true);


    // Sample rides (assuming userID 30003 and 30004 are drivers)
    public static final RideModel ride1 = new RideModel(40001, 30003, // driver 30003
            10003, 10001, // Taguig to DLSU
            "toUniversity", "07:30 AM", "08:30 AM",
            4, 4, 150.0, true);

    public static final RideModel ride2 = new RideModel(40002, 30003, // driver 30003
            10001, 10004, // DLSU to Binondo
            "fromUniversity", "05:30 PM", "06:30 PM",
            4, 4, 180.0, true);

    public static final RideModel ride3 = new RideModel(40003, 30004, // driver 30004
            10006, 10002, // Quezon City to ADMU
            "toUniversity", "06:45 AM", "07:45 AM",
            6, 6, 200.0, true);

    public static final RideModel ride4 = new RideModel(40004, 30004, // driver 30004
            10002, 10005, // ADMU to Marikina
            "fromUniversity", "04:30 PM", "05:30 PM",
            6, 6, 220.0, true);


    ////FRONTEND DEMO ONLY DONT USE FOR FIREBASE
    // Car Data
    public static final CarModel car1 = new CarModel(20001, R.drawable.sedan, "Toyota", "Corolla", "ABC 123", 4);
    public static final CarModel car2 = new CarModel(20002, R.drawable.hatchback, "Honda", "Civic", "XYZ 789", 4);
    // User data
    public static final UserModel user1 = new UserModel(30001, R.drawable.a_icon, "Alice Johnson",
            "alice@example.com", "+639123456789", 10001); // DLSU
    public static final UserModel user2 = new UserModel(30002, R.drawable.b_icon, "Barbie Smith",
            "barbie@example.com", "+639987654321", 10002); // ADMU
    public static final UserModel user3 = new UserModel(30003, R.drawable.c_icon, "Cherry Joe",
            "cherry@example.com", "+639111222333", 10001); // DLSU
    public static final UserModel user4 = new UserModel(30004, R.drawable.d_icon, "Daniella Mellow",
            "daniella@example.com", "+639123123123", 10002); // ADMU
    public static final UserModel user5 = new UserModel(30005, R.drawable.e_icon, "Elon Sunshine",
            "elon@example.com", "+639321321321", 10001); // DLSU
    public static final UserModel user6 = new UserModel(30006, R.drawable.f_icon, "Fairy Akew",
            "fairy@example.com", "+639132132132", 10002); // ADMU
    // Message data
    public static final MessageModel message1 = new MessageModel(70001, 30001, 30002, // Alice to Barbie
            "Hi Barbie, nice to meet you!", getDate(2024, 10, 9, 2, 24, 0));
    public static final MessageModel message2 = new MessageModel(70001, 30002, 30001, // Barbie to Alice
            "Hi Alice, nice to meet you as well!", getDate(2024, 10, 10, 5, 51, 0));
    public static final MessageModel message3 = new MessageModel(70001, 30001, 30002, // Alice to Barbie
            "I'll be waiting down in the lobby :)", getDate(2024, 10, 11, 6, 53, 0));
    public static final MessageModel message4 = new MessageModel(70002, 30003, 30001, // Cherry to Alice
            "Hello there.", getDate(2024, 10, 12, 8, 2, 0));
    public static final MessageModel message5 = new MessageModel(70002, 30001, 30003, // Alice to Cherry
            "Oh hi Cherry! I'll be your driver later!", new Date());

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

    public static ArrayList<RideModel> loadRideData() {
        ArrayList<RideModel> data = new ArrayList<>();
        data.add(ride1);
        data.add(ride2);
        data.add(ride3);
        data.add(ride4);
        return data;
    }

    //FRONTEND DEMO ONLY DONT USE FOR FIREBASE
    public static ArrayList<CarModel> loadCarData() {
        ArrayList<CarModel> data = new ArrayList<>();
        data.add(car1);
        data.add(car2);
        return data;
    }

    //FRONTEND DEMO ONLY DONT USE FOR FIREBASE
    public static ArrayList<UserModel> loadUserData() {
        ArrayList<UserModel> data = new ArrayList<>();

        // Add base users
        data.add(user1);
        data.add(user2);
        data.add(user3);
        data.add(user4);
        data.add(user5);
        data.add(user6);

        // Set up drivers with cars and initial balance
        user1.setDriver(true);
        user1.setCarID(20001); // car1
        user1.setBalance(500.0);

        user3.setDriver(true);
        user3.setCarID(20002); // car2
        user3.setBalance(750.0);

        return data;
    }
    //FRONTEND DEMO ONLY DONT USE FOR FIREBASE
    public static ArrayList<MessageModel> loadMessageData() {
        ArrayList<MessageModel> data = new ArrayList<>();
        data.add(message1);
        data.add(message2);
        data.add(message3);
        data.add(message4);
        data.add(message5);
        return data;
    }

    // Helper method to get a java.util.Date object
    private static Date getDate(int year, int month, int day, int hour, int minute, int second) {
        Calendar calendar = Calendar.getInstance();
        calendar.set(year, month - 1, day, hour, minute, second);
        return calendar.getTime();
    }
}
