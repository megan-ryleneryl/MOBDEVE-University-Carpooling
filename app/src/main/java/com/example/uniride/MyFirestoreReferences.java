package com.example.uniride;

public class MyFirestoreReferences {
    // Constants for Firestore collections
    public static final String BOOKINGS_COLLECTION = "BookingModel";
    public static final String RIDES_COLLECTION = "RideModel";
    public static final String USERS_COLLECTION = "UserModel";
    public static final String CARS_COLLECTION = "CarModel";
    public static final String LOCATIONS_COLLECTION = "LocationModel";
    public static final String REVIEWS_COLLECTION = "ReviewModel";
    public static final String MESSAGES_COLLECTION = "MessageModel";

    // Fields for the Bookings collection
    public static final class Bookings {
        public static final String ID = "bookingID";
        public static final String RIDE = "ride";
        public static final String USER = "user"; // TODO: Is it better to use the object or the id?
        public static final String DATE = "date";
        public static final String IS_PAYMENT_COMPLETE = "isPaymentComplete";
        public static final String IS_BOOKING_DONE = "isBookingDone";
    }

    // Fields for the Rides collection
    public static final class Rides {
        public static final String ID = "rideID";
        public static final String DRIVER = "driver";
        public static final String FROM_LOCATION = "from";
        public static final String TO_LOCATION = "to";
        public static final String RIDE_TYPE = "type";
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
        public static final String UNIVERSITY = "university";
        public static final String IS_DRIVER = "isDriver";
        public static final String CAR = "car";
        public static final String BALANCE = "balance";
    }

    // Fields for the Cars collection
    public static final class Cars {
        public static final String ID = "carID";
        public static final String CAR_IMAGE = "carImage";
        public static final String MAKE = "make";
        public static final String MODEL = "model";
        public static final String PLATE_NUMBER = "plateNumber";
    }

    // Fields for the Messages collection
    public static final class Messages {
        public static final String ID = "messageID";
        public static final String SENDER = "senger";
        public static final String RECIPIENT = "recipient";
        public static final String MESSAGE = "message";
        public static final String DATE = "date";
    }

    // Fields for the Locations collection
    public static final class Locations {
        public static final String ID = "locationID";
        public static final String NAME = "name";
    }

    // Fields for the Reviews collection
    public static final class Reviews {
        public static final String ID = "reviewID";
        public static final String REVIEWER = "reviewer";
        public static final String RECIPIENT = "recipient";
        public static final String RATING = "rating";
        public static final String COMMENT = "comment";
        public static final String DATE = "date";
    }
}
