package com.example.uniride;

import java.io.Serializable;
import java.time.LocalDateTime;

public class ReviewModel implements Serializable {
    private int reviewID;
    private UserModel reviewer;
    private UserModel recipient;
    private float rating;
    private String comment;
    private LocalDateTime date;

    public ReviewModel(int reviewID, UserModel reviewer, UserModel recipient, float rating, String comment, LocalDateTime date) {
        this.reviewID = reviewID;
        this.reviewer = reviewer;
        this.recipient = recipient;
        this.rating = rating;
        this.comment = comment;
        this.date = date;
    }

    // Getters
    public int getReviewID() {
        return reviewID;
    }
    public UserModel getReviewer() {
        return reviewer;
    }
    public UserModel getRecipient() {
        return recipient;
    }
    public float getRating() {
        return rating;
    }
    public String getComment() {
        return comment;
    }
    public LocalDateTime getDate() {
        return date;
    }

    // Setters
    public void setRating(float rating) {
        this.rating = rating;
    }
    public void setComment(String comment) {
        this.comment = comment;
    }
}
