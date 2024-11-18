package com.example.uniride;

public class MyFirestoreReferences {
    // Constants for Firestore collections
    public static final String BOOKINGS_COLLECTION = "bookings";
    public static final String RIDES_COLLECTION = "rides";
    public static final String USERS_COLLECTION = "users";
    public static final String CARS_COLLECTION = "cars";
    public static final String LOCATIONS_COLLECTION = "locations";
    public static final String REVIEWS_COLLECTION = "reviews";
    public static final String MESSAGES_COLLECTION = "messages";

    // Fields for the Bookings collection
    public static final class Bookings {
        public static final String ID = "bookingID";
        public static final String RIDE_ID = "rideID";         // Changed from RIDE
        public static final String PASSENGER_ID = "passengerID"; // Changed from USER
        public static final String DATE = "date";
        public static final String IS_PAYMENT_COMPLETE = "isPaymentComplete";
        public static final String IS_BOOKING_DONE = "isBookingDone";
    }

    // Fields for the Rides collection
    public static final class Rides {
        public static final String ID = "rideID";
        public static final String DRIVER_ID = "driverID";           // Changed from DRIVER
        public static final String FROM_LOCATION_ID = "fromLocationID"; // Changed from FROM_LOCATION
        public static final String TO_LOCATION_ID = "toLocationID";     // Changed from TO_LOCATION
        public static final String TYPE = "type";
        public static final String DEPARTURE_TIME = "departureTime";
        public static final String ARRIVAL_TIME = "arrivalTime";
        public static final String AVAILABLE_SEATS = "availableSeats";
        public static final String TOTAL_SEATS = "totalSeats";
        public static final String PRICE = "price";
        public static final String IS_ACTIVE = "isActive";
    }

    // Fields for the Users collection
    public static final class Users {
        public static final String ID = "userID";
        public static final String PFP = "pfp";
        public static final String NAME = "name";
        public static final String EMAIL = "email";
        public static final String PHONE_NUMBER = "phoneNumber";
        public static final String UNIVERSITY_ID = "universityID";  // Changed from UNIVERSITY
        public static final String IS_DRIVER = "isDriver";
        public static final String CAR_ID = "carID";               // Changed from CAR
        public static final String BALANCE = "balance";
    }

    // Fields for the Cars collection
    public static final class Cars {
        public static final String ID = "carID";
        public static final String CAR_IMAGE = "carImage";
        public static final String MAKE = "make";
        public static final String MODEL = "model";
        public static final String PLATE_NUMBER = "plateNumber";
        public static final String SEATING_CAPACITY = "seatingCapacity";
    }

    // Fields for the Locations collection
    public static final class Locations {
        public static final String ID = "locationID";
        public static final String NAME = "name";
        public static final String IS_UNIVERSITY = "isUniversity";
    }

    // Fields for the Reviews collection
    public static final class Reviews {
        public static final String ID = "reviewID";
        public static final String REVIEWER_ID = "reviewerID";     // Changed from REVIEWER
        public static final String RECIPIENT_ID = "recipientID";   // Changed from RECIPIENT
        public static final String RATING = "rating";
        public static final String COMMENT = "comment";
        public static final String DATE = "date";
    }

    // Fields for the Messages collection
    public static final class Messages {
        public static final String ID = "messageID";
        public static final String CHAT_ID = "chatID";
        public static final String SENDER_ID = "senderID";         // Changed from SENDER
        public static final String RECIPIENT_ID = "recipientID";   // Changed from RECIPIENT
        public static final String MESSAGE = "message";
        public static final String DATE = "date";
    }
}