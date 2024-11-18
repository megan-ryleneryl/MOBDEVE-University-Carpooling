package com.example.uniride;

import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.DocumentSnapshot;
import java.io.Serializable;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class MessageModel implements Serializable {
    private int chatID;
    private int senderID;    // Changed from UserModel sender
    private int recipientID; // Changed from UserModel recipient
    private String message;
    private Date date;

    // Transient fields - not stored in Firebase
    private transient UserModel senderObj;
    private transient UserModel recipientObj;

    // Default constructor for Firebase
    public MessageModel() {}

    public MessageModel(int chatID, int senderID, int recipientID, String message, Date date) {
        this.chatID = chatID;
        this.senderID = senderID;
        this.recipientID = recipientID;
        this.message = message;
        this.date = date;
    }

    // Method to populate related objects
    public void populateObjects(FirebaseFirestore db, OnPopulateCompleteListener listener) {
        final int[] completedQueries = {0};
        final int totalQueries = 2;

        // Get sender data
        db.collection(MyFirestoreReferences.USERS_COLLECTION)
                .whereEqualTo("userID", senderID)
                .get()
                .addOnSuccessListener(querySnapshot -> {
                    if (!querySnapshot.isEmpty()) {
                        DocumentSnapshot doc = querySnapshot.getDocuments().get(0);
                        this.senderObj = doc.toObject(UserModel.class);
                    }
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
                        this.recipientObj = doc.toObject(UserModel.class);
                    }
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
        void onPopulateComplete(MessageModel message);
    }

    // Getters
    public int getChatID() { return chatID; }
    public int getSenderID() { return senderID; }
    public int getRecipientID() { return recipientID; }
    public String getMessage() { return message; }
    public Date getDate() { return date; }

    // Object getters
    public UserModel getSender() { return senderObj; }
    public UserModel getRecipient() { return recipientObj; }

    // Setters
    public void setMessage(String message) { this.message = message; }

    // Helper method to convert to Firebase document
    public Map<String, Object> toMap() {
        Map<String, Object> map = new HashMap<>();
        map.put("chatID", chatID);
        map.put("senderID", senderID);
        map.put("recipientID", recipientID);
        map.put("message", message);
        map.put("date", date);
        return map;
    }

    // Static method to create from Firebase document
    public static MessageModel fromMap(Map<String, Object> map) {
        return new MessageModel(
                ((Long) map.get("chatID")).intValue(),
                ((Long) map.get("senderID")).intValue(),
                ((Long) map.get("recipientID")).intValue(),
                (String) map.get("message"),
                (Date) map.get("date")
        );
    }
}