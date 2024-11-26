package com.example.uniride;

import com.example.uniride.BottomNavigationActivity;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.util.HashMap;
import java.util.Map;
import java.util.Date;

public class HomeChatGenerateActivity {

    private FirebaseFirestore db;
    private FirebaseAuth mAuth;
    private FirebaseUser currentUser;

    public HomeChatGenerateActivity() {
        // Initialize Firebase
        db = FirebaseFirestore.getInstance();
        mAuth = FirebaseAuth.getInstance();
        currentUser = mAuth.getCurrentUser();
    }

    public void sendMessage(int chatID, String messageText, int recipientID) {
        // Ensure the current user is logged in
        if (currentUser == null) {
            // Handle user not being logged in
            return;
        }

        // Create a new message document
        Map<String, Object> message = new HashMap<>();
        message.put("chatID", chatID);
        message.put("senderID", currentUser.getUid()); // Use current user's UID
        message.put("recipientID", recipientID);
        message.put("message", messageText);
        message.put("date", new Date());

        // Add the message to the "messages" collection
        db.collection("messages")  // Firestore will create the "messages" collection automatically
                .add(message)  // Add a new document with auto-generated ID
                .addOnSuccessListener(documentReference -> {
                    // Document added successfully
                    System.out.println("Message sent with ID: " + documentReference.getId());
                })
                .addOnFailureListener(e -> {
                    // Handle error
                    System.out.println("Error adding message: " + e.getMessage());
                });
    }
}