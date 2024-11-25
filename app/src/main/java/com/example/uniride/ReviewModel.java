package com.example.uniride;

import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.DocumentSnapshot;
import java.io.Serializable;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.List;
import java.util.ArrayList;

public class ReviewModel implements Serializable {
    private int reviewID;
    private int reviewerID;
    private int recipientID;
    private float rating;
    private String comment;
    private Date date;        // Changed from LocalDateTime to Date

    // Transient fields - not stored in Firebase
    private transient UserModel reviewerObj;
    private transient UserModel recipientObj;

    // Default constructor for Firebase
    public ReviewModel() {}

    public ReviewModel(int reviewID, int reviewerID, int recipientID, float rating,
                       String comment, Date date) {  // Changed parameter type to Date
        this.reviewID = reviewID;
        this.reviewerID = reviewerID;
        this.recipientID = recipientID;
        this.rating = rating;
        this.comment = comment;
        this.date = date;
    }

    // Getters
    public int getReviewID() { return reviewID; }
    public int getReviewerID() { return reviewerID; }
    public int getRecipientID() { return recipientID; }
    public UserModel getReviewer() { return reviewerObj; }
    public UserModel getRecipient() { return recipientObj; }
    public float getRating() { return rating; }
    public String getComment() { return comment; }
    public Date getDate() { return date; }    // Changed return type to Date

    // Setters
    public void setRating(float rating) { this.rating = rating; }
    public void setComment(String comment) { this.comment = comment; }

    // Helper method to convert to Firebase document
    public Map<String, Object> toMap() {
        Map<String, Object> map = new HashMap<>();
        map.put("reviewID", reviewID);
        map.put("reviewerID", reviewerID);
        map.put("recipientID", recipientID);
        map.put("rating", rating);
        map.put("comment", comment);
        map.put("date", date);
        return map;
    }

    // Static method to create from Firebase document
    public static ReviewModel fromMap(Map<String, Object> map) {
        int reviewID = ((Long) map.get("reviewID")).intValue();
        int reviewerID = ((Long) map.get("reviewerID")).intValue();
        int recipientID = ((Long) map.get("recipientID")).intValue();
        float rating = ((Double) map.get("rating")).floatValue();
        String comment = (String) map.get("comment");
        Date date = (Date) map.get("date");

        return new ReviewModel(reviewID, reviewerID, recipientID, rating, comment, date);
    }

    // Method to populate related objects
    public void populateObjects(FirebaseFirestore db, OnPopulateCompleteListener listener) {
        final int[] completedQueries = {0};
        final int totalQueries = 2;

        // Get reviewer data
        db.collection(MyFirestoreReferences.USERS_COLLECTION)
                .whereEqualTo("userID", reviewerID)
                .get()
                .addOnSuccessListener(querySnapshot -> {
                    if (!querySnapshot.isEmpty()) {
                        DocumentSnapshot doc = querySnapshot.getDocuments().get(0);
                        this.reviewerObj = UserModel.fromMap(doc.getData());
                        this.reviewerObj.populateObjects(db, user -> {
                            completedQueries[0]++;
                            checkCompletion(completedQueries[0], totalQueries, listener);
                        });
                    } else {
                        completedQueries[0]++;
                        checkCompletion(completedQueries[0], totalQueries, listener);
                    }
                })
                .addOnFailureListener(e -> {
                    completedQueries[0]++;
                    checkCompletion(completedQueries[0], totalQueries, listener);
                });

        // Get recipient data
        db.collection(MyFirestoreReferences.USERS_COLLECTION)
                .whereEqualTo("userID", recipientID)
                .get()
                .addOnSuccessListener(querySnapshot -> {
                    if (!querySnapshot.isEmpty()) {
                        DocumentSnapshot doc = querySnapshot.getDocuments().get(0);
                        this.recipientObj = UserModel.fromMap(doc.getData());
                        this.recipientObj.populateObjects(db, user -> {
                            completedQueries[0]++;
                            checkCompletion(completedQueries[0], totalQueries, listener);
                        });
                    } else {
                        completedQueries[0]++;
                        checkCompletion(completedQueries[0], totalQueries, listener);
                    }
                })
                .addOnFailureListener(e -> {
                    completedQueries[0]++;
                    checkCompletion(completedQueries[0], totalQueries, listener);
                });
    }

    private void checkCompletion(int completed, int total, OnPopulateCompleteListener listener) {
        if (completed == total && listener != null) {
            listener.onPopulateComplete(this);
        }
    }

    public interface OnPopulateCompleteListener {
        void onPopulateComplete(ReviewModel review);
    }

    // Helper methods for rating calculations
    public static float calculateAverageRating(List<ReviewModel> reviews) {
        if (reviews == null || reviews.isEmpty()) {
            return 0f;
        }

        float sum = 0f;
        for (ReviewModel review : reviews) {
            sum += review.getRating();
        }
        return sum / reviews.size();
    }

    public static List<ReviewModel> getReviewsForUser(List<ReviewModel> allReviews, int userID) {
        List<ReviewModel> userReviews = new ArrayList<>();
        for (ReviewModel review : allReviews) {
            if (review.getRecipientID() == userID) {
                userReviews.add(review);
            }
        }
        return userReviews;
    }

    public static void calculateUserRating(FirebaseFirestore db, int userID,
                                           OnRatingCalculatedListener listener) {
        db.collection(MyFirestoreReferences.REVIEWS_COLLECTION)
                .whereEqualTo("recipientID", userID)
                .get()
                .addOnSuccessListener(querySnapshot -> {
                    float totalRating = 0f;
                    int count = querySnapshot.size();

                    if (count == 0) {
                        listener.onRatingCalculated(0f);
                        return;
                    }

                    for (DocumentSnapshot doc : querySnapshot.getDocuments()) {
                        ReviewModel review = ReviewModel.fromMap(doc.getData());
                        totalRating += review.getRating();
                    }

                    listener.onRatingCalculated(totalRating / count);
                })
                .addOnFailureListener(e -> listener.onRatingCalculated(0f));
    }

    public interface OnRatingCalculatedListener {
        void onRatingCalculated(float rating);
    }
}